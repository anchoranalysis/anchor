/*-
 * #%L
 * anchor-annotation-io
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

package org.anchoranalysis.annotation.io.assignment.generator;

import io.vavr.Tuple2;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.annotation.io.assignment.Assignment;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.bean.provider.stack.ArrangeRaster;
import org.anchoranalysis.image.core.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.bean.object.draw.Filled;
import org.anchoranalysis.image.io.bean.object.draw.IfElse;
import org.anchoranalysis.image.io.bean.object.draw.Outline;
import org.anchoranalysis.image.io.bean.stack.combine.StackProviderWithLabel;
import org.anchoranalysis.image.io.object.output.rgb.DrawObjectsGenerator;
import org.anchoranalysis.image.io.stack.input.TileRasters;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributes;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributesFactory;
import org.anchoranalysis.image.io.stack.output.generator.RasterGeneratorSelectFormat;
import org.anchoranalysis.image.io.stack.output.generator.StackGenerator;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.overlay.bean.DrawObject;

/**
 * Outputs a raster showing an {@link Assignment} on a background.
 *
 * <p>Specifically two tiled backgrounds appear, one left, and one right, and objects are colored on
 * left and right panels, to indicate how they appear in the assignment.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class AssignmentGenerator extends RasterGeneratorSelectFormat<Assignment> {

    // START REQUIRED ARGUMENTS
    /** The background, which will feature in both left and right panes. */
    private final DisplayStack background;

    private final IntFunction<ColorPool> colorPoolCreator;

    /** Whether to flatten (maximum intensity projection) in the z-dimension. */
    private final boolean flatten;

    private final Tuple2<String, String> names;

    private final boolean appendNumberBrackets;

    /** How many pixels should the outline be around objects */
    private final int outlineWidth;
    // END REQUIRED ARGUMENTS

    private StackGenerator delegate =
            new StackGenerator(true, Optional.of("assignmentComparison"), false);

    @Override
    public Stack transform(Assignment element) throws OutputWriteFailedException {

        ColorPool colorPool = colorPoolCreator.apply(element.numberPaired());

        ArrangeRaster stackProvider =
                createTiledStackProvider(
                        createRGBOutlineStack(true, element, colorPool),
                        createRGBOutlineStack(false, element, colorPool),
                        createLabel(element, true),
                        createLabel(element, false));
        try {
            return delegate.transform(stackProvider.create());

        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", "assignment"));
    }

    @Override
    public StackWriteAttributes guaranteedImageAttributes() {
        return StackWriteAttributesFactory.rgbMaybe3D();
    }

    private static ArrangeRaster createTiledStackProvider(
            Stack stackLeft, Stack stackRight, String nameLeft, String nameRight) {
        List<StackProviderWithLabel> listProvider = new ArrayList<>();
        listProvider.add(new StackProviderWithLabel(stackLeft, nameLeft));
        listProvider.add(new StackProviderWithLabel(stackRight, nameRight));

        return TileRasters.createStackProvider(listProvider, 2, false, false, true);
    }

    private Stack createRGBOutlineStack(boolean left, Assignment assignment, ColorPool colorPool)
            throws OutputWriteFailedException {
        try {
            return createRGBOutlineStack(
                    assignment.getListPaired(left), colorPool, assignment.getListUnassigned(left));
        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    private Stack createRGBOutlineStack(
            List<ObjectMask> matchedObjects,
            ColorPool colorPool,
            final List<ObjectMask> otherObjects)
            throws OutputWriteFailedException, OperationFailedException {
        ObjectCollection objects = ObjectCollectionFactory.of(matchedObjects, otherObjects);
        DrawObjectsGenerator generator =
                createGenerator(
                        otherObjects,
                        colorPool.createColors(otherObjects.size()),
                        colorPool.isDifferentColorsForMatches());
        return generator.transform(new ObjectCollectionWithProperties(objects));
    }

    private DrawObjectsGenerator createGenerator(
            List<ObjectMask> otherObjects, ColorList colors, boolean differentColorsForMatches) {

        DrawObject outlineWriter = createOutlineWriter();

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

    private DrawObject createConditionalWriter(List<ObjectMask> otherObjects, DrawObject writer) {
        return new IfElse(
                (ObjectWithProperties object, RGBStack stack, int id) ->
                        otherObjects.contains(object.withoutProperties()),
                writer,
                new Filled());
    }

    private DrawObject createOutlineWriter() {
        return new Outline(outlineWidth, !flatten);
    }

    private String createLabel(Assignment assignment, boolean left) {
        String name = left ? names._1() : names._2();
        return maybeAppendNumber(appendNumberBrackets, name, assignment, true);
    }

    private static String maybeAppendNumber(
            boolean doAppend, String mainString, Assignment assignment, boolean left) {
        if (doAppend) {
            return String.format("%s (%d)", mainString, assignment.numberUnassigned(left));
        } else {
            return mainString;
        }
    }
}
