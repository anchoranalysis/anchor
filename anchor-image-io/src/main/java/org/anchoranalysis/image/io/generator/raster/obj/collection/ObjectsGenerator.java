/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster.obj.collection;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;

/**
 * Base class for generators that accept a set of objects as input
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class ObjectsGenerator extends RasterGenerator
        implements IterableGenerator<ObjectCollection> {

    // START REQUIRED ARGUMENTS
    @Getter private final ImageDimensions dimensions;
    // END REQUIRED ARGUMENTS

    private ObjectCollection objects;

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", "maskCollection"));
    }

    @Override
    public boolean isRGB() {
        return false;
    }

    @Override
    public ObjectCollection getIterableElement() {
        return objects;
    }

    @Override
    public void setIterableElement(ObjectCollection element) throws SetOperationFailedException {
        this.objects = element;
    }

    @Override
    public Generator getGenerator() {
        return this;
    }

    protected ObjectCollection getObjects() {
        return objects;
    }
}
