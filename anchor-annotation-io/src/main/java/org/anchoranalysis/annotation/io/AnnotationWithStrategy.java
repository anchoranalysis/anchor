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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.annotation.io.bean.AnnotatorStrategy;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.image.core.stack.named.NamedStacksSupplier;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.input.InputReadFailedException;

/**
 * An annotation that has been combined with its strategy.
 *
 * @author Owen Feehan
 */
public class AnnotationWithStrategy<T extends AnnotatorStrategy> implements InputFromManager {

    private ProvidesStackInput input;
    @Getter private T strategy;

    /** Path to annotation */
    @Getter private Path path;

    public AnnotationWithStrategy(ProvidesStackInput input, T strategy)
            throws InputReadFailedException {
        this.input = input;
        this.strategy = strategy;
        this.path = strategy.annotationPathFor(input);
    }

    public Optional<File> associatedFile() {
        return input.pathForBinding().map(Path::toFile);
    }

    /**
     * A label to be used when aggregrating this annotation with others, or null if this makes no
     * sense
     *
     * @throws InputReadFailedException
     */
    public Optional<String> labelForAggregation() throws InputReadFailedException {
        return strategy.annotationLabelFor(input);
    }

    @Override
    public String name() {
        return input.name();
    }

    @Override
    public Optional<Path> pathForBinding() {
        return input.pathForBinding();
    }

    public List<File> deriveAssociatedFiles() {
        return Arrays.asList(path.toFile());
    }

    @Override
    public void close(ErrorReporter errorReporter) {
        input.close(errorReporter);
    }

    public NamedStacksSupplier stacks() {
        return NamedStacksSupplier.cache(input::asSet);
    }
}
