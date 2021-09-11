/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.provider.stack;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.RasterArranger;
import org.anchoranalysis.image.bean.spatial.arrange.ArrangeStackBean;
import org.anchoranalysis.image.core.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.core.channel.factory.ChannelFactoryShort;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.core.stack.Stack;

/**
 * Creates a stack that tiles (or otherwise combines) other providers
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class ArrangeRaster extends StackProvider {

    // START BEAN
    @BeanField @Getter @Setter private ArrangeStackBean arrange;

    /** If set, ensures every stack is converted into 3 channels */
    @BeanField @Getter @Setter private boolean forceRGB = false;

    @BeanField @Getter @Setter private List<Provider<Stack>> list = new ArrayList<>();

    @BeanField @Getter @Setter private boolean createShort = false;
    // END BEAN

    public ArrangeRaster(boolean createShort, boolean forceRGB) {
        this.createShort = createShort;
        this.forceRGB = forceRGB;
    }

    /**
     * Adds a stack to the existing list of stack-providers.
     *
     * @param provider the provider of the stack to add.
     */
    public void addStack(Provider<Stack> provider) {
        list.add(provider);
    }

    @Override
    public Stack get() throws ProvisionFailedException {

        if (list.isEmpty()) {
            throw new ProvisionFailedException("At least one stack must be present in list");
        }

        List<RGBStack> rasterList = new ArrayList<>();
        for (Provider<Stack> provider : list) {

            Stack stack = provider.get();

            if (forceRGB) {
                copyFirstChannelUntil3(stack);
            }
            rasterList.add(new RGBStack(stack));
        }

        RasterArranger rasterArranger = new RasterArranger();
        try {
            rasterArranger.initialize(arrange, rasterList);
        } catch (InitializeException e) {
            throw new ProvisionFailedException(e);
        }

        ChannelFactorySingleType factory =
                createShort ? new ChannelFactoryShort() : new ChannelFactoryByte();

        return rasterArranger.createStack(rasterList, factory).asStack();
    }

    private void copyFirstChannelUntil3(Stack stack) {
        while (stack.getNumberChannels() < 3) {
            try {
                stack.addChannel(stack.getChannel(0).duplicate());
            } catch (IncorrectImageSizeException e) {
                assert false;
            }
        }
    }
}
