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

package org.anchoranalysis.io.manifest.directory;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.OutputtedFile;
import org.anchoranalysis.io.manifest.finder.FindFailedException;

@RequiredArgsConstructor
class FileList implements Serializable {

    /** */
    private static final long serialVersionUID = 5858857164978822313L;

    // START REQUIRED ARGUMENTS
    private final MutableDirectory directory;
    // END REQUIRED ARGUMENTS

    @Getter private List<OutputtedFile> files = new ArrayList<>();

    // Finds a folder a comparator matches
    public void findFile(
            List<OutputtedFile> foundList, Predicate<OutputtedFile> predicate, boolean recursive)
            throws FindFailedException {

        // Adds files that match the predicate to the foundList
        addToFoundList(foundList, predicate);

        if (recursive) {
            for (MutableDirectory subdirectory : directory.subdirectories()) {
                subdirectory.findFile(foundList, predicate, recursive);
            }
        }
    }

    public void add(OutputtedFile outputtedFile) {
        this.files.add(outputtedFile);
    }

    public void write(
            String outputName,
            ManifestDescription manifestDescription,
            Path outfilePath,
            String index) {
        add(
                new OutputtedFile(
                        directory,
                        outfilePath.toString(),
                        outputName,
                        index,
                        Optional.of(manifestDescription)));
    }

    private void addToFoundList(List<OutputtedFile> foundList, Predicate<OutputtedFile> predicate) {
        files.stream().filter(predicate).forEach(foundList::add);
    }
}
