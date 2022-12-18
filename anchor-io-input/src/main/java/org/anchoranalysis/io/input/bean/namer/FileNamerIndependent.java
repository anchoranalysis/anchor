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

package org.anchoranalysis.io.input.bean.namer;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.io.input.file.FileNamerContext;
import org.anchoranalysis.io.input.file.NamedFile;

/**
 * Base class for methods that derive the name independently for each file.
 *
 * <p>By independently, this means the name is derived using information from a particular file
 * only, and not from the collective set of the files that are named together.
 *
 * <p>An exception may occur via the index, which is derived collectively from all files, but may be
 * used in sub-classes.
 *
 * @author Owen Feehan
 */
public abstract class FileNamerIndependent extends FileNamer {

    @Override
    public List<NamedFile> deriveName(List<File> files, FileNamerContext context) {

        List<NamedFile> out = new ArrayList<>();

        int index = 0;
        for (File file : files) {
            String name = deriveNameOrElse(file, index++, context);
            out.add(new NamedFile(name, file));
        }

        return out;
    }

    /**
     * Derives a unique name for this file.
     *
     * @param file the file.
     * @param inputDirectory the root input-directory used in naming.
     * @param index a unique incrementing number, beginning at zero, passed to each call to this
     *     method within a collection.
     * @return the derived unique name.
     * @throws CreateException if unable to create the unique name.
     */
    protected abstract String deriveName(File file, Optional<Path> inputDirectory, int index)
            throws CreateException;

    private String deriveNameOrElse(File file, int index, FileNamerContext context) {
        try {
            return deriveName(file, context.getInputDirectory(), index);
        } catch (CreateException e) {

            String elseNameWithIndex = String.format("%s04%d", context.getElseName(), index);

            context.getLogger()
                    .errorReporter()
                    .recordErrorFormatted(
                            FileNamerIndependent.class,
                            "Cannot create a name for file %s and index %d. Using '%s' instead.",
                            file.getPath(),
                            index,
                            elseNameWithIndex);
            return elseNameWithIndex;
        }
    }
}
