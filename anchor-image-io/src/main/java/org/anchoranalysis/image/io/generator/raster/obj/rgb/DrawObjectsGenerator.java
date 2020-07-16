/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster.obj.rgb;

import java.util.Optional;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 * Generates stacks of RGB unages using a {@link DrawObject} to draw objects on a background.
 *
 * @param T pre-calculation type for the {@link DrawObject}
 * @author Owen Feehan
 */
public class DrawObjectsGenerator extends ObjectsOnRGBGenerator {

    public DrawObjectsGenerator(DrawObject drawObject, ColorIndex colorIndex) {
        this(drawObject, Optional.empty(), colorIndex);
    }

    public DrawObjectsGenerator(
            DrawObject drawObject, Optional<DisplayStack> background, ColorIndex colorIndex) {
        this(
                drawObject,
                null, // No element yet to iterate
                background,
                new ObjectDrawAttributes(colorIndex));
    }

    public DrawObjectsGenerator(
            DrawObject drawObject,
            ObjectCollectionWithProperties masks,
            Optional<DisplayStack> background,
            ColorIndex colorIndex) {
        this(drawObject, masks, background, new ObjectDrawAttributes(colorIndex));
    }

    public DrawObjectsGenerator(
            DrawObject drawObject,
            ObjectCollectionWithProperties masks,
            Optional<DisplayStack> background,
            ObjectDrawAttributes attributes) {
        super(drawObject, attributes, background);
        this.setIterableElement(masks);
    }

    @Override
    protected RGBStack generateBackground(DisplayStack background) {
        return ConvertDisplayStackToRGB.convert(background);
    }

    @Override
    protected ObjectCollectionWithProperties generateMasks() {
        return getIterableElement();
    }
}
