package org.anchoranalysis.image.io.chnl.map;

import java.util.List;

import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.image.io.bean.chnl.map.ImgChnlMapEntry;

public class CreateImgChnlMapFromEntries implements IObjectBridge<
	List<ImgChnlMapEntry>,
	ImgChnlMap,
	AnchorNeverOccursException
> {

	@Override
	public ImgChnlMap bridgeElement(List<ImgChnlMapEntry> list) {
		
		ImgChnlMap beanOut = new ImgChnlMap();
    	
    	for( ImgChnlMapEntry entry : list ) {
    		beanOut.add( entry );
    	}
    	
	    return beanOut;		
	}
}