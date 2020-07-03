package org.anchoranalysis.io.manifest.deserializer.folder;

/*-
 * #%L
 * anchor-io-manifest
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

import org.anchoranalysis.core.index.ITypedGetFromIndex;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.folder.SequencedFolder;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

public abstract class DeserializeFromFolder<T> implements HistoryCreator<T> {
	
	private SequencedFolder folder;
	
	public DeserializeFromFolder( SequencedFolder folder ) {
		this.folder = folder;
	}
	
	@Override
	public LoadContainer<T> create() throws DeserializationFailedException {
		
		SequenceType sequenceType = folder.getAssociatedSequence();
		ITypedGetFromIndex<T> cntr = createCtnr( folder );
		
		assert( sequenceType != null );
		
		LoadContainer<T> history = new LoadContainer<>();
		
		IBoundedIndexContainer<T> boundedContainer = new BoundsFromSequenceType<>(
			cntr,
			sequenceType
		);
		
		history.setCntr( boundedContainer );
		history.setExpensiveLoad( false );
		//history.setExpensiveLoad( true );
		
		return history;
	}

	protected abstract ITypedGetFromIndex<T> createCtnr( SequencedFolder folder );
}
