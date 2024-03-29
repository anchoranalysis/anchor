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

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;

/**
 * Like {@link RecordedOutputs} but accepts two levels, first and second, like in a {@link
 * MultiLevelRecordedOutputs}.
 *
 * @author Owen Feehan
 */
@Accessors(fluent = true)
public class MultiLevelRecordedOutputs {

    /** For recording first-level outputs. */
    @Getter private RecordedOutputs first = new RecordedOutputs();

    /** A hash-map recording second-level outputs. */
    private HashMap<String, RecordedOutputs> second = new HashMap<>();

    /**
     * A {@link RecordedOutputs} for recording second-level outputs for a given {@code outputName}
     * from the first-level.
     *
     * @param outputName the outputName from the first-level
     * @return the {@link RecordedOutputs}.
     */
    public RecordedOutputs second(String outputName) {

        if (!first.isRecordedAsEnabled(outputName)) {
            throw new AnchorFriendlyRuntimeException(
                    "output-name has been recorded as a second-level output, without having being recorded as a first-level output: "
                            + outputName);
        }
        synchronized (this) {
            return second.computeIfAbsent(outputName, name -> new RecordedOutputs());
        }
    }

    /**
     * All second-level recorded output entries.
     *
     * @return the set of entries from the internal hash-map that records outputs.
     */
    public Set<Entry<String, RecordedOutputs>> secondEntries() {
        return second.entrySet();
    }

    /**
     * If there is at least one disabled output.
     *
     * @return true if there is at least one output-name exists that was disabled.
     */
    public boolean hasAtLeastOneDisabled() {

        if (first.hasAtLeastOneDisabled()) {
            return true;
        }

        for (Entry<String, RecordedOutputs> entry : second.entrySet()) {
            if (entry.getValue().hasAtLeastOneDisabled()) {
                return true;
            }
        }

        return false;
    }
}
