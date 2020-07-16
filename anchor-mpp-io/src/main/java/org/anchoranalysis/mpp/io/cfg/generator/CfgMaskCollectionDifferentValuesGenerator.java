/* (C)2020 */
package org.anchoranalysis.mpp.io.cfg.generator;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.obj.collection.ObjectsAsUniqueValueGenerator;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class CfgMaskCollectionDifferentValuesGenerator extends RasterGenerator
        implements IterableGenerator<Cfg> {

    private ObjectsAsUniqueValueGenerator delegate;
    private Cfg cfg;
    private RegionMembershipWithFlags rm;

    public CfgMaskCollectionDifferentValuesGenerator(
            ImageDimensions dimensions, RegionMembershipWithFlags rm) {
        delegate = new ObjectsAsUniqueValueGenerator(dimensions);
        this.rm = rm;
    }

    public CfgMaskCollectionDifferentValuesGenerator(
            ImageDimensions dimensions, RegionMembershipWithFlags rm, Cfg cfg) {
        this(dimensions, rm);
        this.cfg = cfg;
    }

    @Override
    public boolean isRGB() {
        return delegate.isRGB();
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {

        ObjectCollectionWithProperties masks =
                cfg.calcMask(delegate.getDimensions(), this.rm, BinaryValuesByte.getDefault());
        try {
            delegate.setIterableElement(masks.withoutProperties());
        } catch (SetOperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
        return delegate.generate();
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return delegate.createManifestDescription();
    }

    @Override
    public Cfg getIterableElement() {
        return cfg;
    }

    @Override
    public void setIterableElement(Cfg element) throws SetOperationFailedException {
        this.cfg = element;
    }

    @Override
    public void start() throws OutputWriteFailedException {
        delegate.start();
    }

    @Override
    public void end() throws OutputWriteFailedException {
        delegate.end();
    }

    @Override
    public Generator getGenerator() {
        return this;
    }
}
