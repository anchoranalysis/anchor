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
package org.anchoranalysis.test.io.output;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bean.path.prefixer.IncrementingNumber;
import org.anchoranalysis.io.output.bean.path.prefixer.PathPrefixer;
import org.anchoranalysis.io.output.bean.rules.OutputEnabledRules;

/**
 * A fixture that implements a basic implementation of a {@link OutputManager}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OutputManagerFixture {

    /**
     * Creates the {@link OutputManager} fixture.
     *
     * @param pathForPrefixer a path to use for the prefixer in the output-manager. When undefined,
     *     an incrementing number will be used.
     * @param outputEnabledRules rules that determine which outputs occur or not.
     * @return a newly created fixture for {@link OutputManager}.
     */
    public static OutputManager createOutputManager(
            Optional<Path> pathForPrefixer, Optional<OutputEnabledRules> outputEnabledRules) {
        OutputManager outputManager = new OutputManager();
        outputManager.setSilentlyDeleteExisting(true);
        outputManager.setOutputWriteSettings(settings());
        outputManager.setPrefixer(prefixer(pathForPrefixer));
        if (outputEnabledRules.isPresent()) {
            outputManager.setOutputsEnabled(outputEnabledRules.get());
        }
        return outputManager;
    }

    private static PathPrefixer prefixer(Optional<Path> pathForPrefixer) {
        if (pathForPrefixer.isPresent()) {
            return new ConstantPathPrefixer(pathForPrefixer.get());
        } else {
            return new IncrementingNumber();
        }
    }

    private static OutputWriteSettings settings() {

        RegisterBeanFactories.registerAllPackageBeanFactories();

        OutputWriteSettings settings = new OutputWriteSettings();

        // We populate any defaults in OutputWriteSettings from our default bean factory
        try {
            settings.checkMisconfigured(RegisterBeanFactories.getDefaultInstances());
        } catch (BeanMisconfiguredException e) {
            throw new AnchorFriendlyRuntimeException(e);
        }

        return settings;
    }
}
