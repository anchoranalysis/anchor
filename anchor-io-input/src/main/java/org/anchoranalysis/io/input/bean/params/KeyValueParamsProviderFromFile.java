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

package org.anchoranalysis.io.input.bean.params;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.core.value.KeyValueParams;
import org.anchoranalysis.io.input.InputContextParams;
import org.anchoranalysis.io.input.bean.InputManagerParams;
import org.anchoranalysis.io.input.bean.files.FilesProvider;
import org.anchoranalysis.io.input.files.FilesProviderException;

public class KeyValueParamsProviderFromFile extends KeyValueParamsProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FilesProvider filesProvider;
    // END BEAN PROPERTIES

    @Override
    public KeyValueParams create() throws CreateException {
        try {
            Collection<File> files =
                    filesProvider.create(
                            new InputManagerParams(
                                    new InputContextParams(),
                                    ProgressIgnore.get(),
                                    getLogger()));

            if (files.isEmpty()) {
                throw new CreateException("No files are provided");
            }

            if (files.size() > 1) {
                throw new CreateException("More than one file is provided");
            }

            Path filePath = files.iterator().next().toPath();
            return KeyValueParams.readFromFile(filePath);

        } catch (IOException e) {
            throw new CreateException(e);
        } catch (FilesProviderException e) {
            throw new CreateException("Cannot find files", e);
        }
    }
}
