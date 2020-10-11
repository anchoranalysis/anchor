package org.anchoranalysis.io.bean.color.scheme;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.OperationFailedException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Like {@link ColorScheme} but employs a unary operator on a call to an existing {@link ColorScheme}.
 *  
 * @author Owen Feehan
 *
 */
@NoArgsConstructor @AllArgsConstructor
public abstract class ColorSchemeUnary extends ColorScheme {

    // START BEAN PROPERTIES
    /** The delegate that creates the (unshuffled) list. */
    @BeanField @Getter @Setter private ColorScheme colors;
    // END BEAN PROPERTIES

    @Override
    public ColorList createList(int size) throws OperationFailedException {
        ColorList list = colors.createList(size);
        transform(list);
        return list;
    }
    
    /**
     * Transform an existing {@link ColorList}.
     * 
     * <p>Note that the incoming {@link ColorList} may be modified, and can no
     * longer be used in its original form after this method call.
     * 
     * @param source the incoming {@link ColorList} (which may be consumed and modified).
     * @return the transformed (outgoing) {@link ColorList}.
     */
    protected abstract ColorList transform(ColorList source);
}
