package org.anchoranalysis.image.stack.wrap;

/*-
 * #%L
 * anchor-image
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

import java.util.Set;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

public class WrapStackAsTimeSequenceStore extends NamedProviderStore<TimeSequence> {
	
	private NamedProviderStore<Stack> namedProvider;
	private int t;
	
	public WrapStackAsTimeSequenceStore( NamedProviderStore<Stack> namedProvider ) {
		this(namedProvider, 0);
	}
	
	public WrapStackAsTimeSequenceStore( NamedProviderStore<Stack> namedProvider, int t ) {
		this.namedProvider = namedProvider;
		this.t = t;
	}

	@Override
	public TimeSequence getException(String key) throws NamedProviderGetException {
		return new TimeSequence( namedProvider.getException(key) );
	}

	@Override
	public TimeSequence getNull(String key) throws NamedProviderGetException {
		return new TimeSequence( namedProvider.getException(key) );
	}

	@Override
	public Set<String> keys() {
		return namedProvider.keys();
	}

	@Override
	public void add(String name, Operation<TimeSequence,OperationFailedException> getter) throws OperationFailedException {
		namedProvider.add(name, () -> getter.doOperation().get(t) );
	}
}
