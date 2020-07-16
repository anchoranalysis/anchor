/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.kernel;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;

public abstract class KernelPosNeg<T> extends KernelIndependent<T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private double probPos = 0.5;

    @BeanField @Getter @Setter private double probNeg = 0.5;
    // END BEAN PROPERTIES

    protected void duplicateHelper(KernelPosNeg<T> out) {
        out.probPos = probPos;
        out.probNeg = probNeg;
    }
}
