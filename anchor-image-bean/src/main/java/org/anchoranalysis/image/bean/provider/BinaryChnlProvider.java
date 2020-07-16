/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ops.BinaryChnlFromObjects;
import org.anchoranalysis.image.stack.Stack;

@GroupingRoot
public abstract class BinaryChnlProvider extends BeanImgStackProvider<BinaryChnlProvider, Mask> {

    @Override
    public abstract Mask create() throws CreateException;

    public Stack createStack() throws CreateException {
        Channel chnl = createChnlFromBinary(create(), BinaryValues.getDefault());
        return new Stack(chnl);
    }

    private static Channel createChnlFromBinary(Mask binaryImgChnl, BinaryValues bvOut) {
        ObjectCollection objects = expressAsObjects(binaryImgChnl);
        return BinaryChnlFromObjects.createFromObjects(
                        objects, binaryImgChnl.getDimensions(), bvOut)
                .getChannel();
    }

    private static ObjectCollection expressAsObjects(Mask binaryImgChnl) {
        return ObjectCollectionFactory.from(binaryImgChnl);
    }
}
