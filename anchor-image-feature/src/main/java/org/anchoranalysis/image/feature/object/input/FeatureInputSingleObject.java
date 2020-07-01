package org.anchoranalysis.image.feature.object.input;

/*-
 * #%L
 * anchor-image-feature
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.util.Optional;

import org.anchoranalysis.feature.input.FeatureInputNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.object.ObjectMask;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * An input representing a single object-mask (with maybe an NRG-stack associated)
 * 
 * <p>Equals and hash-code must be sensibly defined as these inputs can be used as keys
 * in a cache</p>.
 * 
 * @author Owen Feehan
 *
 */
public class FeatureInputSingleObject extends FeatureInputNRGStack {

	private ObjectMask objMask;
	
	public FeatureInputSingleObject(ObjectMask objMask) {
		this(
			objMask,
			Optional.empty()
		);
	}
	
	public FeatureInputSingleObject(ObjectMask objMask, NRGStackWithParams nrgStack) {
		this(
			objMask,
			Optional.of(nrgStack)
		);
	}
	
	public FeatureInputSingleObject(ObjectMask objMask, Optional<NRGStackWithParams> nrgStack) {
		super(nrgStack);
		this.objMask = objMask;
	}

	public ObjectMask getObjectMask() {
		return objMask;
	}

	public void setObjMask(ObjectMask objMask) {
		this.objMask = objMask;
	}
	
	@Override
	public String toString() {
		return objMask.toString();
	}

	/** This assumes objMask's equals() is cheap i.e. shallow-equals not deep-equals */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		FeatureInputSingleObject rhs = (FeatureInputSingleObject) obj;
		return new EqualsBuilder()
			.appendSuper( super.equals(obj) )
            .append(objMask, rhs.objMask)
            .isEquals();
	}

	/** This assumes objMask's equals() is cheap i.e. shallow-equals not deep-equals */
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.appendSuper( super.hashCode() )
			.append(objMask)
			.toHashCode();
	}
}
