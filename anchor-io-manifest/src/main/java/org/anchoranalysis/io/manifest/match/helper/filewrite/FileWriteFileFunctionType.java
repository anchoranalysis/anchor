/*-
 * #%L
 * anchor-io-manifest
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
package org.anchoranalysis.io.manifest.match.helper.filewrite;

import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.FileWriteManifestMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionFunctionMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionMatchAnd;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionTypeMatch;
import org.anchoranalysis.io.manifest.match.Match;

public class FileWriteFileFunctionType implements Match<FileWrite> {

    private Match<FileWrite> delegate;

    public FileWriteFileFunctionType(String function, String type) {
        super();

        delegate =
                new FileWriteManifestMatch(
                        new ManifestDescriptionMatchAnd(
                                new ManifestDescriptionFunctionMatch(function),
                                new ManifestDescriptionTypeMatch(type)));
    }

    @Override
    public boolean matches(FileWrite obj) {
        return delegate.matches(obj);
    }
}
