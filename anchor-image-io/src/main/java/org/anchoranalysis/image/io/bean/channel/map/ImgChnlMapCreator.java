/* (C)2020 */
package org.anchoranalysis.image.io.bean.channel.map;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.io.chnl.map.ImgChnlMap;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;

public abstract class ImgChnlMapCreator extends AnchorBean<ImgChnlMapCreator> {

    public abstract ImgChnlMap createMap(OpenedRaster openedRaster) throws CreateException;
}
