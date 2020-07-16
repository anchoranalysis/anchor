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
/* (C)2020 */
package org.anchoranalysis.image.io.bean.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
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

/**
 * Combines a number of generators of Raster images by tiling their outputs together
 *
 * <p>The order of generators is left to right, then top to bottom
 *
 * @author Owen Feehan
 * @param <T> iteration-type
 */
public class CombineRasterGenerator<T> extends AnchorBean<CombineRasterGenerator<T>> {

    // START BEAN PROPERTIES
    @BeanField private ArrangeRasterBean arrangeRaster;

    // A list of all generators to be tiled (left to right, then top to bottom)
    @BeanField private List<IterableObjectGenerator<T, Stack>> generatorList = new ArrayList<>();
    // END BEAN PROPERTIES

    private class Generator extends RasterGenerator implements IterableObjectGenerator<T, Stack> {

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

    public CombineRasterGenerator() {
        super();
    }

    public void add(IterableObjectGenerator<T, Stack> generator) {
        generatorList.add(generator);
    }

    public IterableObjectGenerator<T, Stack> createGenerator() {
        return new Generator();
    }

    @Override
    public String getBeanDscr() {
        return getBeanName();
    }

    public ArrangeRasterBean getArrangeRaster() {
        return arrangeRaster;
    }

    public void setArrangeRaster(ArrangeRasterBean arrangeRaster) {
        this.arrangeRaster = arrangeRaster;
    }
}
