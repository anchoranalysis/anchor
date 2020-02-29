package org.anchoranalysis.feature.io.csv;

/*
 * #%L
 * anchor-feature-io
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.nio.file.Path;
import java.util.List;

import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.csv.CSVGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.csv.CSVWriter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;


/**
 * Generates a CSV file from a table
 * 
 * @author FEEHANO
 *
 * @param <T> rows-object type
 */
public abstract class TableCSVGenerator<T> extends CSVGenerator implements IterableGenerator<T> {

	private List<String> headerNames;
	
	private T element;
	
	public TableCSVGenerator( String manifestFunction, List<String> headerNames ) {
		super(manifestFunction);
		this.headerNames = headerNames;
	}

	@Override
	public T getIterableElement() {
		return element;
	}

	@Override
	public void setIterableElement(T element) {
		this.element = element;
	}

	@Override
	public void start() throws OutputWriteFailedException {
	}

	@Override
	public void end() throws OutputWriteFailedException {
	}

	@Override
	public Generator getGenerator() {
		return this;
	}

	@Override
	public void writeToFile(OutputWriteSettings outputWriteSettings,
			Path filePath) throws OutputWriteFailedException {
		
		try (CSVWriter writer = CSVWriter.create(filePath)) {
			writeRowsAndColumns( writer, element, headerNames );
		} catch (AnchorIOException e) {
			throw new OutputWriteFailedException(e);
		}
	}
	
	protected abstract void writeRowsAndColumns(
		CSVWriter writer,
		T rows,
		List<String> headerNames
	) throws OutputWriteFailedException;
}
