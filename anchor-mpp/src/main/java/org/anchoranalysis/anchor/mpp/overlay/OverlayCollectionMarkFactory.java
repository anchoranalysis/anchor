package org.anchoranalysis.anchor.mpp.overlay;

/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.RGBColor;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.gui.videostats.internalframe.markredraw.ColoredCfg;

/**
 * Two-way factory.
 * 
 * Creation of OverlayCollection from marks
 * Retrieval of marks back from OverlayCollections
 * 
 * @author Owen Feehan
 *
 */
public class OverlayCollectionMarkFactory {
	
	public static OverlayCollection createWithoutColor( Cfg cfg, RegionMembershipWithFlags regionMembership ) {
		OverlayCollection out = new OverlayCollection();
		
		for(int i=0; i<cfg.size(); i++) {
			Mark m = cfg.get(i);
			out.add( new OverlayMark(m, regionMembership) );
		}
		
		return out;
	}

	public static ColoredOverlayCollection createColor( ColoredCfg cfg, RegionMembershipWithFlags regionMembership ) {
		return createColor( cfg.getCfg(), cfg.getColorList(), regionMembership );
	}
	
	
	private static ColoredOverlayCollection createColor(
		Cfg cfg,
		ColorIndex colorIndex,
		RegionMembershipWithFlags regionMembership
	) {
		
		ColoredOverlayCollection out = new ColoredOverlayCollection();
		
		for(int i=0; i<cfg.size(); i++) {
			Mark m = cfg.get(i);
			RGBColor color = colorIndex.get(i);
			
			// TODO should we duplicate color?
			out.add( new OverlayMark(m, regionMembership), color );
		}
		
		return out;
	}

	// Creates a cfg from whatever Overlays are found in the collection
	public static Cfg cfgFromOverlays( OverlayCollection overlays ) {
		Cfg out = new Cfg();
		
		for(int i=0; i<overlays.size(); i++) {
			Overlay overlay = overlays.get(i);
			
			if (overlay instanceof OverlayMark) {
				OverlayMark overlayMark = (OverlayMark) overlay;
				out.add( overlayMark.getMark() );
			}
		}
		
		return out;
	}
	
	

	// Creates a cfg from whatever Overlays are found in the collection
	public static ColoredCfg cfgFromOverlays( ColoredOverlayCollection overlays ) {
		ColoredCfg out = new ColoredCfg();
		
		for(int i=0; i<overlays.size(); i++) {
			Overlay overlay = overlays.get(i);
			
			RGBColor col = overlays.getColor(i);
			
			if (overlay instanceof OverlayMark) {
				OverlayMark overlayMark = (OverlayMark) overlay;
				out.add( overlayMark.getMark(), col );
			}
		}
		
		return out;
	}


	
	

}
