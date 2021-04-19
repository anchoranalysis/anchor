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
    @Getter private Optional<List<Path>> paths = Optional.empty();

    /** A directory indicating where inputs can be located */
    @Getter private Optional<Path> directory = Optional.empty();

    /** If defined, a glob that is applied on inputDirectory */
    @Getter private Optional<String> filterGlob = Optional.empty();

    /**
     * If defined, a set of extension filters that can be applied on inputDirectory
     *
     * <p>A defined but empty set implies no check is applied
     *
     * <p>{@link Optional#empty} implies no extension filters exist.
     */
    @Getter private Optional<Set<String>> filterExtensions = Optional.empty();

    /**
     * If true, the entire filename or relative path (excluding extension) is used to determine a
     * unique identifier.
     */
    @Getter private boolean relativeForIdentifier = false;

    /**
     * If True, any files in the input directory that are unused as inputs, are copied to the output
     * directory.
     */
    @Getter private boolean copyUnused = false;

    /** If true, the order of the inputs are shuffled (randomized). */
    @Getter private boolean shuffle = false;

    /** A directory indicating where models can be located */
    private Optional<Path> modelDirectory;

    public void assignPaths(List<Path> inputPaths) {
        this.paths = Optional.of(inputPaths);
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
        this.directory = inputDirectory.map(InputArguments::normalizeDirectory);
    }

    public Path getModelDirectory() {
        return modelDirectory.orElseThrow(
                () -> new AnchorFriendlyRuntimeException("Model-directory is required but absent"));
    }

    public void assignModelDirectory(Path modelDirectory) {
        this.modelDirectory = Optional.of(modelDirectory);
    }

    public void assignFilterGlob(String filterGlob) {
        this.filterGlob = Optional.of(filterGlob);
    }

    public void assignFilterExtensionsIfMissing(Optional<Set<String>> filterExtensions) {
        if (!this.filterExtensions.isPresent()) {
            this.filterExtensions = filterExtensions;
        }
    }

    public void assignFilterExtensions(Set<String> filterExtensions) {
        this.filterExtensions = Optional.of(filterExtensions);
    }

    public void assignCopyUnused() {
        this.copyUnused = true;
    }

    public void assignRelativeForIdentifier() {
        this.relativeForIdentifier = true;
    }

    public void assignShuffle() {
        this.shuffle = true;
    }

    private static Path normalizeDirectory(Path directory) {
        if (!directory.isAbsolute()) {
            return directory.toAbsolutePath().normalize();
        } else {
            return directory.normalize();
        }
    }
}
