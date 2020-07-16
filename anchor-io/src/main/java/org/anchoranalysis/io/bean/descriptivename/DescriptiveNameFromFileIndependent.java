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
package org.anchoranalysis.io.bean.descriptivename;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.input.descriptivename.DescriptiveFile;

/**
 * Calculates the descriptive-name independently for each file
 *
 * @author Owen Feehan
 */
public abstract class DescriptiveNameFromFileIndependent extends DescriptiveNameFromFile {

    @Override
    public List<DescriptiveFile> descriptiveNamesFor(
            Collection<File> files, String elseName, Logger logger) {

        List<DescriptiveFile> out = new ArrayList<>();

        int i = 0;
        for (File f : files) {
            String descriptiveName = createDescriptiveNameOrElse(f, i++, elseName, logger);
            out.add(new DescriptiveFile(f, descriptiveName));
        }

        return out;
    }

    protected abstract String createDescriptiveName(File file, int index) throws CreateException;

    private String createDescriptiveNameOrElse(
            File file, int index, String elseName, Logger logger) {
        try {
            return createDescriptiveName(file, index);
        } catch (CreateException e) {

            String elseNameWithIndex = String.format("%s04%d", elseName, index);

            logger.errorReporter()
                    .recordError(
                            DescriptiveNameFromFileIndependent.class,
                            String.format(
                                    "Cannot create a descriptive-name for file %s and index %d. Using '%s' instead.",
                                    file.getPath(), index, elseNameWithIndex));
            return elseNameWithIndex;
        }
    }
}
