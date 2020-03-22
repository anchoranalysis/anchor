package ch.ethz.biol.cell.gui.overlay;

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


import java.util.Collections;
import java.util.List;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.anchor.overlay.writer.OverlayWriter;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.index.rtree.BBoxRTree;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.scale.ScaleFactor;

/**
 * Caches several operations associated with an OverlayCollection, and allows the creation of subsets.
 * 
 * The class is used in @see{CfgOverlayBridgeFromGenerator} to prevent repeated calculating of objmasks
 *   from cfgs, including at different zoom levels
 * 
 * The following are saved:
 * 	overlayCollection:   		A collection of colored-overlays, the main data item.  No nulls.
 *  generatedObjects:			obj-masks generated from overlayCollection.  No nulls.
 *  listBoundingBox:			bounding-boxes derived from overlayCollection.  No nulls.
 *  generatedObjectsZoomed:		obj-masks at different zoomlevel. Can contain nulls (meaning not yet calculated)
 * 
 * They should all contain the same number of elements (generatedObjectsZoomed can also be null).
 * 
 * @author Owen Feehan
 *
 */
public class OverlayPrecalculatedCache implements OverlayRetriever {

	private PrecalculatedOverlayList overlayList;
	
	
	/**
	 * Spatially indexes the bounding boxes in listBoundingBox (using the array index). Created when needed.
	 */
	private BBoxRTree rTree = null;
	

	
	/**
	 * The zoomFactor associated with the generatedObjectsZoomed cache.
	 * -1 indicates there is none set. In this case, generatedObjectsZoomed should be null.
	 */
	private double zoomFactor = -1;
	
	
	private OverlayWriter overlayWriter;
	
	private ImageDim dimEntireImage;
	
	private ImageDim dimScaled;
	
	/**
	 * The binary values we use for making object masks
	 */
	private final static BinaryValuesByte bvOut = BinaryValues.getDefault().createByte();

	
	public OverlayPrecalculatedCache(ColoredOverlayCollection overlayCollection, ImageDim dimEntireImage, OverlayWriter maskWriter ) throws CreateException {
		super();
		this.overlayWriter = maskWriter;
		this.dimEntireImage = dimEntireImage;
		this.overlayList = new PrecalculatedOverlayList(overlayCollection, dimEntireImage, maskWriter);
		rebuildCache();
	}
	
	public synchronized void setOverlayCollection( ColoredOverlayCollection overlayCollection ) throws SetOperationFailedException {
		overlayList.setOverlayCollection(overlayCollection);
		try {
			rebuildCache();
		} catch (CreateException e) {
			throw new SetOperationFailedException(e);
		}
	}
	
	public synchronized void setMaskWriter( OverlayWriter maskWriter ) throws SetOperationFailedException {
		this.overlayWriter = maskWriter;
		try {
			rebuildCache();
		} catch (CreateException e) {
			throw new SetOperationFailedException(e);
		}
	}
		
	// Creates a subset which only contains marks contained within a particular bounding-box
	//
	public synchronized OverlayPrecalculatedCache subsetWithinView( BoundingBox bboxView, BoundingBox bboxViewZoomed, double zoomFactorNew ) throws OperationFailedException {

		overlayList.assertSizesMatchSimple();
		
		// If we haven't bother initializing these things before, we do now
		if (rTree==null) {
			rTree = new BBoxRTree( overlayList.getListBoundingBox() );
		}
		if (overlayList.hasGeneratedObjectsZoomed() || zoomFactorNew!=zoomFactor) {
			overlayList.setZoomedToNull();
		}
		overlayList.assertZoomedExists();


		// Create appropriate objects related to the zoom (if it's not 1)
		if (zoomFactorNew!=1 && zoomFactorNew!=zoomFactor) {
			// We create a scaled version of our dimensions
			dimScaled = createDimensionsScaled(zoomFactorNew);
		}
		
		// Figure out which indices intersect with our bounding-box
		List<Integer> intersectingIndices = rTree.intersectsWith(bboxView);

		
		
		// The lists for the subset we are creating
		PrecalculatedOverlayList precalcOverlayList = new PrecalculatedOverlayList();
		
		// Loop through each index, and add to the output lists
		for(int i : intersectingIndices) {
		
			BoundingBox bbox = overlayList.getBBox(i);
			
			if (bbox.hasIntersection(bboxView)) {

				overlayList.assertZoomedExists();
				
				addedScaledIndexTo(
					i,
					bbox,
					bboxViewZoomed,
					zoomFactorNew,
					precalcOverlayList
				);
			}
		}
	
		// We update the zoomFactor. Processing until now relies on the old zoomfactor
		this.zoomFactor = zoomFactorNew;
		
		overlayList.assertSizesMatchSimple();
		
		// We create our subset
		return new OverlayPrecalculatedCache(
			precalcOverlayList,
			zoomFactor,
			dimEntireImage
		);			
		
	}
		
