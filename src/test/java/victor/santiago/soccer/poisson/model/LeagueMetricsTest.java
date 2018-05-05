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

package victor.santiago.soccer.poisson.model;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class LeagueMetricsTest {

    @Test
    public void shouldReturnTheMostLikelyChampion() {
        Map<Team, Double> probabilities = new HashMap<>();
        probabilities.put(new Team("A"), 50.0);
        probabilities.put(new Team("B"), 25.0);
        probabilities.put(new Team("C"), 20.0);
        probabilities.put(new Team("D"), 5.0);

        LeagueMetrics toTest = new LeagueMetrics("Some League");
        toTest.setChampion(probabilities);

        Assert.assertEquals(new Team("A"), toTest.getMostLikelyChampion());
    }

    @Test
    public void shouldReturnMergedMetrics() {
        final Team teamA = new Team("A");
        final Team teamB = new Team("B");

        Map<Team, Double> probabilitiesOne = new HashMap<>();
        probabilitiesOne.put(teamA, 1.0);
        probabilitiesOne.put(teamB, 0.0);

        Map<Team, Double> probabilitiesTwo = new HashMap<>();
        probabilitiesTwo.put(teamA, 0.1);
        probabilitiesTwo.put(teamB, 0.9);

        LeagueMetrics metricsOne = new LeagueMetrics("League A");
        metricsOne.setNumberOfSimulations(10);
        metricsOne.setChampion(probabilitiesOne);
        metricsOne.setHighRanking(probabilitiesOne);
        metricsOne.setLowRanking(probabilitiesOne);

        LeagueMetrics metricsTwo = new LeagueMetrics("League A");
        metricsTwo.setNumberOfSimulations(100);
        metricsTwo.setChampion(probabilitiesTwo);
        metricsTwo.setHighRanking(probabilitiesTwo);
        metricsTwo.setLowRanking(probabilitiesTwo);

        Map<Team, Double> expectedMergedProbabilities = new HashMap<>();
        expectedMergedProbabilities.put(teamA, (20.0/110.0));
        expectedMergedProbabilities.put(teamB, (90.0/110.0));

        LeagueMetrics toTest = new LeagueMetrics(Lists.newArrayList(metricsOne, metricsTwo));

        Assert.assertEquals(teamB, toTest.getMostLikelyChampion());
        Assert.assertEquals(expectedMergedProbabilities, toTest.getChampion());
        Assert.assertEquals(expectedMergedProbabilities, toTest.getHighRanking());
        Assert.assertEquals(expectedMergedProbabilities, toTest.getLowRanking());
    }
}
