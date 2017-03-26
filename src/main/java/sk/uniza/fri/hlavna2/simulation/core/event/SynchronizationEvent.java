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

import java.util.logging.Level;
import java.util.logging.Logger;
import sk.uniza.fri.hlavna2.simulation.core.SimulationParameters;
import sk.uniza.fri.hlavna2.simulation.core.Statistics;
import sk.uniza.fri.hlavna2.simulation.core.utils.RandomStorage;

public class SynchronizationEvent extends Event {

    private double planningInterval;
    private long delayInterval;
    private boolean plan;

    public SynchronizationEvent() {
    }

    public SynchronizationEvent(double time, EventSimulationEngine engine, double planningInterval, long delayInterval) {
        super(time, engine);
        this.planningInterval = planningInterval;
        this.delayInterval = delayInterval;
        this.plan = true;
    }

    public SynchronizationEvent(SynchronizationEvent other) {
        super(other);
        this.planningInterval = other.planningInterval;
        this.plan = other.plan;
        this.delayInterval = other.delayInterval;
    }

    @Override
    protected void execute(SimulationParameters parameters, Statistics statistics, RandomStorage randoms) {
        try {
            if (this.plan) {
                engine.plan(this, engine.getCurrentTime() + planningInterval);
            }
            Thread.sleep(delayInterval);
        } catch (InterruptedException ex) {
            Logger.getLogger(SynchronizationEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setDelayInterval(long delayInterval) {
        this.delayInterval = delayInterval;
    }

    public void setPlanningInterval(double planningInterval) {
        this.planningInterval = planningInterval;
    }

    public void setPlan(boolean plan) {
        this.plan = plan;
    }

}
