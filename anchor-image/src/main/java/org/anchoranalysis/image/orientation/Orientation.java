/* (C)2020 */
package org.anchoranalysis.image.orientation;

import java.io.Serializable;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.math.rotation.RotationMatrix;

public abstract class Orientation implements Serializable {

    /** */
    private static final long serialVersionUID = 2450707930231680263L;

    public abstract Orientation duplicate();

    public abstract RotationMatrix createRotationMatrix();

    public abstract int getNumDims();

    public abstract boolean equals(Object other);

    public abstract int hashCode();

    public abstract Orientation negative();

    public void addProperties(NameValueSet<String> nvc) {}

    public void addPropertiesToMask(ObjectWithProperties mask) {}
}
