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

package org.anchoranalysis.experiment;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.OptionalFactory;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerContext;
import org.anchoranalysis.io.output.enabled.OutputEnabledMutable;
import org.anchoranalysis.io.params.DebugModeParams;
import org.anchoranalysis.io.params.InputContextParams;

@NoArgsConstructor
public class ExperimentExecutionArguments {

    /** If defined, parameters for debug-mode. */
    private Optional<DebugModeParams> debugModeParams = Optional.empty();

    /** A list of paths referring to specific inputs; */
    private Optional<List<Path>> inputPaths = Optional.empty();

    /** A directory indicating where inputs can be located */
    @Getter private Optional<Path> inputDirectory = Optional.empty();

    /** A directory indicating where inputs can be located */
    @Getter @Setter private Optional<Path> outputDirectory = Optional.empty();

    /** A directory indicating where models can be located */
    private Optional<Path> modelDirectory = Optional.empty();

    /** If non-null, a glob that is applied on inputDirectory */
    @Setter private Optional<String> inputFilterGlob = Optional.empty();
    
    /** Outputs enabled for experiment that are added to a task's default outputs. */
    @Getter @Setter private Optional<OutputEnabledMutable> additionalOutputEnabled = Optional.empty();

    /**
     * If defined, a set of extension filters that can be applied on inputDirectory
     *
     * <p>A defined but empty set implies no check is applied
     *
     * <p>An Optional.empty() implies no extension filters exist.
     */
    @Getter @Setter private Optional<Set<String>> inputFilterExtensions = Optional.empty();

    /** A name to describe the ongoing task */
    @Getter @Setter private Optional<String> taskName = Optional.empty();

    public ExperimentExecutionArguments(Path modelDirectory) {
        this.modelDirectory = Optional.of(modelDirectory);
    }

    /**
     * Creates an input-context, reusing parameters from the experiment-execution
     *
     * @throws IOException
     */
    public InputContextParams createInputContext() throws IOException {
        InputContextParams out = new InputContextParams();
        out.setDebugModeParams(debugModeParams);
        out.setInputDirectory(inputDirectory);
        out.setInputPaths(inputPaths);
        inputFilterGlob.ifPresent(out::setInputFilterGlob);
        inputFilterExtensions.ifPresent(out::setInputFilterExtensions);
        return out;
    }

    public FilePathPrefixerContext createPrefixerContext() throws FilePathPrefixerException {
        return new FilePathPrefixerContext(isDebugModeEnabled(), outputDirectory);
    }

    // The path will be converted to an absolute path, if it hasn't been already, based upon the
    // current working directory
    public void setInputDirectory(Optional<Path> inputDirectory) {
        this.inputDirectory =
                inputDirectory.map(
                        directory -> {
                            if (!directory.isAbsolute()) {
                                return directory.toAbsolutePath().normalize();
                            } else {
                                return directory.normalize();
                            }
                        });
    }

    /**
     * Activates debug-mode
     *
     * @param debugContains maybe a string used for filtering inputs during debugging, or an empty-string if this isn't enabled
     */
    public void activateDebugMode(String debugContains) {
        Optional<String> debugContainsAsOptional = OptionalFactory.create(debugContains.isEmpty(), () -> debugContains);
        debugModeParams = Optional.of( new DebugModeParams(debugContainsAsOptional) );
    }
    
    /**
     * Assigns outputs to be added to the default outputs for a task.
     * 
     * @param outputs the outputs to add
     */
    public void assignAdditionalOutputs(OutputEnabledMutable outputs) {
        this.additionalOutputEnabled = Optional.of(outputs);
    }

    public boolean isDebugModeEnabled() {
        return debugModeParams.isPresent();
    }

    public boolean hasInputFilterExtensions() {
        return inputFilterExtensions.isPresent();
    }

    public Path getModelDirectory() {
        return modelDirectory.orElseThrow(
                () -> new AnchorFriendlyRuntimeException("Model-directory is required but absent"));
    }

    public void setModelDirectory(Path modelDirectory) {
        this.modelDirectory = Optional.of(modelDirectory);
    }

    public List<Path> getInputPaths() {
        return inputPaths.orElseThrow(
                () -> new AnchorFriendlyRuntimeException("Input-paths are required but absent"));
    }

    public void setInputPaths(List<Path> inputPaths) {
        this.inputPaths = Optional.of(inputPaths);
    }
}
