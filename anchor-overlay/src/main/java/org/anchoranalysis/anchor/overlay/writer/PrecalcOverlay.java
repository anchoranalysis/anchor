package org.anchoranalysis.anchor.overlay.writer;

import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 * Overlays with additional pre-calculations that make them quicker to draw onto a RGBStack
 * 
 * @author Owen Feehan
 *
 */
public abstract class PrecalcOverlay {

	private ObjectWithProperties first;		// Result of the Mark->ObjMaskWithProperties 	
	
	public PrecalcOverlay(ObjectWithProperties first) {
		super();
		this.first = first;
	}

	public ObjectWithProperties getFirst() {
		return first;
	}

	public abstract void writePrecalculatedMask(
		RGBStack stack,
		IDGetter<ObjectWithProperties> idGetter,
		IDGetter<ObjectWithProperties> colorIDGetter,
		int iter,
		ColorIndex colorIndex,
		BoundingBox bboxContainer
	) throws OperationFailedException;
	
}
