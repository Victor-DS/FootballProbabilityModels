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

package victor.santiago.soccer.poisson.calculator.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import victor.santiago.soccer.poisson.calculator.Calculator;
import victor.santiago.soccer.poisson.model.Match;
import victor.santiago.soccer.poisson.model.MatchProbability;

/**
 * We're assuming all matches already come with a pre-defined K, and are all sorted.
 */
@Data
@RequiredArgsConstructor
public class EloCalculator implements Calculator {

    private static final double DEFAULT_INITIAL_ELO_RATING = 1500;

    private Map<String, Double> ratings;
    private Set<Match> alreadyCalculated;
    private final double initialEloRating;

    public EloCalculator() {
        ratings = new ConcurrentHashMap<>();
        alreadyCalculated = ConcurrentHashMap.newKeySet();
        initialEloRating = DEFAULT_INITIAL_ELO_RATING;
    }

    @Override
    public List<MatchProbability> getMatchesProbabilities(List<Match> futureMatches, List<Match> pastMatches) {
        calculateRatings(pastMatches);

        List<MatchProbability> response = new ArrayList<>();

        for (Match match : futureMatches) {
            response.add(getMatchProbability(match));
        }

        return response;
    }

    @Override
    public MatchProbability getMatchProbability(Match match, List<Match> pastMatches) {
        calculateRatings(pastMatches);
        return getMatchProbability(match);
    }

    private MatchProbability getMatchProbability(Match match) {
        final double homeWinProbability = getWinningExpectancy(match.getHome(), match.getAway());
        final double awayWinProbability = getWinningExpectancy(match.getAway(), match.getHome());

        final double[][] scoreProbability = new double[2][2];
        scoreProbability[1][0] = homeWinProbability;
        scoreProbability[0][1] = awayWinProbability;

        return MatchProbability.builder()
                               .homeTeam(match.getHome())
                               .awayTeam(match.getAway())
                               .scoreProbability(scoreProbability)
                               .build();
    }

    private void calculateRatings(List<Match> matches) {
        matches.forEach(match -> {
            if (!alreadyCalculated.contains(match)) {
                updateRatings(match);
            }
        });
    }

    private void updateRatings(Match match) {
        String home = match.getHome();
        String away = match.getAway();

        double diffHome = getPointsDifference(match, true);
        double diffAway = getPointsDifference(match, false);

        double newRatingHome = getNewRating(diffHome, home);
        double newRatingAway = getNewRating(diffAway, away);

        ratings.put(home, newRatingHome);
        ratings.put(away, newRatingAway);

        alreadyCalculated.add(match);
    }

    public double getNewRating(double pointDiff, String team) {
        return ratings.getOrDefault(team, initialEloRating) + pointDiff;
    }

    public double getPointsDifference(Match match, boolean home) {
        String a = home ? match.getHome() : match.getAway();
        String b = !home ? match.getHome() : match.getAway();

        double goalDifferenceIndex = getGoalDifferenceIndex(match);
        double matchResultValue = getMatchResultValue(match, home);
        double winningExpectancy = getWinningExpectancy(a, b);

        return match.getK() * goalDifferenceIndex * (matchResultValue - winningExpectancy);
    }

    public double getMatchResultValue(Match match, boolean home) {
        Match.Result matchResult = match.getResult();

        switch (matchResult) {
            case VICTORY:
                return home ? 1.0 : 0.0;

            case TIE:
                return 0.5;

            case LOSS:
                return home ? 0.0 : 1.0;

            default:
                throw new IllegalArgumentException("Unexpected match result.");
        }
    }

    public double getGoalDifferenceIndex(Match match) {
        int diff = Math.abs(match.getHomeGoals() - match.getAwayGoals());

        if (diff == 0 || diff == 1) {
            return 1.00;
        } else if (diff == 2) {
            return 1.50;
        }

        return (11.00 + ((double) diff)) / 8.00;
    }

    public double getWinningExpectancy(String a, String b) {
        return 1.00 / (Math.pow(10.00, (-getRatingDifference(a, b) / 400.00)) + 1.00);
    }

    private double getRatingDifference(String a, String b) {
        return ratings.getOrDefault(a, initialEloRating) - ratings.getOrDefault(b, initialEloRating);
    }

}
