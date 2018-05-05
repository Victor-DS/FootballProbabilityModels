# SoccerPoisson

[![CircleCI](https://circleci.com/gh/Victor-DS/SoccerPoisson.svg?style=svg)](https://circleci.com/gh/Victor-DS/SoccerPoisson)[![codecov](https://codecov.io/gh/Victor-DS/SoccerPoisson/branch/master/graph/badge.svg)](https://codecov.io/gh/Victor-DS/SoccerPoisson)

A poisson-based model to calculate probabilities in soccer matches.

## Poisson Distribution

> In probability theory and statistics, the Poisson distribution, named after French mathematician Siméon Denis Poisson, is a discrete probability distribution that expresses the probability of a given number of events occurring in a fixed interval of time or space if these events occur with a known constant rate and independently of the time since the last event.

[Wikipedia](https://en.wikipedia.org/wiki/Poisson_distribution)

## Soccer Model

The idea behind this model is that the number of goals scored by a team is a Poisson distribution and independent from the other team.

Having that in mind, we calculate the expected number of goals that both teams team would score (considering the team's past matches), and calculate all the score probabilities with a given goal limit.

This model can give you not only the most likely winner, but the most likely scores as well.

The software can also simulate an entire league and generate metrics based on it, such as the teams with the most chances to win and probabilities for a team to end up in the lowest or highest end of the standings.

## Examples

This software uses Guice as a dependency injection library, so always remember to add the modules when creating the injector.

For a simple example, let's say you want simulation metrics for a a list of leagues. 

First, you create your injector and get your League metrics instance:

```java
Injector injector = Guice.createInjector(new Modules());
Metrics metrics = injector.getInstance(BrazilianChampionshipMetrics.class);
```

Then load your data from the JSON files:

```java
List<League> allLeagues = SerializerUtil.getLeagues("/Users/Me/DataOne.json", "/Users/Me/DataTwo.json", "/Users/Me/InfiniteData.json");
List<League> leaguesToSimulate = SerializerUtil.getLeagues("/Users/Me/LeaguesToSimulate.json");

List<Match> allMatches = new ArrayList<>();
allLeagues.forEach(x -> allMatches.addAll(x.getMatches()));
```

Now you can simulate it the leagues, and generate metrics based on it (like who is the most likely champion?):

```java
final int simulations = 10_000; // Number of simulations per match.
final int historyLimit = 380; // Maximum number of past matches to be used on calculations. (Use -1 to use all)
final int goalLimit = 5; // Limit of goals per match. This example will use all combinations from 0x0 to 5x5.
Map<League, LeagueMetrics> leagueMetrics = metrics.generate(allMatches, historyLimit, leaguesToSimulate, goalLimit, simulations);
```

If you'd like you can just serialize your data to a CSV file.

```java
SerializerUtil.saveMetricsToCsv("/Users/Me/MyData.csv", leagueMetrics.values());
```

## TODO

- [x] Input leagues and matches from JSON;
- [x] Poisson calculations model;
- [ ] Implement metrics for more types of championships;

## LICENSE

```
The MIT License (MIT)

Copyright (c) 2018 Victor Domingos Santiago

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
