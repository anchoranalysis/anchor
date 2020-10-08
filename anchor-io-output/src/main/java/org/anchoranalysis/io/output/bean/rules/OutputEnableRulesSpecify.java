package org.anchoranalysis.io.output.bean.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.bean.annotation.BeanField;
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
    @BeanField @Getter @Setter private StringSet first;

    /** Output-names in the second-level (for all first level output-names) */
    @BeanField @Getter @Setter private List<NamedBean<StringSet>> second = new ArrayList<>();
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

    protected boolean firstLevelContains(String outputName) {
        return first.contains(outputName);
    }

    /**
     * Creates a new second-level {@link SingleLevelOutputEnabled} from the relevant set of strings.
     */
    protected abstract SingleLevelOutputEnabled createSecondLevelFromSet(StringSet outputNames);

    /**
     * @param outputName
     * @param defaultValue
     * @return
     */
    protected SingleLevelOutputEnabled secondLevelOutputs(
            String outputName, OutputEnabled defaultValue) {
        createSecondLevelMapIfNecessary();
        return mapSecondLevel.getOrDefault(outputName, defaultValue);
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
