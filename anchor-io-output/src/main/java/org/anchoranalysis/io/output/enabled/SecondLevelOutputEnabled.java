/*-
 * #%L
 * anchor-io-output
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
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
 * #L%
 */
package org.anchoranalysis.io.output.enabled;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Stores the second-level outputs, a set of output-names for a corresponding first-level output.
 *
 * @author Owen Feehan
 */
class SecondLevelOutputEnabled {

    /**
     * Second level output-names indexed by the first-level output with which they are associated.
     */
    private Map<String, Set<String>> map = new HashMap<>();

    /**
     * Adds enabled second-level outputs.
     *
     * @param outputNameFirstLevel the first-level output with which the second-level outputs are
     *     associated.
     * @param outputNames the names of the enabled-outputs
     */
    public void addEnabledOutputs(String outputNameFirstLevel, String[] outputNames) {
        addToSecond(outputNameFirstLevel, set -> Arrays.stream(outputNames).forEach(set::add));
    }

    /**
     * Second-level outputs exist for a particular output-name.
     *
     * @param outputNameFirstLevel the output name
     * @return true the set of second-level outputs if they are defined for a {@code
     *     outputNameFirstLevel} otherwise {@link Optional#empty}.
     */
    public Optional<Set<String>> secondLevelOutputsFor(String outputNameFirstLevel) {
        return Optional.ofNullable(map.get(outputNameFirstLevel));
    }

    /**
     * Adds enabled second-level outputs.
     *
     * @param outputNameFirstLevel the first-level output with which the second-level outputs are
     *     associated.
     * @param outputNames the names of the enabled-outputs
     */
    public void addEnabledOutputs(String outputNameFirstLevel, Set<String> outputNames) {
        addToSecond(outputNameFirstLevel, set -> set.addAll(outputNames));
    }

    /**
     * Adds enabled outputs from another {@link OutputEnabledMutable}.
     *
     * @param other the other {@link OutputEnabledMutable} to add from
     */
    public void addEnabledOutputs(SecondLevelOutputEnabled other) {

        for (String key : other.map.keySet()) {
            addEnabledOutputs(key, other.map.get(key));
        }
    }

    private void addToSecond(String outputNameFirstLevel, Consumer<Set<String>> consumer) {
        Set<String> set = map.computeIfAbsent(outputNameFirstLevel, name -> new HashSet<>());
        consumer.accept(set);
    }
}
