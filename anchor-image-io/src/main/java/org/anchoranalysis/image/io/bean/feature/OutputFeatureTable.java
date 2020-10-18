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

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.bean.ImageBean;
import org.anchoranalysis.image.bean.provider.ObjectCollectionProvider;
import org.anchoranalysis.image.feature.bean.evaluator.FeatureListEvaluator;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.InputOutputContext;

/**
 * Specifies a table of feature-calculations on objects.
 *
 * <p>Note the input objects are not changed during feature-calculation and outputting.
 *
 * <p>The following outputs are produced:
 *
 * <table>
 * <caption></caption>
 * <thead>
 * <tr><th>Output Name</th><th>Default?</th><th>Description</th></tr>
 * </thead>
 * <tbody>
 * <tr><td>features</td><td>yes</td><td>A CSV of calculated-features for objects.</td></tr>
 * </tbody>
 * </table>
 *
 * @author Owen Feehan
 */
public class OutputFeatureTable extends ImageBean<OutputFeatureTable> {

    public static final String OUTPUT_FEATURE_TABLE = "features";

    // START BEAN PROPERTIES
    /** The objects for which features are calculated. */
    @BeanField @Getter @Setter private ObjectCollectionProvider objects;

    /** A list of features to calculate in an evaluation context. */
    @BeanField @Getter @Setter private FeatureListEvaluator<FeatureInputSingleObject> feature;
    // END BEAN PROPERTIES

    /**
     * Outputs the feature-table.
     *
     * @param context the input-output context.
     */
    public void output(InputOutputContext context) {

        // Early exit if we're not allowed output anything anyway
        if (!context.getOutputter().outputsEnabled().isOutputEnabled(OUTPUT_FEATURE_TABLE)) {
            return;
        }

        context.getOutputter()
                .writerSelective()
                .write(
                        OUTPUT_FEATURE_TABLE,
                        () -> createGenerator(context.getLogger()),
                        this::createObjectsWrapException);
    }

    private ObjectCollection createObjectsWrapException() throws OutputWriteFailedException {
        try {
            return objects.create();
        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    private ObjectFeatureListCSVGenerator createGenerator(Logger logger)
            throws OutputWriteFailedException {
        try {
            return new ObjectFeatureListCSVGenerator(
                    feature, getInitializationParameters().getSharedObjects(), logger);
        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }
}
