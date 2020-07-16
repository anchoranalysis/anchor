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
/* (C)2020 */
package org.anchoranalysis.io.bean.provider.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * A FileProviderWithDirectory where the directory is specified a String BeanField
 *
 * @author Owen Feehan
 */
public abstract class FileProviderWithDirectoryString extends FileProviderWithDirectory {

    // START BEAN FIELDS
    /**
     * A directory in which to look for files.
     *
     * <p>If empty, first the bean will try to use any input-dir set in the input context if it
     * exists, or otherwise use the current working-directory
     */
    @BeanField @AllowEmpty @Getter @Setter private String directory = "";
    // END BEAN FIELDS

    @Override
    public Path getDirectoryAsPath(InputContextParams inputContext) {

        // If no directory is explicitly specified, and the input-context provides an
        // input-directory
        //  then we use this
        //
        // This is a convenient way for the ExperimentLauncher to parameterize the input-directory
        if (!directory.isEmpty()) {
            return Paths.get(directory);
        } else {
            // The current working directory
            return inputContext.getInputDir().orElseGet(() -> Paths.get("."));
        }
    }
}
