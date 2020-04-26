package org.anchoranalysis.image.stack;

/*
 * #%L
 * anchor-image
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


import java.nio.Buffer;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

import org.anchoranalysis.core.cache.IdentityOperation;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.IdentityOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.extent.ImageDim;

// A collection of Image Stacks each with a name
public class NamedImgStackCollection extends NamedProviderStore<Stack> {
	
	private HashMap<String, OperationWithProgressReporter<Stack,OperationFailedException>> map;
	
	public NamedImgStackCollection() {
		map = new HashMap<>();
	}
	
	public OperationWithProgressReporter<Stack,OperationFailedException> getAsOperation( String identifier ) throws IllegalArgumentException {
		return map.get(identifier);
	}
	
	public Stack getException( String identifier ) throws NamedProviderGetException {
		OperationWithProgressReporter<Stack,OperationFailedException> ret = getAsOperation(identifier);

		if (ret==null) {
			throw new IllegalArgumentException("Cannot find image: '" + identifier + "'" );
		}
		try {
			return ret.doOperation( ProgressReporterNull.get() );
		} catch (OperationFailedException e) {
			throw new NamedProviderGetException(identifier, e);
		}
	}
	
	@Override
	public Stack getNull(String identifier)	throws NamedProviderGetException {
		OperationWithProgressReporter<Stack,OperationFailedException> ret = getAsOperation(identifier);

		if (ret==null) {
			return null;
		}
		try {
			return ret.doOperation( ProgressReporterNull.get() );
		} catch (OperationFailedException e) {
			throw new NamedProviderGetException(identifier, e);
		}
	}
	
	public Stack getImageNoException( String identifier ) {
		try {
			return map.get(identifier).doOperation( ProgressReporterNull.get() );
		} catch (OperationFailedException e) {
			assert false;
			return null;
		}
	}
	
	@Override
	public Set<String> keys() {
		return map.keySet();
	}

	public void addImageStack(String identifier, Stack inputImage) {
				
		//if (!inputImage.isUniformSized()) {
		//	throw new IncorrectImageSizeException("stack is not uniform sized");
		//}
		
		map.put(identifier, new IdentityOperationWithProgressReporter<>(inputImage));
	}
	
	public void addImageStack(String identifier, OperationWithProgressReporter<Stack,OperationFailedException> inputImage) {
		
		//if (!inputImage.isUniformSized()) {
		//	throw new IncorrectImageSizeException("stack is not uniform sized");
		//}
		
		map.put(identifier, inputImage);
	}
	
	@Override
	public void add(String name, final Operation<Stack,OperationFailedException> getter) throws OperationFailedException {
		
		OperationWithProgressReporter<Stack,OperationFailedException> operationWithout
		= new OperationWithProgressReporter<Stack,OperationFailedException>() {

			@Override
			public Stack doOperation(ProgressReporter progressReporter)	throws OperationFailedException {
				return getter.doOperation();
			}
			
		};
		
		map.put(name, operationWithout);
	}
	
	public NamedImgStackCollection maxIntensityProj() {
		
		NamedImgStackCollection out = new NamedImgStackCollection();
		
		for ( String name : map.keySet() ) {
			try {
				Stack projection = map
						.get(name)
						.doOperation( ProgressReporterNull.get() )
						.maxIntensityProj();
				out.addImageStack( name, projection );
			} catch (Throwable e) {
				assert false;
			}
		}
		
		return out;
	}

	/** Applies an operation on each stack in the collection and returns a new derived collection */
	public NamedImgStackCollection applyOperation( ImageDim dim, Function<Stack,Stack> stackOperation ) throws OperationFailedException {
		
		NamedImgStackCollection out = new NamedImgStackCollection();
		
		try {
			for( String key : keys() ) {
				Stack img = getException(key);
				
				if (!img.getDimensions().equals(dim) ) {
					throw new OperationFailedException(
						String.format(
							"The image-dimensions of %s (%s) does not match what is expected (%s)",
							key,
							img.getDimensions(),
							dim
						)
					);
				}
				
				out.add(
					key,
					new IdentityOperation<>( stackOperation.apply(img) )
				);
			}
			return out;
			
		} catch (NamedProviderGetException e) {
			throw new OperationFailedException(e.summarize());
		}
		
	}
		
	public void addFrom( INamedProvider<Stack> src ) {
		
		for( String name : src.keys() ) {
			addImageStack(name, new OperationStack<>(src,name) );
		}
	}
	
	public void addFromWithPrefix( final INamedProvider<Stack> src, String prefix ) {
		
		for( final String name : src.keys() ) {
			addImageStack( prefix+name, new OperationStack<>(src,name) );
		}
	}
	
	private static class OperationStack<BufferType extends Buffer> implements OperationWithProgressReporter<Stack, OperationFailedException> {
		
		private INamedProvider<Stack> src;
		private String name;
		
		public OperationStack(INamedProvider<Stack> src,
				String name) {
			super();
			this.src = src;
			this.name = name;
		}
		
		@Override
		public Stack doOperation(ProgressReporter progressReporter)	throws OperationFailedException {
			try {
				return src.getException(name);
			} catch (NamedProviderGetException e) {
				throw new OperationFailedException(e.summarize());
			}
		}
		
	};
}
