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

package org.anchoranalysis.io.input;

import com.github.davidmoten.guavamini.Preconditions;
import io.vavr.control.Either;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.collection.StringSetTrie;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.index.range.IndexRangeNegative;
import org.anchoranalysis.io.input.bean.DebugModeParameters;

/**
 * Additional parameters that offer context for many beans that provide input-functions.
 *
 * @author Owen Feehan
 */
public class InputContextParameters {

    /** A list of paths referring to specific inputs. */
    @Getter @Setter private Optional<List<Path>> inputPaths = Optional.empty();

    /** If defined, a directory which can be used by beans to find input. */
    @Getter private Optional<Path> inputDirectory = Optional.empty();

    /**
     * A glob that can be used by beans to filter input.
     *
     * <p>It requires the syntax of Java's <a
     * href="https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)">getPathMatcher</a>.
     */
    @Getter @Setter private Optional<String> inputFilterGlob = Optional.empty();

    /** A trie of extensions that can be used filter inputs. */
    @Getter @Setter private Optional<StringSetTrie> inputFilterExtensions = Optional.empty();

    /** Parameters for debug-mode (only defined if we are in debug mode). */
    @Getter @Setter private Optional<DebugModeParameters> debugModeParameters = Optional.empty();

    /**
     * If true, the entire filename or relative path (excluding extension) is used to determine a
     * unique identifier.
     */
    @Getter @Setter private boolean relativeForIdentifier = false;

    /** If true, the order of the inputs are shuffled (randomized). */
    @Getter @Setter private boolean shuffle = false;

    /**
     * If defined, an upper limit that is imposed on the number of inputs.
     *
     * <p>When an {@link Integer} is is a fixed number of inputs.
     *
     * <p>When a {@link Double} it is a ratio of the total number of inputs (and should only be in
     * the interval {@code (0.0, 1.0)}).
     */
    @Getter @Setter private Optional<Either<Integer, Double>> limitUpper = Optional.empty();

    /** If defined, this indicates and specifies only a subset of the naming-elements to use. */
    @Getter @Setter private Optional<IndexRangeNegative> identifierSubrange = Optional.empty();

    /**
     * If defined, a directory which can be used by beans to find input.
     *
     * <p>This should always be an absolute path, never a relative one.
     *
     * @param inputDirectory the absolute path of the input-directory.
     * @throws IOException if the path passed is not absolute.
     */
    public void setInputDirectory(Optional<Path> inputDirectory) throws IOException {
        OptionalUtilities.ifPresent(inputDirectory, InputContextParameters::checkAbsolutePath);
        this.inputDirectory = inputDirectory;
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
        this.inputDirectory = inputDirectory.map(InputContextParameters::normalizeDirectory);
    }

    /**
     * Assigns a glob that can be used by beans to filter input.
     *
     * <p>The syntax should be from Java's <a
     * href="https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)">getPathMatcher</a>.
     *
     * @param glob a string describing a glob in the syntax expected by Java.
     */
    public void assignFilterGlob(String glob) {
        this.inputFilterGlob = Optional.of(glob);
    }

    /**
     * If defined, this indicates and specifies only a subset of the elements of the identifier to
     * use.
     *
     * @param identifierSubrange if defined, this indicates and specifies only a subset of the
     *     naming-elements to use.
     */
    public void assignIdentifierSubrange(IndexRangeNegative identifierSubrange) {
        this.identifierSubrange = Optional.of(identifierSubrange);
    }

    /**
     * Assigns extensions that can be used filter inputs, but <b>only when</b> no extensions have
     * already been set.
     *
     * @param filterExtensions the extensions sued to filter inputs (without the leading period).
     */
    public void assignInputFilterExtensionsIfMissing(
            Supplier<Optional<StringSetTrie>> filterExtensions) {
        if (!this.inputFilterExtensions.isPresent()) {
            this.inputFilterExtensions = filterExtensions.get();
        }
    }

    /**
     * Assigns extensions that can be used filter inputs, but <b>irrespective of whether</b>
     * extensions have already been set.
     *
     * <p>Any existing input-filter-extensions are repalced with {@code filterExtensions}.
     *
     * @param filterExtensions the extensions sued to filter inputs (without the leading period).
     */
    public void assignInputFilterExtensions(StringSetTrie filterExtensions) {
        this.inputFilterExtensions = Optional.of(filterExtensions);
    }

    /**
     * Specify that the entire filename or relative path (excluding extension) is used to determine
     * a unique identifier.
     */
    public void assignRelativeForIdentifier() {
        this.relativeForIdentifier = true;
    }

    /**
     * Assigns a fixed upper limit of number of inputs.
     *
     * @param fixedLimit the maximum number of inputs allowed.
     */
    public void assignFixedLimit(int fixedLimit) {
        this.limitUpper = Optional.of(Either.left(fixedLimit));
    }

    /**
     * Assigns a fixed upper limit that is a ratio of the number of inputs allowed.
     *
     * @param ratioLimit the maximum number of inputs allowed.
     */
    public void assignRatioLimit(double ratioLimit) {
        Preconditions.checkArgument(ratioLimit > 0.0);
        Preconditions.checkArgument(ratioLimit < 1.0);
        this.limitUpper = Optional.of(Either.right(ratioLimit));
    }

    /**
     * Assigns a specific list of paths that are used for the inputs.
     *
     * @param paths the paths.
     */
    public void assignPaths(List<Path> paths) {
        this.inputPaths = Optional.of(paths);
    }

    /** Indicates that the order of the inputs should be shuffled (randomized). */
    public void assignShuffle() {
        this.shuffle = true;
    }

    private static void checkAbsolutePath(Path inputDirectory) throws IOException {
        if (!inputDirectory.isAbsolute()) {
            throw new IOException(
                    String.format(
                            "An non-absolute path was passed to setInputDirectory() of %s",
                            inputDirectory));
        }
    }

    private static Path normalizeDirectory(Path directory) {
        if (!directory.isAbsolute()) {
            return directory.toAbsolutePath().normalize();
        } else {
            return directory.normalize();
        }
    }
}
