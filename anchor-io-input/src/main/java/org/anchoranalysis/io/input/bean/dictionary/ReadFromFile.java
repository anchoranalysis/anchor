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

package org.anchoranalysis.io.input.bean.dictionary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.shared.dictionary.DictionaryProvider;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.io.input.bean.InputManagerParameters;
import org.anchoranalysis.io.input.bean.files.FilesProvider;
import org.anchoranalysis.io.input.file.FilesProviderException;

/**
 * Reads a {@link Dictionary} from a file in <a
 * href="https://docs.oracle.com/javase/tutorial/essential/environment/properties.html">Java
 * properties</a> format.
 *
 * @author Owen Feehan
 */
public class ReadFromFile extends DictionaryProvider {

    // START BEAN PROPERTIES
    /** Provides a single file containing a dictionary. */
    @BeanField @Getter @Setter private FilesProvider files;
    // END BEAN PROPERTIES

    @Override
    public Dictionary get() throws ProvisionFailedException {
        try {
            Collection<File> providedFiles = files.create(new InputManagerParameters(getLogger()));

            if (providedFiles.isEmpty()) {
                throw new ProvisionFailedException("No files are provided");
            }

            if (providedFiles.size() > 1) {
                throw new ProvisionFailedException("More than one file is provided");
            }

            Path filePath = providedFiles.iterator().next().toPath();
            return Dictionary.readFromFile(filePath);

        } catch (IOException e) {
            throw new ProvisionFailedException(e);
        } catch (FilesProviderException e) {
            throw new ProvisionFailedException("Cannot find files", e);
        }
    }
}
