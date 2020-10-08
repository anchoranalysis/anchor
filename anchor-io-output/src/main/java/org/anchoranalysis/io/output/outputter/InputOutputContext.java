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
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.CommonContext;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;

/**
 * A context for performing input generally and outputting to a particular output-directory.
 *
 * @author Owen Feehan
 */
public interface InputOutputContext {

    /**
     * An input-directory in which models are stored.
     *
     * <p>return a path to the model-directory
     */
    Path getModelDirectory();

    /**
     * An outputter that writes to the particular output-directory.
     *
     * @return the outputter
     */
    Outputter getOutputter();

    /**
     * Is debug-mode enabled?
     *
     * @return true iff debug-mode is enabled
     */
    boolean isDebugEnabled();

    /**
     * The associated logger.
     *
     * @return the logger associated with input-output operations.
     */
    Logger getLogger();

    /**
     * Creates a {@link CommonContext} a context that contains a subset of this context.
     *
     * @return a newly created {@link CommonContext} reusing the objects from this context.
     */
    default CommonContext common() {
        return new CommonContext(getLogger(), getModelDirectory());
    }

    /**
     * The associated error reporter.
     *
     * @return the error reporter
     */
    default ErrorReporter getErrorReporter() {
        return getLogger().errorReporter();
    }

    /**
     * The associated message reporter.
     *
     * @return the message reporter
     */
    default MessageLogger getMessageReporter() {
        return getLogger().messageLogger();
    }

    /**
     * Creates a new context that writes instead to a sub-directory.
     *
     * @param subDirectoryName subdirectory name
     * @param inheritOutputRulesAndRecording if true, the output rules and recording are inherited
     *     from the parent directory. if false, they are not, and all outputs are allowed and are
     *     unrecorded.
     * @return newly created context
     */
    default InputOutputContext subdirectory(
            String subDirectoryName,
            ManifestFolderDescription manifestFolderDescription,
            boolean inheritOutputRulesAndRecording) {
        return new ChangeOutputter(
                this,
                getOutputter()
                        .deriveSubdirectory(
                                subDirectoryName,
                                manifestFolderDescription,
                                inheritOutputRulesAndRecording));
    }

    /**
     * Optionally creates a new context like with {@link #subdirectory} but only if a directory-name
     * is defined
     *
     * @param subdirectoryName if defined, a new context is created that writes into a sub-directory
     *     of this name
     * @param inheritOutputRulesAndRecording if true, the output rules and recording are inherited
     *     from the parent directory. if false, they are not, and all outputs are allowed and are
     *     unrecorded.
     * @return either a newly created context, or the existing context
     */
    default InputOutputContext maybeSubdirectory(
            Optional<String> subdirectoryName,
            ManifestFolderDescription manifestFolderDescription,
            boolean inheritOutputRulesAndRecording) {
        return subdirectoryName
                .map(
                        name ->
                                subdirectory(
                                        name,
                                        manifestFolderDescription,
                                        inheritOutputRulesAndRecording))
                .orElse(this);
    }
}
