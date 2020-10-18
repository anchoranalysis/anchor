/*-
 * #%L
 * anchor-overlay
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

package org.anchoranalysis.overlay.writer;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;

/**
 * A means of extracting attributes associated with a particular object (e.g. color, ID) when
 * drawing
 */
@Value
@AllArgsConstructor
public class ObjectDrawAttributes {

    /** Colors for a given index */
    private final ColorIndex colorIndex;

    /** Gets a unique ID associated with the object */
    private final IDGetter<ObjectWithProperties> idGetter;

    /** Gets a color ID associated with the object */
    private final IDGetter<ObjectWithProperties> colorIDGetter;

    /**
     * Creates with a specific color-index and uses the iteration-index as both the ID and color-ID
     *
     * @param colorIndex color-index
     */
    public ObjectDrawAttributes(ColorIndex colorIndex) {
        this.colorIndex = colorIndex;
        this.idGetter = new IDGetterIter<>();
        this.colorIDGetter = new IDGetterIter<>();
    }

    /**
     * A color for a particular object
     *
     * @param object the object
     * @param index the index of the object (unique incrementing ID for each object in a collection)
     * @return the color
     */
    public RGBColor colorFor(ObjectWithProperties object, int index) {
        return colorIndex.get(colorIDGetter.getID(object, index));
    }

    /**
     * ID for a particular object
     *
     * @param object the object
     * @param index the index of the object (unique incrementing ID for each object in a collection)
     * @return the id
     */
    public int idFor(ObjectWithProperties object, int index) {
        return idGetter.getID(object, index);
    }
}
