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

package me.victorsantiago.footballprobabilitymodel.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LeagueMetrics {

    private enum MapType {
        CHAMPION, HIGH_RANKING, LOW_RANKING
    }

    private final String leagueName;

    private Map<Team, Double> champion;

    // Teams probabilities to end up in the top 5 positions;
    private Map<Team, Double> highRanking;

    // Teams probabilities to end up in the low 5 positions;
    private Map<Team, Double> lowRanking;

    private int numberOfSimulations;

    public LeagueMetrics(List<LeagueMetrics> leagueMetricsList) {
        if (leagueMetricsList.isEmpty()) {
            String message = "List of league metrics must NOT be empty.";
            throw new IllegalArgumentException(message);
        }

        this.leagueName = leagueMetricsList.get(0).getLeagueName();
        this.champion = getMergedMap(leagueMetricsList, MapType.CHAMPION);
        this.highRanking = getMergedMap(leagueMetricsList, MapType.HIGH_RANKING);
        this.lowRanking = getMergedMap(leagueMetricsList, MapType.LOW_RANKING);
    }

    private Map<Team, Double> getMergedMap(List<LeagueMetrics> metrics, MapType type) {
        Map<Team, Double> mergedMap = new HashMap<>();

        int totalSimulations = 0;
        for (LeagueMetrics metric : metrics) {
            for (Map.Entry<Team, Double> entry : getMap(metric, type).entrySet()) {
                int finalTotalSimulations = totalSimulations;
                mergedMap.merge(entry.getKey(), entry.getValue(), (key, value) ->
                        getSumOfProbabilities(value, metric.getNumberOfSimulations(),
                                mergedMap.getOrDefault(entry.getKey(), 0.0), finalTotalSimulations));
            }
            totalSimulations += metric.getNumberOfSimulations();
        }

        return mergedMap;
    }

    private Map<Team, Double> getMap(LeagueMetrics leagueMetrics, MapType type) {
        if (type == MapType.CHAMPION) {
            return leagueMetrics.getChampion();
        } else if (type == MapType.HIGH_RANKING) {
            return leagueMetrics.getHighRanking();
        } else if (type == MapType.LOW_RANKING) {
            return leagueMetrics.getLowRanking();
        }

        throw new IllegalArgumentException("Invalid type passed");
    }

    private double getSumOfProbabilities(double originalProbability, int numberOfSimulationsOnOriginal,
                                         double mergedProbability, int numberOfSimulationsOnMerged) {
        int totalSimulations = numberOfSimulationsOnMerged + numberOfSimulationsOnOriginal;

        if (totalSimulations <= 0.0) {
            return 0.0;
        }

        return ((originalProbability * numberOfSimulationsOnOriginal) + (mergedProbability * numberOfSimulationsOnMerged)) / totalSimulations;
    }

    public Team getMostLikelyChampion() {
        return champion.entrySet().stream()
                       .max(Map.Entry.comparingByValue())
                       .get().getKey();
    }

}
