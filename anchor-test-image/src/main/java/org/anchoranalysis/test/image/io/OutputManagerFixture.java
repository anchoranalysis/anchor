/*-
 * #%L
 * anchor-test-image
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
package org.anchoranalysis.test.image.io;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OutputManagerFixture {

    public static OutputManager createOutputManager(Path pathForPrefixer) {
        OutputManager outputManager = new OutputManager();
        outputManager.setSilentlyDeleteExisting(true);
        outputManager.setOutputWriteSettings(settings());
        outputManager.setFilePathPrefixer(new FilePathPrefixerConstantPath(pathForPrefixer));
        return outputManager;
    }

    private static OutputWriteSettings settings() {

        TestReaderWriterUtilities.ensureRasterWriter();

        OutputWriteSettings settings = new OutputWriteSettings();

        // We populate any defaults in OutputWriteSettings from our default bean factory
        try {
            settings.checkMisconfigured(RegisterBeanFactories.getDefaultInstances());
        } catch (BeanMisconfiguredException e1) {
            throw new AnchorFriendlyRuntimeException(e1);
        }

        return settings;
    }
}
