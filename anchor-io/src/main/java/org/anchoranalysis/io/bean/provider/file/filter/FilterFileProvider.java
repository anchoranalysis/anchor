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
package org.anchoranalysis.io.bean.provider.file.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.bean.provider.file.FileProvider;
import org.anchoranalysis.io.error.FileProviderException;

public abstract class FilterFileProvider extends FileProvider {

    // START BEAN PROPERTIES
    @BeanField private FileProvider fileProvider;
    // END BEAN PROPERTIES

    @Override
    public Collection<File> create(InputManagerParams params) throws FileProviderException {

        Collection<File> filesIn = fileProvider.create(params);

        List<File> filesOut = new ArrayList<>();

        for (File f : filesIn) {

            if (isFileAccepted(f, params.isDebugModeActivated())) {
                filesOut.add(f);
            }
        }

        return filesOut;
    }

    protected abstract boolean isFileAccepted(File file, boolean debugMode)
            throws FileProviderException;

    public FileProvider getFileProvider() {
        return fileProvider;
    }

    public void setFileProvider(FileProvider fileProvider) {
        this.fileProvider = fileProvider;
    }
}
