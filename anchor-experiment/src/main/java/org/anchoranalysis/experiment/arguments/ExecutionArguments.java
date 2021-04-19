/*-
 * #%L
 * anchor-experiment
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

package org.anchoranalysis.experiment.arguments;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.anchoranalysis.bean.OptionalFactory;
import org.anchoranalysis.io.input.InputContextParams;
import org.anchoranalysis.io.input.bean.DebugModeParams;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerContext;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerException;

/**
 * Arguments that can further specify an experiment's <b>execution</b> (in its entirety) in addition
 * to its bean specification.
 *
 * <p>This is often used to insert further parameters from the command-line into bean plugins, that
 * have otherwise been loaded from BeanXML.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@Accessors(fluent = true)
public class ExecutionArguments {

    /** Arguments to help determine the input to the experiment. */
    @Getter private final InputArguments input = new InputArguments();

    /** Arguments to help specify the outputs from the experiment. */
    @Getter private final OutputArguments output = new OutputArguments();

    /** Arguments to help specify the outputs from the experiment. */
    @Getter private final TaskArguments task = new TaskArguments();

    /** If defined, parameters for debug-mode. */
    private Optional<DebugModeParams> debugModeParams = Optional.empty();

    public ExecutionArguments(Path modelDirectory) {
        input.assignModelDirectory(modelDirectory);
    }

    /**
     * Creates an input-context, reusing parameters from the experiment-execution
     *
     * @throws IOException
     */
    public InputContextParams createInputContext() throws IOException {
        InputContextParams out = new InputContextParams();
        out.setDebugModeParams(debugModeParams);
        out.setInputDirectory(input.getDirectory());
        out.setInputPaths(input.getPaths());
        input.getFilterGlob().ifPresent(out::setInputFilterGlob);
        input.getFilterExtensions().ifPresent(out::setInputFilterExtensions);
        out.setRelativeForIdentifier(input.isRelativeForIdentifier());
        out.setCopyUnused(input.isCopyUnused());
        out.setShuffle(input.isShuffle());
        return out;
    }

    public PathPrefixerContext createPrefixerContext() throws PathPrefixerException {
        return new PathPrefixerContext(isDebugModeEnabled(), output.getPrefixer());
    }

    /**
     * Activates debug-mode
     *
     * @param debugContains maybe a string used for filtering inputs during debugging, or an
     *     empty-string if this isn't enabled
     */
    public void activateDebugMode(String debugContains) {
        Optional<String> debugContainsAsOptional =
                OptionalFactory.create(!debugContains.isEmpty(), () -> debugContains);
        debugModeParams = Optional.of(new DebugModeParams(debugContainsAsOptional));
    }

    public boolean isDebugModeEnabled() {
        return debugModeParams.isPresent();
    }
}
