/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.object.ObjectCollection;

/**
 * Base class for {@link ObjectCollectionProvider} that take a {@code objects} bean-field of same
 * type as provided.
 *
 * @author Owen Feehan
 */
public abstract class ObjectCollectionProviderUnary extends ObjectCollectionProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ObjectCollectionProvider objects;
    // END BEAN PROPERTIES

    @Override
    public ObjectCollection create() throws CreateException {
        return createFromObjects(objects.create());
    }

    protected abstract ObjectCollection createFromObjects(ObjectCollection objects)
            throws CreateException;
}
