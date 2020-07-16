/* (C)2020 */
package org.anchoranalysis.bean.permute.property;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.permute.setter.PermutationSetter;
import org.anchoranalysis.bean.permute.setter.PermutationSetterException;
import org.anchoranalysis.bean.permute.setter.PermutationSetterList;
import org.anchoranalysis.bean.permute.setter.PermutationSetterUtilities;

/**
 * Base classes for PermuteProperty that require a path
 *
 * @author Owen Feehan
 */
public abstract class PermutePropertyWithPath<T> extends PermuteProperty<T> {

    // START BEAN PROPERTIES

    /**
     * Either a direct property of a bean e.g. "someproperty" Or a nested-property with the children
     * separated by full-stops: e.g. "somechild1.somechild2.someproperty"
     *
     * <p>Children must be single multiplicity, and always be a subclass of IBean
     */
    @BeanField @Getter @Setter private String propertyPath;

    /** Additional property paths that are also changed, along with the main propertyPath * */
    @BeanField @OptionalBean @Getter @Setter private StringSet additionalPropertyPaths;
    // END BEAN PROPERTIES

    // Searches through a list of property fields to find one that matches the propertyName
    @Override
    public PermutationSetter createSetter(AnchorBean<?> parentBean)
            throws PermutationSetterException {

        PermutationSetterList out = new PermutationSetterList();

        addForPath(out, parentBean, propertyPath);

        // We add any additional paths
        if (additionalPropertyPaths != null) {
            for (String additionalPath : additionalPropertyPaths.set()) {
                addForPath(out, parentBean, additionalPath);
            }
        }

        return out;
    }

    private void addForPath(PermutationSetterList out, AnchorBean<?> parentBean, String path)
            throws PermutationSetterException {
        out.add(PermutationSetterUtilities.createForSingle(parentBean, path));
    }
}
