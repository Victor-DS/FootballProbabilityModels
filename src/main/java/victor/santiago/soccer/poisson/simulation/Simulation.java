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

package victor.santiago.soccer.poisson.simulation;

import com.google.inject.Inject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import lombok.AllArgsConstructor;

import victor.santiago.soccer.poisson.calculator.Calculator;
import victor.santiago.soccer.poisson.model.Match;
import victor.santiago.soccer.poisson.model.MatchProbability;
import victor.santiago.soccer.poisson.model.SimulatedMatch;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class Simulation {

    private static final String NO_LEAGUE = "UNKOWN";

    private final Calculator calculator;

    public Map<Match, List<SimulatedMatch>> simulate(String leagueName, List<Match> matches, List<Match> pastMatches,
                                                     int goalLimit, int times) {
        Map<Match, List<SimulatedMatch>> simulatedMatches = new HashMap<>();

        for (Match match : matches) {
            simulatedMatches.put(match, simulate(leagueName, match, pastMatches, goalLimit, times));
        }

        return simulatedMatches;
    }

    /**
     * Simulates a given match N number of times.
     *
     * @param leagueName The name of the league you're simulating (for DB purposes).
     * @param match The match you want to simulate.
     * @param pastMatches The matches that happened before that. It could be an entire league, and not just this team's matches.
     * @param goalLimit The number of goals to the simulation be limited to for a single team.
     * @param times The amount of times you want to simulate the given match.
     * @return A list of this match simulated N times.
     */
    public List<SimulatedMatch> simulate(String leagueName, Match match, List<Match> pastMatches, int goalLimit, int times) {
        MatchProbability probability = calculator.getMatchProbability(match, pastMatches, goalLimit);
        List<SimulatedMatch> simulatedMatches = new ArrayList<>();

        while (times > 0) {
            simulatedMatches.add(getRandomSimulation(leagueName, probability, match));
            times--;
        }

        return simulatedMatches;
    }

    public List<SimulatedMatch> simulate(Match match, List<Match> pastMatches, int goalLimit, int times) {
        return simulate(NO_LEAGUE, match, pastMatches, goalLimit, times);
    }

    private SimulatedMatch getRandomSimulation(final String league, MatchProbability matchProbability, Match originalMatch) {
        double totalProbabilities = matchProbability.getHomeWinProbability() +
                matchProbability.getAwayWinProbability() +
                matchProbability.getTieProbability() + 1;
        final double[][] scoreProbabilities = matchProbability.getScoreProbability();

        double randomValue = ThreadLocalRandom.current().nextDouble(0, totalProbabilities);

        int homeScore = 0;
        int awayScore = 0;
        for (int homeGoal = 0; homeGoal < scoreProbabilities.length; homeGoal++) {
            for (int awayGoal = 0; awayGoal < scoreProbabilities[homeGoal].length; awayGoal++) {
                randomValue -= scoreProbabilities[homeGoal][awayGoal];

                if (randomValue <= 0.0) {
                    homeScore = homeGoal;
                    awayScore = awayGoal;
                    break;
                }
            }
        }

        return new SimulatedMatch(league, Instant.now(), originalMatch.getHome(), originalMatch.getAway(),
                homeScore, awayScore, originalMatch.getDate());
    }
}
