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

package victor.santiago.soccer.poisson.calculator;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

import victor.santiago.soccer.poisson.model.Match;
import victor.santiago.soccer.poisson.model.MatchProbability;

/**
 * Poisson calculator.
 *
 * We're assuming all matches that get here are already sorted.
 *
 */
public class Calculator {

    public List<MatchProbability> getMatchesProbabilities(List<Match> futureMatches, List<Match> pastMatches, int goalLimit) {
        List<MatchProbability> probabilities = new ArrayList<>();

        for (Match match : futureMatches) {
            probabilities.add(getMatchProbability(match, pastMatches, goalLimit));
        }

        return probabilities;
    }

    public MatchProbability getMatchProbability(Match match, List<Match> pastMatches, int goalLimit) {
        final double expectedNumberOfHomeGoals = getExpectedHomeTeamGoals(match.getHome(), match.getAway(), pastMatches);
        final double expectedNumberOfAwayGoals = getExpectedAwayTeamGoals(match.getHome(), match.getAway(), pastMatches);

        double[][] scoreProbabilities = new double[goalLimit+1][goalLimit+1];

        double currentProbabilityForHome, currentProbabilityForAway;
        for (int homeGoal = 0; homeGoal < scoreProbabilities.length; homeGoal++) {
            for (int awayGoal = 0; awayGoal < scoreProbabilities[homeGoal].length; awayGoal++) {
                currentProbabilityForHome = getPoissonProbability(homeGoal, expectedNumberOfHomeGoals);
                currentProbabilityForAway = getPoissonProbability(awayGoal, expectedNumberOfAwayGoals);

                scoreProbabilities[homeGoal][awayGoal] = currentProbabilityForHome * currentProbabilityForAway;
            }
        }

        return MatchProbability.builder()
                               .homeTeam(match.getHome())
                               .awayTeam(match.getAway())
                               .scoreProbability(scoreProbabilities)
                               .build();
    }

    /**
     * Calculates the probability of a team scoring an X number of goals
     * based on the calculated expected number of goals.
     *
     * @param numberOfGoals The number of goals scored that you want the probability for.
     * @param expectedNumberOfGoals The expected number of goals calculated previously.
     * @return
     */
    public double getPoissonProbability(int numberOfGoals, double expectedNumberOfGoals) {
        return Math.pow(expectedNumberOfGoals, numberOfGoals) * Math.pow(Math.E, -expectedNumberOfGoals) / factorial(numberOfGoals);
    }

    /**
     * Simple factorial calculation.
     * Considering it should only be used with the number of goals, we should be fine with ints.
     *
     * @param n n!
     * @return Factorial value of n.
     */
    private int factorial(int n) {
        if (n <= 1) {
            return 1;
        }

        return n * factorial(n-1);
    }

    public double getExpectedHomeTeamGoals(String home, String away, List<Match> allMatches) {
        double homeTeamAttackStrength = getHomeTeamsAttackStrength(home, allMatches);
        double awayTeamDefensiveStrength = getAwayTeamsDefensiveStrength(away, allMatches);

        List<Match> homeTeamMatches = allMatches.stream().filter(x -> x.getHome().equals(home)).collect(toList());
        double averageGoalsAtHome = getAverageGoalsScoredAtHome(homeTeamMatches);

        return homeTeamAttackStrength * awayTeamDefensiveStrength * averageGoalsAtHome;
    }

    public double getExpectedAwayTeamGoals(String home, String away, List<Match> allMatches) {
        double awayTeamAttackStrength = getAwayTeamsAttackStrength(away, allMatches);
        double homeTeamDefensiveStrength = getHomeTeamsDefensiveStrength(home, allMatches);

        List<Match> awayTeamMatches = allMatches.stream().filter(x -> x.getAway().equals(away)).collect(toList());
        double averageGoalsAway = getAverageGoalsScoredAtHome(awayTeamMatches);

        return awayTeamAttackStrength * homeTeamDefensiveStrength * averageGoalsAway;
    }

