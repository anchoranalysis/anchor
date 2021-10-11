/*-
 * #%L
 * anchor-annotation-io
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

package org.anchoranalysis.annotation.io;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.annotation.io.bean.AnnotatorStrategy;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.stack.named.NamedStacksSupplier;
import org.anchoranalysis.image.io.stack.input.ProvidesStackInput;
import org.anchoranalysis.io.input.InputFromManagerDelegate;

/**
 * One particular annotation, associated with its strategy.
 *
 * @author Owen Feehan
 * @param <T> type of annotation-strategy.
 */
public class AnnotationWithStrategy<T extends AnnotatorStrategy>
        extends InputFromManagerDelegate<ProvidesStackInput> {

    /** The strategy on how annotation occurs. */
    @Getter private final T strategy;

    /** Path to annotation. */
    @Getter private final Path path;

    /**
     * Creates for a particular input and associated annotation strategy.
     *
     * @param input the input.
     * @param strategy the strategy.
     * @throws OperationFailedException if a path cannot be determined by {@code strategy} for the
     *     respective {@code input}.
     */
    public AnnotationWithStrategy(ProvidesStackInput input, T strategy)
            throws OperationFailedException {
        super(input);
        this.strategy = strategy;
        this.path = strategy.pathFor(input);
    }

    /**
     * The file associated with the annotation, if such a file exists.
     *
     * @return the file, if it exists.
     */
    public Optional<File> associatedFile() {
        return getDelegate().pathForBinding().map(Path::toFile);
    }

    /**
     * A human-friendly textual description of the annotation, or {@link Optional#empty()} if no
     * label is available.
     *
     * @return the label, if available.
     * @throws OperationFailedException if a label cannot be successfully determined.
     */
    public Optional<String> label() throws OperationFailedException {
        return strategy.annotationLabelFor(getDelegate());
    }

    /**
     * All stacks associated with the input, lazily evaluated.
     *
     * <p>The stacks are cached the first time they are evaluated, to avoid repeated computation.
     *
     * @return a supplier of the stacks.
     */
    public NamedStacksSupplier stacks() {
        return NamedStacksSupplier.cache(getDelegate()::asSet);
    }
}
