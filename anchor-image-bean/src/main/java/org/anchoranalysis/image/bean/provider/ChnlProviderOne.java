/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.channel.Channel;

public abstract class ChnlProviderOne extends ChnlProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ChnlProvider chnl;
    // END BEAN PROPERTIES

    @Override
    public Channel create() throws CreateException {
        return createFromChnl(chnl.create());
    }

    protected abstract Channel createFromChnl(Channel chnl) throws CreateException;
}
