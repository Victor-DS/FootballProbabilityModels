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

package victor.santiago.soccer.poisson.util;

import static java.util.stream.Collectors.joining;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.experimental.UtilityClass;

import victor.santiago.soccer.poisson.model.League;
import victor.santiago.soccer.poisson.model.LeagueMetrics;
import victor.santiago.soccer.poisson.model.Team;

@UtilityClass
public class SerializerUtil {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("MMM dd, yyyy HH:mm:ss aa")
            .create();

    /**
     * Parses a list of Leagues from a JSON file path.
     *
     * @param path File path.
     * @return List of leagues from the Json file.
     * @throws IOException Thrown if can't read file.
     */
    public List<League> getLeagues(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        String json = new String(encoded, StandardCharsets.UTF_8);
        return gson.fromJson(json, new TypeToken<ArrayList<League>>(){}.getType());
    }

    /**
     * Saves a list of list metrics to a CSV file.
     *
     * @param fullPath The full path on where to save the file.
     * @param metrics The list of metrics for each league.
     * @throws IOException Thrown when something goes wrong when saving the file.
     */
    public void saveMetricsToCsv(String fullPath, List<LeagueMetrics> metrics) throws IOException {
        StringBuilder csvText = new StringBuilder();
        csvText.append("Champion,Probability,High Ranking,Low Ranking");

        for (LeagueMetrics leagueMetrics : metrics) {
            csvText.append(getLeagueMetricsCsvLine(leagueMetrics));
        }

        Files.write(Paths.get(fullPath), csvText.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String getLeagueMetricsCsvLine(LeagueMetrics leagueMetrics) {
        Team mostLikelyChampion = leagueMetrics.getMostLikelyChampion();
        Map<Team, Double> champion = leagueMetrics.getChampion();
        final String highRanking = mapKeysToSortedString(leagueMetrics.getHighRanking());
        final String lowRanking = mapKeysToSortedString(leagueMetrics.getLowRanking());

        return String.format("%s,%s,%s,%s", mostLikelyChampion.getName(),
                champion.get(mostLikelyChampion), highRanking, lowRanking);
    }

    private String mapKeysToSortedString(Map<Team, Double> map) {
        return map.keySet().stream()
                  .map(Team::getName)
                  .sorted()
                  .collect(joining(","));
    }

}
