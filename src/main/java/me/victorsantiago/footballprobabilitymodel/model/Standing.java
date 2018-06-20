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

import com.google.common.collect.ComparisonChain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Standing implements Comparable<Standing> {
    private Team team;
    private int wins;
    private int losses;
    private int draws;
    private int goalsFor;
    private int goalsAgainst;

    public Standing(Team team, SimulatedMatch match) {
        this.team = team;
        incrementWithMatch(match);
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public int getPoints() {
        return wins * 3 + draws;
    }

    public Standing incrementWithMatch(SimulatedMatch match) {
        boolean isHome = team.getName().equals(match.getHomeTeam());

        if (isHome) {
            incrementWithMatchAsHome(match);
        } else {
            incrementWithMatchAsAway(match);
        }

        return this;
    }

    private void incrementWithMatchAsHome(SimulatedMatch match) {
        this.goalsFor += match.getHomeGoals();
        this.goalsAgainst += match.getAwayGoals();

        int goalDifference = match.getHomeGoals() - match.getAwayGoals();
        incrementResult(goalDifference);
    }

    private void incrementWithMatchAsAway(SimulatedMatch match) {
        this.goalsFor += match.getAwayGoals();
        this.goalsAgainst += match.getHomeGoals();

        int goalDifference = match.getAwayGoals() - match.getHomeGoals();
        incrementResult(goalDifference);
    }

    private void incrementResult(int goalDifference) {
        if (goalDifference > 0) {
            wins++;
        } else if (goalDifference == 0) {
            draws++;
        } else {
            losses++;
        }
    }

    @Override
    public int compareTo(Standing o) {
        return ComparisonChain.start()
                .compare(o.getPoints(), this.getPoints())
                .compare(o.getGoalDifference(), this.getGoalDifference())
                .compare(o.getGoalsFor(), this.getGoalsFor())
                .result();
    }
}
