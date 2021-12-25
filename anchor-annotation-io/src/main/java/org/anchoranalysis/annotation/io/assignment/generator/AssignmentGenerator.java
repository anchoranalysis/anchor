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
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.image.bean.provider.stack.Arrange;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.bean.stack.combine.StackProviderWithLabel;
import org.anchoranalysis.image.io.stack.input.TileRasters;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributes;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributesFactory;
import org.anchoranalysis.image.io.stack.output.generator.RasterGeneratorSelectFormat;
import org.anchoranalysis.image.io.stack.output.generator.StackGenerator;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Outputs a raster showing an {@link Assignment} on a background.
 *
 * <p>Specifically two tiled backgrounds appear, one left, and one right, and objects are colored on
 * left and right panels, to indicate how they appear in the assignment.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class AssignmentGenerator extends RasterGeneratorSelectFormat<Assignment<ObjectMask>> {

    // START REQUIRED ARGUMENTS
    /** How to color objects in the image. */
    private final DrawColoredObjects objectDrawer;

    /** Creates a {@link AssignmentColorPool} given a count of paired objects. */
    private final IntFunction<AssignmentColorPool> colorPoolCreator;

    /** Names to assign respectively to the left and right images. */
    private final Tuple2<String, String> names;

    /**
     * Whether to append a count of unassigned objects (in parantheses) to the name of each image.
     */
    private final boolean appendUnassignedCount;
    // END REQUIRED ARGUMENTS

    /** A delegated generator. */
    private StackGenerator generator =
            new StackGenerator(true, Optional.of("assignmentComparison"), false);

    @Override
    public Stack transform(Assignment<ObjectMask> element) throws OutputWriteFailedException {

        AssignmentColorPool colorPool = colorPoolCreator.apply(element.numberPaired());

        Arrange stackProvider =
                createTiledStackProvider(
                        objectDrawer.createObjectsImage(element, true, colorPool),
                        objectDrawer.createObjectsImage(element, false, colorPool),
                        createLabel(element, true),
                        createLabel(element, false));
        try {
            return generator.transform(stackProvider.get());

        } catch (ProvisionFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", "assignment"));
    }

    @Override
    public StackWriteAttributes guaranteedImageAttributes() {
        return StackWriteAttributesFactory.rgbMaybe3D(false);
    }

    private static Arrange createTiledStackProvider(
            Stack stackLeft, Stack stackRight, String nameLeft, String nameRight) {
        List<StackProviderWithLabel> listProvider = new ArrayList<>();
        listProvider.add(new StackProviderWithLabel(stackLeft, nameLeft));
        listProvider.add(new StackProviderWithLabel(stackRight, nameRight));

        return TileRasters.createStackProvider(listProvider, 2, false, false, true);
    }

    /** Creates a label with the name (and optionally numeric count) for a particular image. */
    private String createLabel(Assignment<ObjectMask> assignment, boolean left) {
        String name = left ? names._1() : names._2();
        return maybeAppendUnassignedCount(appendUnassignedCount, name, assignment, true);
    }

    /** Appends in the count of unassigned objects (in parentheses), if configured. */
    private static String maybeAppendUnassignedCount(
            boolean doAppend, String mainString, Assignment<ObjectMask> assignment, boolean left) {
        if (doAppend) {
            return String.format("%s (%d)", mainString, assignment.numberUnassigned(left));
        } else {
            return mainString;
        }
    }
}
