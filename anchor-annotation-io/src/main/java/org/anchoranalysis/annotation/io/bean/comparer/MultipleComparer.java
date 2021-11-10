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
import org.anchoranalysis.annotation.io.assignment.AssignOverlappingObjects;
import org.anchoranalysis.annotation.io.assignment.OverlappingObjects;
import org.anchoranalysis.annotation.io.assignment.generator.AssignmentColorPool;
import org.anchoranalysis.annotation.io.assignment.generator.AssignmentGenerator;
import org.anchoranalysis.annotation.io.assignment.generator.DrawColoredObjects;
import org.anchoranalysis.annotation.io.image.findable.Findable;
import org.anchoranalysis.annotation.mark.AnnotationWithMarks;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonEmpty;
import org.anchoranalysis.bean.shared.color.scheme.ColorScheme;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.identifier.name.NameValue;
import org.anchoranalysis.core.identifier.name.SimpleNameValue;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.shared.FeaturesInitialization;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.feature.bean.evaluator.FeatureEvaluator;
import org.anchoranalysis.image.feature.input.FeatureInputPairObjects;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Allows comparison of an annotation with multiple other entities.
 *
 * @author Owen Feehan
 */
public class MultipleComparer extends AnchorBean<MultipleComparer> {

    // START BEAN PROPERTIES
    /** Calculates the <i>cost</i> used when making assignments. */
    @BeanField @Getter @Setter private FeatureEvaluator<FeatureInputPairObjects> featureEvaluator;

    /**
     * The maximum cost (as calculated by {@code featureEvaluator}) to accept when creating an
     * assignment between objects.
     */
    @BeanField @Getter @Setter private double maxCost = 1.0;

    /** The other entities to compare with the annotation. */
    @BeanField @NonEmpty @Getter @Setter
    private List<NamedBean<ComparableSource>> sources = new ArrayList<>();

    /**
     * If true, a maximum-intensity-projection is first applied to any 3D objects into a 2D plane,
     * before comparison.
     */
    @BeanField @Getter @Setter private boolean flatten = false;
    // END BEAN PROPERTIES

    /**
     * Creates a {@link Stack} illustrating the comparison between {@code annotation} and each
     * source in {@code sources}.
     *
     * @param annotation the annotation to compare.
     * @param background the background to use when comparing.
     * @param annotationPath the path on the file-system to {@code annotation} to use as a
     *     reference.
     * @param colorScheme the color-scheme to use for unpaired objects.
     * @param modelDirectory a directory in which models reside.
     * @param logger the logger.
     * @param debugMode whether in debug-mode or not.
     * @return a list of stacks showing the comparisons, with with a corresponding name.
     * @throws CreateException if any one of the stacks cannot be created.
     */
    public List<NameValue<Stack>> createComparisonStacks(
            AnnotationWithMarks annotation,
            DisplayStack background,
            Path annotationPath,
            ColorScheme colorScheme,
            Path modelDirectory,
            Logger logger,
            boolean debugMode)
            throws CreateException {

        FeaturesInitialization initialization =
                FeaturesInitialization.create(logger, modelDirectory);
        try {
            featureEvaluator.initializeRecursive(initialization, logger);
        } catch (InitializeException e) {
            throw new CreateException(e);
        }

        List<NameValue<Stack>> out = new ArrayList<>();

        for (NamedBean<ComparableSource> source : sources) {

            ObjectCollection annotationObjects =
                    annotation.convertToObjects(background.dimensions());

            Findable<ObjectCollection> compareObjects;
            try {
                compareObjects =
                        source.getValue()
                                .loadAsObjects(
                                        annotationPath, background.dimensions(), debugMode, logger);
            } catch (InputReadFailedException e) {
                throw new CreateException(e);
            }

            Optional<ObjectCollection> foundObjects =
                    compareObjects.getOrLog(source.getName(), logger);

            if (foundObjects.isPresent()) {
                out.add(
                        compare(
                                annotationObjects,
                                foundObjects.get(),
                                background,
                                source.getName(),
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
        // happening, so we prevent it from interfering with the rest of the analysis as a
        // workaround.
        removeObjectsWithNoPixels(annotationObjects);
        removeObjectsWithNoPixels(compareObjects);

        OverlappingObjects assignment;
        try {
            assignment =
                    new AssignOverlappingObjects(featureEvaluator, flatten)
                            .createAssignment(
                                    annotationObjects,
                                    compareObjects,
                                    maxCost,
                                    background.dimensions());

            AssignmentGenerator generator =
                    new AssignmentGenerator(
                            new DrawColoredObjects(background, flatten, 3),
                            numberPaired -> new AssignmentColorPool(numberPaired, colorScheme),
                            Tuple.of("annotator", rightName),
                            true);
            return new SimpleNameValue<>(rightName, generator.transform(assignment));

        } catch (FeatureCalculationException | OutputWriteFailedException e1) {
            throw new CreateException(e1);
        }
    }

    /** Removes any objects with zero pixels from the collection. */
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
