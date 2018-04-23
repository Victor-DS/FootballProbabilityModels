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

package victor.santiago.soccer.poisson.metrics.impl;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import victor.santiago.soccer.poisson.metrics.Metrics;
import victor.santiago.soccer.poisson.model.League;
import victor.santiago.soccer.poisson.model.LeagueMetrics;
import victor.santiago.soccer.poisson.model.Match;
import victor.santiago.soccer.poisson.model.SimulatedMatch;
import victor.santiago.soccer.poisson.model.Standing;
import victor.santiago.soccer.poisson.model.Team;
import victor.santiago.soccer.poisson.simulation.Simulation;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class BrazilianChampionshipMetrics implements Metrics {

    private final Simulation simulator;

    @Override
    public Map<League, LeagueMetrics> generate(List<Match> allMatches, List<League> leaguesToSimulate,
                                               int goalLimit, int simulations) {
        Collections.sort(allMatches);

        Map<League, LeagueMetrics> results = new HashMap<>();

        for (League league : leaguesToSimulate) {
            results.put(league, generateMetricsForLeague(allMatches, league, goalLimit, simulations));
        }

        return results;
    }

    @Override
    public LeagueMetrics generate(List<Match> allMatches, League leagueToSimulate, int goalLimit, int simulations) {
        Collections.sort(allMatches);
        return generateMetricsForLeague(allMatches, leagueToSimulate, goalLimit, simulations);
    }

    private LeagueMetrics generateMetricsForLeague(List<Match> allMatches, League league, int goalLimit, int simulations) {
        Date firstMatchDate = league.getMatches().get(0).getDate();
        List<Match> filteredMatches = allMatches.stream()
                .filter(x -> x.getDate().before(firstMatchDate))
                .collect(Collectors.toList());

        Map<Match, List<SimulatedMatch>> simulatedMatches = new HashMap<>();

        for (Match match : league.getMatches()) {
            simulatedMatches.put(match, simulator.simulate(league.getName(), match,
                    filteredMatches, goalLimit, simulations));
            filteredMatches.add(match);
        }

        List<List<SimulatedMatch>> matchesFromLeague = new ArrayList<>();
        for (int simulationIndex = 0; simulationIndex < simulations; simulationIndex++) {
            matchesFromLeague.add(getSimulatedMatchesFromLeague(league, simulatedMatches, simulationIndex));
        }

        simulatedMatches.clear();

        List<List<Standing>> standings = new ArrayList<>();
        matchesFromLeague.forEach(x -> standings.add(generateStandings(x)));
        matchesFromLeague.clear();

        return getMetricsFromStandings(league.getName(), standings);
    }

    private List<SimulatedMatch> getSimulatedMatchesFromLeague(League league,
                                                               Map<Match, List<SimulatedMatch>> simulatedMatches,
                                                               int currentIndex) {
        List<SimulatedMatch> matches = new ArrayList<>();

        Match currentMatch;
        for (int matchIndex = 0; matchIndex < league.getMatches().size(); matchIndex++) {
            currentMatch = league.getMatches().get(matchIndex);

            matches.add(simulatedMatches.get(currentMatch).get(currentIndex));
        }

        return matches;
    }

    private List<Standing> generateStandings(List<SimulatedMatch> matchesFromLeague) {
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

    private LeagueMetrics getMetricsFromStandings(String leagueName, List<List<Standing>> leaguesStandings) {
        LeagueMetrics metrics = new LeagueMetrics(leagueName);
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
