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

package me.victorsantiago.footballprobabilitymodel.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.experimental.UtilityClass;
import me.victorsantiago.footballprobabilitymodel.model.League;
import me.victorsantiago.footballprobabilitymodel.model.LeagueMetrics;
import me.victorsantiago.footballprobabilitymodel.model.Match;
import me.victorsantiago.footballprobabilitymodel.model.Team;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

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

    public List<League> getLeagues(String... paths) throws IOException {
        List<League> allLeagues = new ArrayList<>();

        for (String path : paths) {
            allLeagues.addAll(getLeagues(path));
        }

        return allLeagues;
    }

    /**
     * Parses a list of Leagues from a JSON file path,
     * and return a list of all its matches unsorted.
     *
     * @param paths File path.
     * @return List of leagues from the Json file.
     * @throws IOException Thrown if can't read file.
     */
    public List<Match> getMatchesFromLeaguesFiles(String... paths) throws IOException {
        List<League> leagues = getLeagues(paths);
        List<Match> allMatches = new ArrayList<>();
        leagues.forEach(x -> allMatches.addAll(x.getMatches()));
        return allMatches;
    }

    /**
     * Parses a list of Leagues from a JSON file path, sets a given K value to them
     * and return a list of all its matches unsorted.
     *
     * @param paths File path.
     * @return List of leagues from the Json file.
     * @throws IOException Thrown if can't read file.
     */
    public List<Match> getMatchesFromLeaguesFilesWithK(double k, String... paths) throws IOException {
        List<Match> matches = getMatchesFromLeaguesFiles(paths);
        matches.forEach(m -> m.setK(k));
        return matches;
    }

    /**
     * Saves a list of list metrics to a CSV file.
     *
     * @param fullPath The full path on where to save the file.
     * @param metrics The list of metrics for each league.
     * @throws IOException Thrown when something goes wrong when saving the file.
     */
    public void saveMetricsToCsv(String fullPath, Collection<LeagueMetrics> metrics) throws IOException {
        StringBuilder csvText = new StringBuilder();
        csvText.append("League,Champion,Probability,High Ranking,Low Ranking");
        csvText.append(System.lineSeparator());

        for (LeagueMetrics leagueMetrics : metrics) {
            csvText.append(getLeagueMetricsCsvLine(leagueMetrics));
            csvText.append(System.lineSeparator());
        }

        Files.write(Paths.get(fullPath), csvText.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String getLeagueMetricsCsvLine(LeagueMetrics leagueMetrics) {
        Team mostLikelyChampion = leagueMetrics.getMostLikelyChampion();
        Map<Team, Double> champion = leagueMetrics.getChampion();
        final String highRanking = mapKeysToSortedString(leagueMetrics.getHighRanking(), 5);
        final String lowRanking = mapKeysToSortedString(leagueMetrics.getLowRanking(), 5);

        final String line = String.format("%s,%s,%s,%s,%s", leagueMetrics.getLeagueName(), mostLikelyChampion.getName(),
                champion.get(mostLikelyChampion), highRanking, lowRanking);

        return normalizeText(line);
    }

    private String mapKeysToSortedString(Map<Team, Double> map, int limit) {
        List<String> teams = map.entrySet().stream()
                  .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                  .map(x -> x.getKey().getName())
                  .limit(limit)
                  .collect(toList());

        return teams.stream().sorted().collect(joining("/"));
    }

    public String normalizeText(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                         .replaceAll("[^\\p{ASCII}]", "");
    }

}
