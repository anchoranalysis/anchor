/*-
 * #%L
 * anchor-io
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
package org.anchoranalysis.io.input.bean.files;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.input.InputContextParameters;
import org.anchoranalysis.io.input.bean.InputManagerParameters;
import org.anchoranalysis.io.input.file.FilesProviderException;

/**
 * Like {@link FilesProvider} but employs a unary operator on a call to an existing {@link
 * FilesProvider}.
 *
 * @author Owen Feehan
 */
public abstract class FilesProviderUnary extends FilesProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FilesProvider files;

    // END BEAN PROPERTIES

    @Override
    public List<File> create(InputManagerParameters parameters) throws FilesProviderException {
        return transform(files.create(parameters), parameters.isDebugModeActivated());
    }

    @Override
    public Optional<Path> rootDirectory(InputContextParameters inputContext)
            throws FilesProviderException {
        return files.rootDirectory(inputContext);
    }

    /**
     * Transform an existing collection of files.
     *
     * <p>Note that the incoming collection of files may be modified, and can no longer be used in
     * its original form after this method call.
     *
     * @param source the incoming files (which may be consumed and modified).
     * @param debugMode whether we are executing in debug-mode or not
     * @return the transformed (outgoing) files.
     * @throws FilesProviderException if unable to complete the transform operation successfully.
     */
    protected abstract List<File> transform(List<File> source, boolean debugMode)
            throws FilesProviderException;
}
