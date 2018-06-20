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

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.time.Instant;
import java.util.Date;

import lombok.Value;

@Value
@DynamoDBTable(tableName = SimulatedMatch.TABLE_NAME)
public class SimulatedMatch {

    public static final String TABLE_NAME = "SimulatedMatches";

    private static final String LEAGUE_NAME = "LEAGUE_NAME";
    private static final String SIMULATION_DATE = "SIMULATION_DATE";
    private static final String HOME_TEAM = "HOME_TEAM";
    private static final String AWAY_TEAM = "AWAY_TEAM";
    private static final String HOME_TEAM_GOALS = "HOME_TEAM_GOALS";
    private static final String AWAY_TEAM_GOALS = "AWAY_TEAM_GOALS";
    private static final String MATCH_DATE = "MATCH_DATE";

    @DynamoDBRangeKey(attributeName = LEAGUE_NAME)
    private final String leagueName;

    @DynamoDBHashKey(attributeName = SIMULATION_DATE)
    private final Instant simulationDate;

    @DynamoDBAttribute(attributeName = HOME_TEAM)
    private final String homeTeam;

    @DynamoDBAttribute(attributeName = AWAY_TEAM)
    private final String awayTeam;

    @DynamoDBAttribute(attributeName = HOME_TEAM_GOALS)
    private final int homeGoals;

    @DynamoDBAttribute(attributeName = AWAY_TEAM_GOALS)
    private final int awayGoals;

    @DynamoDBAttribute(attributeName = MATCH_DATE)
    private final Date matchDate;
}
