/* (C)2020 */
package org.anchoranalysis.image.orientation;

import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.math.rotation.RotationMatrix;
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
    public void addPropertiesToMask(ObjectWithProperties mask) {
        delegate.addPropertiesToMask(mask);
    }
}
