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
package sk.uniza.fri.hlavna2.simulation.core.event;

import sk.uniza.fri.hlavna2.simulation.core.SimulationEngine;
import sk.uniza.fri.hlavna2.simulation.core.SimulationParameters;
import sk.uniza.fri.hlavna2.simulation.core.Statistics;
import sk.uniza.fri.hlavna2.simulation.core.exception.PastTimeException;
import sk.uniza.fri.hlavna2.simulation.core.utils.RandomStorage;

/**
 *
 * @author Martin Hlavňa {@literal <mato.hlavna@gmail.com>}
 */
public abstract class EventSimulationEngine implements SimulationEngine {

    private double currentTime;
    private final double maxTime;
    private final EventCalendar timeline;
    private boolean running;

    public EventSimulationEngine(double maxTime) {
        currentTime = 0.0;
        timeline = new DefaultEventCalendar();
        this.maxTime = maxTime;
    }

    @Override
    public void simulate(SimulationParameters parameters, Statistics statistics, RandomStorage randoms) {
        this.running = true;
        while (!timeline.isEmpty() && currentTime < maxTime && running) {
            Event currentEvent = timeline.nextEvent();
            currentTime = currentEvent.getTime();
            currentEvent.execute(parameters, statistics, randoms);
        }
    }

    public void stop() {
        running = false;
    }

    public final void plan(Event e, double time) {
        if (Double.compare(time, currentTime) >= 0) {
            e.setTime(time);
            timeline.planEvent(e);
        } else {
            throw new PastTimeException();
        }
    }

    protected double getCurrentTime() {
        return currentTime;
    }

}
