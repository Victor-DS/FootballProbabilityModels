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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import victor.santiago.soccer.poisson.model.Match;

public class EloCalculatorTest {

    private static final double DELTA = 0.000001;

    Match homeWin, tie, homeLoss;

    private EloCalculator toTest;

    @Before
    public void setup() {
        toTest = new EloCalculator();

        tie = Match.builder()
                   .home("A")
                   .homeGoals(0)
                   .away("B")
                   .awayGoals(0)
                   .build();

        homeWin = Match.builder()
                       .home("A")
                       .homeGoals(6)
                       .away("B")
                       .awayGoals(0)
                       .build();

        homeLoss = Match.builder()
                        .home("A")
                        .homeGoals(0)
                        .away("B")
                        .awayGoals(2)
                        .build();
    }

    @Test
    public void shouldReturnCorrectWinningProbability() {
        Assert.assertEquals(0.50, toTest.getWinningExpectancy("A", "B"), DELTA);
    }

    @Test
    public void shouldReturnCorrectMatchResultValue() {
        Assert.assertEquals(0.5, toTest.getMatchResultValue(tie, true), DELTA);
        Assert.assertEquals(1.0, toTest.getMatchResultValue(homeWin, true), DELTA);
        Assert.assertEquals(0.0, toTest.getMatchResultValue(homeLoss, true), DELTA);
    }

    @Test
    public void shouldReturnCorrectGoalIndexDifference() {
        Assert.assertEquals(1.0, toTest.getGoalDifferenceIndex(tie), DELTA);
        Assert.assertEquals(1.5, toTest.getGoalDifferenceIndex(homeLoss), DELTA);
        Assert.assertEquals(2.125, toTest.getGoalDifferenceIndex(homeWin), DELTA);
    }
}
