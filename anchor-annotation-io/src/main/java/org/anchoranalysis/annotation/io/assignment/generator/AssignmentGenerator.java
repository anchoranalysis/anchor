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

import io.vavr.control.Either;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.annotation.io.assignment.Assignment;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.bean.provider.stack.ArrangeRaster;
import org.anchoranalysis.image.io.bean.stack.StackProviderWithLabel;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.StackGenerator;
import org.anchoranalysis.image.io.generator.raster.object.rgb.DrawObjectsGenerator;
import org.anchoranalysis.image.io.stack.TileRasters;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.bean.object.writer.Filled;
import org.anchoranalysis.io.bean.object.writer.IfElse;
import org.anchoranalysis.io.bean.object.writer.Outline;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class AssignmentGenerator extends RasterGenerator {

    private DisplayStack background;
    private Assignment assignment;
    private StackGenerator delegate;
    private boolean mipOutline;

    private ColorPool colorPool;

    @Getter @Setter private int outlineWidth = 1;

    @Getter @Setter private String leftName = "annotation";

    @Getter @Setter private String rightName = "result";

    /**
     * @param background
     * @param assignment
     * @param colorSetGeneratorPaired
     * @param colorSetGeneratorUnpaired
     * @param mipOutline
     * @param factory
     * @param replaceMatchesWithSolids if TRUE, then any matching objects are displayed as solids,
     *     rather than outlines. if FALSE, all objects are displayed as outlines.
     */
    AssignmentGenerator(
            DisplayStack background,
            Assignment assignment,
            ColorPool colorPool,
            boolean mipOutline) {
        super();
        this.background = background;
        this.assignment = assignment;
        this.mipOutline = mipOutline;
        this.colorPool = colorPool;

        delegate = new StackGenerator(true, "assignmentComparison");
    }

    @Override
    public boolean isRGB() {
        return true;
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {

        ArrangeRaster stackProvider =
                createTiledStackProvider(
                        createRGBOutlineStack(true),
                        createRGBOutlineStack(false),
                        leftName,
                        rightName);

        try {
            Stack combined = stackProvider.create();
            delegate.setIterableElement(combined);
            return delegate.generate();

        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    private static ArrangeRaster createTiledStackProvider(
            Stack stackLeft, Stack stackRight, String nameLeft, String nameRight) {
        List<StackProviderWithLabel> listProvider = new ArrayList<>();
        listProvider.add(new StackProviderWithLabel(stackLeft, nameLeft));
        listProvider.add(new StackProviderWithLabel(stackRight, nameRight));

        return TileRasters.createStackProvider(listProvider, 2, false, false, true);
    }

    private Stack createRGBOutlineStack(boolean left) throws OutputWriteFailedException {
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
                .generate();
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
            DrawObject drawObject, ColorList cols, ObjectCollection objects) {
        return new DrawObjectsGenerator(
                drawObject,
                new ObjectCollectionWithProperties(objects),
                Either.right(background),
                cols);
    }

    private DrawObject createConditionalWriter(List<ObjectMask> otherObjects, DrawObject writer) {
        return new IfElse(
                (ObjectWithProperties object, RGBStack stack, int id) ->
                        otherObjects.contains(object.withoutProperties()),
                writer,
                new Filled());
    }

    private DrawObject createOutlineWriter() {
        return new Outline(outlineWidth, !mipOutline);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", "assignment"));
    }
}
