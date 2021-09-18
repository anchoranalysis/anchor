/*-
 * #%L
 * anchor-annotation-io
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.annotation.io.assignment.generator;

import java.util.List;
import org.anchoranalysis.annotation.io.assignment.Assignment;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.bean.object.draw.Filled;
import org.anchoranalysis.image.io.bean.object.draw.IfElse;
import org.anchoranalysis.image.io.bean.object.draw.Outline;
import org.anchoranalysis.image.io.object.output.rgb.DrawObjectsGenerator;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.overlay.bean.DrawObject;
import lombok.AllArgsConstructor;

/**
 * Outlines or completely fills in each {@link ObjectMask} with a color.
 * 
 * <p> The color depending on whether it is paired or unpaired in an {@link Assignment}.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class DrawColoredObjects {

    /** The background, which will feature in both left and right panes. */
    private final DisplayStack background;
    
    /** Whether to flatten (maximum intensity projection) in the z-dimension. */
    private final boolean flatten;
    
    /** How many pixels should the outline be around objects. */
    private final int outlineWidth;
    
    /** 
     * Creates an image with the objects colored, as indicated in the class description.
     * 
     * @param assignment the assignment to draw.
     * @param left if true, draws the <i>left</i> objects from the assignment, otherwise the <i>right</i> objects.
     * @param colorPool the colors to use for drawing objects.
     * @return the stack with the colored objects.
     * @throws OutputWriteFailedException if the image cannot be created.
     */
    public Stack createObjectsImage(Assignment<ObjectMask> assignment, boolean left, AssignmentColorPool colorPool)
            throws OutputWriteFailedException {
        try {
            return createObjectsImage(
                    assignment.paired(left), colorPool, assignment.unassigned(left));
        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    private Stack createObjectsImage(
            List<ObjectMask> matchedObjects,
            AssignmentColorPool colorPool,
            final List<ObjectMask> otherObjects)
            throws OutputWriteFailedException, OperationFailedException {
        ObjectCollection objects = ObjectCollectionFactory.of(matchedObjects, otherObjects);
        DrawObjectsGenerator drawObjects =
                createGenerator(
                        otherObjects,
                        colorPool.createColors(otherObjects.size()),
                        colorPool.isDifferentColorsForPairs());
        return drawObjects.transform(new ObjectCollectionWithProperties(objects));
    }

    /** Creates the generator that draws on objects on a background. */
    private DrawObjectsGenerator createGenerator(
            List<ObjectMask> otherObjects, ColorList colors, boolean differentColorsForMatches) {

        DrawObject outlineWriter = new Outline(outlineWidth, !flatten);

        if (differentColorsForMatches) {
            DrawObject conditionalWriter = createConditionalWriter(otherObjects, outlineWriter);
            return createGenerator(conditionalWriter, colors);
        } else {
            return createGenerator(outlineWriter, colors);
        }
    }
    
    private DrawObjectsGenerator createGenerator(DrawObject drawObject, ColorList colors) {
        return DrawObjectsGenerator.withBackgroundAndColors(drawObject, background, colors);
    }

    private static DrawObject createConditionalWriter(List<ObjectMask> otherObjects, DrawObject writer) {
        return new IfElse(
                (ObjectWithProperties object, RGBStack stack, int id) ->
                        otherObjects.contains(object.withoutProperties()),
                writer,
                new Filled());
    }
}
