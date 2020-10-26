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

package org.anchoranalysis.image.io.object.output.rgb;

import io.vavr.control.Either;
import org.anchoranalysis.bean.shared.color.scheme.HSB;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorIndexModulo;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.io.bean.object.draw.Outline;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.writer.ObjectDrawAttributes;

/**
 * Generates stacks of RGB images using a {@link DrawObject} to draw objects on a background.
 *
 * @author Owen Feehan
 */
public class DrawObjectsGenerator extends ObjectsAsRGBGenerator {

    private DrawObjectsGenerator(
            DrawObject drawObject,
            Either<Dimensions, DisplayStack> background,
            ColorIndex colorIndex) {
        super(drawObject, new ObjectDrawAttributes(colorIndex), background);
    }

    /**
     * A generator that draws an object in a particular way with particular colors and background.
     *
     * @param drawObject how to draw the object
     * @param background the background
     * @param colorIndex the colors
     * @return the generator
     */
    public static DrawObjectsGenerator withBackgroundAndColors(
            DrawObject drawObject, DisplayStack background, ColorIndex colorIndex) {
        return new DrawObjectsGenerator(drawObject, Either.right(background), colorIndex);
    }

    /**
     * A generator that draws an outline around objects on a background using varied colors for the
     * objects.
     *
     * @param outlineWidth the width of the outline
     * @param background the background
     * @return the generator
     */
    public static DrawObjectsGenerator outlineVariedColors(
            int numberColors, int outlineWidth, DisplayStack background) {
        return outlineVariedColors(numberColors, outlineWidth, Either.right(background));
    }

    /**
     * A generator that draws an outline around objects using a particular color-index.
     *
     * @param outlineWidth the width of the outline
     * @param colorIndex the color-index
     * @return the generator
     */
    public static DrawObjectsGenerator outlineWithColorIndex(
            int outlineWidth, ColorIndex colorIndex) {
        return new DrawObjectsGenerator(new Outline(outlineWidth), null, colorIndex);
    }

    /**
     * A generator that draws an outline around objects on a background using varied colors for the
     * objects.
     *
     * @param outlineWidth the width of the outline
     * @param background the background or dimensions for a background (drawn as all black)
     * @return the generator
     */
    public static DrawObjectsGenerator outlineVariedColors(
            int numberColors, int outlineWidth, Either<Dimensions, DisplayStack> background) {
        return new DrawObjectsGenerator(
                new Outline(outlineWidth), background, defaultColorsFor(numberColors));
    }

    /**
     * A generator that draws an outline around objects on a background using a single color for all
     * objects
     *
     * @param outlineWidth the width of the outline
     * @param background the background or dimensions for a background (drawn as all black)
     * @param color the single color to use for all objects
     * @return the generator
     */
    public static DrawObjectsGenerator outlineSingleColor(
            int outlineWidth, DisplayStack background, RGBColor color) {
        return new DrawObjectsGenerator(
                new Outline(outlineWidth), Either.right(background), singleColorIndex(color));
    }

    @Override
    protected RGBStack generateBackground(
            ObjectCollectionWithProperties element, Either<Dimensions, DisplayStack> background) {
        return background.fold(
                DrawObjectsGenerator::createEmptyStackFor, ConvertDisplayStackToRGB::convert);
    }

    @Override
    protected ObjectCollectionWithProperties generateMasks(ObjectCollectionWithProperties element) {
        return element;
    }

    private static ColorIndex defaultColorsFor(int size) {
        return new HSB().createList(size);
    }

    private static ColorIndex singleColorIndex(RGBColor color) {
        return new ColorIndexModulo(new ColorList(color));
    }
}
