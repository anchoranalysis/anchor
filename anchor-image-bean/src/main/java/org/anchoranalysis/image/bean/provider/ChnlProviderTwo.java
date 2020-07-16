/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.channel.Channel;

/**
 * A chnl-provider based-on two input chnl-providers that must be of the same dimensionality
 *
 * @author Owen Feehan
 */
public abstract class ChnlProviderTwo extends ChnlProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ChnlProvider chnl1;

    @BeanField @Getter @Setter private ChnlProvider chnl2;
    // END BEAN PROPERTIES

    @Override
    public Channel create() throws CreateException {

        Channel chnlFirst = chnl1.create();
        Channel chnlSecond = chnl2.create();

        if (!chnlFirst.getDimensions().equals(chnlSecond.getDimensions())) {
            throw new CreateException("Dimensions of channels do not match");
        }

        return process(chnlFirst, chnlSecond);
    }

    protected abstract Channel process(Channel chnl1, Channel chnl2) throws CreateException;
}
