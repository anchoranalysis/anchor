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

package org.anchoranalysis.image.bean.segment.binary;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.bean.nonbean.segment.BinarySegmentationParameters;
import org.anchoranalysis.image.bean.nonbean.segment.SegmentationFailedException;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Uses an existing {@link BinarySegmentation} that is located by a unique identifier.
 *
 * <p>The identifier locates an entity from the shared-objects passed during initialization in the
 * {@link ImageInitialization}.
 *
 * @author Owen Feehan
 */
public class BinarySegmentationReference extends BinarySegmentation {

    // START BEAN PROPERTIES
    /** The identifier of the existing {@link BinarySegmentation} to use. */
    @BeanField @Getter @Setter private String id;

    // END BEAN PROPERTIES

    private BinarySegmentation proxy;

    @Override
    public void onInitialization(ImageInitialization initialization) throws InitializeException {
        super.onInitialization(initialization);
        try {
            proxy = getInitialization().binarySegmentations().getException(id);
        } catch (NamedProviderGetException e) {
            throw new InitializeException(e.summarize());
        }
    }

    @Override
    public BinaryVoxels<UnsignedByteBuffer> segment(
            VoxelsUntyped voxels,
            BinarySegmentationParameters parameters,
            Optional<ObjectMask> objectMask)
            throws SegmentationFailedException {
        return proxy.segment(voxels, parameters, objectMask);
    }
}
