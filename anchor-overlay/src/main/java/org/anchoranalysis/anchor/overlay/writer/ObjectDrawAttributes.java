/* (C)2020 */
package org.anchoranalysis.anchor.overlay.writer;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

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
