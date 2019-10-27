package org.anchoranalysis.io.generator.histogram;

/*
 * #%L
 * anchor-io
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


import java.io.IOException;
import java.nio.file.Path;

import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.io.bean.output.OutputWriteSettings;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.csv.CSVGenerator;
import org.anchoranalysis.io.output.OutputWriteFailedException;

public class HistogramCSVGenerator extends CSVGenerator implements IterableGenerator<Histogram> {

	public HistogramCSVGenerator() {
		super("histogram");
	}

	private Histogram histogram;
	private boolean ignoreZeros = false;
	
	@Override
	public Histogram getIterableElement() {
		return histogram;
	}

	@Override
	public void setIterableElement(Histogram element)
			throws SetOperationFailedException {
		this.histogram = element;
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
		try {
			HistogramCSVWriter.writeHistogramToFile( histogram, filePath, ignoreZeros );
		} catch (IOException e) {
			throw new OutputWriteFailedException(e);
		}
	}
	
	public boolean isIgnoreZeros() {
		return ignoreZeros;
	}

	public void setIgnoreZeros(boolean ignoreZeros) {
		this.ignoreZeros = ignoreZeros;
	}

}
