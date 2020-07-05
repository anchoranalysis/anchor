package org.anchoranalysis.io.bean.objmask.writer;

import java.util.Optional;

/*
 * #%L
 * anchor-io
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



import org.anchoranalysis.anchor.overlay.bean.objmask.writer.ObjMaskWriter;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IfElseWriter extends ObjMaskWriter {

	// START BEAN PROPERTIES
	@BeanField
	private ObjMaskWriter trueWriter;
	
	@BeanField
	private ObjMaskWriter falseWriter;
	// END BEAN PROPERTIES
	
	private Optional<Condition> condition = Optional.empty();
	
	@FunctionalInterface
	public interface Condition {
		boolean isTrue(ObjectWithProperties mask, RGBStack stack, int id);
	}
	
	public IfElseWriter(Condition condition, ObjMaskWriter trueWriter, ObjMaskWriter falseWriter) {
		super();
		this.condition = Optional.of(condition);
		this.trueWriter = trueWriter;
		this.falseWriter = falseWriter;
	}

	public ObjMaskWriter getTrueWriter() {
		return trueWriter;
	}

	public void setTrueWriter(ObjMaskWriter trueWriter) {
		this.trueWriter = (ObjMaskWriter) trueWriter;
	}

	public ObjMaskWriter getFalseWriter() {
		return falseWriter;
	}

	public void setFalseWriter(ObjMaskWriter falseWriter) {
		this.falseWriter = (ObjMaskWriter) falseWriter;
	}

	@Override
	public PrecalcOverlay precalculate(ObjectWithProperties mask,
			ImageDimensions dim) throws CreateException {
		
		// We calculate both the TRUE and FALSE precalculations
		PrecalcOverlay precalcTrue = trueWriter.precalculate(mask, dim);
		PrecalcOverlay precalcFalse = falseWriter.precalculate(mask, dim);
				
		return new PrecalcOverlay(mask) {

			@Override
			public void writePrecalculatedMask(RGBStack stack, IDGetter<ObjectWithProperties> idGetter,
					IDGetter<ObjectWithProperties> colorIDGetter, int iter, ColorIndex colorIndex,
					BoundingBox bboxContainer) throws OperationFailedException {
				if (condition.isPresent() && condition.get().isTrue(mask, stack, idGetter.getID(mask, iter))) {
					precalcTrue.writePrecalculatedMask(stack, idGetter, colorIDGetter, iter, colorIndex, bboxContainer);
				} else {
					precalcFalse.writePrecalculatedMask(stack, idGetter, colorIDGetter, iter, colorIndex, bboxContainer);
				}
			}
			
		};
	}
}
