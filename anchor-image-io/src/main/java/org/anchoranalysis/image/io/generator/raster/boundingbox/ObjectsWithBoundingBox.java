package org.anchoranalysis.image.io.generator.raster.boundingbox;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.ops.ObjectMaskMerger;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * One or more objects with the a bounding-box that contains them all fully
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor(access=AccessLevel.PRIVATE) @Accessors(fluent=true)
public class ObjectsWithBoundingBox {

    /** The objects in the collection. This collection should not be altered after the constructor (treated immutably). */
    @Getter
    private final ObjectCollection objects;
    
    /** A bounding-box that must contain all objects in the collection */
    @Getter
    private final BoundingBox boundingBox;
    
    /**
     * Constructor - create for a single object, using its current bounding-box
     * 
     * @param object the object
     */
    public ObjectsWithBoundingBox(ObjectMask object) {
        this.objects = ObjectCollectionFactory.of(object);
        this.boundingBox = object.boundingBox();
    }
    
    /**
     * Constructor - create for a collection of objects, minimally fitting a bounding-box around all objects
     * 
     * @param objects objects
     * @throws OperationFailedException if the object-collection is empty 
     */
    public ObjectsWithBoundingBox(ObjectCollection objects) throws OperationFailedException {
        Preconditions.checkArgument(!objects.isEmpty());
        this.objects = objects;
        this.boundingBox = ObjectMaskMerger.mergeBoundingBoxes(objects);
    }
    
    /**
     * Maps the containing bounding-box to one (that must contain the existing box)
     * 
     * @param boundingBoxToAssign the new bounding-box to assign
     * @return newly-created with the same object-container but a different bounding-box
     * @throws OperationFailedException if the new bounding-box does not contain the existing one
     */
    public ObjectsWithBoundingBox mapBoundingBoxToBigger(BoundingBox boundingBoxToAssign) throws OperationFailedException {
        if (!boundingBoxToAssign.contains().box(boundingBox)) {
            throw new OperationFailedException(
                 String.format(
                     "The bounding-box to be assigned (%s) must contain the existing bounding-box (%s), but it does not",
                     boundingBoxToAssign,
                     boundingBox
                 )
            );
        }
        return new ObjectsWithBoundingBox(objects, boundingBoxToAssign);
    }
    
    /**
     * Maps the containing-box to the entire image-dimensions, and changes each object-mask to belong to the entiore dimensions
     * 
     * @param dimensions the scene dimensions
     * @return newly-created with new objects and a new bounding-box
     * @throws OperationFailedException if the image-dimensions don't contain the existing bounding-boc
     */
    public ObjectsWithBoundingBox mapObjectsToUseEntireImage(ImageDimensions dimensions) throws OperationFailedException {
        if (!dimensions.contains(boundingBox)) {
            throw new OperationFailedException(
                    String.format(
                        "The dimensions-box to be assigned (%s) must contain the existing bounding-box (%s), but it does not",
                        dimensions,
                        boundingBox
                    )
               );
        }
        BoundingBox boxToAssign = new BoundingBox(dimensions.extent());
        return new ObjectsWithBoundingBox(objects.stream().mapBoundingBoxChangeExtent(boxToAssign), boxToAssign);
    }
        
    /** The number of objects */
    public int numberObjects() {
        return objects.size();
    }

    /** Gets a particular object */
    public ObjectMask get(int index) {
        return objects.get(index);
    }
}
