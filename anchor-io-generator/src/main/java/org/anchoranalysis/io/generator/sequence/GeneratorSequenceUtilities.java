package org.anchoranalysis.io.generator.sequence;

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


import java.util.Collection;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class GeneratorSequenceUtilities {

	public static <T> void generateListAsSubfolder(
		String folderName,
		int numDigits,
		Collection<T> items,
		IterableObjectGenerator<T,Stack> generator,
		BoundOutputManagerRouteErrors outputManager,
		ErrorReporter errorReporter
	) {
		
		//CfgGenerator generator = new CfgGenerator( new RGBOutlineWriter(2), colorIndex, new IDGetterMarkID() );
		
		IndexableOutputNameStyle outputStyle = new IntegerSuffixOutputNameStyle(folderName,numDigits);
		
		GeneratorSequenceIncrementalRerouteErrors<T> sequenceWriter = new GeneratorSequenceIncrementalRerouteErrors<>( 
			new GeneratorSequenceIncrementalWriter<>( 
				outputManager.getDelegate(),
				outputStyle.getOutputName(),
				outputStyle,
				generator,
				new ManifestDescription("raster",folderName),
				0,
				true
			),
			errorReporter
		);
		
		sequenceWriter.start();
		
		for( T item : items ) {
			sequenceWriter.add(item);
		}
		
		sequenceWriter.end();
	}
	
	
	public static <T> void generateListAsSubfolderWithException(
			String folderName,
			int numDigits,
			Collection<T> items,
			IterableObjectGenerator<T,Stack> generator,
			BoundOutputManagerRouteErrors outputManager
		) throws OutputWriteFailedException {
			
			//CfgGenerator generator = new CfgGenerator( new RGBOutlineWriter(2), colorIndex, new IDGetterMarkID() );
			
			IndexableOutputNameStyle outputStyle = new IntegerSuffixOutputNameStyle(folderName,numDigits);
			
			GeneratorSequenceIncrementalWriter<T> sequenceWriter =  
				new GeneratorSequenceIncrementalWriter<>( 
					outputManager.getDelegate(),
					outputStyle.getOutputName(),
					outputStyle,
					generator,
					new ManifestDescription("raster",folderName),
					0,
					true
				);
				
			sequenceWriter.start();
			
			for( T item : items ) {
				sequenceWriter.add(item);
			}
			
			sequenceWriter.end();
		}
}
