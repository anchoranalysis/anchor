/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.dummyProvider;

import static org.mockito.Mockito.*;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.bean.provider.ChnlProvider;
import org.anchoranalysis.image.channel.Channel;

/** For referencing in BeanXML */
public class DummyChnlProvider extends ChnlProvider {

    @Override
    public Channel create() throws CreateException {
        return mock(Channel.class);
    }
}
