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

package org.anchoranalysis.io.manifest.finder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.manifest.file.OutputtedFile;
import org.anchoranalysis.io.manifest.finder.match.FileMatch;

public class FinderDictionary extends FinderSingleFile {

    private String manifestFunction;

    public FinderDictionary(String manifestFunction, ErrorReporter errorReporter) {
        super(errorReporter);
        this.manifestFunction = manifestFunction;
    }

    public Dictionary get() throws OperationFailedException {
        assert (exists());
        try {
            return Dictionary.readFromFile(getFoundFile().calculatePath());
        } catch (IOException e) {
            throw new OperationFailedException(e);
        }
    }

    @Override
    protected Optional<OutputtedFile> findFile(Manifest manifestRecorder)
            throws FindFailedException {
        List<OutputtedFile> files =
                FinderUtilities.findListFile(
                        manifestRecorder, FileMatch.description(manifestFunction));

        if (files.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(files.get(0));
    }
}
