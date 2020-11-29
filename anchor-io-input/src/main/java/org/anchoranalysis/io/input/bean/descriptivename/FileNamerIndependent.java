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

package org.anchoranalysis.io.input.bean.descriptivename;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.input.files.NamedFile;

/**
 * Base class for methods that derive the name independently for each file.
 *
 * @author Owen Feehan
 */
public abstract class FileNamerIndependent extends FileNamer {

    @Override
    public List<NamedFile> deriveName(Collection<File> files, String elseName, Logger logger) {

        List<NamedFile> out = new ArrayList<>();

        int i = 0;
        for (File file : files) {
            String name = deriveNameOrElse(file, i++, elseName, logger);
            out.add(new NamedFile(name, file));
        }

        return out;
    }

    protected abstract String deriveName(File file, int index) throws CreateException;

    private String deriveNameOrElse(File file, int index, String elseName, Logger logger) {
        try {
            return deriveName(file, index);
        } catch (CreateException e) {

            String elseNameWithIndex = String.format("%s04%d", elseName, index);

            logger.errorReporter()
                    .recordError(
                            FileNamerIndependent.class,
                            String.format(
                                    "Cannot create a name for file %s and index %d. Using '%s' instead.",
                                    file.getPath(), index, elseNameWithIndex));
            return elseNameWithIndex;
        }
    }
}
