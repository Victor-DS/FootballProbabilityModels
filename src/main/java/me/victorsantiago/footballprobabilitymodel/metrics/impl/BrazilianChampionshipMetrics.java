/*
 * Copyright (c) 2018 victords
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.victorsantiago.footballprobabilitymodel.metrics.impl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import me.victorsantiago.footballprobabilitymodel.metrics.Metrics;
import me.victorsantiago.footballprobabilitymodel.model.League;
import me.victorsantiago.footballprobabilitymodel.model.LeagueMetrics;
import me.victorsantiago.footballprobabilitymodel.model.Match;
import me.victorsantiago.footballprobabilitymodel.model.SimulatedMatch;
import me.victorsantiago.footballprobabilitymodel.model.Standing;
import me.victorsantiago.footballprobabilitymodel.model.Team;
import me.victorsantiago.footballprobabilitymodel.simulation.Simulation;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class BrazilianChampionshipMetrics implements Metrics {

    // TODO: Make it more customizable, either pass as a parameter or set in a config file.
    private static final int METRICS_BATCH_SIZE = 100;

    private final Simulation simulator;

    @Override
    public Map<League, LeagueMetrics> generate(List<Match> allMatches, int historyLimit, List<League> leaguesToSimulate, int simulations) {
        Collections.sort(allMatches);
        List<Match> pastMatches = Collections.synchronizedList(allMatches);
        leaguesToSimulate = Collections.synchronizedList(leaguesToSimulate);

        Map<League, LeagueMetrics> results = new ConcurrentHashMap<>();

        leaguesToSimulate.parallelStream()
                         .forEach(league -> results.put(league,
                                 generateMetricsForLeague(pastMatches, historyLimit, league, simulations, METRICS_BATCH_SIZE)));

        return results;
    }

    @Override
    public LeagueMetrics generate(List<Match> allMatches, int historyLimit, League leagueToSimulate, int simulations) {
        Collections.sort(allMatches);
        allMatches = Collections.synchronizedList(allMatches);
        return generateMetricsForLeague(allMatches, historyLimit, leagueToSimulate, simulations, METRICS_BATCH_SIZE);
    }

    /**
     * Generates metrics for a league by breaking up the metrics into small batches,
     * calculating the metrics for those small batches, and then unifying them.
     *
     * @param allMatches List of all past matches.
     * @param historyLimit Limit of matches to be used from the raw list. Use -1 to use all.
     * @param league League to be simulated.
     * @param simulations Number of simulations to be made by match.
     * @param batchSize Size of each individual simulation batch.
     * @return The unified league metrics.
     */
    private LeagueMetrics generateMetricsForLeague(List<Match> allMatches, int historyLimit, League league,
                                                   int simulations, int batchSize) {
        final Date firstMatchDate = league.getMatches().get(0).getDate();
        final List<Match> limitedMatches = getLimitedMatchesBeforeDate(allMatches, firstMatchDate, historyLimit);

        List<LeagueMetrics> batchOfMetrics = new ArrayList<>();

        while (simulations > 0) {
            batchOfMetrics.add(generateMetricsForLeague(limitedMatches, league, simulations));
            simulations -= batchSize;
        }

        if (simulations < 0) {
            batchOfMetrics.add(generateMetricsForLeague(limitedMatches, league, batchSize + simulations));
        }

        return new LeagueMetrics(batchOfMetrics);
    }

    /**
     * Calculates metrics for a league without breaking it up into small batches.
     * May cause GC overhead with large numbers. Proceed with caution.
     *
     * @param allMatches List of all past matches.
     * @param historyLimit Limit of matches to be used from the raw list. Use -1 to use all.
     * @param league League to be simulated.
     * @param simulations Number of simulations to be made by match.
     * @return The metrics from the simulations.
     */
    @Deprecated
    private LeagueMetrics generateMetricsForLeague(List<Match> allMatches, int historyLimit, League league, int simulations) {
        Date firstMatchDate = league.getMatches().get(0).getDate();
        List<Match> limitedMatches = getLimitedMatchesBeforeDate(allMatches, firstMatchDate, historyLimit);
        return generateMetricsForLeague(limitedMatches, league, simulations);
    }

    /**
     * Receives the prepared matches list and final simulations number to generate metrics.
     *
     * @param limitedMatches List of all the matches that will be used on the calculations.
     * @param league The league to be simulated.
     * @param simulations Number of simulations to be made by match.
     * @return The metrics from the simulations.
     */
    private LeagueMetrics generateMetricsForLeague(List<Match> limitedMatches, League league, int simulations) {
        Map<Match, SimulatedMatch[]> simulatedMatches = new HashMap<>();

        for (Match match : league.getMatches()) {
            simulatedMatches.put(match, simulator.simulate(league.getName(), match,
                    limitedMatches, simulations));
            limitedMatches.add(match);
            limitedMatches.remove(0);
        }

        List<List<SimulatedMatch>> matchesFromLeague = new ArrayList<>();
        for (int simulationIndex = 0; simulationIndex < simulations; simulationIndex++) {
            matchesFromLeague.add(getSimulatedMatchesFromLeague(league, simulatedMatches, simulationIndex));
        }

        simulatedMatches.clear();

        List<List<Standing>> standings = new ArrayList<>();
        matchesFromLeague.forEach(x -> standings.add(generateStandings(x)));
        matchesFromLeague.clear();

        return getMetricsFromStandings(league.getName(), standings, simulations);
    }

    private List<Match> getLimitedMatchesBeforeDate(List<Match> allOrderedMatches, Date firstMatchDate, int limit) {
        if (limit == -1) {
            return allOrderedMatches;
        }

        return allOrderedMatches.stream()
                                .filter(x -> x.getDate().before(firstMatchDate))
                                .limit(limit)
                                .collect(Collectors.toList());
    }

    private List<SimulatedMatch> getSimulatedMatchesFromLeague(League league,
                                                               Map<Match, SimulatedMatch[]> simulatedMatches,
                                                               int currentIndex) {
        List<SimulatedMatch> matches = new ArrayList<>();

        Match currentMatch;
        for (int matchIndex = 0; matchIndex < league.getMatches().size(); matchIndex++) {
            currentMatch = league.getMatches().get(matchIndex);

            matches.add(simulatedMatches.get(currentMatch)[currentIndex]);
        }

        return matches;
    }

    @VisibleForTesting
    List<Standing> generateStandings(List<SimulatedMatch> matchesFromLeague) {
        Map<Team, Standing> teamStanding = new HashMap<>();

        for (SimulatedMatch match : matchesFromLeague) {
            teamStanding.computeIfPresent(new Team(match.getHomeTeam()),
                (k, v) -> v.incrementWithMatch(match));
            teamStanding.computeIfAbsent(new Team(match.getHomeTeam()),
                k -> new Standing(new Team(match.getHomeTeam()), match));

            teamStanding.computeIfPresent(new Team(match.getAwayTeam()),
                (k, v) -> v.incrementWithMatch(match));
            teamStanding.computeIfAbsent(new Team(match.getAwayTeam()),
                k -> new Standing(new Team(match.getAwayTeam()), match));
        }

        return teamStanding.values().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    private LeagueMetrics getMetricsFromStandings(String leagueName, List<List<Standing>> leaguesStandings, int simulations) {
        LeagueMetrics metrics = new LeagueMetrics(leagueName);
        metrics.setNumberOfSimulations(simulations);
        metrics.setChampion(getProbabilitiesByPosition(leaguesStandings, 0));
        metrics.setHighRanking(getProbabilitiesByPositionRange(leaguesStandings, 0, 4));

        int lastPosition = leaguesStandings.get(0).size() - 1;
        metrics.setLowRanking(getProbabilitiesByPositionRange(leaguesStandings, lastPosition - 5, lastPosition));

        return metrics;
    }

    private Map<Team, Double> getProbabilitiesByPosition(List<List<Standing>> standings, int position) {
        Multiset<Team> teamMultiset = HashMultiset.create();
        standings.forEach(x -> teamMultiset.add(x.get(position).getTeam()));

        double simulations = standings.size();

        Map<Team, Double> response = new HashMap<>();
        teamMultiset.forEach(team -> response.put(team, teamMultiset.count(team) / simulations));

        return response;
    }

    private Map<Team, Double> getProbabilitiesByPositionRange(List<List<Standing>> standings,
                                                              int positionStart, int positionEnd) {
        Map<Team, Double> sumOfAllProbabilities = new HashMap<>();

        Map<Team, Double> currentMapOfProbabilities;
        for (int i = positionStart; i <= positionEnd; i++) {
            currentMapOfProbabilities = getProbabilitiesByPosition(standings, i);
            currentMapOfProbabilities.forEach((k, v) -> sumOfAllProbabilities.merge(k, v, Double::sum));
        }

        return sumOfAllProbabilities;
    }
}
