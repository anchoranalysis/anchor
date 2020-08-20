package org.anchoranalysis.test.image;

import io.vavr.control.Either;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.io.generator.raster.DisplayStackGenerator;
import org.anchoranalysis.image.io.generator.raster.object.collection.ObjectAsMaskGenerator;
import org.anchoranalysis.image.io.generator.raster.object.rgb.DrawObjectsGenerator;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.ObjectsWithBoundingBox;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.io.bean.object.writer.Outline;
import org.anchoranalysis.io.generator.collection.IterableGeneratorWriter;
import org.anchoranalysis.io.output.bound.BindFailedException;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.test.image.io.OutputManagerFixture;
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

    /** Constructor - print directory to the console */
    public WriteIntoFolder() {
        this.printDirectoryToConsole = true;
    }

    private BoundOutputManagerRouteErrors outputManager;

    private DisplayStackGenerator generatorStack = new DisplayStackGenerator("irrelevant");

    private ObjectAsMaskGenerator generatorSingleObject = new ObjectAsMaskGenerator();

    @Override
    public Statement apply(Statement base, Description description) {
        return folder.apply(base, description);
    }

    public void writeStack(String outputName, DisplayStack stack) {

        setupOutputManagerIfNecessary();

        generatorStack.setIterableElement(stack);

        outputManager.getWriterAlwaysAllowed().write(outputName, () -> generatorStack);
    }

    public void writeObject(String outputName, ObjectMask object) {

        setupOutputManagerIfNecessary();

        generatorSingleObject.setIterableElement(object);

        outputManager.getWriterAlwaysAllowed().write(outputName, () -> generatorSingleObject);
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

        writeObjects(outputName, objects, Either.left(dimensionsResolved));
    }

    /**
     * Writes the outline of objects on a background.
     *
     * @param outputName output-name
     * @param objects the objects to draw an outline for
     * @param background the background
     */
    public void writeObjects(String outputName, ObjectCollection objects, Stack background) {
        writeObjects(outputName, objects, Either.right(displayStackFor(background)));
    }

    public void writeVoxels(String outputName, Voxels<ByteBuffer> voxels) {

        Channel channel = ChannelFactory.instance().create(voxels, new Resolution());

        writeChannel(outputName, channel);
    }

    public void writeChannel(String outputName, Channel channel) {

        setupOutputManagerIfNecessary();

        writeStack(outputName, displayStackFor(channel));
    }

    public void writeList(String outputName, List<DisplayStack> stacks) {

        setupOutputManagerIfNecessary();

        IterableGeneratorWriter.writeSubfolder(
                outputManager, outputName, outputName, () -> generatorStack, stacks, true);
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

    private void setupOutputManagerIfNecessary() {
        try {
            if (outputManager == null) {

                Path path = folder.getRoot().toPath();

                outputManager = OutputManagerFixture.outputManagerForRouterErrors(path);

                if (printDirectoryToConsole) {
                    System.out.println("Outputs written in test to: " + path); // NOSONAR
                }
            }
        } catch (BindFailedException e) {
            throw new AnchorFriendlyRuntimeException(e);
        }
    }

    /** Writes objects with either dimensions (for a blank background) or a particular background */
    private void writeObjects(
            String outputName,
            ObjectCollection objects,
            Either<Dimensions, DisplayStack> background) {

        setupOutputManagerIfNecessary();

        DrawObjectsGenerator generatorObjects =
                new DrawObjectsGenerator(
                        new Outline(), new ObjectCollectionWithProperties(objects), background);

        outputManager.getWriterAlwaysAllowed().write(outputName, () -> generatorObjects);
    }

    /**
     * Finds dimensions that place the objects in the center
     *
     * @throws OperationFailedException
     */
    private static Dimensions dimensionsToCenterObjects(ObjectCollection objects) {

        if (objects.size() == 0) {
            return FALLBACK_SIZE;
        }

        try {
            BoundingBox boxSpans = new ObjectsWithBoundingBox(objects).boundingBox();

            BoundingBox boxCentered =
                    boxSpans.changeExtent(boxSpans.extent().growBy(boxSpans.cornerMin()));

            return new Dimensions(boxCentered.calculateCornerMaxExclusive());
        } catch (OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }
    }
}
