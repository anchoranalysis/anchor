package org.anchoranalysis.mpp.sgmn.optscheme;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.mpp.sgmn.cfgnrg.transformer.StateTransformer;
import org.anchoranalysis.plugin.mpp.sgmn.optscheme.TransformationContext;

/*
 * #%L
 * anchor-mpp-sgmn
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

/**
 * Remembers the current state of type T and some best state (which was once set from the current state)
 *  
 * @author FEEHANO
 *
 * @param <T>
 */
public class DualState<T> {
	
	private T crnt;
	private T best;
	
	public DualState() {
		
	}
	
	public DualState(T crnt, T best) {
		super();
		this.crnt = crnt;
		this.best = best;
	}
	
	public T releaseKeepBest() {
		T cfgNRG = this.best;
		this.crnt = null;
		clearBest();
		return cfgNRG;
	}

	public T getCrnt() {
		return crnt;
	}
	
	public void assignCrnt(T cfgNRG) {
	
		assert(cfgNRG!=null);
		
		this.crnt = cfgNRG;
	}

	public T getBest() {
		return best;
	}

	public void assignBestFromCrnt() {
		this.best = this.crnt;
	}

	public void clearBest() {
		this.best = null;
	}
	
	public boolean isBestUndefined() {
		return this.best==null;
	}
	
	public <S> DualState<S> transform( StateTransformer<T,S> func, TransformationContext context ) throws OperationFailedException {
		return new DualState<S>(
			func.transform(crnt, context),
			func.transform(best, context)
		);
	}
}