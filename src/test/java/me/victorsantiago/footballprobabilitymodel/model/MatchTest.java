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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MatchTest {

    Match homeWin, tie, homeLoss;

    @Before
    public void setup() {
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
    public void shouldReturnDefaultK() {
        Assert.assertEquals(20.0, Match.builder().build().getK(), 0.00001);
    }

    @Test
    public void shouldReturnCorrectResult() {
        Assert.assertEquals(Match.Result.LOSS, homeLoss.getResult());
        Assert.assertEquals(Match.Result.TIE, tie.getResult());
        Assert.assertEquals(Match.Result.VICTORY, homeWin.getResult());
    }
}
