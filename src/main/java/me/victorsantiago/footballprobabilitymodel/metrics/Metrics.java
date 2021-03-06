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

package me.victorsantiago.footballprobabilitymodel.metrics;

import me.victorsantiago.footballprobabilitymodel.model.League;
import me.victorsantiago.footballprobabilitymodel.model.LeagueMetrics;
import me.victorsantiago.footballprobabilitymodel.model.Match;

import java.util.List;
import java.util.Map;

public interface Metrics {

    /**
     * Generate metrics for a list of leagues based on a raw list of matches,
     * and a limit on the goals for each match simulation.
     *
     * The algorithm will sort and select the matches prior to the date of
     * the first match of the leagues you want to simulate, according to a limit,
     * then proceed to generate random simulations based on the probabilities calculated
     * and finally generate metrics based on those simulations.
     *
     * @param allMatches Raw list of past matches that can be used.
     * @param historyLimit Limit of matches to be used from the raw list. Use -1 to use all.
     * @param leaguesToSimulate The leagues to be simulated.
     * @param simulations Number of simulations for each match.
     * @return A Map containing each league and its metrics.
     */
    Map<League, LeagueMetrics> generate(List<Match> allMatches, int historyLimit, List<League> leaguesToSimulate, int simulations);

    /**
     * Generate metrics for a list of leagues based on a raw list of matches,
     * and a limit on the goals for each match simulation.
     *
     * The algorithm will sort and select the matches prior to the date of
     * the first match of the leagues you want to simulate, according to a limit,
     * then proceed to generate random simulations based on the probabilities calculated
     * and finally generate metrics based on those simulations.
     *
     * @param allMatches All the past matches to be used as input data.
     * @param historyLimit Limit of matches to be used from the raw list. Use -1 to use all.
     * @param leagueToSimulate The league to be simulated.
     * @param simulations Number of simulations for each match.
     * @return The metrics for the league.
     */
    LeagueMetrics generate(List<Match> allMatches, int historyLimit, League leagueToSimulate, int simulations);
}
