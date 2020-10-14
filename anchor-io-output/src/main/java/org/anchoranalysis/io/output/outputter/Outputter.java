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

package org.anchoranalysis.io.output.outputter;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.manifest.directory.Subdirectory;
import org.anchoranalysis.io.manifest.operationrecorder.WriteOperationRecorder;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.writer.WriterRouterErrors;

/**
 * Like {@link OutputterChecked} but exceptions are reported in a {@link ErrorReporter}.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class Outputter {

    private final OutputterChecked delegate;

    @Getter private final ErrorReporter errorReporter;

    /**
     * Creates a new {@link Outputter} by appending a relative folder-path to the current {@link
     * Outputter}
     *
     * @see OutputterChecked#deriveSubdirectory
     * @param subdirectoryName the subdirectory-name
     * @param manifestDescription manifest-description
     * @param inheritOutputRulesAndRecording if true, the output rules and recording are inherited
     *     from the parent directory. if false, they are not, and all outputs are allowed and are
     *     unrecorded.
     * @return the new output manager
     */
    public Outputter deriveSubdirectory(
            String subdirectoryName,
            ManifestDirectoryDescription manifestDescription,
            boolean inheritOutputRulesAndRecording) {
        return new Outputter(
                delegate.deriveSubdirectory(
                        subdirectoryName,
                        manifestDescription,
                        Optional.of(new Subdirectory()),
                        inheritOutputRulesAndRecording),
                errorReporter);
    }

    public void addOperationRecorder(WriteOperationRecorder toAdd) {
        delegate.addOperationRecorder(toAdd);
    }

    /**
     * The writer that allows all outputs.
     *
     * @return the permissive writer
     */
    public WriterRouterErrors writerPermissive() {
        return new WriterRouterErrors(delegate.getWriters().permissive(), errorReporter);
    }

    /**
     * The writer that allows only certain selected outputs.
     *
     * @return the non-permissive writer
     */
    public WriterRouterErrors writerSelective() {
        return new WriterRouterErrors(delegate.getWriters().selective(), errorReporter);
    }

    /**
     * Multiplexes between the {@code selective} and {@code permissive} writers based on a flag.
     *
     * @param selectSelective if true, {@link #writerSelective} is returned, otherwise {@link
     *     #writerPermissive}.
     * @return the chosen writer
     */
    public WriterRouterErrors writerMultiplex(boolean selectSelective) {
        if (selectSelective) {
            return writerSelective();
        } else {
            return writerPermissive();
        }
    }

    /**
     * A writer that performs a second-level check on which outputs occur, but writes to the
     * top-level directory.
     *
     * @return a newly created writer checking on particular second-level otuput names.
     */
    public WriterRouterErrors writerSecondLevel(String outputNameFirstLevel) {
        return new WriterRouterErrors(
                delegate.getWriters().secondLevel(outputNameFirstLevel), errorReporter);
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public OutputWriteSettings getSettings() {
        return delegate.getSettings();
    }

    public Path getOutputFolderPath() {
        return delegate.getOutputFolderPath();
    }

    public String toString() {
        return delegate.toString();
    }

    public MultiLevelOutputEnabled outputsEnabled() {
        return delegate.getOutputsEnabled();
    }

    /**
     * Gets the underlying delegate of {@link Outputter} that throws checked-exceptions instead of
     * using a {@link ErrorReporter}.
     *
     * @return the checked-ouputter
     */
    public OutputterChecked getChecked() {
        return delegate;
    }
}
