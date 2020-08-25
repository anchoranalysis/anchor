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

package org.anchoranalysis.annotation.io.input;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.annotation.io.bean.AnnotatorStrategy;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.image.stack.NamedStacksSet;
import org.anchoranalysis.image.stack.NamedStacksSupplier;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequenceStore;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * An annotation that has been combined with it's strategy
 *
 * @author Owen Feehan
 */
public class AnnotationWithStrategy<T extends AnnotatorStrategy> implements InputFromManager {

    private ProvidesStackInput input;
    private T annotationStrategy;
    private Path annotationPath;

    public AnnotationWithStrategy(ProvidesStackInput input, T strategy) throws AnchorIOException {
        this.input = input;
        this.annotationStrategy = strategy;
        this.annotationPath = annotationStrategy.annotationPathFor(input);
    }

    public Optional<File> associatedFile() {
        return input.pathForBinding().map(Path::toFile);
    }

    public T getStrategy() {
        return annotationStrategy;
    }

    public Path getAnnotationPath() {
        return annotationPath;
    }

    /**
     * A label to be used when aggregrating this annotation with others, or NULL if this makes no
     * sense
     *
     * @throws AnchorIOException
     */
    public Optional<String> labelForAggregation() throws AnchorIOException {
        return annotationStrategy.annotationLabelFor(input);
    }

    @Override
    public String descriptiveName() {
        return input.descriptiveName();
    }

    @Override
    public Optional<Path> pathForBinding() {
        return input.pathForBinding();
    }

    public List<File> deriveAssociatedFiles() {
        return Arrays.asList(getAnnotationPath().toFile());
    }

    @Override
    public void close(ErrorReporter errorReporter) {
        input.close(errorReporter);
    }

    public NamedStacksSupplier stacks() {
        return NamedStacksSupplier.cache(this::buildStacks);
    }

    private NamedStacksSet buildStacks(ProgressReporter progressReporter)
            throws OperationFailedException {
        NamedStacksSet stackCollection = new NamedStacksSet();
        input.addToStoreInferNames(
                new WrapStackAsTimeSequenceStore(stackCollection, 0), 0, progressReporter);
        return stackCollection;
    }
}
