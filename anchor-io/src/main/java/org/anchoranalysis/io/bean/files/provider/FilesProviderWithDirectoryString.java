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

package org.anchoranalysis.io.bean.files.provider;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * A {@link FilesProviderWithDirectory} where the directory is optionally specified as a string
 * constant
 *
 * <p>If an explicit absolute {@code directory} is set then it is used. Otherwise a directory is
 * inferred by trying in order of priority:
 *
 * <ul>
 *   <li>The directory in which the bean-XML file is located (iff {@code localized}==true)
 *   <li>The input-directory set in the input context
 *   <li>The current working-directory
 * </ul>
 *
 * <p>If an explicit relative {@code directory} it is not normalized (unless {@code
 * localized}==true, in which case it is resolved against the location of the BeanXML).
 *
 * @author Owen Feehan
 */
public abstract class FilesProviderWithDirectoryString extends FilesProviderWithDirectory {

    // START BEAN FIELDS
    /**
     * A directory in which to look for files. If set, takes precedence over any other inference of
     * a directory.
     */
    @BeanField @AllowEmpty @Getter @Setter private String directory = "";

    @BeanField @Getter @Setter private boolean localized = false;
    // END BEAN FIELDS

    @Override
    public Path getDirectoryAsPath(InputContextParams inputContext) {

        if (!directory.isEmpty()) {
            Path directoryAsPath = Paths.get(directory);
            if (localized && !directoryAsPath.isAbsolute()) {
                return localRoot().resolve(directoryAsPath);
            } else {
                return directoryAsPath;
            }
        } else {
            // The current working directory
            return inferDirectory(inputContext);
        }
    }

    private Path inferDirectory(InputContextParams inputContext) {
        if (localized) {
            return localRoot();
        } else {
            return inputContext.getInputDirectory().orElseGet(() -> Paths.get("."));
        }
    }

    private Path localRoot() {
        return getLocalPath().getParent();
    }
}
