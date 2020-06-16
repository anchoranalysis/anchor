package org.anchoranalysis.anchor.overlay.writer;

import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.objectmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 * Overlays with additional pre-calculations that make them quicker to draw onto a RGBStack
 * 
 * @author Owen Feehan
 *
 */
public abstract class PrecalcOverlay {

	private ObjMaskWithProperties first;		// Result of the Mark->ObjMaskWithProperties 	
	
	public PrecalcOverlay(ObjMaskWithProperties first) {
		super();
		this.first = first;
	}

	public ObjMaskWithProperties getFirst() {
		return first;
	}

	public abstract void writePrecalculatedMask(
		RGBStack stack,
		IDGetter<ObjMaskWithProperties> idGetter,
		IDGetter<ObjMaskWithProperties> colorIDGetter,
		int iter,
		ColorIndex colorIndex,
		BoundingBox bboxContainer
	) throws OperationFailedException;
	
}
