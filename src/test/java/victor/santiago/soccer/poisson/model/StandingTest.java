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

import java.time.Instant;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StandingTest {

    private Standing toTest;

    @Before
    public void setup() {
        toTest = Standing.builder()
                         .team(new Team("São Paulo"))
                         .wins(5)
                         .draws(2)
                         .losses(10)
                         .goalsFor(10)
                         .goalsAgainst(12)
                         .build();
    }

    @Test
    public void shouldIncrementWithMatchCorrectly() {
        SimulatedMatch simulatedMatch = new SimulatedMatch("LEAGUE", Instant.now(),
                "São Paulo", "Corinthians", 2, 1, new Date());

        toTest.incrementWithMatch(simulatedMatch);

        Assert.assertEquals(12, toTest.getGoalsFor());
        Assert.assertEquals(13, toTest.getGoalsAgainst());
        Assert.assertEquals(6, toTest.getWins());
    }

    @Test
    public void shouldReturnCorrectGoalDifference() {
        Assert.assertEquals(-2, toTest.getGoalDifference());
    }

    @Test
    public void shouldReturnCorrectAmountOfPoints() {
        Assert.assertEquals(17, toTest.getPoints());
    }
}
