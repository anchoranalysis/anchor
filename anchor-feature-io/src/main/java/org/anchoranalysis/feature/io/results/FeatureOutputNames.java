/*-
 * #%L
 * anchor-feature-io
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
package org.anchoranalysis.feature.io.results;

import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.core.functional.OptionalFactory;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;

/**
 * The customizable output names used by {@link LabelledResultsCollector}, which all follow a
 * pattern based on a prefix.
 *
 * <p>The group and aggregated outputs can be toggled on and off.
 *
 * @author Owen Feehan
 */
public class FeatureOutputNames {

    /** If not otherwise specified, the output name for a non-aggregated CSV of the results. */
    public static final String OUTPUT_DEFAULT_NON_AGGREGATED = "features";

    /**
     * Suffix appended to {@link #csvFeaturesNonAggregated} to form an output-name for <i>aggregated
     * features</i>.
     */
    public static final String OUTPUT_SUFFIX_AGGREGATED = "Aggregated";

    /**
     * Suffix appended to {@link #csvFeaturesNonAggregated} to form an output-name for <i>grouped
     * features</i>.
     */
    public static final String OUTPUT_SUFFIX_GROUP = "Group";

    /**
     * Suffix appended to {@link #csvFeaturesNonAggregated} to form an output-name for <i>aggregated
     * grouped features</i>.
     */
    public static final String OUTPUT_SUFFIX_AGGREGATED_GROUP = "AggregatedGroup";

    /** The CSV of non-aggregated feature-results. */
    @Getter private String csvFeaturesNonAggregated;

    /** The CSV of aggregated feature-results, if enabled. */
    @Getter private Optional<String> csvFeaturesAggregated;

    /** The CSV individually written for each group, if enabled. */
    @Getter private Optional<String> csvFeaturesGroup;

    /** The name of the XML file outputted with aggregated values for each group, if enabled. */
    @Getter private Optional<String> xmlAggregatedGroup;

    /**
     * Creates using default names, equivalent to a prefix of <i>features</i>.
     *
     * <p>All outputs are enabled.
     */
    public FeatureOutputNames() {
        this(OUTPUT_DEFAULT_NON_AGGREGATED, true, true);
    }

    /**
     * Creates output-names based upon a prefix.
     *
     * @param prefix prefix used to determine the output-names.
     * @param enableAggregated whether to enable the aggregated outputs.
     * @param enableGroup whether to enable the group outputs.
     */
    public FeatureOutputNames(String prefix, boolean enableAggregated, boolean enableGroup) {
        csvFeaturesNonAggregated = prefix; // No suffix
        csvFeaturesAggregated = joinIfEnabled(enableAggregated, prefix, OUTPUT_SUFFIX_AGGREGATED);
        csvFeaturesGroup = joinIfEnabled(enableGroup, prefix, OUTPUT_SUFFIX_GROUP);
        xmlAggregatedGroup = joinIfEnabled(enableGroup, prefix, OUTPUT_SUFFIX_AGGREGATED_GROUP);
    }

    /**
     * If any output is enabled that requires calculation of feature results?
     *
     * @param outputEnabled which outputs are enabled or not.
     * @return true if at least one output is enabled that requires calculation of results, false if
     *     none do.
     */
    public boolean calculationResultsNeeded(MultiLevelOutputEnabled outputEnabled) {
        if (outputEnabled.isOutputEnabled(csvFeaturesNonAggregated)) {
            return true;
        }

        if (outputPresentAndEnabled(csvFeaturesAggregated, outputEnabled)) {
            return true;
        }

        if (outputPresentAndEnabled(csvFeaturesGroup, outputEnabled)) {
            return true;
        }

        return outputPresentAndEnabled(xmlAggregatedGroup, outputEnabled);
    }

    /** If a particular output-name is present and enabled for output. */
    private static boolean outputPresentAndEnabled(
            Optional<String> outputName, MultiLevelOutputEnabled outputEnabled) {
        return outputName.isPresent() && outputEnabled.isOutputEnabled(outputName.get());
    }

    private static Optional<String> joinIfEnabled(boolean enabled, String prefix, String suffix) {
        return OptionalFactory.create(enabled, () -> prefix + suffix);
    }
}
