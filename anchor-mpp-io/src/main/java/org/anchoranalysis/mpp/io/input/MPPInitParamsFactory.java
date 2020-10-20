/*-
 * #%L
 * anchor-mpp-io
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

package org.anchoranalysis.mpp.io.input;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.initializable.property.PropertyInitializer;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.value.KeyValueParams;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.mpp.bean.MPPBean;
import org.anchoranalysis.mpp.bean.init.MPPInitParams;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MPPInitParamsFactory {

    private static final String KEY_VALUE_PARAMS_IDENTIFIER = "input_params";

    public static MPPInitParams create(
            InputOutputContext context,
            Optional<Define> define,
            Optional<? extends InputForMPPBean> input)
            throws CreateException {

        SharedObjects sharedObjects = new SharedObjects(context.common());
        ImageInitParams imageInit = new ImageInitParams(sharedObjects);
        MPPInitParams mppInit = new MPPInitParams(imageInit, sharedObjects);

        if (input.isPresent()) {
            try {
                input.get().addToSharedObjects(mppInit, imageInit);
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        }

        if (define.isPresent()) {
            try {
                // Tries to initialize any properties (of type MPPInitParams) found in the
                // NamedDefinitions
                PropertyInitializer<MPPInitParams> pi = MPPBean.initializerForMPPBeans();
                pi.setParam(mppInit);
                mppInit.populate(pi, define.get(), context.getLogger());

            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        }

        return mppInit;
    }

    public static MPPInitParams createFromExistingCollections(
            InputOutputContext context,
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
                soImage.addToKeyValueParamsCollection(
                        KEY_VALUE_PARAMS_IDENTIFIER, keyValueParams.get());
            }

            return soMPP;

        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }
}
