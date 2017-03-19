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
package sk.uniza.fri.hlavna2.simulation.core.utils;

import java.util.Map;
import sk.uniza.fri.hlavna2.commons.randomness.generators.RandomGenerator;
import sk.uniza.fri.hlavna2.simulation.core.exception.SimulationAlreadyInitializedException;
import sk.uniza.fri.hlavna2.simulation.core.exception.SimulationNotFullyInitializedException;

/**
 * Implementation of the RandomStorage
 *
 * @author Martin Hlavňa {@literal <mato.hlavna@gmail.com>}
 */
public class RandomStorageImpl implements RandomStorage {

    private final Map<String, RandomGenerator> randomStorage;
    private boolean initialized;

    public RandomStorageImpl(Map<String, RandomGenerator> randomStorage) {
        this.randomStorage = randomStorage;
    }

    @Override
    public void registerRandomGenerator(String key, RandomGenerator random) {
        if (initialized) {
            throw new SimulationAlreadyInitializedException();
        }
        randomStorage.put(key, random);
    }

    @Override
    public RandomGenerator deregisterRandomGenerator(String key) {
        if (initialized) {
            throw new SimulationAlreadyInitializedException();
        }
        return randomStorage.remove(key);
    }

    @Override
    public RandomGenerator getRandom(String key) {
        if (!initialized) {
            throw new SimulationNotFullyInitializedException();
        }
        return randomStorage.get(key);
    }

    /**
     * Set this instance as initialized
     */
    public void setInitialized() {
        this.initialized = true;
    }

}
