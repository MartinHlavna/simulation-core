/*
 * The MIT License
 *
 * Copyright 2017 Martin Hlavňa <mato.hlavna@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sk.uniza.fri.hlavna2.simulation.core;

import sk.uniza.fri.hlavna2.simulation.core.utils.RandomStorage;

/**
 * Simulation engine. Provides simulation setup and ability to simulate one replication
 *
 * @author Martin Hlavňa {@literal <mato.hlavna@gmail.com>}
 */
public interface SimulationEngine {

    /**
     * Simulate one replication. All Random generators registered in the init phase are suplied in the randoms parameter
     *
     * @param parameters
     * @param statistics
     * @param randoms
     */
    void simulate(SimulationParameters parameters, Statistics statistics, RandomStorage randoms);

    /**
     * Initialize. Custom parameters, statistics are suplied. Celled before first replication is called
     *
     * @param parameters Parametrs of the simulation. Should be specified by by the implementation
     * @param statistics Statistics for the simulation. May be extended to suit needs of the simulation
     * @param storage storage place for random generators. All of the generators needs to be created in this phase.
     * After this phsae random storage is closed to the changes
     */
    void init(SimulationParameters parameters, Statistics statistics, RandomStorage storage);

    /**
     * CleanUp hook. Called after last replication is finished
     */
    void cleanUp();
}
