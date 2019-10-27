package ch.ethz.biol.cell.imageprocessing.io.generator.raster;

/*
 * #%L
 * anchor-mpp-io
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.stack.DisplayStack;

import ch.ethz.biol.cell.gui.image.provider.DisplayUpdate;
import ch.ethz.biol.cell.gui.image.provider.BoundOverlayedDisplayStack;
import ch.ethz.biol.cell.gui.overlay.BoundColoredOverlayCollection;
import ch.ethz.biol.cell.gui.overlay.Overlay;
import ch.ethz.biol.cell.gui.overlay.ColoredOverlayCollection;
import ch.ethz.biol.cell.gui.overlay.OverlayCollection;

/**
 * An update to an OverlayCollection/DisplayStack combination indicating a change to either or both
 * 
 * @author Owen Feehan
 *
 */
public class OverlayedDisplayStackUpdate {

	private ColoredOverlayCollection coloredCfg;			// If null, we don't change the current cfg
	private DisplayStack backgroundStack;	// If null, we don't change the existing background
	private OverlayCollection changedCfg;					// If non-null, additional marks that should be updated, as they might have changed in some way
	private boolean redrawSpecific = false;			// If true, then the coloredCfg passed can be considered similar to the existing cfg. If false, not
	
	// Assigns a new configuration completely, throwing out what's already there
	public static OverlayedDisplayStackUpdate assignOverlays( ColoredOverlayCollection coloredCfg ) {
		assert( coloredCfg!=null );
		return new OverlayedDisplayStackUpdate(coloredCfg, null, null,false);
	}
	
	// Replaces the existing coloredCfg with something similar, but we don't know what's changed exactly
	//  coloredCfg is the entire new Cfg
	public static OverlayedDisplayStackUpdate updateOverlaysWithSimilar( ColoredOverlayCollection coloredCfg, OverlayCollection changedCfg ) {
		assert( coloredCfg!=null );
		return new OverlayedDisplayStackUpdate(coloredCfg, null, changedCfg,true);
		//return new ColoredCfgRedrawUpdate(coloredCfg, null, null,false);
		
	}
	
	public static OverlayedDisplayStackUpdate updateOverlaysWithSimilar( ColoredOverlayCollection coloredCfg ) {
		assert( coloredCfg!=null );
		return new OverlayedDisplayStackUpdate(coloredCfg, null, null,true);
		//return new ColoredCfgRedrawUpdate(coloredCfg, null, null,false);
		
	}
	
	
	public static OverlayedDisplayStackUpdate assignBackground(DisplayStack backgroundStack ) {
		assert( backgroundStack!=null );
		return new OverlayedDisplayStackUpdate(null, backgroundStack, null,false);
	}
	
	public static OverlayedDisplayStackUpdate assignOverlaysAndBackground( ColoredOverlayCollection coloredCfg, DisplayStack backgroundStack ) {
		assert( coloredCfg!=null );
		assert( backgroundStack!=null );
		return new OverlayedDisplayStackUpdate(coloredCfg, backgroundStack, null,false);
	}
	
	
	public static OverlayedDisplayStackUpdate redrawAll() {
		return new OverlayedDisplayStackUpdate(null, null, null,false);
	}
	
	public static OverlayedDisplayStackUpdate updateChanged( OverlayCollection changedCfg ) {
		assert( changedCfg!= null );
		return new OverlayedDisplayStackUpdate(null, null, changedCfg,false);
		//return new ColoredCfgRedrawUpdate(null, null, null,false);
	}
		
	
	private OverlayedDisplayStackUpdate(
			ColoredOverlayCollection coloredCfg,			
			DisplayStack backgroundStack,
			OverlayCollection redrawParts,
			boolean similar) {
		super();
		this.coloredCfg = coloredCfg;
		this.backgroundStack = backgroundStack;
		this.changedCfg = redrawParts;
		this.redrawSpecific = similar;
	}
	

	public OverlayCollection getRedrawParts() {
		return changedCfg;
	}

	public ColoredOverlayCollection getColoredCfg() {
		return coloredCfg;
	}

	public DisplayStack getBackgroundStack() {
		return backgroundStack;
	}

	public void mergeWithNewerUpdate( OverlayedDisplayStackUpdate newer ) {
	
		if (!newer.redrawSpecific) {
			this.redrawSpecific=false;
		}
		
		if (newer.backgroundStack!=null) {
			this.backgroundStack = newer.backgroundStack;
		}
		
		if (newer.getColoredCfg()!=null) {
			this.coloredCfg = newer.getColoredCfg();
			assert( newer.getColoredCfg()!=null );
		}
		
		// Any time the background stack changes, we need to redraw everyything anyway
		if (newer.getRedrawParts()==null || newer.backgroundStack==null) {
			this.changedCfg = null;
		} else {
			
			// Then we add our new parts to the existing parts 
			if (this.changedCfg==null) {
				this.changedCfg = newer.getRedrawParts();
			} else {
				this.changedCfg.addAll( newer.getRedrawParts() );
			}
		}
	}
	
