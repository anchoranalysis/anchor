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


import java.util.List;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.anchor.overlay.writer.OverlayWriter;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 *  Contains set of colored-overlays that is bound with various additional objects provided
 *  by the environment, needed to write the overlays on actual RGB-stacks (with invariant dim)
 *  
 * @author Owen Feehan
 *
 */
public class BoundColoredOverlayCollection {

	
	private OverlayWriter maskWriter;
	
	private IDGetter<Overlay> idGetter;
	
	private ImageDim dimEntireImage;
	
	// The current overlay, with additional cached objects
	private OverlayPrecalculatedCache cache;
		
	public BoundColoredOverlayCollection(OverlayWriter maskWriter, IDGetter<Overlay> idGetter, ImageDim dim) throws CreateException {
		super();
		this.maskWriter = maskWriter;
		this.idGetter = idGetter;
		this.dimEntireImage = dim;
		this.cache = new OverlayPrecalculatedCache( new ColoredOverlayCollection(), dimEntireImage, maskWriter);
	}

	public void updateMaskWriter( OverlayWriter maskWriter ) throws SetOperationFailedException {
		this.maskWriter = maskWriter;
		this.cache.setMaskWriter(maskWriter);
	}
	
	
	public void addOverlays( ColoredOverlayCollection oc ) throws OperationFailedException {
		this.cache.addOverlays( oc );
	}
	
	// Note that the order of items in indices is changed during processing
	public void removeOverlays( List<Integer> indices ) {
		this.cache.removeOverlays( indices );
	}
	
	
	
	public void setOverlayCollection( ColoredOverlayCollection oc ) throws SetOperationFailedException {
		this.cache.setOverlayCollection( oc );
	}
	
	public void drawRGB( RGBStack stack, BoundingBox bbox, double zoomFactor ) throws OperationFailedException {

		//System.out.printf("Draw on DS: %s with zoom %f%n", bbox.toString(), zoomFactor );
		// Draw the coloredcfg
		//CfgGenerator cfgGenerator = new CfgGenerator();
		
		
		// Create a containing bounding box with the zoom
		BoundingBox container = createZoomedContainer( bbox, zoomFactor, stack.getDimensions().getExtnt() );
		
		OverlayPrecalculatedCache marksWithinView = cache.subsetWithinView( bbox, container, zoomFactor );

//		System.out.printf("Found %d marks within view for bbox %s with zoom %f %n", marksWithinView.size(), bbox, zoomFactor );
		
//		System.out.println("START: Listing all objects\n");
//		for( int i=0; i< marksWithinView.getGeneratedObjectsZoomed().size(); i++ ) {
//			CfgPrecalc cp = marksWithinView.getGeneratedObjects().get(i);
//			CfgPrecalc cp2 = marksWithinView.getGeneratedObjectsZoomed().get(i);
//			RGBColor col = marksWithinView.().getColorList().get(i);
//			System.out.printf("OM First BBox=%s  Second BBox=%s   Color=%s\n", cp.getFirst().getBoundingBox(), cp2.getFirst().getBoundingBox(), col );
//		}
//		System.out.println("END: Listing all objects\n");
		
		
		//maskWriter.writeMasks(masks, cfg, dim, background, idGetter, bboxContainer, zoomFactor);
		maskWriter.writePrecalculatedOverlays(
			marksWithinView.getGeneratedObjectsZoomed(),
			marksWithinView.getOverlayCollection(),
			dimEntireImage,
			stack,
			idGetter,
			new IDGetterIter<ObjMaskWithProperties>(),
			container
		);
		//TempBoundOutputManager.instance().getBoundOutputManager().getWriterAlwaysAllowed().write("test", new StackGenerator(stack.asStack(),true,"fdfd",new ChnlFactoryMulti()) );
	}
	
	private static BoundingBox createZoomedContainer( BoundingBox bbox, double zoomFactor, Extent stackExtnt ) {
		Point3i crnrMin = new Point3i( bbox.getCrnrMin() );
		crnrMin.scaleXY(zoomFactor);
		//System.out.printf("Zooming container %s with %f and %s to %s and %s\n", bbox, zoomFactor, stackExtnt, crnrMin, stackExtnt );
		return new BoundingBox(crnrMin, new Extent(stackExtnt) );
	}
	
	// Note the overlay do not actually have to be contained in the OverlayCollection for this to work
	//  it will work with any overlay.... simply using the settings from the bound maskWriter
	public List<BoundingBox> bboxList( ColoredOverlayCollection oc ) {
		return oc.bboxList(maskWriter, dimEntireImage);
	}
	
	public List<BoundingBox> bboxList( OverlayCollection oc ) {
		return oc.bboxList(maskWriter, dimEntireImage);
	}

	public synchronized OverlayPrecalculatedCache getPrecalculatedCache() {
		return cache;
	}
	
	
}
