/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.generator.raster.object.rgb;

import io.vavr.control.Either;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.bean.color.generator.HSBColorSetGenerator;
import org.anchoranalysis.io.bean.object.writer.Outline;
import org.anchoranalysis.io.color.ColorIndexModulo;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.writer.ObjectDrawAttributes;

/**
 * Generates stacks of RGB images using a {@link DrawObject} to draw objects on a background.
 *
 * @author Owen Feehan
 */
public class DrawObjectsGenerator extends ObjectsOnRGBGenerator {

    /**
     * Creates generator without any element set
     *  
     * @param drawObject how to draw the object
     * @param colorIndex what determines the colors for succesive objects
     */
    public DrawObjectsGenerator(DrawObject drawObject, ColorIndex colorIndex) {
        this(
                drawObject,
                null, // No element yet to iterate
                null, // No background set yet
                new ObjectDrawAttributes(colorIndex));
    }

    private DrawObjectsGenerator(
            DrawObject drawObject,
            ObjectCollectionWithProperties objects,
            Either<Dimensions, DisplayStack> background) {
        this(drawObject, objects, background, defaultColorsFor(objects));
    }

    public DrawObjectsGenerator(
            DrawObject drawObject,
            ObjectCollectionWithProperties objects,
            Either<Dimensions, DisplayStack> background,
            ColorIndex colorIndex) {
        this(drawObject, objects, background, new ObjectDrawAttributes(colorIndex));
    }

    public DrawObjectsGenerator(
            DrawObject drawObject,
            ObjectCollectionWithProperties objects,
            Either<Dimensions, DisplayStack> background,
            ObjectDrawAttributes attributes) {
        super(drawObject, attributes, background);
        this.setIterableElement(objects);
    }
    
    /**
     * A generator that draws an outline around objects on a background using varied colors for the objects.
     * 
     * @param objects the objects
     * @param outlineWidth the width of the outline
     * @param background the background
     * @return the generator
     */
    public static DrawObjectsGenerator outlineVariedColors( ObjectCollection objects, int outlineWidth, DisplayStack background ) {
        return outlineVariedColors(objects, outlineWidth, Either.right(background));
    }
    
    /**
     * A generator that draws an outline around objects on a background using varied colors for the objects.
     * 
     * @param objects the objects
     * @param outlineWidth the width of the outline
     * @param background the background or dimensions for a background (drawn as all black)
     * @return the generator
     */
    public static DrawObjectsGenerator outlineVariedColors( ObjectCollection objects, int outlineWidth, Either<Dimensions, DisplayStack> background ) {
        return new DrawObjectsGenerator(
            new Outline(outlineWidth),
            new ObjectCollectionWithProperties(objects),
            background
        );
    }
    
    /**
     * A generator that draws an outline around objects on a background using a single color for all objects
     * 
     * @param objects the objects
     * @param outlineWidth the width of the outline
     * @param background the background or dimensions for a background (drawn as all black)
     * @param color the single color to use for all objects
     * @return the generator
     */
    public static DrawObjectsGenerator outlineSingleColor( ObjectCollection objects, int outlineWidth, DisplayStack background, RGBColor color ) {
        return new DrawObjectsGenerator(
            new Outline(outlineWidth),
            new ObjectCollectionWithProperties(objects),
            Either.right(background),
            singleColorIndex(color)
        );
    }

    @Override
    protected RGBStack generateBackground(Either<Dimensions, DisplayStack> background) {
        return background.fold(
                DrawObjectsGenerator::createEmptyStackFor, ConvertDisplayStackToRGB::convert);
    }

    @Override
    protected ObjectCollectionWithProperties generateMasks() {
        return getIterableElement();
    }

    private static ColorIndex defaultColorsFor(ObjectCollectionWithProperties objects) {
        return new HSBColorSetGenerator().generateColors(objects.size());
    }
    

    private static ColorIndex singleColorIndex(RGBColor color) {
        return new ColorIndexModulo(new ColorList(color));
    }
}
