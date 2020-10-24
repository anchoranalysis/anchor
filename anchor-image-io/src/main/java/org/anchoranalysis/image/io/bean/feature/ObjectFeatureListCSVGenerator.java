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

import java.nio.file.Path;
import java.util.stream.Stream;
import one.util.streamex.StreamEx;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.io.csv.FeatureListCSVGeneratorVertical;
import org.anchoranalysis.feature.io.csv.FeatureTableCSVGenerator;
import org.anchoranalysis.feature.results.ResultsVectorList;
import org.anchoranalysis.feature.session.calculator.multi.FeatureCalculatorMulti;
import org.anchoranalysis.image.core.dimensions.SpatialUnits.UnitSuffix;
import org.anchoranalysis.image.core.orientation.DirectionVector;
import org.anchoranalysis.image.feature.bean.evaluator.FeatureListEvaluator;
import org.anchoranalysis.image.feature.bean.object.single.CenterOfGravity;
import org.anchoranalysis.image.feature.bean.object.single.NumberVoxels;
import org.anchoranalysis.image.feature.bean.physical.convert.ConvertToPhysicalDistance;
import org.anchoranalysis.image.feature.evaluator.NamedFeatureCalculatorMulti;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.io.generator.tabular.CSVGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.spatial.axis.AxisType;

/** @author Owen Feehan */
class ObjectFeatureListCSVGenerator extends CSVGenerator<ObjectCollection> {

    private static final String MANIFEST_FUNCTION = "objectFeatures";

    private FeatureCalculatorMulti<FeatureInputSingleObject> featureCalculator;

    private FeatureTableCSVGenerator<ResultsVectorList> delegate;

    private final Logger logger;

    public ObjectFeatureListCSVGenerator(
            FeatureListEvaluator<FeatureInputSingleObject> featureEvaluator,
            SharedObjects sharedObjects,
            Logger logger)
            throws CreateException {
        super(MANIFEST_FUNCTION);
        this.logger = logger;

        try {
            NamedFeatureCalculatorMulti<FeatureInputSingleObject> tuple =
                    featureEvaluator.createFeatureSession(
                            this::createFullFeatureList, sharedObjects);
            this.featureCalculator = tuple.getCalculator();

            delegate = new FeatureListCSVGeneratorVertical(MANIFEST_FUNCTION, tuple.getNames());
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    @Override
    public void writeToFile(
            ObjectCollection element, OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {

        // We calculate a results vector for each object, across all features in memory. This is
        // more efficient
        ResultsVectorList results = new ResultsVectorList();
        for (ObjectMask objectMask : element) {
            results.add(
                    featureCalculator.calculateSuppressErrors(
                            new FeatureInputSingleObject(objectMask), logger.errorReporter()));
        }

        delegate.writeToFile(results, outputWriteSettings, filePath);
    }

    // Puts in some extra descriptive features at the start
    private FeatureList<FeatureInputSingleObject> createFullFeatureList(
            FeatureList<FeatureInputSingleObject> features) {

        StreamEx<Feature<FeatureInputSingleObject>> stream =
                StreamEx.of(addFeaturesForAxis(AxisType.X));
        stream.append(addFeaturesForAxis(AxisType.Y));
        stream.append(addFeaturesForAxis(AxisType.Z));
        stream.append(createNumVoxels());
        stream.append(
                features.asList().stream()
                        .map(ObjectFeatureListCSVGenerator::duplicateSetCustomNameIfMissing));

        return FeatureListFactory.fromStream(stream);
    }

    private Feature<FeatureInputSingleObject> createNumVoxels() {
        NumberVoxels feature = new NumberVoxels();
        feature.setCustomName("numVoxels");
        return feature;
    }

    /** If there's no custom-name set, this sets in using the long description */
    private static <T extends FeatureInput> Feature<T> duplicateSetCustomNameIfMissing(
            Feature<T> feature) {
        if (feature.getCustomName() == null || feature.getCustomName().isEmpty()) {
            return feature.duplicateChangeName(feature.getFriendlyName());
        } else {
            return feature.duplicateBean();
        }
    }

    private static Stream<Feature<FeatureInputSingleObject>> addFeaturesForAxis(AxisType axis) {

        // Using non-physical distances, and physical distances respectively
        Feature<FeatureInputSingleObject> feature = new CenterOfGravity(axis);
        Feature<FeatureInputSingleObject> featurePhysical =
                convertToPhysical(feature, new DirectionVector(axis));

        String axisLabel = axis.toString().toLowerCase();
        feature.setCustomName(axisLabel);
        featurePhysical.setCustomName(axisLabel + "_p");
        return Stream.of(feature, featurePhysical);
    }

    private static Feature<FeatureInputSingleObject> convertToPhysical(
            Feature<FeatureInputSingleObject> feature, DirectionVector dir) {
        return new ConvertToPhysicalDistance<>(feature, UnitSuffix.MICRO, dir);
    }
}
