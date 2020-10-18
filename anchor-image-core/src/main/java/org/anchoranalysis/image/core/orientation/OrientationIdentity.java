/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.image.core.orientation;

import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.spatial.rotation.RotationMatrix;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class OrientationIdentity extends Orientation {

    /** */
    private static final long serialVersionUID = 1L;

    private OrientationRotationMatrix delegate;

    public OrientationIdentity(int numDim) {
        RotationMatrix rotMat = new RotationMatrix(numDim);

        // Create identity matrix
        for (int i = 0; i < numDim; i++) {
            rotMat.getMatrix().set(i, i, 1);
        }
        delegate = new OrientationRotationMatrix(rotMat);
    }

    @Override
    public Orientation duplicate() {
        return new OrientationIdentity(delegate.getNumDims());
    }

    @Override
    public RotationMatrix createRotationMatrix() {
        return delegate.createRotationMatrix();
    }

    @Override
    public int getNumDims() {
        return delegate.getNumDims();
    }

    @Override
    public boolean equals(Object other) {

        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (!(other instanceof OrientationIdentity)) {
            return false;
        }

        OrientationIdentity otherC = (OrientationIdentity) other;

        return delegate.equals(otherC.delegate);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(delegate).toHashCode();
    }

    @Override
    public Orientation negative() {
        return delegate.negative();
    }

    @Override
    public void addProperties(NameValueSet<String> nvc) {
        delegate.addProperties(nvc);
    }

    @Override
    public void addPropertiesToMask(ObjectWithProperties object) {
        delegate.addPropertiesToMask(object);
    }
}
