package org.anchoranalysis.io.generator.collection;

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


import java.util.Set;

import org.anchoranalysis.core.error.combinable.AnchorCombinableException;
import org.anchoranalysis.core.error.friendly.IFriendlyException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceNonIncrementalRerouterErrors;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceNonIncrementalWriter;
import org.anchoranalysis.io.manifest.sequencetype.SetSequenceType;
import org.anchoranalysis.io.namestyle.StringSuffixOutputNameStyle;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class IterableGeneratorOutputHelper {

	private IterableGeneratorOutputHelper() {}
	
	// TODO remove padding for nrgStack and switch to single channel outputs
	public static <T> void output( NamedProvider<T> providers, IterableGenerator<T> generator, BoundOutputManager outputManager, String outputName, String prefix, ErrorReporter errorReporter, boolean suppressSubfoldersIn ) {

		StringSuffixOutputNameStyle outputNameStyle = new StringSuffixOutputNameStyle( prefix, prefix + "%s");
		outputNameStyle.setOutputName(outputName);

		GeneratorSequenceNonIncrementalRerouterErrors<T> writer = new GeneratorSequenceNonIncrementalRerouterErrors<>(
			new GeneratorSequenceNonIncrementalWriter<>(
				outputManager,
				outputNameStyle.getOutputName(),
				outputNameStyle,
				generator,
				true
			),
			errorReporter
		);
		writer.setSuppressSubfolder(suppressSubfoldersIn);
		
		Set<String> keys = providers.keys();
		
		writer.start( new SetSequenceType(), keys.size() );
		
		for ( String name : keys ) {
			
			try {
				writer.add(
					providers.getException(name),
					name
				);
			} catch (NamedProviderGetException e) {
				errorReporter.recordError(IterableGeneratorOutputHelper.class, e.summarize());
			}
		}
		
		writer.end();
	}
	
	
	
	
	public static <T> void outputWithException( NamedProvider<T> providers, IterableGenerator<T> generator, BoundOutputManager outputManager, String outputName, String suffix, boolean suppressSubfoldersIn ) throws OutputWriteFailedException {

		StringSuffixOutputNameStyle outputNameStyle = new StringSuffixOutputNameStyle( suffix, suffix + "%s");
		outputNameStyle.setOutputName(outputName);

		GeneratorSequenceNonIncrementalWriter<T> writer = new GeneratorSequenceNonIncrementalWriter<>(
			outputManager,
			outputNameStyle.getOutputName(),
			outputNameStyle,
			generator,
			true
		);

		writer.setSuppressSubfolder(suppressSubfoldersIn);
		
		Set<String> keys = providers.keys();
		
		writer.start( new SetSequenceType(), keys.size() );
		
		for ( String name : keys ) {
			
			try {
				T item = providers.getException(name);
				writer.add( item, name );
			} catch (Exception e) {
				throwExceptionInWriter(e, name);
			}
		}
		
		writer.end();
	}
	
	private static void throwExceptionInWriter(Exception e, String name) throws OutputWriteFailedException {
		
		String errorMsg = String.format("An error occurred outputting %s",name); 
		
		if (e instanceof IFriendlyException) {
			IFriendlyException eCast = (IFriendlyException) e;
			throw new OutputWriteFailedException(errorMsg, eCast);
		}
		
		if (e instanceof AnchorCombinableException) {
			AnchorCombinableException eCast = (AnchorCombinableException) e;
			throw new OutputWriteFailedException( errorMsg, eCast );	
		}
		
		throw new OutputWriteFailedException(errorMsg + ":" + e);		
	}
	
	public static <T> NamedProvider<T> subset( NamedProvider<T> providers, OutputAllowed oa, ErrorReporter errorReporter ) {
		
		NameValueSet<T> out = new NameValueSet<>();
		
		for ( String name : providers.keys() ) {
			
			if (oa.isOutputAllowed(name)) {
				try {
					out.add( new SimpleNameValue<>(name,providers.getException(name)) );
				} catch (NamedProviderGetException e) {
					errorReporter.recordError(IterableGeneratorOutputHelper.class, e.summarize());
				}
			}
		}
		
		return out;
	}
	
	
	public static <T> NamedProvider<T> subsetWithException( NamedProvider<T> providers, OutputAllowed oa ) throws OutputWriteFailedException {
		
		NameValueSet<T> out = new NameValueSet<>();
		
		for ( String name : providers.keys() ) {
			
			if (oa.isOutputAllowed(name)) {
				try {
					out.add( new SimpleNameValue<>(name,providers.getException(name)) );
				} catch (NamedProviderGetException e) {
					throw new OutputWriteFailedException(e.summarize());
				}
			}
		}
		
		return out;
	}
}
