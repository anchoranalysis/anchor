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
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.spatial.arrange.StackArranger;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.channel.factory.ChannelFactoryUnsignedByte;
import org.anchoranalysis.image.core.channel.factory.ChannelFactoryUnsignedShort;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.core.stack.Stack;

/**
 * Creates a stack that combines other stacks.
 *
 * <p>The stacks to be combined are specified in {@code list}.
 *
 * <p>Instructions on how to combine the stacks are specified in {@code arrange}, relative to the
 * order of the elements of {@code list}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class Arrange extends StackProvider {

    // START BEAN
    /** The stacks that are passed in respect order into {@code arrange}. */
    @BeanField @Getter @Setter private List<Provider<Stack>> list = new ArrayList<>();

    /** Determines how the stacks in {@code list} are arranged. */
    @BeanField @Getter @Setter private StackArranger arrange;

    /** Iff true, ensures every stack is converted into 3 channels. */
    @BeanField @Getter @Setter private boolean forceRGB = false;

    /**
     * If true, the created raster has <i>unsigned short</i> voxel data type. If false, then
     * <i>unsigned byte</i>.
     */
    @BeanField @Getter @Setter private boolean createShort = false;
    // END BEAN

    /**
     * Shortcut to create with some explicit parameters.
     *
     * @param createShort if true, the created raster has <i>unsigned short</i> voxel data type. If
     *     false, then <i>unsigned byte</i>.
     * @param forceRGB iff true, ensures every stack is converted into 3 channels.
     */
    public Arrange(boolean createShort, boolean forceRGB) {
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
                copyFirstChannelUntilThree(stack);
            }
            rasterList.add(new RGBStack(stack));
        }

        try {
            ChannelFactorySingleType factory =
                    createShort
                            ? new ChannelFactoryUnsignedShort()
                            : new ChannelFactoryUnsignedByte();

            return arrange.combine(rasterList, factory).asStack();
        } catch (ArrangeStackException e) {
            throw new ProvisionFailedException(e);
        }
    }

    private void copyFirstChannelUntilThree(Stack stack) {
        while (stack.getNumberChannels() < 3) {
            try {
                stack.addChannel(stack.getChannel(0).duplicate());
            } catch (IncorrectImageSizeException e) {
                assert false;
            }
        }
    }
}
