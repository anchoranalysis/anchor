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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;

/**
 * Arguments that can further specify an experiment's <b>input</b> in addition to its bean
 * specification.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class InputArguments {

    /** A list of paths referring to specific inputs; */
    private Optional<List<Path>> inputPaths = Optional.empty();

    /** A directory indicating where inputs can be located */
    @Getter private Optional<Path> inputDirectory = Optional.empty();

    /** If non-null, a glob that is applied on inputDirectory */
    @Getter private Optional<String> inputFilterGlob = Optional.empty();

    /**
     * If defined, a set of extension filters that can be applied on inputDirectory
     *
     * <p>A defined but empty set implies no check is applied
     *
     * <p>An Optional.empty() implies no extension filters exist.
     */
    @Getter private Optional<Set<String>> inputFilterExtensions = Optional.empty();

    /** A directory indicating where models can be located */
    private Optional<Path> modelDirectory;

    Optional<List<Path>> getInputPaths() {
        return inputPaths;
    }

    public void assignInputPaths(List<Path> inputPaths) {
        this.inputPaths = Optional.of(inputPaths);
    }

    /**
     * Sets an input-directory.
     *
     * <p>If defined, The path will be converted to an absolute path, if it hasn't been already,
     * based upon the current working directory.
     *
     * @param inputDirectory the input-directory to an assign
     */
    public void assignInputDirectory(Optional<Path> inputDirectory) {
        this.inputDirectory = inputDirectory.map(InputArguments::normalizeDirectory);
    }

    public Path getModelDirectory() {
        return modelDirectory.orElseThrow(
                () -> new AnchorFriendlyRuntimeException("Model-directory is required but absent"));
    }

    public void assignModelDirectory(Path modelDirectory) {
        this.modelDirectory = Optional.of(modelDirectory);
    }

    public void assignInputFilterGlob(String inputFilterGlob) {
        this.inputFilterGlob = Optional.of(inputFilterGlob);
    }

    public void assignInputFilterExtensionsIfMissing(Optional<Set<String>> inputFilterExtensions) {
        if (!this.inputFilterExtensions.isPresent()) {
            this.inputFilterExtensions = inputFilterExtensions;
        }
    }

    public void assignInputFilterExtensions(Set<String> inputFilterExtensions) {
        this.inputFilterExtensions = Optional.of(inputFilterExtensions);
    }

    private static Path normalizeDirectory(Path directory) {
        if (!directory.isAbsolute()) {
            return directory.toAbsolutePath().normalize();
        } else {
            return directory.normalize();
        }
    }
}
