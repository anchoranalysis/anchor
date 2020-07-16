/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.mask.Mask;

public abstract class BinaryChnlProviderOne extends BinaryChnlProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private BinaryChnlProvider binaryChnl;
    // END BEAN PROPERTIES

    @Override
    public Mask create() throws CreateException {
        return createFromChnl(binaryChnl.create());
    }

    protected abstract Mask createFromChnl(Mask chnl) throws CreateException;
}
