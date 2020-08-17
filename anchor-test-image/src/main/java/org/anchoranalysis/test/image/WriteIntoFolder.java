package org.anchoranalysis.test.image;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.io.generator.raster.DisplayStackGenerator;
import org.anchoranalysis.image.io.generator.raster.object.collection.ObjectAsMaskGenerator;
import org.anchoranalysis.image.io.generator.raster.object.rgb.DrawObjectsGenerator;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.ObjectsWithBoundingBox;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.io.bean.object.writer.Outline;
import org.anchoranalysis.io.generator.collection.IterableGeneratorWriter;
import org.anchoranalysis.io.output.bound.BindFailedException;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.test.image.io.OutputManagerFixture;
import org.junit.rules.TemporaryFolder;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;

/**
 * Utilities for writing one or more stacks into a folder during testing
 * 
 * @author Owen Feehan
 *
 */
@RequiredArgsConstructor
public class WriteIntoFolder {
    
    /** If there are no objects or specified dimensions, this size is used for an output image as a fallback*/
    private static final ImageDimensions FALLBACK_SIZE = new ImageDimensions(100,100,1);
    
    // START REQUIRED ARGUMENTS
    /** The folder in which stacks are written */
    private final TemporaryFolder folder;
    
    /** If true, the path of {@code folder} is printed to the console */
    private final boolean printDirectoryToConsole;
    // END REQUIRED ARGUMENTS
    
    /**
     * Constructor
     * 
     * @param folder the folder in which stacks are written
     */
    public WriteIntoFolder(TemporaryFolder folder) {
        this.folder = folder;
        this.printDirectoryToConsole = true;
    }
        
    private BoundOutputManagerRouteErrors outputManager;

    private DisplayStackGenerator generatorStack = new DisplayStackGenerator("irrelevant");
    
    private ObjectAsMaskGenerator generatorSingleObject = new ObjectAsMaskGenerator();
    
    public void writeStack(String outputName, DisplayStack stack) throws OperationFailedException {
        
        setupOutputManagerIfNecessary();
        
        generatorStack.setIterableElement(stack);
        
        outputManager.getWriterAlwaysAllowed().write(outputName, () -> generatorStack);
    }
    
    public void writeObject(String outputName, ObjectMask object) throws OperationFailedException {
        
        setupOutputManagerIfNecessary();
        
        generatorSingleObject.setIterableElement(object);
        
        outputManager.getWriterAlwaysAllowed().write(outputName, () -> generatorSingleObject);
    }
    
    /**
     * Writes the outline of objects on a blank RGB image, inferring dimensions of the image to center the object
     * 
     * @param outputName output-name
     * @param objects the objects to draw an outline for
     * @throws OperationFailedException
     */
    public void writeObjects(String outputName, ObjectCollection objects) throws OperationFailedException {
        writeObjects(outputName, objects, Optional.empty());
    }
    
    /**
     * Writes the outline of objects on a blank RGB image, inferring dimensions if they are not provided.
     * <p>
     * The dimensions are inferred so as to center the objects in a scene.
     * 
     * @param outputName output-name
     * @param objects the objects to draw an outline for
     * @param dimensions the explicit dimensions, if specified
     * @throws OperationFailedException
     */
    public void writeObjects(String outputName, ObjectCollection objects, Optional<ImageDimensions> dimensions) throws OperationFailedException {
        
        setupOutputManagerIfNecessary();
        
        ImageDimensions dimensionsResolved = dimensions.orElseGet( ()->dimensionsToForObjects(objects) );
        
        DrawObjectsGenerator generatorObjects = new DrawObjectsGenerator(new Outline(), new ObjectCollectionWithProperties(objects), Either.left(dimensionsResolved));
        
        outputManager.getWriterAlwaysAllowed().write(outputName, () -> generatorObjects);
    }
    
    /** 
     * Finds dimensions that place the objects in the center
     *  
     * @throws OperationFailedException */
    private static ImageDimensions dimensionsToForObjects(ObjectCollection objects) {
        
        if (objects.size()==0) {
            return FALLBACK_SIZE;
        }
        
        try {
            BoundingBox boxSpans = new ObjectsWithBoundingBox(objects).boundingBox();
            
            BoundingBox boxCentered = boxSpans.changeExtent( boxSpans.extent().growBy(boxSpans.cornerMin()) );
            
            return new ImageDimensions(boxCentered.calcCornerMaxExclusive());
        } catch (OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }
    }
    
    public void writeVoxels(String outputName, Voxels<ByteBuffer> voxels) throws OperationFailedException {
        
        Channel channel = ChannelFactory.instance().create(voxels, new ImageResolution() );
        
        writeChannel(outputName, channel);
    }
    
    public void writeChannel(String outputName, Channel channel) throws OperationFailedException {
        
        setupOutputManagerIfNecessary();
        
        try {
            writeStack(outputName, DisplayStack.create(channel) );
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }
    
    public void writeList(String outputName, List<DisplayStack> stacks) throws OperationFailedException {
        
        setupOutputManagerIfNecessary();
        
        IterableGeneratorWriter.writeSubfolder(
                outputManager,
                outputName,
                outputName,
                () -> generatorStack,
                stacks,
                true);
    }
    
    private void setupOutputManagerIfNecessary() throws OperationFailedException {
        try {
            if (outputManager==null) {
                
                Path path = folder.getRoot().toPath();
                
                outputManager = OutputManagerFixture.outputManagerForRouterErrors(path);
                
                if (printDirectoryToConsole) {
                    System.out.println("Outputs written in test to: " + path);  // NOSONAR
                }
            }
        } catch (BindFailedException e) {
            throw new OperationFailedException(e);
        }        
    }
}
