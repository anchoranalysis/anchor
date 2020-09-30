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
package org.anchoranalysis.io.output.writer;

import java.util.Set;
import java.util.TreeSet;

/**
 * Outputs recorded from {@link RecordOutputNamesForWriter}.
 *
 * <p>Three separate sets are recorded:
 *
 * <ul>
 *   <li>outputs that are written as they were enabled.
 *   <li>outputs that not written as they are <b>not</b> enabled.
 * </ul>
 *
 * @author Owen Feehan
 */
public class RecordedOutputs {

    // TreeSet is used to ensure alphabetical ordering

    /** Names of outputs that have been allowed and written. */
    private Set<String> namesEnabled = new TreeSet<>();

    /** Names of outputs that were not allowed and therefore not written. */
    private Set<String> namesDisabled = new TreeSet<>();

    /**
     * Adds a new output-name to the set of recorded names.
     *
     * @param outputName the output-name
     * @param enabled where the output was allowed or not
     */
    public void add(String outputName, boolean enabled) {
        if (enabled) {
            namesEnabled.add(outputName);
        } else {
            namesDisabled.add(outputName);
        }    
    }

    /**
     * Number of output-names that were enabled.
     *
     * @return the number of names
     */
    public int numberEnabled() {
        return namesEnabled.size();
    }

    /**
     * Number of output-names that were not enabled.
     *
     * @return the number of names
     */
    public int numberDisabled() {
        return namesDisabled.size();
    }
    
    /**
     * Multiplex between {@link #numberEnabled()} and {@link #numberDisabled()}.
     * 
     * @param enabled if true, consider the number of output names that were enabled, otherwise those that were disabled.
     * @return the number of output-names that were enabled/disabled (depending on {@code enabled}.
     */
    public int numberMultiplex(boolean enabled) {
        if (enabled) {
            return numberEnabled();
        } else {
            return numberDisabled();
        }
    }

    /**
     * If no output-names exist.
     *
     * @return true iff no output-names have been recorded.
     */
    public boolean isEmpty() {
        return namesEnabled.isEmpty() && namesDisabled.isEmpty();
    }

    /**
     * A comma-separated string of all output-names that were <b>enabled</b>.
     *
     * <p>The strings are separated by a comma and a space, and are alphabetically-ordered.
     *
     * @return the output-names as a string
     */
    public String summarizeEnabled() {
        return summarizeNames(namesEnabled);
    }

    /**
     * A comma-separated string of all output-names that were <b>not enabled</b>.
     *
     * <p>The strings are separated by a comma and a space, and are alphabetically-ordered.
     *
     * @return the output-names as a string
     */
    public String summarizeDisabled() {
        return summarizeNames(namesDisabled);
    }
    
    /**
     * Multiplex between {@link #summarizeEnabled()} and {@link #summarizeDisabled()}.
     * 
     * @param enabled if true, perform a summary of output names that were enabled, otherwise those that were disabled.
     * @return a summarize of the output-names as a string, separated by a comma and a space, and are alphabetically-ordered.
     */
    public String summarizeMultiplex(boolean enabled) {
        if (enabled) {
            return summarizeEnabled();
        } else {
            return summarizeDisabled();
        }
    }

    private static String summarizeNames(Set<String> names) {
        return String.join(", ", names);
    }
}
