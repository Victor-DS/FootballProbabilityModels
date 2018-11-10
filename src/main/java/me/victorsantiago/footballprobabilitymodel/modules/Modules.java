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

package me.victorsantiago.footballprobabilitymodel.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import me.victorsantiago.footballprobabilitymodel.calculator.impl.EloCalculator;
import me.victorsantiago.footballprobabilitymodel.calculator.impl.PoissonCalculator;
import me.victorsantiago.footballprobabilitymodel.metrics.Metrics;
import me.victorsantiago.footballprobabilitymodel.metrics.impl.BrazilianChampionshipMetrics;
import me.victorsantiago.footballprobabilitymodel.simulation.Simulation;

public class Modules extends AbstractModule {

    public static final String BRAZILIAN_CHAMPIONSHIP_METRICS_POISSON = "BrazilianChampionshipWithPoisson";
    public static final String BRAZILIAN_CHAMPIONSHIP_METRICS_ELO = "BrazilianChampionshipWithElo";

    @Override
    protected void configure() { }

    @Provides
    @Singleton
    @Named(BRAZILIAN_CHAMPIONSHIP_METRICS_POISSON)
    Metrics getBrazilianChampionshipPoissonBasedMetrics() {
        return new BrazilianChampionshipMetrics(new Simulation(new PoissonCalculator()));
    }

    @Provides
    @Singleton
    @Named(BRAZILIAN_CHAMPIONSHIP_METRICS_ELO)
    Metrics getBrazilianChampionshipEloBasedMetrics() {
        return new BrazilianChampionshipMetrics(new Simulation(new EloCalculator()));
    }
}
