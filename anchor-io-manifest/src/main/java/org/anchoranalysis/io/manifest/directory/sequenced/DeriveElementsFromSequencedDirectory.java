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

package org.anchoranalysis.io.manifest.directory.sequenced;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.GetterFromIndex;
import org.anchoranalysis.io.manifest.file.OutputtedFile;
import org.anchoranalysis.io.manifest.finder.FindFailedException;

/**
 * Derives elements of type {@code T} from a directory containing a sequence of files.
 *
 * @author Owen Feehan
 * @param <T> the element-type in the collection
 */
@AllArgsConstructor
public abstract class DeriveElementsFromSequencedDirectory<T> implements GetterFromIndex<T> {

    /** The manifest entry for directory in which a sequence of serialized files exists. */
    private SequencedDirectory directory;

    @Override
    public T get(int index) throws GetOperationFailedException {

        try {
            List<OutputtedFile> foundList = new ArrayList<>();

            String indexForElement =
                    directory.getAssociatedElementRange().stringRepresentationForElement(index);

            directory.findFileFromIndex(foundList, indexForElement, true);

            if (foundList.size() != 1) {
                throw new IllegalArgumentException(
                        String.format("Cannot find index %s", indexForElement));
            }

            Path path = foundList.get(0).calculatePath();
            return createFromFile(path);

        } catch (CreateException | FindFailedException e) {
            throw new GetOperationFailedException(index, e);
        }
    }

    /**
     * Creates an element of type {@code T} from a one particular file in the sequence.
     *
     * @param path path to the file
     * @return a newly created element corresponding to the file
     * @throws CreateException if anything goes wrong
     */
    protected abstract T createFromFile(Path path) throws CreateException;
}
