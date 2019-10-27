package org.anchoranalysis.core.progress;

/*
 * #%L
 * anchor-core
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


public class ProgressReporterIncrement implements AutoCloseable {

	private ProgressReporter progressReporter;
	private int cnt = 0;
	
	public ProgressReporterIncrement(ProgressReporter progressReporter) {
		super();
		this.progressReporter = progressReporter;
	}
	
	public void open() {
		cnt = progressReporter.getMin();
		progressReporter.open();
	}
	
	@Override
	public void close() {
		progressReporter.close();
	}
	
	public void update() {
		progressReporter.update(++cnt);
	}

	public int getMin() {
		return progressReporter.getMin();
	}

	public void setMin(int min) {
		progressReporter.setMin(min);
	}

	public int getMax() {
		return progressReporter.getMax();
	}

	public void setMax(int max) {
		progressReporter.setMax(max);
	}
	
	
}
