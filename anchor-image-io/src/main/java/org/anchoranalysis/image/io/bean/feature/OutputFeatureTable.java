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

package org.anchoranalysis.image.io.bean.feature;

import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.bean.ImageBean;
import org.anchoranalysis.image.bean.provider.ObjectCollectionProvider;
import org.anchoranalysis.image.feature.bean.evaluator.FeatureListEvaluator;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

// Doesn't change the objects, just uses a generator to output a feature list as a CSV
public class OutputFeatureTable extends ImageBean<OutputFeatureTable> {

    private static final String OUTPUT_NAME_OBJECTS_FEATURE_LIST = "features";

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ObjectCollectionProvider objects;

    @BeanField @Getter @Setter private FeatureListEvaluator<FeatureInputSingleObject> feature;
    // END BEAN PROPERTIES

    public void output(BoundIOContext context) throws IOException {

        // Early exit if we're not allowed output anything anyway
        if (!context.getOutputManager().isOutputAllowed(OUTPUT_NAME_OBJECTS_FEATURE_LIST)) {
            return;
        }

        try {
            ObjectCollection objectCollection = objects.create();

            context.getOutputManager()
                    .getWriterCheckIfAllowed()
                    .write(
                            OUTPUT_NAME_OBJECTS_FEATURE_LIST,
                            () -> createGenerator(objectCollection, context.getLogger()));

        } catch (CreateException e) {
            throw new IOException(e);
        }
    }

    private ObjectFeatureListCSVGenerator createGenerator(ObjectCollection objects, Logger logger)
            throws OutputWriteFailedException {
        try {
            ObjectFeatureListCSVGenerator generator =
                    new ObjectFeatureListCSVGenerator(
                            feature, getInitializationParameters().getSharedObjects(), logger);
            generator.assignElement(objects);
            return generator;
        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }
}
