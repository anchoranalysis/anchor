package org.anchoranalysis.annotation.io.input;

/*-
 * #%L
 * anchor-annotation-io
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.anchoranalysis.annotation.io.bean.strategy.AnnotatorStrategy;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.input.InputFromManager;

/** 
 * 
 * An annotation that has been combined with it's strategy
 * 
 * @author FEEHANO
 *
 */
public class AnnotationWithStrategy<T extends AnnotatorStrategy> implements InputFromManager {
	
	private ProvidesStackInput input;
	private T annotationStrategy;
	private Path annotationPath;
	
	public AnnotationWithStrategy(
		ProvidesStackInput input,
		T strategy
	) throws AnchorIOException {
		this.input = input;
		this.annotationStrategy = strategy;
		this.annotationPath = annotationStrategy.annotationPathFor(input);
	}
	
	public File associatedFile() {
		return input.pathForBinding().toFile();
	}

	public T getStrategy() {
		return annotationStrategy;
	}

	public Path getAnnotationPath() {
		return annotationPath;
	}
	
	/** A label to be used when aggregrating this annotation with others, or NULL if this makes no sense 
	 * @throws IOException */
	public Optional<String> labelForAggregation() throws AnchorIOException {
		return annotationStrategy.annotationLabelFor(input);
	}
	
	@Override
	public String descriptiveName() {
		return input.descriptiveName();
	}

	@Override
	public Path pathForBinding() {
		return input.pathForBinding();
	}
		
	public List<File> deriveAssociatedFiles() {
		return Arrays.asList( getAnnotationPath().toFile() );
	}

	@Override
	public void close(ErrorReporter errorReporter) {
		input.close(errorReporter);
	}

	public OperationWithProgressReporter<INamedProvider<Stack>,CreateException> stacks() {
		return new OperationCreateStackCollection(input);
	}
	
}
