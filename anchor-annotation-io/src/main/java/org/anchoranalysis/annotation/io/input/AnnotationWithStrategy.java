/* (C)2020 */
package org.anchoranalysis.annotation.io.input;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.annotation.io.bean.strategy.AnnotatorStrategy;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.image.stack.Stack;
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

    public OperationWithProgressReporter<NamedProvider<Stack>, CreateException> stacks() {
        return new OperationCreateStackCollection(input);
    }
}
