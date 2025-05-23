/*-
 * #%L
 * anchor-image-bean
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
package org.anchoranalysis.image.bean.spatial.direction;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.spatial.axis.AxisConversionException;
import org.anchoranalysis.spatial.axis.AxisConverter;
import org.anchoranalysis.spatial.orientation.DirectionVector;

/**
 * Creates a {@link DirectionVector} that is a unit-vector along a particular image-axis.
 *
 * @author Owen
 */
public class AxisAlignedUnitVector extends DirectionVectorBean {

    // START BEAN PROPERTIES
    /** Which axis the unit-vector will align with. {@code x} or {@code y} or {@code z} */
    @BeanField @Getter @Setter private String axis;

    // END BEAN PROPRERTIES

    @Override
    public DirectionVector createVector() throws CreateException {
        try {
            return new DirectionVector(AxisConverter.createFromString(axis));
        } catch (AxisConversionException e) {
            throw new CreateException(e);
        }
    }
}
