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

package me.victorsantiago.footballprobabilitymodel.simulation;

import me.victorsantiago.footballprobabilitymodel.calculator.Calculator;
import me.victorsantiago.footballprobabilitymodel.model.Match;
import me.victorsantiago.footballprobabilitymodel.model.MatchProbability;
import me.victorsantiago.footballprobabilitymodel.model.SimulatedMatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimulationTest {

    @Mock
    private Calculator calculator;

    @InjectMocks
    private Simulation toTest;

    @Test
    public void shouldSimulateCorrectly() {
        Match match = Match.builder()
                           .home("São Paulo")
                           .away("Corinthians")
                           .date(new Date())
                           .build();
        List<Match> inputMatches = Collections.singletonList(match);
        final String league = "Campeonato Brasileiro";
        final int timesToSimulate = 10;
        final double[][] mockProbabilities = new double[][]{
                {0.2, 0.2},
                {0.3, 0.3}
        };

        MatchProbability mockProbability = MatchProbability.builder()
                                                           .homeTeam("São Paulo")
                                                           .awayTeam("Corinthians")
                                                           .scoreProbability(mockProbabilities)
                                                           .build();

        when(calculator.getMatchProbability(eq(match), eq(Collections.emptyList())))
                .thenReturn(mockProbability);

        Map<Match, SimulatedMatch[]> simulations = toTest.simulate(league, inputMatches,
                Collections.emptyList(), timesToSimulate);

        verify(calculator).getMatchProbability(eq(match), eq(Collections.emptyList()));
        Assert.assertEquals(timesToSimulate, simulations.get(match).length);
    }
}
