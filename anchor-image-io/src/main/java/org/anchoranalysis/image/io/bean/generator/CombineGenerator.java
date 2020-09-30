/*-
 * #%L
 * anchor-image-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.io.bean.generator;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.RasterArranger;
import org.anchoranalysis.image.bean.spatial.arrange.ArrangeStackBean;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.rasterwriter.RasterWriteOptions;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.generator.SingleFileTypeGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@AllArgsConstructor
class CombineGenerator<T> extends RasterGenerator<T> {

    private final ArrangeStackBean arrangeRaster;
    private final List<RasterGenerator<T>> generators;

    @Override
    public Stack transform() throws OutputWriteFailedException {

        List<RGBStack> generated = generateAll();

        RasterArranger rasterArranger = new RasterArranger();

        try {
            rasterArranger.init(arrangeRaster, generated);
        } catch (InitException e) {
            throw new OutputWriteFailedException(e);
        }

        // We get an image-stack for each generator
        //
        // Then we tile them
        //
        // We assume iterable generators always produce images of the same size
        //   and base our measurements on the first call to generate
        return rasterArranger.createStack(generated, new ChannelFactoryByte()).asStack();
    }

    @Override
    public String getFileExtension(OutputWriteSettings outputWriteSettings)
            throws OperationFailedException {
        return generators.get(0).getFileExtension(outputWriteSettings);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", "combinedEnergy"));
    }

    @Override
    public void start() throws OutputWriteFailedException {

        for (RasterGenerator<T> generator : generators) {
            generator.start();
        }
    }

    @Override
    public void end() throws OutputWriteFailedException {

        for (RasterGenerator<T> generator : generators) {
            generator.end();
        }
    }

    @Override
    public T getElement() {
        return generators.get(0).getElement();
    }

    @Override
    public void assignElement(T element) throws SetOperationFailedException {
        for (SingleFileTypeGenerator<T, Stack> generator : generators) {
            generator.assignElement(element);
        }
    }

    @Override
    public boolean isRGB() {
        return generators.stream().allMatch(RasterGenerator::isRGB);
    }

    private List<RGBStack> generateAll() throws OutputWriteFailedException {
        return FunctionalList.mapToList(
                generators,
                OutputWriteFailedException.class,
                generator -> new RGBStack(generator.transform()));
    }

    @Override
    public RasterWriteOptions rasterWriteOptions() {
        return generators.stream()
                .map(RasterGenerator::rasterWriteOptions)
                .reduce((first, second) -> first.and(second))
                .get(); // NOSONAR
    }
}
