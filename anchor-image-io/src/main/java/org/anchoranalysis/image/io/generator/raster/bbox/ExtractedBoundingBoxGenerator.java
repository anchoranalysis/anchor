/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster.bbox;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@RequiredArgsConstructor
public class ExtractedBoundingBoxGenerator extends RasterGenerator
        implements IterableObjectGenerator<BoundingBox, Stack> {

    // START REQUIRED ARGUMENTS
    private final Stack stack;
    private final String manifestFunction;
    // END REQUIRED ARGUMENTS

    private BoundingBox bbox;

    @Getter @Setter private int paddingXY = 0;

    @Getter @Setter private int paddingZ = 0;

    @Override
    public Stack generate() throws OutputWriteFailedException {

        if (getIterableElement() == null) {
            throw new OutputWriteFailedException("no mutable element set");
        }

        try {
            return createExtract(stack);
        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public BoundingBox getIterableElement() {
        return bbox;
    }

    @Override
    public void setIterableElement(BoundingBox element) {
        this.bbox = element;
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
        return stack.getNumChnl() == 3;
    }

    private Stack createExtract(Stack stackIn) throws CreateException {
        Stack stackOut = new Stack();

        for (Channel chnlIn : stackIn) {

            VoxelBox<?> vbIn = chnlIn.getVoxelBox().any();

            VoxelBox<?> vbExtracted = vbIn.region(bbox, false);

            Channel chnlExtracted =
                    ChannelFactory.instance().create(vbExtracted, stackIn.getDimensions().getRes());
            try {
                stackOut.addChnl(chnlExtracted);
            } catch (IncorrectImageSizeException e) {
                throw new CreateException(e);
            }
        }

        return stackOut;
    }
}
