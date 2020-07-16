/* (C)2020 */
package org.anchoranalysis.bean.permute.setter;

import org.anchoranalysis.bean.AnchorBean;

/**
 * Imposes a permutation on some property of a bean. This can be an immediate property of a bean, or
 * property of some nested-bean. e.g.
 *
 * <p>Bean.x (immediate property) Bean.child1.child2.child3.x (nested)
 *
 * <p>All intermediate children must be beans.
 *
 * @author Owen Feehan
 */
public interface PermutationSetter {

    void setPermutation(AnchorBean<?> bean, Object val) throws PermutationSetterException;
}
