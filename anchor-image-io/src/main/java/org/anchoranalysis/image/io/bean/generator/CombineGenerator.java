/* (C)2020 */
package org.anchoranalysis.image.io.bean.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.bean.arrangeraster.ArrangeRasterBean;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.RasterArranger;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@AllArgsConstructor
class CombineGenerator<T> extends RasterGenerator implements IterableObjectGenerator<T, Stack> {

    private final ArrangeRasterBean arrangeRaster;
    private final List<IterableObjectGenerator<T, Stack>> generatorList;

    @Override
    public Stack generate() throws OutputWriteFailedException {

        List<RGBStack> generated = generateAll();

        RasterArranger rasterArranger = new RasterArranger();

        try {
            rasterArranger.init(arrangeRaster, generated);
        } catch (InitException e) {
            throw new OutputWriteFailedException(e);
        }

        // We get an ImgStack<ImgChnl> for each generator
        //
        // Then we tile them
        //
        // We assume iterable generators always produce images of the same size
        //   and base our measurements on the first call to generate
        return rasterArranger.createStack(generated, new ChannelFactoryByte()).asStack();
    }

    @Override
    public String getFileExtension(OutputWriteSettings outputWriteSettings) {
        return generatorList.get(0).getGenerator().getFileExtension(outputWriteSettings);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", "combinedNRG"));
    }

    @Override
    public void start() throws OutputWriteFailedException {

        for (IterableObjectGenerator<T, Stack> generator : generatorList) {
            generator.start();
        }
    }

    @Override
    public void end() throws OutputWriteFailedException {

        for (IterableObjectGenerator<T, Stack> generator : generatorList) {
            generator.end();
        }
    }

    @Override
    public T getIterableElement() {
        return generatorList.get(0).getIterableElement();
    }

    @Override
    public void setIterableElement(T element) throws SetOperationFailedException {
        for (IterableObjectGenerator<T, Stack> generator : generatorList) {
            generator.setIterableElement(element);
        }
    }

    @Override
    public ObjectGenerator<Stack> getGenerator() {
        return this;
    }

    @Override
    public boolean isRGB() {
        return true;
    }

    private List<RGBStack> generateAll() throws OutputWriteFailedException {
        List<RGBStack> listOut = new ArrayList<>();
        for (IterableObjectGenerator<T, Stack> generator : generatorList) {
            Stack stackOut = generator.getGenerator().generate();
            listOut.add(new RGBStack(stackOut));
        }
        return listOut;
    }
}
