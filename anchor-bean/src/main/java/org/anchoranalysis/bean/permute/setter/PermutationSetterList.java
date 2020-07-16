/* (C)2020 */
package org.anchoranalysis.bean.permute.setter;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.AnchorBean;

/**
 * Applies several permutation setters to the same object
 *
 * @author Owen Feehan
 */
public class PermutationSetterList implements PermutationSetter {

    private List<PermutationSetter> list = new ArrayList<>();

    @Override
    public void setPermutation(AnchorBean<?> bean, Object val) throws PermutationSetterException {

        for (PermutationSetter ps : list) {
            ps.setPermutation(bean, val);
        }
    }

    public boolean add(PermutationSetter ps) {
        return list.add(ps);
    }
}
