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

package me.victorsantiago.footballprobabilitymodel.metrics.impl;

import me.victorsantiago.footballprobabilitymodel.model.Match;
import me.victorsantiago.footballprobabilitymodel.model.SimulatedMatch;
import me.victorsantiago.footballprobabilitymodel.model.Standing;
import me.victorsantiago.footballprobabilitymodel.simulation.Simulation;
import me.victorsantiago.footballprobabilitymodel.util.SerializerUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BrazilianChampionshipMetricsTest {

    @Mock
    private Simulation calculator;

    @InjectMocks
    private BrazilianChampionshipMetrics toTest;

    @Test
    public void shouldReturnCorrectStanding() throws Exception {
        List<Match> matches = SerializerUtil.getLeagues("src/test/resources/sample.json").get(0).getMatches();
        List<SimulatedMatch> simulatedMatches = convertMatches(matches);

        List<Standing> standings = toTest.generateStandings(simulatedMatches);

        Assert.assertEquals("Palmeiras", standings.get(0).getTeam().getName());
        Assert.assertEquals(80, standings.get(0).getPoints());
        Assert.assertEquals("América-MG", standings.get(19).getTeam().getName());
        Assert.assertEquals(28, standings.get(19).getPoints());
    }

    private List<SimulatedMatch> convertMatches(List<Match> matches) {
        List<SimulatedMatch> response = new ArrayList<>();

        for (Match match : matches) {
            response.add(convertMatch(match));
        }

        return response;
    }

    private SimulatedMatch convertMatch(Match match) {
        return new SimulatedMatch("", Instant.now(), match.getHome(),
                match.getAway(), match.getHomeGoals(), match.getAwayGoals(), match.getDate());
    }
}
