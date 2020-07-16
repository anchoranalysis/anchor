/* (C)2020 */
package org.anchoranalysis.image.io.bean.feature;

import java.nio.file.Path;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import one.util.streamex.StreamEx;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.calc.results.ResultsVectorCollection;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.io.csv.writer.FeatureListCSVGeneratorVertical;
import org.anchoranalysis.feature.io.csv.writer.TableCSVGenerator;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.image.feature.bean.object.single.CenterOfGravity;
import org.anchoranalysis.image.feature.bean.object.single.NumberVoxels;
import org.anchoranalysis.image.feature.bean.physical.convert.ConvertToPhysicalDistance;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.orientation.DirectionVector;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.csv.CSVGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * @author Owen Feehan
 * @param <T> feature calculation params
 */
class ObjectFeatureListCSVGenerator extends CSVGenerator
        implements IterableGenerator<ObjectCollection> {

    private static final String MANIFEST_FUNCTION = "objectFeatures";

    private final NRGStackWithParams nrgStack;
    private final Logger logger;
    private final FeatureList<FeatureInputSingleObject> features;

    private TableCSVGenerator<ResultsVectorCollection> delegate;

    @Getter @Setter private FeatureInitParams paramsInit; // Optional initialization parameters

    @Getter @Setter private SharedFeatureMulti sharedFeatures = new SharedFeatureMulti();

    private ObjectCollection element; // Iteration element

    public ObjectFeatureListCSVGenerator(
            FeatureList<FeatureInputSingleObject> features,
            NRGStackWithParams nrgStack,
            Logger logger) {
        super(MANIFEST_FUNCTION);
        this.nrgStack = nrgStack;
        this.logger = logger;
        this.features = createFullFeatureList(features);

        delegate = new FeatureListCSVGeneratorVertical(MANIFEST_FUNCTION, features.createNames());
    }

    @Override
    public Generator getGenerator() {
        return this;
    }

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {

        ResultsVectorCollection rvc;
        try {
            FeatureCalculatorMulti<FeatureInputSingleObject> session =
                    FeatureSession.with(features, paramsInit, sharedFeatures, logger);

            // We calculate a results vector for each object, across all features in memory. This is
            // more efficient
            rvc = new ResultsVectorCollection();
            for (ObjectMask objectMask : element) {
                rvc.add(
                        session.calcSuppressErrors(
                                createParams(objectMask, nrgStack), logger.errorReporter()));
            }
        } catch (FeatureCalcException e) {
            throw new OutputWriteFailedException(e);
        }

        delegate.setIterableElement(rvc);
        delegate.writeToFile(outputWriteSettings, filePath);
    }

    @Override
    public ObjectCollection getIterableElement() {
        return element;
    }

    @Override
    public void setIterableElement(ObjectCollection element) {
        this.element = element;
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

    private static FeatureInputSingleObject createParams(
            ObjectMask object, NRGStackWithParams nrgStack) {
        return new FeatureInputSingleObject(object, nrgStack);
    }
}
