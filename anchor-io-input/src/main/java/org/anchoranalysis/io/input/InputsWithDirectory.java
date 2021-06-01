/*-
 * #%L
 * anchor-io-input
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.functional.FunctionalProgress;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.io.input.file.NamedFile;

/**
 * All inputs for an experiment, together with any parent directory which is specified as a parent
 * for these inputs.
 *
 * <p>All inputs must be contained in this directory or one of its sub-direcotries.
 *
 * @author Owen Feehan
 * @param <T> input-type
 */
@Value
@AllArgsConstructor
@Accessors(fluent = true)
public class InputsWithDirectory<T extends InputFromManager> {

    /** The inputs. */
    private List<T> inputs;

    /** The directory associated with the inputs. */
    private Optional<Path> directory;

    /**
     * Creates without any parent directory.
     *
     * @param inputs the inputs.
     */
    public InputsWithDirectory(List<T> inputs) {
        this(inputs, Optional.empty());
    }

    /**
     * Creates a new {@link InputsWithDirectory} which is the result of mapping the existing inputs.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param <S> the type of inputs that are mapped to
     * @param mapFunction the function that transforms and existing input into a new input
     * @return a newly created input-manager with the mapped inputs, but an identical directory.
     */
    public <S extends InputFromManager> InputsWithDirectory<S> map(Function<T, S> mapFunction) {
        return new InputsWithDirectory<>(FunctionalList.mapToList(inputs, mapFunction), directory);
    }

    /**
     * Like {{@link #map(Function)} but increments a {@link Progress}.
     *
     * @param <S> the type of inputs that are mapped to
     * @param mapFunction the function that transforms and existing input into a new input
     * @return a newly created input-manager with the mapped inputs, but an identical directory.
     * @throws E if {@code mapFunction} throws E
     */
    public <S extends InputFromManager, E extends Exception> InputsWithDirectory<S> map(
            CheckedFunction<T, S, E> mapFunction, Progress progress) throws E {
        return withInputs(FunctionalProgress.mapList(inputs, progress, mapFunction));
    }

    /**
     * Changes the inputs, but preserves the directory.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param inputsToAssign inputs to assign
     * @param <S> type of inputs to assign
     * @return a newly created input-manager with <code>inputsToAssign</code>, but an unchanged
     *     directory.
     */
    public <S extends InputFromManager> InputsWithDirectory<S> withInputs(List<S> inputsToAssign) {
        return new InputsWithDirectory<>(inputsToAssign, directory);
    }

    /**
     * Find all files in the input directory are not used as inputs.
     *
     * @return the files, with an identifier derived relative to the input-directory
     * @throws OperationFailedException if directory isn't defined
     */
    public Collection<NamedFile> findAllNonInputFiles() throws OperationFailedException {
        if (directory.isPresent()) {
            return FindNonInputFiles.from(directory.get(), inputs);
        } else {
            throw new OperationFailedException(
                    "A directory is not defined, so this operation is not possible.");
        }
    }

    public boolean isEmpty() {
        return inputs.isEmpty();
    }

    public Iterator<T> iterator() {
        return inputs.iterator();
    }

    public ListIterator<T> listIterator() {
        return inputs.listIterator();
    }
}
