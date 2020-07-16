/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
/* (C)2020 */
package org.anchoranalysis.io.bean.object.writer;

import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

/**
 * Branches to two different writers depending on a binary condition.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class IfElse extends DrawObject {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private DrawObject whenTrue;

    @BeanField @Getter @Setter private DrawObject whenFalse;
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
    public PrecalcOverlay precalculate(ObjectWithProperties mask, ImageDimensions dim)
            throws CreateException {

        // We calculate both the TRUE and FALSE precalculations
        PrecalcOverlay precalcTrue = whenTrue.precalculate(mask, dim);
        PrecalcOverlay precalcFalse = whenFalse.precalculate(mask, dim);

        return new PrecalcOverlay(mask) {

            @Override
            public void writePrecalculatedMask(
                    RGBStack background,
                    ObjectDrawAttributes attributes,
                    int iteration,
                    BoundingBox restrictTo)
                    throws OperationFailedException {

                if (condition.isPresent()
                        && condition
                                .get()
                                .isTrue(mask, background, attributes.idFor(mask, iteration))) {
                    precalcTrue.writePrecalculatedMask(
                            background, attributes, iteration, restrictTo);
                } else {
                    precalcFalse.writePrecalculatedMask(
                            background, attributes, iteration, restrictTo);
                }
            }
        };
    }
}
