/* (C)2020 */
package org.anchoranalysis.image.io.chnl.map;

import java.util.List;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.image.io.bean.channel.map.ImgChnlMapEntry;

public class CreateImgChnlMapFromEntries
        implements FunctionWithException<
                List<ImgChnlMapEntry>, ImgChnlMap, AnchorNeverOccursException> {

    @Override
    public ImgChnlMap apply(List<ImgChnlMapEntry> list) {

        ImgChnlMap beanOut = new ImgChnlMap();

        for (ImgChnlMapEntry entry : list) {
            beanOut.add(entry);
        }

        return beanOut;
    }
}
