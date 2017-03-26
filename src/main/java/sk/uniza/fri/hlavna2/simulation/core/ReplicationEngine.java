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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import sk.uniza.fri.hlavna2.simulation.core.exception.ReplicationsStoppedException;
import sk.uniza.fri.hlavna2.simulation.core.listeners.SimulationProgressListener;
import sk.uniza.fri.hlavna2.simulation.core.utils.RandomStorageImpl;

/**
 * Basic replication engine for simulation
 *
 * @author Martin Hlavňa {@literal <mato.hlavna@gmail.com>}
 */
public class ReplicationEngine {

    private final Statistics statistics;
    private final SimulationParameters parameters;
    private final SimulationEngine command;
    private final RandomStorageImpl randomStorage;
    private final List<SimulationProgressListener> listeners;
    private boolean isStopped;

    private ReplicationEngine(SimulationEngine command, Statistics statistics, SimulationParameters parameters) {
        this.statistics = statistics;
        this.randomStorage = new RandomStorageImpl(new HashMap<>());
        this.command = command;
        this.listeners = new LinkedList<>();
        this.parameters = parameters;
    }

    /**
     * Register new progress liustener
     *
     * @param listener Listener to register
     */
    public void addProgressListener(SimulationProgressListener listener) {
        listeners.add(listener);
    }

    /**
     * Deregister prevoiusly registered progress listener
     *
     * @param listener Listener to remove
     */
    public void removeProgressListener(SimulationProgressListener listener) {
        listeners.remove(listener);
    }

    /**
     * Solve number of iterations. This method can be called number of times, resulting in better precistion of the
     * result.
     *
     * If simulation is stopped before calling of this method, this method will throw exception
     *
     * @param replications Number of iterations to solve in this iteration
     */
    public void solve(int replications) {
        if (isStopped) {
            throw new ReplicationsStoppedException();
        }
        int i;
        for (i = 0; i < replications; i++) {
            if (!isStopped) {
                command.simulate(parameters, statistics, randomStorage);
                statistics.setIterationsRunned(i);
                invokeReplicationEnded(i);
            } else {
                break;
            }

        }
        isStopped = true;
        invokeSimulationEnded();
    }

    /**
     * Create solver with given command, parameters, and default statistics
     *
     * @param engine Command to use for simulation
     * @param parameters Parameters mostly used by command
     * @return Instance of the solver
     */
    public static ReplicationEngine getSolver(SimulationEngine engine, SimulationParameters parameters) {
        return ReplicationEngine.getSolver(engine, parameters, new Statistics());
    }

    /**
     * Create solver with given simulation engine, parameters and custom statistics
     *
     * @param engine Command to use for simulation
     * @param parameters parameters mostly used by the command
     * @param statistics Custom statstics
     * @return Instance of the solver
     */
    public static ReplicationEngine getSolver(SimulationEngine engine, SimulationParameters parameters, Statistics statistics) {
        ReplicationEngine solver = new ReplicationEngine(engine, statistics, parameters);
        engine.init(parameters, statistics, solver.randomStorage);
        solver.randomStorage.setInitialized();
        return solver;
    }

    /**
     * Stop the simulation.
     *
     * If simulation is currently solving for number of iteration, Solver waits to the end of current simulation and
     * then stops solving. Another call to the solve method will throw exception
     */
    public void stop() {
        isStopped = true;
    }

    private void invokeReplicationEnded(int number) {
        for (SimulationProgressListener listener : listeners) {
            listener.replicationEnded(number, new Statistics(statistics));
        }
    }

    private void invokeSimulationEnded() {
        for (SimulationProgressListener listener : listeners) {
            listener.simulationEnded(new Statistics(statistics));
        }
    }
}
