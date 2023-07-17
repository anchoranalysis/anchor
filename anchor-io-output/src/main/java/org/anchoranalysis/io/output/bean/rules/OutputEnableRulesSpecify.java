/*-
 * #%L
 * anchor-io-output
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
package org.anchoranalysis.io.output.bean.rules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.primitive.StringSet;
import org.anchoranalysis.io.output.bean.enabled.OutputEnabled;
import org.anchoranalysis.io.output.enabled.single.SingleLevelOutputEnabled;

/**
 * Base class for an {@link OutputEnabledRules} that specifies particular output-names for first and
 * second levels.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class OutputEnableRulesSpecify extends OutputEnabledRules {

    // START BEAN PROPERTIES
    /** Output-names in the first-level. */
    @BeanField @OptionalBean @Getter @Setter private StringSet first;

    /**
     * Output-names in the second-level (for all first level output-names).
     *
     * <p>If the list is empty, all second-level outputs are considered permissive.
     */
    @BeanField @Getter @Setter private List<NamedBean<StringSet>> second = Arrays.asList();
    // END BEAN PROPERTIES

    // We cache the second-level map here.
    private Map<String, SingleLevelOutputEnabled> mapSecondLevel = null;

    /**
     * Create with a specific set of first-level output names.
     *
     * @param first first-level output-names
     */
    protected OutputEnableRulesSpecify(StringSet first) {
        this.first = first;
    }

    /**
     * Whether the first-level names contain a particular output (if defined)?
     *
     * @param outputName the output-name to query.
     * @return true iff the first-level names are defined <b>and</b> {@code outputName} is
     *     contained.
     */
    protected boolean firstLevelContains(String outputName) {
        return first != null && first.contains(outputName);
    }

    /**
     * Creates a new second-level {@link SingleLevelOutputEnabled} from the relevant set of strings.
     *
     * @param outputNames a set of output-names that are used to create the {@link
     *     SingleLevelOutputEnabled}.
     * @return the newly created {@link SingleLevelOutputEnabled}.
     */
    protected abstract SingleLevelOutputEnabled createSecondLevelFromSet(StringSet outputNames);

    /**
     * Retrieves a second-level {@link SingleLevelOutputEnabled} corresponding to a first-level
     * output-name.
     *
     * @param outputName the name of the first-level output.
     * @param defaultValue the default-value used if no existing second-level entry exists.
     * @return an existing corresponding {@link SingleLevelOutputEnabled} or otherwise a newly
     *     created one.
     */
    protected SingleLevelOutputEnabled secondLevelOutputs(
            String outputName, OutputEnabled defaultValue) {

        if (second.isEmpty()) {
            return new Permissive().create(Optional.empty());
        }

        createSecondLevelMapIfNecessary();
        return mapSecondLevel.getOrDefault(outputName, defaultValue);
    }

    /**
     * Are output-names in the first-level defined?
     *
     * @return true iff they are defined.
     */
    protected boolean isFirstDefined() {
        return first != null;
    }

    private void createSecondLevelMapIfNecessary() {
        if (mapSecondLevel == null) {
            mapSecondLevel = createSecondLevelMap();
        }
    }

    private Map<String, SingleLevelOutputEnabled> createSecondLevelMap() {
        Map<String, SingleLevelOutputEnabled> map = new HashMap<>();
        for (NamedBean<StringSet> bean : second) {
            map.put(bean.getName(), createSecondLevelFromSet(bean.getItem()));
        }
        return map;
    }
}
