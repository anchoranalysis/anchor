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

import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.MultipleFileTypeGenerator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

// currently untested
public class CombinedListGenerator extends MultipleFileTypeGenerator  {

	private ArrayList<NameValue<Generator>> list = new ArrayList<>();
	
	@Override
	public FileType[] getFileTypes(OutputWriteSettings outputWriteSettings) {

		ArrayList<FileType> all = new ArrayList<>();
		
		for( NameValue<Generator> ni : list) {
			FileType[] arr = ni.getValue().getFileTypes(outputWriteSettings);
			for (int i=0; i<arr.length; i++) {
				all.add( arr[i] );
			}
		}
		
		return all.toArray( new FileType[]{} );
	}

	@Override
	public void write(OutputNameStyle outputNameStyle, BoundOutputManager outputManager) throws OutputWriteFailedException {
		
		for( NameValue<Generator> ni : list) {

			if (ni.getName()!=null) {
				outputNameStyle.setOutputName( ni.getName() );
			}
			
			ni.getValue().write(outputNameStyle, outputManager);
		}
		
	}

	@Override
	public int write(IndexableOutputNameStyle outputNameStyle, String index, BoundOutputManager outputManager)
			throws OutputWriteFailedException {
		
		int maxWritten = -1;
		for( NameValue<Generator> ni : list) {
			
			if (ni.getName()!=null) {
				outputNameStyle = outputNameStyle.duplicate();
				outputNameStyle.setOutputName( ni.getName() );
			}
			
			int numWritten = ni.getValue().write(outputNameStyle, index, outputManager);
			maxWritten = Math.max(maxWritten, numWritten);
		}
		
		return maxWritten;
	}

	// A null name is allowed, in which case, the iterableObjectGenerator is not changed
	// Either everything should have a null name, or not.  Mixed is not a good idea!
	public boolean add(String name, Generator generator) {
		return list.add( new SimpleNameValue<>(name,generator) );
	}
}
