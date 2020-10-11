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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.io.bean.path.derive.DerivePath;
import org.anchoranalysis.io.exception.AnchorIOException;
import org.anchoranalysis.io.exception.FilesProviderException;

public class FilterForExistingFiles extends FilesProviderUnary {

    // START BEAN PROPERTIES
    /** All files need to be present */
    @BeanField @Getter @Setter
    private List<DerivePath> paths = new ArrayList<>();
    // END BEAN PROPERTIES

    @Override
    protected Collection<File> transform(Collection<File> source, boolean debugMode) throws FilesProviderException {
        return FunctionalList.filterToList(source, FilesProviderException.class, file -> isFileAccepted(file, debugMode));
    }

    private boolean isFileAccepted(File file, boolean debugMode) throws FilesProviderException {

        try {
            for (DerivePath deriver : paths) {
                Path derivedPath = deriver.deriveFrom(file.toPath(), debugMode);

                if (!derivedPath.toFile().exists()) {
                    return false;
                }
            }
            return true;

        } catch (AnchorIOException e) {
            throw new FilesProviderException(e);
        }
    }

}
