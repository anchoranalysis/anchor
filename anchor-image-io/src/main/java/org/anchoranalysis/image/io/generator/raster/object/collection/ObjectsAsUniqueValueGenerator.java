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

package org.anchoranalysis.image.io.generator.raster.object.collection;

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.io.generator.raster.ChannelGenerator;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Writes objects as a Raster with unique id values for each object.
 *
 * <p>Note that a maximum of 254 objects are allowed to be written on a channel in this way (for a
 * 8-bit image)
 *
 * @author Owen Feehan
 */
public class ObjectsAsUniqueValueGenerator extends ObjectsGenerator {

    private static ChannelFactoryByte factory = new ChannelFactoryByte();

    public ObjectsAsUniqueValueGenerator(Dimensions dimensions) {
        super(dimensions);
    }

    public ObjectsAsUniqueValueGenerator(Dimensions dimensions, ObjectCollection objects) {
        super(dimensions, objects);
    }

    @Override
    public Stack transform() throws OutputWriteFailedException {

        Channel out = factory.createEmptyInitialised(dimensions());

        if (getObjects().size() > 254) {
            throw new OutputWriteFailedException(
                    String.format(
                            "Collection has %d objects. A max of 254 is allowed",
                            getObjects().size()));
        }

        int value = 1;

        for (ObjectMask object : getObjects()) {
            out.assignValue(value++).toObject(object);
        }

        return new ChannelGenerator("maskCollection", out).transform();
    }
}
