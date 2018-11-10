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

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Match implements Comparable<Match> {

    private static final double DEFAULT_K = 20;

    public enum Result {
        VICTORY, TIE, LOSS
    }

    private String home;
    private String away;
    private int homeGoals;
    private int awayGoals;
    private Date date;
    private double k;

    public Result getResult() {
        if (homeGoals == awayGoals) {
            return  Result.TIE;
        } else if (homeGoals > awayGoals) {
            return Result.VICTORY;
        } else {
            return Result.LOSS;
        }
    }

    public double getK() {
        if (k == 0) {
            return DEFAULT_K;
        }

        return k;
    }

    @Override
    public int compareTo(Match o) {
        return this.date.compareTo(o.getDate());
    }
}
