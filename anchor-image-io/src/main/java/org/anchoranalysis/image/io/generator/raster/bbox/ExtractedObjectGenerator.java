/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster.bbox;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.obj.rgb.DrawObjectsGenerator;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@RequiredArgsConstructor
public class ExtractedObjectGenerator extends RasterGenerator
        implements IterableObjectGenerator<ObjectMask, Stack> {

    // START REQUIRED ARGUMENTS
    private final DrawObjectsGenerator rgbObectGenerator;
    private final IterableObjectGenerator<BoundingBox, Stack> chnlGenerator;
    private final String manifestFunction;
    private final boolean mip;
    // END REQUIRED ARGUMENTS

    private ObjectMask element;

    @Override
    public Stack generate() throws OutputWriteFailedException {

        if (getIterableElement() == null) {
            throw new OutputWriteFailedException("no mutable element set");
        }

        try {
            chnlGenerator.setIterableElement(element.getBoundingBox());
        } catch (SetOperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }

        Stack chnlExtracted = chnlGenerator.getGenerator().generate();

        if (mip) {
            chnlExtracted = chnlExtracted.maxIntensityProj();
        }

        // We apply the generator
        try {
            rgbObectGenerator.setBackground(Optional.of(DisplayStack.create(chnlExtracted)));
        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }

        ObjectMask object = this.getIterableElement();

        if (mip) {
            object = object.flattenZ();
        }

        // We create a version that is relative to the extracted section
        ObjectCollectionWithProperties objects =
                new ObjectCollectionWithProperties(
                        new ObjectMask(
                                new BoundingBox(object.getVoxelBox().extent()),
                                object.binaryVoxelBox()));
        rgbObectGenerator.setIterableElement(objects);

        return rgbObectGenerator.generate();
    }

    @Override
    public ObjectMask getIterableElement() {
        return element;
    }

    @Override
    public void setIterableElement(ObjectMask element) {
        this.element = element;
    }

    @Override
    public ObjectGenerator<Stack> getGenerator() {
        return this;
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", manifestFunction));
    }

    @Override
    public boolean isRGB() {
        return rgbObectGenerator.isRGB();
    }
}
