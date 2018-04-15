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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MatchProbabilityTest {

    private MatchProbability toTest;

    @Before
    public void setup() {
        double[][] mockProbabilities = new double[][] {
                {0.4, 0.2},
                {0.0, 0.4}
        };

        toTest = MatchProbability.builder()
                                 .homeTeam("A")
                                 .awayTeam("B")
                                 .scoreProbability(mockProbabilities)
                                 .build();
    }

    @Test
    public void shouldReturnCorrectProbabilities() {
        Assert.assertEquals(0.8, toTest.getTieProbability(), 0.001);
        Assert.assertEquals(0.0, toTest.getHomeWinProbability(), 0.001);
        Assert.assertEquals(0.2, toTest.getAwayWinProbability(), 0.001);
    }
}
