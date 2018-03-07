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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import victor.santiago.soccer.poisson.model.Match;

/**
 * Return values provided by Wolfram Alpha for individual calculations,
 * or manually calculated here to test correct filters.
 */
public class CalculatorTest {

    private static final double DELTA = 0.001;

    private Calculator toTest;
    private List<Match> sampleMatches;

    @Before
    public void setup() {
        toTest = new Calculator();
        sampleMatches = getSampleMatches();
    }

    @Test
    public void shouldReturnCorrectPoissonProbability() {
        final double expectedNumberOfGoals = 1.123;

        Assert.assertEquals(0.205, toTest.getPoissonProbability(2, expectedNumberOfGoals), DELTA);
        Assert.assertEquals(0.365, toTest.getPoissonProbability(1, expectedNumberOfGoals), DELTA);
        Assert.assertEquals(0.325, toTest.getPoissonProbability(0, expectedNumberOfGoals), DELTA);
    }

    @Test
    public void shouldReturnCorrectExpectedGoals() {
        double aAttackStrength = 1 / ((1+1+1+1+3) / 6.0);
        double bDefensiveStrength = 2 / ((1+1+1+1+3) / 6.0);
        double aAverageGoalsAtHome = 1;

        Assert.assertEquals(aAttackStrength * bDefensiveStrength * aAverageGoalsAtHome,
                toTest.getExpectedHomeTeamGoals("A", "B", sampleMatches), DELTA);
    }

    @Test
    public void shouldGetTeamsDefensiveStrength() {
        double allAverageGoalsConcealed = (2+1+2) / 6.0;
        double aTeamAverageGoalsConcealed = 1;

        Assert.assertEquals((aTeamAverageGoalsConcealed / allAverageGoalsConcealed),
                toTest.getHomeTeamsDefensiveStrength("A", sampleMatches),
                DELTA);

    }

    @Test
    public void shouldGetTeamsAttackStrength() {
        double allAverageGoals = (1+1+1+1+3) / 6.0;
        double aTeamAverageGoals = 1;

        Assert.assertEquals((aTeamAverageGoals / allAverageGoals),
                toTest.getHomeTeamsAttackStrength("A", sampleMatches),
                DELTA);
    }

    @Test
    public void shouldReturnCorrectGoalsAverage() {
        double correctHomeAverage = (1+1+1+1+3) / 6.0;
        double correctAwayAverage = (2+1+2) / 6.0;

        Assert.assertEquals(correctHomeAverage, toTest.getAverageGoalsScoredAtHome(sampleMatches), DELTA);
        Assert.assertEquals(correctHomeAverage, toTest.getAverageGoalsConcealedAway(sampleMatches), DELTA);
        Assert.assertEquals(correctAwayAverage, toTest.getAverageGoalsScoredAway(sampleMatches), DELTA);
        Assert.assertEquals(correctAwayAverage, toTest.getAverageGoalsConcealedAtHome(sampleMatches), DELTA);
    }

    private List<Match> getSampleMatches() {
        sampleMatches = new ArrayList<>();

        sampleMatches.add(Match.builder().home("A").homeGoals(1)
                               .away("B").awayGoals(2).build());

        sampleMatches.add(Match.builder().home("A").homeGoals(1)
                               .away("C").awayGoals(0).build());

        sampleMatches.add(Match.builder().home("B").homeGoals(1)
                               .away("A").awayGoals(1).build());

        sampleMatches.add(Match.builder().home("B").homeGoals(1)
                               .away("C").awayGoals(2).build());

        sampleMatches.add(Match.builder().home("C").homeGoals(3)
                               .away("B").awayGoals(0).build());

        sampleMatches.add(Match.builder().home("C").homeGoals(0)
                               .away("A").awayGoals(0).build());

        return sampleMatches;
    }

}
