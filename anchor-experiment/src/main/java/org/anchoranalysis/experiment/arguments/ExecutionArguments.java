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

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.functional.OptionalFactory;
import org.anchoranalysis.io.input.InputContextParameters;
import org.anchoranalysis.io.input.bean.DebugModeParameters;
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
@Accessors(fluent = true)
public class ExecutionArguments {

    /** Arguments to help determine the input to the experiment. */
    @Getter private final InputArguments input = new InputArguments();

    /** Arguments to help specify the outputs from the experiment. */
    @Getter private final OutputArguments output = new OutputArguments();

    /** Arguments to help specify the outputs from the experiment. */
    @Getter private final TaskArguments task;

    /** If defined, parameters for debug-mode. */
    private Optional<DebugModeParameters> debugModeParameters = Optional.empty();

    /** Creates with neither a model-directory nor initial task-arguments. */
    public ExecutionArguments() {
        this.task = new TaskArguments();
    }

    /**
     * Creates with a model-directory and an image-size suggestion.
     *
     * @param modelDirectory the model-directory.
     * @param task arguments to help specify the outputs from the experiment.
     */
    public ExecutionArguments(Path modelDirectory, TaskArguments task) {
        input.assignModelDirectory(modelDirectory);
        this.task = task;
    }

    /**
     * Additional parameters that offer context for many beans that provide input-functions.
     *
     * @return the parameters, reusing the internal data-structure.
     */
    public InputContextParameters inputContextParameters() {
        return input.getContextParameters();
    }

    /**
     * Derive a {@link PathPrefixerContext} from the arguments in this class.
     *
     * @return a newly created {@link PathPrefixerContext} derived from this class.
     * @throws PathPrefixerException if the the path in {@code outputDirectory} of {@link
     *     PathPrefixerContext} is relative instead of absolute.
     */
    public PathPrefixerContext derivePathPrefixerContext() throws PathPrefixerException {
        return new PathPrefixerContext(isDebugModeEnabled(), output.getPrefixer());
    }

    /**
     * Activates debug-mode.
     *
     * @param debugContains maybe a string used for filtering inputs during debugging, or an
     *     empty-string if this isn't enabled.
     */
    public void activateDebugMode(String debugContains) {
        Optional<String> debugContainsAsOptional =
                OptionalFactory.create(!debugContains.isEmpty(), () -> debugContains);
        debugModeParameters = Optional.of(new DebugModeParameters(debugContainsAsOptional));
    }

    /**
     * Has debug mode been enabled when executing an experiment?
     *
     * @return true iff debug mode has been enabled.
     */
    public boolean isDebugModeEnabled() {
        return debugModeParameters.isPresent();
    }
}
