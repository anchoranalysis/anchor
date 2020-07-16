/* (C)2020 */
package org.anchoranalysis.io.output.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.output.bean.allowed.AllOutputAllowed;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.bean.allowed.SpecificOutputDisallowed;

/**
 * Allows everything to be outputted except a particular list
 *
 * @author Owen Feehan
 */
public class OutputManagerPermissiveExcept extends OutputManagerWithPrefixer {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private StringSet except;

    /** Ignores all these strings in the second-level (for respect keys) */
    @BeanField @Getter @Setter
    private List<NamedBean<StringSet>> exceptSecondLevel = new ArrayList<>();
    // END BEAN PROPERTIES

    // We cache the second-level map here
    private Map<String, OutputAllowed> mapSecondLevel = null;

    @Override
    public boolean isOutputAllowed(String outputName) {
        return !except.contains(outputName);
    }

    @Override
    public OutputAllowed outputAllowedSecondLevel(String key) {
        createSecondLevelMapIfNecessary();
        return mapSecondLevel.getOrDefault(key, new AllOutputAllowed());
    }

    private void createSecondLevelMapIfNecessary() {
        if (mapSecondLevel == null) {
            mapSecondLevel = createSecondLevelMap();
        }
    }

    private Map<String, OutputAllowed> createSecondLevelMap() {
        Map<String, OutputAllowed> map = new HashMap<>();
        for (NamedBean<StringSet> bean : exceptSecondLevel) {
            map.put(bean.getName(), new SpecificOutputDisallowed(bean.getItem()));
        }
        return map;
    }
}
