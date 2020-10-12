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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.annotation.io.assignment.Assignment;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.bean.provider.stack.ArrangeRaster;
import org.anchoranalysis.image.io.bean.object.draw.Filled;
import org.anchoranalysis.image.io.bean.object.draw.IfElse;
import org.anchoranalysis.image.io.bean.object.draw.Outline;
import org.anchoranalysis.image.io.bean.stack.provider.StackProviderWithLabel;
import org.anchoranalysis.image.io.generator.raster.RasterGeneratorWithElement;
import org.anchoranalysis.image.io.generator.raster.StackGenerator;
import org.anchoranalysis.image.io.generator.raster.object.rgb.DrawObjectsGenerator;
import org.anchoranalysis.image.io.stack.StackWriteOptions;
import org.anchoranalysis.image.io.stack.TileRasters;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.factory.ObjectCollectionFactory;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
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
public class AssignmentGenerator extends RasterGeneratorWithElement<Assignment> {

    private DisplayStack background;
    private StackGenerator delegate;
    private boolean flatten;

    private ColorPool colorPool;

    /** How many pixels should the outline be around objects */
    @Getter @Setter private int outlineWidth = 1;

    /** Name printed in left-panel. */
    @Getter @Setter private String leftName = "annotation";

    /** Name printed in right-panel. */
    @Getter @Setter private String rightName = "result";

    /**
     * Create with a background and assignment
     *
     * @param background the background, which will feature in both left and right panes
     * @param assignment the assignment
     * @param colorPool
     * @param flatten whether to flatten (maximum intensity projection) in the z-dimension
     */
    AssignmentGenerator(
            DisplayStack background, Assignment assignment, ColorPool colorPool, boolean flatten) {
        super();
        this.background = background;
        this.flatten = flatten;
        this.colorPool = colorPool;

        assignElement(assignment);

        delegate = new StackGenerator(true, "assignmentComparison", false);
    }

    @Override
    public boolean isRGB() {
        return true;
    }

    @Override
    public Stack transform() throws OutputWriteFailedException {

        Assignment assignment = getElement();

        ArrangeRaster stackProvider =
                createTiledStackProvider(
                        createRGBOutlineStack(true, assignment),
                        createRGBOutlineStack(false, assignment),
                        leftName,
                        rightName);

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
    public StackWriteOptions writeOptions() {
        return StackWriteOptions.rgbMaybe3D();
    }

    private static ArrangeRaster createTiledStackProvider(
            Stack stackLeft, Stack stackRight, String nameLeft, String nameRight) {
        List<StackProviderWithLabel> listProvider = new ArrayList<>();
        listProvider.add(new StackProviderWithLabel(stackLeft, nameLeft));
        listProvider.add(new StackProviderWithLabel(stackRight, nameRight));

        return TileRasters.createStackProvider(listProvider, 2, false, false, true);
    }

    private Stack createRGBOutlineStack(boolean left, Assignment assignment)
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
        return createGenerator(
                        otherObjects,
                        colorPool.createColors(otherObjects.size()),
                        ObjectCollectionFactory.of(matchedObjects, otherObjects))
                .transform();
    }

    private DrawObjectsGenerator createGenerator(
            List<ObjectMask> otherObjects, ColorList cols, ObjectCollection objects) {

        DrawObject outlineWriter = createOutlineWriter();

        if (colorPool.isDifferentColorsForMatches()) {
            DrawObject conditionalWriter = createConditionalWriter(otherObjects, outlineWriter);
            return createGenerator(conditionalWriter, cols, objects);
        } else {
            return createGenerator(outlineWriter, cols, objects);
        }
    }

    private DrawObjectsGenerator createGenerator(
            DrawObject drawObject, ColorList colors, ObjectCollection objects) {
        return DrawObjectsGenerator.withBackgroundAndColors(
                drawObject, new ObjectCollectionWithProperties(objects), background, colors);
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
}
