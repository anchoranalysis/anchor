/* (C)2020 */
package org.anchoranalysis.feature.bean.list;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.bean.permute.property.SequenceInteger;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.operator.FeatureListElem;
import org.anchoranalysis.feature.bean.operator.Reference;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Populates a FeatureListElem with references to other features formed by one or two sequences of
 * integers, so that all the features of the following pattern are contained in the list.
 *
 * <p>prependString seperator sequenceInteger prependString seperator sequenceInteger seperator
 * sequenceAdditionalInteger
 *
 * <p>where all these are concatenated together.
 *
 * @author Owen Feehan
 */
public class FeatureListProviderListSequence<T extends FeatureInput>
        extends FeatureListProviderReferencedFeatures<T> {

    // START BEAN PROPERTIES
    /** The list feature that is duplicated, and populated. */
    @BeanField @SkipInit @Getter @Setter private FeatureListElem<T> feature;

    @BeanField @Getter @Setter private String prependString;

    /** First number range that is appended */
    @BeanField @Getter @Setter private SequenceInteger sequence;

    /** Another number range that is appended */
    @BeanField @OptionalBean @Getter @Setter private SequenceInteger sequenceAdditional;

    @BeanField @Getter @Setter private String seperator = ".";
    // END BEAN PROPERTIES

    private String determineFeatureName(String prepend, String append) {
        StringBuilder sb = new StringBuilder();
        sb.append(prepend);
        sb.append(seperator);
        sb.append(append);
        return sb.toString();
    }

    private Feature<T> createReferenceFeature(String prepend, String append) {
        String featureName = determineFeatureName(prepend, append);

        Reference<T> out = new Reference<>();
        out.setId(featureName);
        return out;
    }

    private Feature<T> createSingleSequence() {

        FeatureListElem<T> out = feature;

        for (int i = sequence.getStart(); i <= sequence.getEnd(); i += sequence.getIncrement()) {
            out.getList().add(createReferenceFeature(prependString, Integer.toString(i)));
        }

        return out;
    }

    private Feature<T> createDoubleSequence() {

        FeatureListElem<T> out = feature;

        for (int i = sequence.getStart(); i <= sequence.getEnd(); i += sequence.getIncrement()) {
            for (int j = sequenceAdditional.getStart();
                    j <= sequenceAdditional.getEnd();
                    j += sequenceAdditional.getIncrement()) {
                String suffix = String.format("%d%s%d", i, seperator, j);
                out.getList().add(createReferenceFeature(prependString, suffix));
            }
        }

        return out;
    }

    @Override
    public FeatureList<T> create() throws CreateException {
        return FeatureListFactory.from(featureFromSequence());
    }

    private Feature<T> featureFromSequence() {
        if (sequenceAdditional != null) {
            return createDoubleSequence();
        } else {
            return createSingleSequence();
        }
    }
}