    /**
     * Calculates the Home team's attack strength, given by:
     * (This team's average goals scored at home) / (All team's scored goals at home)
     *
     * @param homeTeam Target home team to get the attack strength.
     * @param allMatches All league matches.
     * @return Team's attack strength.
     */
    public double getHomeTeamsAttackStrength(String homeTeam, List<Match> allMatches) {
        double allHomeTeamAverageGoals = getAverageGoalsScoredAtHome(allMatches);

        List<Match> homeTeamMatches = allMatches.stream().filter(x -> x.getHome().equals(homeTeam)).collect(toList());
        double targetHomeTeamAverageGoals = getAverageGoalsScoredAtHome(homeTeamMatches);

        return targetHomeTeamAverageGoals / allHomeTeamAverageGoals;
    }

    /**
     * Calculates the Away team's attack strength, given by:
     * (This team's average goals scored away) / (All team's scored goals away)
     *
     * @param awayTeam Target away team to get the attack strength.
     * @param allMatches All league matches.
     * @return Team's attack strength.
     */
    public double getAwayTeamsAttackStrength(String awayTeam, List<Match> allMatches) {
        double allAwayTeamsAverageGoals = getAverageGoalsScoredAway(allMatches);

        List<Match> awayTeamMatches = allMatches.stream().filter(x -> x.getAway().equals(awayTeam)).collect(toList());
        double targetAwayTeamAverageGoals = getAverageGoalsScoredAway(awayTeamMatches);

        return targetAwayTeamAverageGoals / allAwayTeamsAverageGoals;

    }

    /**
     * Calculates the Home team's defensive strength, given by:
     * (This team's average goals concealed at home) / (All team's concealed goals at home)
     *
     * @param homeTeam Target home team to get the defensive strength.
     * @param allMatches All league matches.
     * @return Team's defensive strength.
     */
    public double getHomeTeamsDefensiveStrength(String homeTeam, List<Match> allMatches) {
        double allHomeTeamAverageGoals = getAverageGoalsConcealedAtHome(allMatches);

        List<Match> homeTeamMatches = allMatches.stream().filter(x -> x.getHome().equals(homeTeam)).collect(toList());
        double targetHomeTeamAverageGoals = getAverageGoalsConcealedAtHome(homeTeamMatches);

        return targetHomeTeamAverageGoals / allHomeTeamAverageGoals;
    }

    /**
     * Calculates the Away team's defensive strength, given by:
     * (This team's average goals concealed away) / (All team's concealed goals away)
     *
     * @param awayTeam Target away team to get the defensive strength.
     * @param allMatches All league matches.
     * @return Team's defensive strength.
     */
    public double getAwayTeamsDefensiveStrength(String awayTeam, List<Match> allMatches) {
        double allAwayTeamAverageGoals = getAverageGoalsConcealedAway(allMatches);

        List<Match> awayTeamMatches = allMatches.stream().filter(x -> x.getAway().equals(awayTeam)).collect(toList());
        double targetAwayTeamAverageGoals = getAverageGoalsConcealedAway(awayTeamMatches);

        return targetAwayTeamAverageGoals / allAwayTeamAverageGoals;
    }

    public double getAverageGoalsScoredAtHome(List<Match> matches) {
        return  getAverageGoals(matches, Match::getHomeGoals);
    }

    public double getAverageGoalsScoredAway(List<Match> matches) {
        return  getAverageGoals(matches, Match::getAwayGoals);
    }

    public double getAverageGoalsConcealedAtHome(List<Match> matches) {
        return getAverageGoalsScoredAway(matches);
    }

    public double getAverageGoalsConcealedAway(List<Match> matches) {
        return  getAverageGoalsScoredAtHome(matches);
    }

    private Double getAverageGoals(List<Match> matches, ToIntFunction<Match> mapper) {
        double goalSum = matches.stream().mapToInt(mapper).sum();
        return goalSum / matches.size();
    }

}
