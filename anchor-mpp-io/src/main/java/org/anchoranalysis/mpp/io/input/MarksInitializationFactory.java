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
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.experiment.io.InitializationContext;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.mpp.bean.MarksBean;
import org.anchoranalysis.mpp.bean.init.MarksInitialization;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MarksInitializationFactory {

    private static final String KEY_VALUE_PARAMS_IDENTIFIER = "input_params";

    public static MarksInitialization create(
            InitializationContext context,
            Optional<Define> define,
            Optional<? extends InputForMarksBean> input)
            throws CreateException {

        SharedObjects sharedObjects = new SharedObjects(context.common());
        ImageInitialization image =
                new ImageInitialization(sharedObjects, context.getSuggestedResize());
        MarksInitialization marks = new MarksInitialization(image, sharedObjects);

        if (input.isPresent()) {
            try {
                input.get().addToSharedObjects(marks, image);
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        }

        if (define.isPresent()) {
            try {
                // Tries to initialize any properties (of type MarksInitialization found in the
                // NamedDefinitions
                PropertyInitializer<MarksInitialization> initializer = MarksBean.initializerForMarksBeans();
                initializer.setParam(marks);
                marks.populate(initializer, define.get(), context.getLogger());

            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        }

        return marks;
    }

    public static MarksInitialization createFromExistingCollections(
            InitializationContext context,
            Optional<Define> define,
            Optional<NamedProvider<Stack>> stacks,
            Optional<NamedProvider<ObjectCollection>> objects,
            Optional<Dictionary> dictionary)
            throws CreateException {

        try {
            MarksInitialization marks = create(context, define, Optional.empty());

            ImageInitialization image = marks.getImage();

            if (stacks.isPresent()) {
                image.copyStacksFrom(stacks.get());
            }

            if (objects.isPresent()) {
                image.copyObjectsFrom(objects.get());
            }

            if (dictionary.isPresent()) {
                image.addDictionary(
                        KEY_VALUE_PARAMS_IDENTIFIER, dictionary.get());
            }

            return marks;

        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }
}
