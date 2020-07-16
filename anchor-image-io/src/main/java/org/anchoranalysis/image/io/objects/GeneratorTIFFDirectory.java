/* (C)2020 */
package org.anchoranalysis.image.io.objects;

import java.util.List;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.io.generator.raster.obj.ObjWithBoundingBoxGenerator;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.io.generator.IterableGeneratorBridge;
import org.anchoranalysis.io.generator.collection.SubfolderGenerator;

/**
 * Writes the object-mask-collection as a TIFF to a directory
 *
 * <p>Writes the corner information as a binary-serialized file in the directory
 *
 * @author Owen Feehan
 */
public class GeneratorTIFFDirectory
        extends IterableGeneratorBridge<ObjectCollection, List<ObjectMask>> {

    public GeneratorTIFFDirectory() {
        super(
                new SubfolderGenerator<ObjectMask, List<ObjectMask>>(
                        new ObjWithBoundingBoxGenerator(
                                new ImageResolution()), // We don't specify a sceneres as we don't
                        // know what images they belong to
                        "obj"),
                ObjectCollection::asList);
    }
}
