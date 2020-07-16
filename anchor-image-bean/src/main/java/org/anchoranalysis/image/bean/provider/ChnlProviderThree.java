/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.channel.Channel;

public abstract class ChnlProviderThree extends ChnlProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ChnlProvider chnl1;

    @BeanField @Getter @Setter private ChnlProvider chnl2;

    @BeanField @Getter @Setter private ChnlProvider chnl3;
    // END BEAN PROPERTIES

    @Override
    public Channel create() throws CreateException {

        return process(chnl1.create(), chnl2.create(), chnl3.create());
    }

    protected abstract Channel process(Channel chnl1, Channel chnl2, Channel chnl3)
            throws CreateException;
}
