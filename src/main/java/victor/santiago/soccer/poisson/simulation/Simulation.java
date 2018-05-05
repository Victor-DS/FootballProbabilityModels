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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;

import victor.santiago.soccer.poisson.calculator.Calculator;
import victor.santiago.soccer.poisson.model.Match;
import victor.santiago.soccer.poisson.model.MatchProbability;
import victor.santiago.soccer.poisson.model.SimulatedMatch;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class Simulation {

    private static final String NO_LEAGUE = "UNKOWN";

    private final Calculator calculator;

    public Map<Match, SimulatedMatch[]> simulate(String leagueName, List<Match> matches, List<Match> pastMatches,
                                                     int goalLimit, int times) {
        Map<Match, SimulatedMatch[]> simulatedMatches = new ConcurrentHashMap<>();

        matches.parallelStream().forEach(match -> simulatedMatches.put(match, simulate(leagueName, match, pastMatches, goalLimit, times)));

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
    public SimulatedMatch[] simulate(String leagueName, Match match, List<Match> pastMatches, int goalLimit, int times) {
        MatchProbability probability = calculator.getMatchProbability(match, pastMatches, goalLimit);
        SimulatedMatch[] simulatedMatches = new SimulatedMatch[times];

        IntStream.range(0, times).parallel().forEach(
                n -> simulatedMatches[n] = getRandomSimulation(leagueName, probability, match)
        );

        return simulatedMatches;
    }

    public SimulatedMatch[] simulate(Match match, List<Match> pastMatches, int goalLimit, int times) {
        return simulate(NO_LEAGUE, match, pastMatches, goalLimit, times);
    }

    private SimulatedMatch getRandomSimulation(final String league, MatchProbability matchProbability, Match originalMatch) {
        double totalProbabilities = matchProbability.getTotalSumOfProbabilities();
        final double[][] scoreProbabilities = matchProbability.getScoreProbability();
        final int goalLimit = scoreProbabilities.length;

        double randomValue = ThreadLocalRandom.current().nextDouble(0, totalProbabilities);

        int homeScore = 0;
        int awayScore = 0;
        for (int homeGoal = goalLimit - 1; homeGoal >= 0; homeGoal--) {
            for (int awayGoal = goalLimit - 1; awayGoal >= 0; awayGoal--) {
                randomValue -= scoreProbabilities[homeGoal][awayGoal];

                if (randomValue <= 0.0) {
                    homeScore = homeGoal;
                    awayScore = awayGoal;
                    break;
                }
            }

            if (randomValue <= 0.0) {
                break;
            }
        }

        return new SimulatedMatch(league, Instant.now(), originalMatch.getHome(), originalMatch.getAway(),
                homeScore, awayScore, originalMatch.getDate());
    }
}
