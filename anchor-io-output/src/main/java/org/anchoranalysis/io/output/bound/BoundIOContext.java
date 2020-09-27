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

package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.CommonContext;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;

/**
 * Certain parameters that are exposed after any filesystem binding for inputs and outputs has
 * occurred.
 *
 * @author Owen Feehan
 */
public interface BoundIOContext {

    Path getModelDirectory();

    Outputter getOutputter();

    boolean isDebugEnabled();

    Logger getLogger();

    default CommonContext common() {
        return new CommonContext(getLogger(), getModelDirectory());
    }

    default ErrorReporter getErrorReporter() {
        return getLogger().errorReporter();
    }

    default MessageLogger getLogReporter() {
        return getLogger().messageLogger();
    }

    /**
     * Creates a new context that writes instead to a sub-directory
     *
     * @param subDirectoryName subdirectory name
     * @return newly created context
     */
    default BoundIOContext subdirectory(
            String subDirectoryName, ManifestFolderDescription manifestFolderDescription) {
        return new RedirectIntoSubdirectory(this, subDirectoryName, manifestFolderDescription);
    }

    /**
     * Optionally creates a new context like with {@link #subdirectory} but only if a directory-name
     * is defined
     *
     * @param subdirectoryName if defined, a new context is created that writes into a sub-directory
     *     of this name
     * @return either a newly created context, or the existing context
     */
    default BoundIOContext maybeSubdirectory(
            Optional<String> subdirectoryName,
            ManifestFolderDescription manifestFolderDescription) {
        return subdirectoryName
                .map(name -> subdirectory(name, manifestFolderDescription))
                .orElse(this);
    }
}
