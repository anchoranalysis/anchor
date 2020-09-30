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

import java.util.Optional;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.io.output.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Two writers that record any output-names that are passed as arguments.
 *
 * <p>The two writers are:
 *
 * <ul>
 *   <li>A {@link CheckIfAllowed} that selectively outputs certain output-names.
 *   <li>A {@link AlwaysAllowed} that allows all output-names.
 * </ul>
 *
 * @author Owen Feehan
 */
@Accessors(fluent = true)
public class RecordingWriters {

    /** A writer that allows all output-names, and does not record the written output-names */
    private final Writer permissiveNoRecording;
    
    /** A writer that allows all output-names, and records the written output-names */
    private final Writer permissiveRecording;

    /** A writer that allows only certain selected output-names, and records the written output-names */
    private final Writer selectiveRecording;

    /**
     * Id defined, all output-names that are passed as arguments to both writers are recorded here.
     */
    @Getter private final Optional<RecordedOutputs> recordedOutputs;
    
    private final MultiLevelOutputEnabled outputEnabled;

    /**
     * Creates the two writers.
     *
     * @param outputter the output-manager with which the writers are associated.
     * @param preop an operation executed before creation of every directory, if defined.
     * @param recordedOutputs all output-names that are passed as arguments to both writers are
     *     recorded here.
     */
    public RecordingWriters(
            OutputterChecked outputter,
            Optional<WriterExecuteBeforeEveryOperation> preop,
            Optional<RecordedOutputs> recordedOutputs) {
        this.recordedOutputs = recordedOutputs;
        this.outputEnabled = outputter.getOutputsEnabled();
        this.permissiveNoRecording = new AlwaysAllowed(outputter, preop);
        this.permissiveRecording = record(permissiveNoRecording);
        this.selectiveRecording =
                record(new CheckIfAllowed(outputter.getOutputsEnabled(), preop, permissiveRecording));
    }

    /**
     * Multiplexes between the {@code selective} and {@code permissive} writers based on a flag.
     *
     * @param selectSelective if true, {@code selective} is returned, otherwise {@code permissive}.
     * @return the chosen writer
     */
    public Writer multiplex(boolean selectSelective) {
        if (selectSelective) {
            return selectiveRecording;
        } else {
            return permissiveRecording;
        }
    }
    
    /**
     * A writer that performs a second-level check on which outputs occur, but writes to the top-level directory.
     *
     * @return a newly created writer checking on particular second-level otuput names.
     */
    public Writer secondLevel(String outputNameFirstLevel) {
        return new CheckIfAllowed(outputEnabled.second(outputNameFirstLevel), Optional.empty(), permissiveNoRecording);
    }

    /** A writer that allows all output-names, and records the written output-names */
    public Writer permissive() {
        return permissiveRecording;
    }

    /** A writer that allows only certain selected output-names, and records the written output-names */
    public Writer selective() {
        return selectiveRecording;
    }

    private Writer record(Writer writer) {
        // Indexable outputs are ignored, as it is assumed that the outputName
        // used for the containing directory is the relevant identifier to
        // show the user
        if (recordedOutputs.isPresent()) {
            return new RecordOutputNamesForWriter(writer, recordedOutputs.get(), false);
        } else {
            return writer;
        }
    }
}
