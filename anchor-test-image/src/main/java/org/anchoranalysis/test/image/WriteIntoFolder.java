/*-
 * #%L
 * anchor-test-image
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
package org.anchoranalysis.test.image;

import io.vavr.control.Either;
import java.nio.file.Path;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.generator.raster.DisplayStackGenerator;
import org.anchoranalysis.image.io.generator.raster.object.collection.ObjectAsMaskGenerator;
import org.anchoranalysis.image.io.generator.raster.object.rgb.DrawObjectsGenerator;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.io.generator.collection.CollectionGenerator;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.spatial.box.BoundedList;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.test.image.io.OutputterFixture;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * JUnit rule for writing one or more stacks/objects/channels into a temporary-folder during testing
 *
 * <p>Any checked-exceptions thrown during writing stacks are converted into run-time exceptions, to
 * make it easy to temporarily use this class in a test for debugging with minimal alteration of
 * functions.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class WriteIntoFolder implements TestRule {

    /**
     * If there are no objects or specified dimensions, this size is used for an output image as a
     * fallback
     */
    private static final Dimensions FALLBACK_SIZE = new Dimensions(100, 100, 1);

    // START REQUIRED ARGUMENTS
    /** If true, the path of {@code folder} is printed to the console */
    private final boolean printDirectoryToConsole;
    // END REQUIRED ARGUMENTS

    /** The folder in which stacks are written */
    @Getter private TemporaryFolder folder = new TemporaryFolder();

    /** Creates to print directory to the console. */
    public WriteIntoFolder() {
        this.printDirectoryToConsole = true;
    }

    private Outputter outputter;

    private DisplayStackGenerator generatorStack = new DisplayStackGenerator("irrelevant", false);

    private ObjectAsMaskGenerator generatorSingleObject = new ObjectAsMaskGenerator();

    @Override
    public Statement apply(Statement base, Description description) {
        return folder.apply(base, description);
    }

    /**
     * Writes a stack up to a maximum of three channels.
     *
     * @param outputName
     * @param stack
     * @throws CreateException
     */
    public void writeStack(String outputName, Stack stack) throws CreateException {
        writeStack(outputName, DisplayStack.create(stack.extractUpToThreeChannels()));
    }

    public void writeStack(String outputName, DisplayStack stack) {
        setupOutputterIfNecessary();
        outputter.writerPermissive().write(outputName, () -> generatorStack, () -> stack);
    }

    public void writeObject(String outputName, ObjectMask object)
            throws SetOperationFailedException {
        setupOutputterIfNecessary();
        outputter.writerPermissive().write(outputName, () -> generatorSingleObject, () -> object);
    }

    /**
     * Writes the outline of objects on a blank RGB image, inferring dimensions of the image to
     * center the object
     *
     * @param outputName output-name
     * @param objects the objects to draw an outline for
     */
    public void writeObjects(String outputName, ObjectCollection objects) {

        Dimensions dimensionsResolved = dimensionsToCenterObjects(objects);

        writeObjectsEither(outputName, objects, Either.left(dimensionsResolved));
    }

    /**
     * Writes the outline of objects on a background.
     *
     * @param outputName output-name
     * @param objects the objects to draw an outline for
     * @param background the background
     */
    public void writeObjects(String outputName, ObjectCollection objects, Stack background) {
        writeObjectsEither(outputName, objects, Either.right(displayStackFor(background)));
    }

    public void writeVoxels(String outputName, Voxels<UnsignedByteBuffer> voxels) {

        Channel channel = ChannelFactory.instance().create(voxels);

        writeChannel(outputName, channel);
    }

    public void writeChannel(String outputName, Channel channel) {

        setupOutputterIfNecessary();

        writeStack(outputName, displayStackFor(channel));
    }

    /**
     * Writes a list of display-stacks
     *
     * @param outputName the output-name
     * @param stacks the list of display-stacks
     * @param always2D if true, the stacks are guaranteed to always to have only one z-slice (which
     *     can influence the output format).
     * @throws OutputWriteFailedException
     */
    public void writeList(String outputName, List<DisplayStack> stacks, boolean always2D)
            throws OutputWriteFailedException {

        setupOutputterIfNecessary();
        outputter
                .getChecked()
                .getWriters()
                .permissive()
                .write(
                        outputName,
                        () -> new CollectionGenerator<>(generatorStack, outputName),
                        () -> stacks);
    }

    private static DisplayStack displayStackFor(Channel channel) {
        try {
            return DisplayStack.create(channel);
        } catch (CreateException e) {
            throw new AnchorFriendlyRuntimeException(e);
        }
    }

    private static DisplayStack displayStackFor(Stack stack) {
        try {
            return DisplayStack.create(stack);
        } catch (CreateException e) {
            throw new AnchorFriendlyRuntimeException(e);
        }
    }

    private void setupOutputterIfNecessary() {
        try {
            if (outputter == null) {

                Path path = folder.getRoot().toPath();

                outputter = OutputterFixture.outputter(path);

                if (printDirectoryToConsole) {
                    System.out.println("Outputs written in test to: " + path); // NOSONAR
                }
            }
        } catch (BindFailedException e) {
            throw new AnchorFriendlyRuntimeException(e);
        }
    }

    /** Writes objects with either dimensions (for a blank background) or a particular background */
    private void writeObjectsEither(
            String outputName,
            ObjectCollection objects,
            Either<Dimensions, DisplayStack> background) {

        setupOutputterIfNecessary();

        DrawObjectsGenerator generatorObjects =
                DrawObjectsGenerator.outlineVariedColors(objects.size(), 1, background);
        outputter
                .writerPermissive()
                .write(
                        outputName,
                        () -> generatorObjects,
                        () -> new ObjectCollectionWithProperties(objects));
    }

    /** Finds dimensions that place the objects in the center */
    private static Dimensions dimensionsToCenterObjects(ObjectCollection objects) {

        if (objects.size() == 0) {
            return FALLBACK_SIZE;
        }

        BoundingBox boxSpans = boundingBoxThatSpans(objects);

        BoundingBox boxCentered =
                boxSpans.changeExtent(boxSpans.extent().growBy(boxSpans.cornerMin()));

        return new Dimensions(boxCentered.calculateCornerMaxExclusive());
    }

    private static BoundingBox boundingBoxThatSpans(ObjectCollection objects) {
        return BoundedList.createFromList(objects.asList(), ObjectMask::boundingBox).boundingBox();
    }
}
