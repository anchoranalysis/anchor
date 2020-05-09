package org.anchoranalysis.mpp.sgmn.transformer;

/*-
 * #%L
 * anchor-plugin-mpp-sgmn
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

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;

/**
 * Combines a transformation from S->U with U->T
 * 
 * @author FEEHANO
 *
 * @param <S> source type
 * @param <T> destination type
 * @param <U> intermediate type
 */
public class Compose<S, T, U> extends StateTransformerBean<S, T> {

	// START BEAN PROPERTIES
	@BeanField
	private StateTransformerBean<S, U> first;
	
	@BeanField
	private StateTransformerBean<U, T> second;
	// END BEAN PROPERTIES
	
	@Override
	public T transform(S in, TransformationContext context) throws OperationFailedException {

		U inter = first.transform(in, context);
		
		return second.transform(inter, context);
	}

	public StateTransformerBean<S, U> getFirst() {
		return first;
	}

	public void setFirst(StateTransformerBean<S, U> first) {
		this.first = first;
	}

	public StateTransformerBean<U, T> getSecond() {
		return second;
	}

	public void setSecond(StateTransformerBean<U, T> second) {
		this.second = second;
	}



}
