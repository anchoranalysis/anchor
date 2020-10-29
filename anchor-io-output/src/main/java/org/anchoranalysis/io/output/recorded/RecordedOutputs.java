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
package org.anchoranalysis.io.output.recorded;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
 * <p>Adding outputs to this class is a <i>thread-safe</i> operation.
 *
 * @author Owen Feehan
 */
public class RecordedOutputs {

    // TreeSet is used to ensure alphabetical ordering

    /** Names of outputs that have been allowed and written. */
    private Set<String> namesEnabled = new TreeSet<>();

    /** Names of outputs that were not allowed and therefore not written. */
    private Set<String> namesDisabled = new TreeSet<>();
    
    /** The maximum number of outputs to list before using a "and others" message. */
    private static final int MAX_OUTPUTS_LISTED = 8;

    /**
     * Adds a new output-name to the set of recorded names.
     *
     * @param outputName the output-name
     * @param allowed where the output was allowed or not
     */
    public synchronized void add(String outputName, boolean allowed) {
        if (allowed) {
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
     * If there is at least one enabled output.
     *
     * @return true if there is at least one output-name exists that was enabled.
     */
    public boolean hasAtLeastOneEnabled() {
        return !namesEnabled.isEmpty();
    }

    /**
     * If there is at least one disabled output.
     *
     * @return true if there is at least one output-name exists that was disabled.
     */
    public boolean hasAtLeastOneDisabled() {
        return !namesDisabled.isEmpty();
    }

    /**
     * Multiplex between {@link #hasAtLeastOneEnabled()} and {@link #hasAtLeastOneDisabled()}.
     *
     * @param enabled if true, consider the number of output names that were enabled, otherwise
     *     those that were disabled.
     * @return true if there is at least one output-name exists that was enabled/disabled.
     */
    public boolean hasAtLeastOneMultiplex(boolean enabled) {
        if (enabled) {
            return hasAtLeastOneEnabled();
        } else {
            return hasAtLeastOneDisabled();
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
        return summarizeNames(namesEnabled, MAX_OUTPUTS_LISTED);
    }

    /**
     * A comma-separated string of all output-names that were <b>not enabled</b>.
     *
     * <p>The strings are separated by a comma and a space, and are alphabetically-ordered.
     *
     * @return the output-names as a string
     */
    public String summarizeDisabled() {
        return summarizeNames(namesDisabled, MAX_OUTPUTS_LISTED);
    }

    /**
     * Multiplex between {@link #summarizeEnabled()} and {@link #summarizeDisabled()}.
     *
     * @param enabled if true, perform a summary of output names that were enabled, otherwise those
     *     that were disabled.
     * @return a summarize of the output-names as a string, separated by a comma and a space, and
     *     are alphabetically-ordered.
     */
    public String summarizeMultiplex(boolean enabled) {
        if (enabled) {
            return summarizeEnabled();
        } else {
            return summarizeDisabled();
        }
    }

    /**
     * Is a particular output-name recorded as enabled?
     *
     * @param outputName the output-name
     * @return true if the output-name has been recorded as enabled
     */
    public boolean isRecordedAsEnabled(String outputName) {
        return namesEnabled.contains(outputName);
    }

    /**
     * Builds a one-line string summary of a set of strings.
     * 
     * <p>If the number of names {@code <= maxNumber} then all elements are listed. Otherwise, some are listed with a {@code plus X others.} string at the end.
     */
    private static String summarizeNames(Set<String> names, int maxNumber) {
        if (names.size() > maxNumber) {
            return collapseToString(extractElements(names, maxNumber)) + String.format(" plus %d others.", names.size() - maxNumber);
        } else {
            return collapseToString(names);
        }
    }
    
    /** Extracts the first (by the default iterator) number of elements from a set. */
    private static <T> Set<T> extractElements(Set<T> set, int numberElements) {
        return set.stream().limit(numberElements).collect( Collectors.toCollection(TreeSet::new) );
    }
    
    /** Describes a set of strings as a (comma plus whitespace) separated string. */
    private static String collapseToString(Set<String> names) {
        return String.join(", ", names);
    }
}
