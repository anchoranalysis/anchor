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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.manifest.directory.MutableDirectory;
import org.anchoranalysis.io.manifest.file.OutputtedFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FinderUtilities {

    public static List<OutputtedFile> findListFile(
            Manifest manifestRecorder, Predicate<OutputtedFile> predicate)
            throws FindFailedException {

        ArrayList<OutputtedFile> foundList = new ArrayList<>();
        manifestRecorder.getRootFolder().findFile(foundList, predicate, false);
        return foundList;
    }

    public static List<MutableDirectory> findListFolder(
            Manifest manifestRecorder, Predicate<MutableDirectory> predicate) {

        ArrayList<MutableDirectory> foundList = new ArrayList<>();
        manifestRecorder.getRootFolder().findDirectory(foundList, predicate);
        return foundList;
    }

    public static Optional<OutputtedFile> findSingleItem(
            Manifest manifestRecorder, Predicate<OutputtedFile> predicate)
            throws FindFailedException {

        List<OutputtedFile> files = findListFile(manifestRecorder, predicate);
        if (files.isEmpty()) {
            return Optional.empty();
        }
        if (files.size() > 1) {
            throw new FindFailedException(
                    "More than one matching object was found in the manifest");
        }

        return Optional.of(files.get(0));
    }
}
