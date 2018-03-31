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

import lombok.Builder;
import lombok.Getter;

@Builder
public class MatchProbability {
    @Getter
    private String homeTeam, awayTeam;

    @Getter
    private double[][] scoreProbability;

    private double homeWinProbability = -1.0;
    private double awayWinProbability= -1.0;
    private double tieProbability = -1.0;

    public double getHomeWinProbability() {
        if (homeWinProbability > -1.0) {
            return homeWinProbability;
        }

        double totalHomeWinProbability = 0.0;

        for (int homeGoal = 0; homeGoal < scoreProbability.length; homeGoal++) {
            for (int awayGoal = 0; awayGoal < scoreProbability[homeGoal].length; awayGoal++) {
                if (homeGoal > awayGoal) {
                    totalHomeWinProbability += scoreProbability[homeGoal][awayGoal];
                }
            }
        }

        this.homeWinProbability = totalHomeWinProbability;
        return homeWinProbability;
    }

    public double getAwayWinProbability() {
        if (awayWinProbability > -1.0) {
            return awayWinProbability;
        }

        double totalAwayWinProbability = 0.0;

        for (int homeGoal = 0; homeGoal < scoreProbability.length; homeGoal++) {
            for (int awayGoal = 0; awayGoal < scoreProbability[homeGoal].length; awayGoal++) {
                if (awayGoal > homeGoal) {
                    totalAwayWinProbability += scoreProbability[homeGoal][awayGoal];
                }
            }
        }

        this.awayWinProbability = totalAwayWinProbability;
        return awayWinProbability;
    }

    public double getTieProbability() {
        if (tieProbability > -1.0) {
            return tieProbability;
        }

        double totalTieProbability = 0.0;

        for (int goal = 0; goal < scoreProbability.length; goal++) {
            totalTieProbability += scoreProbability[goal][goal];
        }

        this.tieProbability = totalTieProbability;
        return tieProbability;
    }
}
