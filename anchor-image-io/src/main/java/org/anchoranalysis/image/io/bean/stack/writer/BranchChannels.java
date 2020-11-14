/*-
 * #%L
 * anchor-plugin-io
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
package org.anchoranalysis.image.io.bean.stack.writer;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.image.io.stack.output.StackWriteOptions;

/**
 * Uses different raster-writers depending on the number/type of channels.
 *
 * <p>If any optional condition does not have a writer, then {@code writer} is used in this case.
 *
 * @author Owen Feehan
 */
public class BranchChannels extends StackWriterDelegateBase {

    // START BEAN PROPERTIES
    /** Default writer, if a more specific writer is not specified for a condition. */
    @BeanField @Getter @Setter private StackWriter writer;

    /**
     * Writer employed if a stack is a single-channeled image.
     */
    @BeanField @OptionalBean @Getter @Setter private StackWriter whenSingleChannel;
    
    /**
     * Writer employed if a stack is a <b>three-channeled non-RGB</b> image.
     */
    @BeanField @OptionalBean @Getter @Setter private StackWriter whenThreeChannels;

    /** 
     * Writer employed if a stack is a <b>three-channeled RGB</b> image.
     */
    @BeanField @OptionalBean @Getter @Setter private StackWriter whenRGB;
    // END BEAN PROPERTIES

    @Override
    protected StackWriter selectDelegate(StackWriteOptions writeOptions) {
        if (writeOptions.isRgb()) {
            return writerOrDefault(whenRGB);
        } else if (writeOptions.isThreeChannels()) {
            return writerOrDefault(whenThreeChannels);
        } else if (writeOptions.isSingleChannel()) {
            return writerOrDefault(whenSingleChannel);            
        } else {
            return writer;
        }
    }

    private StackWriter writerOrDefault(StackWriter writerMaybeNull) {
        if (writerMaybeNull != null) {
            return writerMaybeNull;
        } else {
            return writer;
        }
    }
}
