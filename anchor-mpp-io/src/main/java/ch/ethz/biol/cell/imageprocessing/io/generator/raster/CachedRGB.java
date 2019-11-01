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


import java.nio.ByteBuffer;
import java.util.List;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.writer.OverlayWriter;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeShort;

public class CachedRGB {

	// Start - these parameters never change
	private final IDGetter<Overlay> idGetter;
	
	private DisplayStack backgroundOrig;
	
	private RGBStack rgb;
	
	private ColoredOverlayCollection currentCfg;
	
	private OverlayWriter maskWriter;
	
	private boolean needsBackgroundRefresh = false;
	
	public CachedRGB( final IDGetter<Overlay> idGetter ) {
		
		this.idGetter = idGetter;
		
		this.currentCfg = new ColoredOverlayCollection();
		
		// We have no background yet, setBackground must be called before we start
		needsBackgroundRefresh = true;
	}
	
	public void updateMaskWriter( OverlayWriter maskWriter ) {
		
		// change to only trigger a redraw at the next operation
		
		//resetToChnlOrig( currentCfg );
		this.maskWriter = maskWriter;
		//drawCfg( currentCfg );
		
		needsBackgroundRefresh = true;
	}
	
	
	// resets it all to the orginal, but doesn't draw any cfg
	private void resetToChnlOrigAll() {
		
		createRGBFromChnl(backgroundOrig);
		
		needsBackgroundRefresh = false;
	}
	
	// When we replace the current Cfg with a new cfg
	public void updateCfg( OverlayedDisplayStackUpdate update ) throws OperationFailedException {

		//System.out.println("updateCfg");

		if (update==null) {
			return;
		}
		
		// If we have a null cfg, we replace it with an empty cfg
//		if (cfgNew==null) {
//			cfgNew = new ColoredCfg();
//		}
		
		if (update.getBackgroundStack()!=null) {
			//System.out.printf("BackgroundStack updated\n");
			setBackground(update.getBackgroundStack());
		}

			
		// We do the whole image
		if (update.getRedrawParts()==null) {
		
			List<BoundingBox> bboxListReset = currentCfg.bboxList(maskWriter,backgroundOrig.getDimensions());
			
			if (update.getColoredCfg()!=null) {
				ColoredOverlayCollection cfgNew = update.getColoredCfg();
				resetToChnlOrig( bboxListReset );
				drawCfg( cfgNew );
				this.currentCfg = cfgNew;
				
				//System.out.printf("Updating for cfg=%d ALL\n", cfgNew.size() );
				
			} else {
				resetToChnlOrig( bboxListReset );
				drawCfg( currentCfg );
				
				//System.out.printf("Updating for same cfg ALL\n");
			}

			
		} else {
			List<BoundingBox> listBBox = update.getRedrawParts().bboxList(maskWriter,backgroundOrig.getDimensions());
			
			if (update.getColoredCfg()!=null) {
				ColoredOverlayCollection cfgNew = update.getColoredCfg();
				resetToChnlOrig( listBBox );
				drawCfgIfIntersects( cfgNew, listBBox );
				this.currentCfg = cfgNew;
				
				
				//System.out.printf("Updating cfg=%d with parts=%d\n", cfgNew.size(), listBBox.size() );
				
			} else {
				resetToChnlOrig( listBBox );
				drawCfgIfIntersects( currentCfg, listBBox );
				
				//System.out.printf("Updating only parts=%d\n", listBBox.size() );
			}
		}
		
		
	}
	

	
	// START we will remove these

	// Allows us to change just the background
	private void setBackground( DisplayStack background ) {
		assert(background.dataTypeBeforeConversion().equals(VoxelDataTypeByte.instance)
			|| (background.dataTypeBeforeConversion().equals(VoxelDataTypeShort.instance)&&background.numNonNullConverters()>0) );
		if (background!=this.backgroundOrig) {
			this.backgroundOrig = background;
			needsBackgroundRefresh = true;
		}
	}
	
	// STOP - we will remove these
	
	
	private void resetToChnlOrig( List<BoundingBox> listBBox ) {
		
		if (needsBackgroundRefresh) {
			resetToChnlOrigAll();
			return;
		}
		
		if (listBBox==null) {
			resetToChnlOrigAll();
		}
		
		for (BoundingBox bbox : listBBox) {
			
			bbox.clipTo(backgroundOrig.getDimensions().getExtnt());
			
			for (int c=0; c<3; c++) {
				Chnl rgbTarget = rgb.getChnl(c);
				
				VoxelBox<ByteBuffer> vbTarget = rgbTarget.getVoxelBox().asByte();
				
				int bgChnl = selectBackgroundChnl(c, backgroundOrig.getNumChnl());
				backgroundOrig.copyPixelsTo(bgChnl,bbox, vbTarget, bbox);
			}
		}
	}
	
	private static int selectBackgroundChnl( int iter, int numChnlsInBackground ) {
		// If backgroundOrig is single channel always take from 0
		if (numChnlsInBackground==1) {
			return 0;
		} else if (numChnlsInBackground==3) {
			return iter;
		} else {
			assert false;
			return 0;
		}
	}
	
	
	private void drawCfg( ColoredOverlayCollection cfg ) throws OperationFailedException {
		assert( cfg.getColorList()!=null);
		assert( cfg.getColorList().size()==cfg.size() );
		//assert( cfg.getColorList().numUniqueColors() > 0 );
		// TODO We only draw marks which intersect with the bounding box
		maskWriter.writeOverlays( cfg, rgb, idGetter );
	}
	
	/** Draws objects from cfg, but only if they intersect with the bounding boxes of
	   objects in intersectCfg */
	private void drawCfgIfIntersects( ColoredOverlayCollection oc, List<BoundingBox> bboxList ) throws OperationFailedException {
		
		// We only draw marks which intersect with the bounding box
		maskWriter.writeOverlaysIfIntersects( oc, rgb, idGetter, bboxList );
	}
		
	private void createRGBFromChnl(DisplayStack background) {
		rgb = ConvertDisplayStackToRGB.convert(background);
	}
	
	public RGBStack getRGB() {
		if (rgb==null) {
			resetToChnlOrigAll();
		}
		return rgb;
	}
	
	public ColoredOverlayCollection getColoredCfg() {
		return currentCfg;
	}
	
//	public ColoredCfgWithDisplayStack createCfgWithStack() {
//		return new ColoredCfgWithDisplayStack(currentCfg, backgroundOrig);
//	}

	public DisplayStack getBackground() {
		return backgroundOrig;
	}
}