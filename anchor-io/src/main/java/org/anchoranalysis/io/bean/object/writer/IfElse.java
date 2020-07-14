package org.anchoranalysis.io.bean.object.writer;

import java.util.Optional;

import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Branches to two different writers depending on a binary condition.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor
public class IfElse extends DrawObject {

	// START BEAN PROPERTIES
	@BeanField @Getter @Setter
	private DrawObject whenTrue;
	
	@BeanField @Getter @Setter
	private DrawObject whenFalse;
	// END BEAN PROPERTIES
	
	private Optional<Condition> condition = Optional.empty();
	
	@FunctionalInterface
	public interface Condition {
		boolean isTrue(ObjectWithProperties mask, RGBStack stack, int id);
	}
	
	public IfElse(Condition condition, DrawObject trueWriter, DrawObject falseWriter) {
		super();
		this.condition = Optional.of(condition);
		this.whenTrue = trueWriter;
		this.whenFalse = falseWriter;
	}

	@Override
	public PrecalcOverlay precalculate(ObjectWithProperties mask,
			ImageDimensions dim) throws CreateException {
		
		// We calculate both the TRUE and FALSE precalculations
		PrecalcOverlay precalcTrue = whenTrue.precalculate(mask, dim);
		PrecalcOverlay precalcFalse = whenFalse.precalculate(mask, dim);
				
		return new PrecalcOverlay(mask) {

			@Override
			public void writePrecalculatedMask(RGBStack background, ObjectDrawAttributes attributes, int iteration,
					BoundingBox restrictTo) throws OperationFailedException {
				
				if (condition.isPresent() && condition.get().isTrue(
					mask,
					background,
					attributes.idFor(mask, iteration)
				)) {
					precalcTrue.writePrecalculatedMask(background, attributes, iteration, restrictTo);
				} else {
					precalcFalse.writePrecalculatedMask(background, attributes, iteration, restrictTo);
				}
			}
		
		};
	}
}
