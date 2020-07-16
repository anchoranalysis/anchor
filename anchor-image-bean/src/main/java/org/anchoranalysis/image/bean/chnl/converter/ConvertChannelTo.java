/* (C)2020 */
package org.anchoranalysis.image.bean.chnl.converter;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.bean.ImageBean;
import org.anchoranalysis.image.stack.region.chnlconverter.ChannelConverter;

/** Converts a channel from one type to another */
public abstract class ConvertChannelTo extends ImageBean<ConvertChannelTo> {

    public abstract ChannelConverter<?> createConverter() throws CreateException;
}