	public synchronized void addOverlays( ColoredOverlayCollection overlaysToAdd ) throws OperationFailedException {
		
		try {
			for( int i=0; i<overlaysToAdd.size(); i++ ) {
				
				Overlay ol = overlaysToAdd.get(i);
								
				ObjMaskWithProperties om = ol.createObjMask(overlayWriter, dimEntireImage, bvOut );
				PrecalcOverlay precalc = OverlayWriter.createPrecalc(overlayWriter, om, dimEntireImage);
				
				BoundingBox bbox = ol.bbox(overlayWriter, dimEntireImage);
				
				overlayList.add(
					ol,
					overlaysToAdd.getColor(i),
					precalc,
					bbox,
					null
				);
				
				if (rTree!=null) {
					// We add it under the ID of what we've just added
					rTree.add(overlayList.size()-1, bbox);
				}
				
				overlayList.assertSizesMatchSimple();
			}
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
		overlayList.assertSizesMatchSimple();
	}
	
	
	// Note that the order of items in indices is changed during processing
	public synchronized void removeOverlays( List<Integer> indices ) {

		// Sort indices in descending order, so that we can remove without changing the order of other items
		Collections.sort(indices, Collections.reverseOrder());
	
		for( int i : indices ) {
			// Remove each item
			overlayList.remove(i);
		}
		
		// We invalidate the rTree as our indices now change completely.
		// TODO we can make this more efficient by updating indices in the rTree (using a pointer object) rather than deleting it from scratch
		//  and recreating
		
		rTree = null;
		overlayList.assertSizesMatchSimple();

	}
	
	// Finds all the overlays at a given point
	@Override
	public OverlayCollection overlaysAt( Point3i pnt) {
		OverlayCollection out = new OverlayCollection();
		List<Integer> ids = rTree.contains(pnt);
		
		for( Integer id : ids ) {
			
			Overlay ol = overlayList.getOverlay(id);
			
			// Check if it's actually inside
			if (ol.isPointInside(overlayWriter, pnt)) {
				out.add( ol );
			}
		}
		return out;
	}
	
	@Override
	public synchronized ColoredOverlayCollection getOverlayCollection() {
		return overlayList.getOverlayCollection();
	}

	public final int size() {
		return getOverlayCollection().size();
	}

	public List<PrecalcOverlay> getGeneratedObjects() {
		return overlayList.getListGeneratedObjects();
	}

	public List<PrecalcOverlay> getGeneratedObjectsZoomed() {
		return overlayList.getListGeneratedObjectsZoomed();
	}
	
	private OverlayPrecalculatedCache(
		PrecalculatedOverlayList overlayList,
		double zoomFactor,
		ImageDim dim
	) {
		this.overlayList = overlayList;
		overlayList.assertListsSizeMatch();
		this.zoomFactor = zoomFactor;
		this.dimEntireImage = dim;
	}
	
	private void rebuildCache() throws CreateException {
		overlayList.rebuild(dimEntireImage, overlayWriter);
		zoomFactor = -1;
		rTree = null;
	}
	
	/**
	 * Gets a scaled mask if it can from the cache. Otherwise creates a new one
	 * 
	 * @param zoomFactorNew
	 * @param om
	 * @param ol
	 * @param i
	 * @param dimScaled
	 * @return NULL if rejected
	 * @throws OperationFailedException
	 */
	private PrecalcOverlay getOrCreateScaledMask( double zoomFactorNew, ObjMaskWithProperties om, Overlay ol, int i, ImageDim dimScaled ) throws OperationFailedException {
		overlayList.assertZoomedExists();
		if (zoomFactorNew==1) {
			// We can steal from the main object
			return overlayList.getPrecalc(i);
		}
		else if (zoomFactorNew==zoomFactor) {
			// Great, we can steal the object from the cache, if it's non null
			PrecalcOverlay omScaledCache = overlayList.getPrecalcZoomed(i);
			
			if (omScaledCache!=null) {
				return omScaledCache;
			}
		}
		overlayList.assertZoomedExists();
		assert(zoomFactorNew>0);
		try {
			
			ObjMaskWithProperties omScaledProps = ol.createScaledMask(
				overlayWriter,
				zoomFactorNew,
				om,
				ol,
				dimEntireImage,
				dimScaled,
				bvOut
			);
			
			// If the mask we make from the overlay has no pixels, then we reject it by returning NULL
			if (!omScaledProps.getMask().hasPixelsGreaterThan(0)) {
				return null;
			}
			
			overlayList.assertZoomedExists();
			// We precalculate this
			PrecalcOverlay precalc = OverlayWriter.createPrecalc(overlayWriter, omScaledProps, dimEntireImage);
			overlayList.assertZoomedExists();
			overlayList.setPrecalcZoomed(i, precalc);
			return precalc;
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}
	
	private void addedScaledIndexTo(
		int i,
		BoundingBox bbox,
		BoundingBox bboxViewZoomed,
		double zoomFactorNew,
		PrecalculatedOverlayList addTo
	) throws OperationFailedException {
				
		// Our main object
		PrecalcOverlay originalSize = overlayList.getPrecalcOverlay(i);
		
		RGBColor col = overlayList.getColor(i);
		Overlay ol = overlayList.getOverlay(i);
		
		// We scale our object, retrieving from the cache if we can
		PrecalcOverlay scaled = getOrCreateScaledMask( zoomFactorNew, originalSize.getFirst(), ol, i, dimScaled );

		// We check that our zoomed-version also has an intersection, as sometimes it doesn't
		if (scaled!=null && scaled.getFirst().getMask().getBoundingBox().hasIntersection(bboxViewZoomed)) {
			
			// Previously, we duplicated color here, now we don't
			addTo.add(ol, col, originalSize, bbox, scaled);
		}
	}
	
	private ImageDim createDimensionsScaled( double zoomFactorNew ) {
		// We create a scaled version of our dimensions
		ImageDim dimScaled = new ImageDim(dimEntireImage);
		dimScaled.scaleXYBy(
			new ScaleFactor(zoomFactorNew)
		);
		return dimScaled;
	}
}