	// Applies this change to a boundoverlay and creates an appropriate DisplayUpdate
	public DisplayUpdate applyAndCreateDisplayUpdate( BoundColoredOverlayCollection boundOverlay ) throws OperationFailedException {
		synchronized(boundOverlay.getPrecalculatedCache()) {
		try {
			// If the stack has changed, when we have to redraw everything anyways
			// Let's double-check to make sure the stack is diffrent
			if (getBackgroundStack()!=null) {
	
				// Assign a coloredCfg if it exists
				if (getColoredCfg()!=null) {
					boundOverlay.setOverlayCollection(getColoredCfg());
				}
				
				BoundOverlayedDisplayStack overlayedDisplayStack = new BoundOverlayedDisplayStack(getBackgroundStack(), boundOverlay );
				return DisplayUpdate.assignNewStack(overlayedDisplayStack);
			}
			
			// Otherwise if we've received a new ColoredCfg, then it's time to take action
			// We assume than we receive a new ColoredCfg redraw parts is also not simultaenously set
			if (getColoredCfg()!=null) {
				
				ColoredOverlayCollection cachedOverlayCollection = boundOverlay.getPrecalculatedCache().getOverlayCollection();
				
//				if (getRedrawParts()!=null) {
//					System.out.println("Assuming existing redraw parts knows best");
//					boundOverlay.updateColoredCfg( getColoredCfg() );
//					return DisplayUpdate.redrawParts( boundOverlay.bboxListForCfg(getRedrawParts()) );
//				}
				//assert( update.getRedrawParts()==null );
				
				// Find out the difference between the old cfg, and the new cfg, and this becomes our redraw part
				
				if (isRedrawSpecific()) {
					// If the *similar* boolean is set, it means the update is similar to the previous cfg
					//  so we:
					//    1. assume no mark has changed internally. So the only differences are adding/removing marks the cfg
					//    2. find these added/removed marks
					//	  3. change the boundOverlay accordingly
					//    4. issue DisplayUpdates only for these locations
					
					ColoredOverlayCollection added = new ColoredOverlayCollection();
					ColoredOverlayCollection removed = new ColoredOverlayCollection();
					List<Integer> removedIndices = new ArrayList<Integer>();
					
					// Find specifily the marks that were added and removed
					createDiff( cachedOverlayCollection, getColoredCfg(), added, removed, removedIndices );
					 
					//System.out.printf( "Diff: added=%d, removed=%s\n", added.size(), removed.size() );
					
					List<BoundingBox> bboxToRefresh = new ArrayList<BoundingBox>();
					bboxToRefresh.addAll( boundOverlay.bboxList(removed) );
					
					boundOverlay.removeOverlays( removedIndices );
					boundOverlay.addOverlays( added );
					
					bboxToRefresh.addAll( boundOverlay.bboxList(added) );
					
					if (getRedrawParts()!=null) {
						
						//List<IndexExstNew> indices = findIndices( getColoredCfg(), getRedrawParts() );
						//boundOverlay.updateColorSpecificIndices( getColoredCfg(), indices );
						
						bboxToRefresh.addAll( boundOverlay.bboxList(getRedrawParts()) );
					}
					
					return DisplayUpdate.redrawParts( bboxToRefresh );
					
				} else {
					
					OverlayCollection cfgForUpdate = ColoredOverlayCollection.createIntersectionComplement( cachedOverlayCollection, getColoredCfg() );
					boundOverlay.setOverlayCollection( getColoredCfg() );
					
					List<BoundingBox> bboxToRefresh = boundOverlay.bboxList(cfgForUpdate);
					
					if (getRedrawParts()!=null) {
						bboxToRefresh.addAll( boundOverlay.bboxList(getRedrawParts()) );
					}
					
					return DisplayUpdate.redrawParts( bboxToRefresh );
				}
			}
	
			// Otherwise we just have a redraw parts command
			if (isRedrawSpecific()) {
				return DisplayUpdate.redrawParts( boundOverlay.bboxList(getRedrawParts()) );
			} else {
				// Then we update everything
				return DisplayUpdate.redrawEverything();
			}
		} catch (SetOperationFailedException e) {
			throw new OperationFailedException(e);
		}
		}
	}
	
	
	// Compare a new-overlay to a previous one and finds the differences. 
	//
	//  Note we compare overlays (via memory refernces)
	//
	//    the added overlays are put in outAdded
	//    the removed overys are put in outRemovedCfg and their indices in indicesRemoved
	private static void createDiff( ColoredOverlayCollection cfgPrevious, ColoredOverlayCollection cfgNew, ColoredOverlayCollection outAdded, ColoredOverlayCollection outRemovedCfg, List<Integer> outRemovedIndices ) {
		
		Set<Overlay> setPrevious = cfgPrevious.createSet();
		Set<Overlay> setNew = cfgNew.createSet();
				
		for (int i=0; i<cfgPrevious.size(); i++) {
			
			Overlay ol = cfgPrevious.get(i);
		
			if (!setNew.contains(ol)) {
				// If it's not in the new, for sure we've removed it
				RGBColor col = cfgPrevious.getColor(i);
				outRemovedCfg.add( ol, col );
				outRemovedIndices.add( i );
			}
		}
		
		for (int i=0; i<cfgNew.size(); i++ ) {
			
			Overlay ol = cfgNew.get(i);
			
			if (!setPrevious.contains(ol)) {
				// If it's in the New, but not the previous, for sure we've added it
				RGBColor col = cfgNew.getColor(i);
				outAdded.add( ol, col );
			}
		}
		
	}
	
	
	// Returns the indices of all the elements of cfgToFind found in cfgToSearch
//	private static List<IndexExstNew> findIndices( ColoredCfg cfgToSearch, Cfg cfgToFind ) {
//		
//		List<IndexExstNew> listOut = new ArrayList<IndexExstNew>();
//		
//		Map<Mark,Integer> setToFind = cfgToFind.createHashMapToId();
//		
//		for (int i=0; i<cfgToSearch.size(); i++ ) {
//			Mark m = cfgToSearch.getCfg().get(i);
//			
//			Integer id = setToFind.get(m);
//			if (id!=null) {
//				listOut.add( new IndexExstNew(i, id) );
//			}
//		}
//		
//		return listOut;
//	}

	// Do we redraw specific bounding boxes, or look for everything
	public boolean isRedrawSpecific() {
		return redrawSpecific;
	}
	

}
