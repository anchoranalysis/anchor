/*-
 * #%L
 * anchor-annotation-io
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

package org.anchoranalysis.annotation.io.bean.comparer;

import io.vavr.Tuple;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.annotation.io.assignment.AssignmentObjectFactory;
import org.anchoranalysis.annotation.io.assignment.AssignmentOverlapFromPairs;
import org.anchoranalysis.annotation.io.assignment.generator.AssignmentGenerator;
import org.anchoranalysis.annotation.io.assignment.generator.ColorPool;
import org.anchoranalysis.annotation.io.image.findable.Findable;
import org.anchoranalysis.annotation.mark.AnnotationWithMarks;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonEmpty;
import org.anchoranalysis.bean.shared.color.scheme.ColorScheme;
import org.anchoranalysis.bean.shared.color.scheme.VeryBright;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.identifier.name.NameValue;
import org.anchoranalysis.core.identifier.name.SimpleNameValue;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.feature.bean.evaluator.FeatureEvaluator;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Allows comparison of an annotation with multiple other entities.
 *
 * @author Owen Feehan
 */
public class MultipleComparer extends AnchorBean<MultipleComparer> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FeatureEvaluator<FeatureInputPairObjects> featureEvaluator;

    @BeanField @NonEmpty @Getter @Setter
    private List<NamedBean<Comparer>> listComparers = new ArrayList<>();

    @BeanField @Getter @Setter private boolean useMIP = false;

    @BeanField @Getter @Setter private double maxCost = 1.0;
    // END BEAN PROPERTIES

    public List<NameValue<Stack>> createRasters(
            AnnotationWithMarks annotation,
            DisplayStack background,
            Path annotationPath,
            ColorScheme colorScheme,
            Path modelDirectory,
            Logger logger,
            boolean debugMode)
            throws CreateException {

        SharedFeaturesInitParams so = SharedFeaturesInitParams.create(logger, modelDirectory);
        try {
            featureEvaluator.initRecursive(so, logger);
        } catch (InitException e) {
            throw new CreateException(e);
        }

        List<NameValue<Stack>> out = new ArrayList<>();

        for (NamedBean<Comparer> ni : listComparers) {

            ObjectCollection annotationObjects =
                    annotation.convertToObjects(background.dimensions());

            Findable<ObjectCollection> compareObjects =
                    ni.getValue().createObjects(annotationPath, background.dimensions(), debugMode);

            Optional<ObjectCollection> foundObjects =
                    compareObjects.getFoundOrLog(ni.getName(), logger);

            if (foundObjects.isPresent()) {
                out.add(
                        compare(
                                annotationObjects,
                                foundObjects.get(),
                                background,
                                ni.getName(),
                                colorScheme));
            }
        }

        return out;
    }

    private SimpleNameValue<Stack> compare(
            ObjectCollection annotationObjects,
            ObjectCollection compareObjects,
            DisplayStack background,
            String rightName,
            ColorScheme colorScheme)
            throws CreateException {
        // Don't know how it's possible for an object with 0 pixels to end up here, but it's somehow
        // happening, so we prevent it from interfereing
        //  with the rest of the analysis as a workaround
        removeObjectsWithNoPixels(annotationObjects);
        removeObjectsWithNoPixels(compareObjects);

        AssignmentOverlapFromPairs assignment;
        try {
            assignment =
                    new AssignmentObjectFactory(featureEvaluator, useMIP)
                            .createAssignment(
                                    annotationObjects,
                                    compareObjects,
                                    maxCost,
                                    background.dimensions());

            AssignmentGenerator generator =
                    new AssignmentGenerator(
                            background,
                            numberPaired -> createColorPool(numberPaired, colorScheme),
                            useMIP,
                            Tuple.of("annotator", rightName),
                            true,
                            3);
            return new SimpleNameValue<>(rightName, generator.transform(assignment));

        } catch (FeatureCalculationException | OutputWriteFailedException e1) {
            throw new CreateException(e1);
        }
    }

    private ColorPool createColorPool(int numberPaired, ColorScheme colorScheme) {
        return new ColorPool(numberPaired, colorScheme, new VeryBright(), true);
    }

    private static void removeObjectsWithNoPixels(ObjectCollection objects) {

        Iterator<ObjectMask> itr = objects.iterator();
        while (itr.hasNext()) {

            ObjectMask object = itr.next();

            if (object.numberVoxelsOn() == 0) {
                itr.remove();
            }
        }
    }
}
