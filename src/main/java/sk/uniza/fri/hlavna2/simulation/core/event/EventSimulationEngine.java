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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sk.uniza.fri.hlavna2.simulation.core.SimulationEngine;
import sk.uniza.fri.hlavna2.simulation.core.SimulationParameters;
import sk.uniza.fri.hlavna2.simulation.core.Statistics;
import sk.uniza.fri.hlavna2.simulation.core.exception.PastTimeException;
import sk.uniza.fri.hlavna2.simulation.core.listeners.EventListener;
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
    private boolean paused;
    private final List<EventListener> listeners;
    private SynchronizationEvent syncroEvent;

    public EventSimulationEngine(double maxTime) {
        currentTime = 0.0;
        timeline = new DefaultEventCalendar();
        this.maxTime = maxTime;
        listeners = new LinkedList<>();
    }

    @Override
    public void simulate(SimulationParameters parameters, Statistics statistics, RandomStorage randoms) {
        this.running = true;
        while (!timeline.isEmpty() && currentTime < maxTime && running) {
            synchronized (this) {
                while (paused) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(EventSimulationEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                Event currentEvent = timeline.nextEvent();
                currentTime = currentEvent.getTime();
                currentEvent.execute(parameters, statistics, randoms);
                notifyListeners(currentEvent);
            }
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

    public double getCurrentTime() {
        return currentTime;
    }

    public void notifyListeners(Event event) {
        for (EventListener listener : listeners) {
            listener.onEvent(event, this);
        }
    }

    public void setPaused(boolean paused) {
        boolean wasPaused = this.paused;
        this.paused = paused;
        if (!paused && wasPaused) {
            synchronized (this) {
                notify(); //NOTE: wake up main loop
            }
        }

    }

    public void synchronizeSpeed(double timeInterval, long delayInterval) {
        if (syncroEvent == null) {
            this.plan(new SynchronizationEvent(currentTime + timeInterval, this, timeInterval, delayInterval), currentTime + timeInterval);
        } else {
            syncroEvent.setDelayInterval(delayInterval);
            syncroEvent.setPlanningInterval(timeInterval);
        }
    }

    public void stopSpeedSynchronization() {
        if (syncroEvent != null) {
            syncroEvent.setPlan(false);
            syncroEvent = null;
        }
    }

}
