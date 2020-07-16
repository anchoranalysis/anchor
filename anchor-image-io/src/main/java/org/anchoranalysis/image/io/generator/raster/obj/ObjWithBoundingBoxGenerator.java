/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster.obj;

import java.util.Optional;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.IterableGeneratorBridge;
import org.anchoranalysis.io.generator.combined.IterableCombinedListGenerator;
import org.anchoranalysis.io.generator.serialized.ObjectOutputStreamGenerator;

/**
 * Like {@link org.anchoranalysis.image.io.generator.raster.obj.ObjectsAsBinaryChnlGenerator} but
 * also outputs a serialized bounding box.
 *
 * @author Owen Feehan
 */
public class ObjWithBoundingBoxGenerator extends IterableCombinedListGenerator<ObjectMask> {

    public ObjWithBoundingBoxGenerator(ImageResolution res) {
        this(new ObjectsAsBinaryChnlGenerator(BinaryValuesByte.getDefault().getOnByte(), res));
    }

    private ObjWithBoundingBoxGenerator(IterableGenerator<ObjectMask> generator) {
        super(
                generator,
                // We create an iterable bridge from object-mask to BoundingBox
                new IterableGeneratorBridge<>(
                        new ObjectOutputStreamGenerator<>(Optional.of("BoundingBox")),
                        ObjectMask::getBoundingBox));
    }
}
