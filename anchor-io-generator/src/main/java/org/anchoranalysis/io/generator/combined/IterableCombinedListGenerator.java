package org.anchoranalysis.io.generator.combined;

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


import java.util.ArrayList;

import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.filepath.prefixer.FilePathCreator;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.MultipleFileTypeGenerator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.operationrecorder.IWriteOperationRecorder;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

// We can probably have a more efficient implementation by not using the CombinedListGenerator as a delegate
//  but we leave it for now
// Should always have at least one item added
public class IterableCombinedListGenerator<T> extends MultipleFileTypeGenerator implements IterableGenerator<T> {

	private CombinedListGenerator delegate = new CombinedListGenerator();
	
	private ArrayList<IterableGenerator<T>> list = new ArrayList<>();
	
	public IterableCombinedListGenerator() {
	
		super();
		list = new ArrayList<>();
	}
	
	@Override
	public void write(OutputNameStyle outputNameStyle,
			FilePathCreator filePathGnrtr,
			IWriteOperationRecorder writeOperationRecorder,
			BoundOutputManager outputManager) throws OutputWriteFailedException {
		delegate.write(outputNameStyle, filePathGnrtr, writeOperationRecorder, outputManager);
	}

	@Override
	public int write(IndexableOutputNameStyle outputNameStyle,
			FilePathCreator filePathGnrtr,
			IWriteOperationRecorder writeOperationRecorder,
			String index, BoundOutputManager outputManager)
			throws OutputWriteFailedException {
		return delegate.write(outputNameStyle, filePathGnrtr, writeOperationRecorder, index, outputManager);
	}

	@Override
	public FileType[] getFileTypes(OutputWriteSettings outputWriteSettings) {
		return delegate.getFileTypes(outputWriteSettings);
	}

	@Override
	public T getIterableElement() {
		
		if (list.size()==0) {
			return null;
		} else {
			return list.get(0).getIterableElement();
		}
	}

	@Override
	public void setIterableElement(T element) throws SetOperationFailedException {

		for (IterableGenerator<T> generator : list) {
			generator.setIterableElement(element);
		}
	}

	@Override
	public Generator getGenerator() {
		return this;
	}

	public boolean add(String name, IterableGenerator<T> element) {
		list.add(element);
		return delegate.add(name, element.getGenerator());
	}
	
	@Override
	public void start() throws OutputWriteFailedException {
		for (IterableGenerator<T> generator : list) {
			generator.start();
		}
	}


	@Override
	public void end() throws OutputWriteFailedException {
		for (IterableGenerator<T> generator : list) {
			generator.end();
		}
	}


}
