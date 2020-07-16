/* (C)2020 */
package org.anchoranalysis.mpp.io.input;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.bound.BoundIOContext;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MPPInitParamsFactory {

    public static MPPInitParams create(
            BoundIOContext context,
            Optional<Define> define,
            Optional<? extends InputForMPPBean> input)
            throws CreateException {

        SharedObjects so = new SharedObjects(context.common());
        ImageInitParams soImage = new ImageInitParams(so);
        MPPInitParams soMPP = new MPPInitParams(soImage, so);

        if (input.isPresent()) {
            try {
                input.get().addToSharedObjects(soMPP, soImage);
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        }

        if (define.isPresent()) {
            try {
                // Tries to initialize any properties (of type MPPInitParams) found in the
                // NamedDefinitions
                PropertyInitializer<MPPInitParams> pi = MPPBean.initializerForMPPBeans();
                pi.setParam(soMPP);
                soMPP.populate(pi, define.get(), context.getLogger());

            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        }

        return soMPP;
    }

    public static MPPInitParams createFromExistingCollections(
            BoundIOContext context,
            Optional<Define> define,
            Optional<NamedProvider<Stack>> stacks,
            Optional<NamedProvider<ObjectCollection>> objects,
            Optional<KeyValueParams> keyValueParams)
            throws CreateException {

        try {
            MPPInitParams soMPP = create(context, define, Optional.empty());

            ImageInitParams soImage = soMPP.getImage();

            if (stacks.isPresent()) {
                soImage.copyStackCollectionFrom(stacks.get());
            }

            if (objects.isPresent()) {
                soMPP.getImage().copyObjectsFrom(objects.get());
            }

            if (keyValueParams.isPresent()) {
                soImage.addToKeyValueParamsCollection("input_params", keyValueParams.get());
            }

            return soMPP;

        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }
}
