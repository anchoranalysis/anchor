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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.output.bean.enabled.All;
import org.anchoranalysis.io.output.bean.enabled.OutputEnabled;
import org.anchoranalysis.io.output.bean.enabled.SpecificDisabled;

/**
 * Allows everything to be outputted except a particular list.
 *
 * <p>The {@code extendSecondLevel} entries apply to <i>all</i> second-level outputs.
 * 
 * @author Owen Feehan
 */
@NoArgsConstructor
public class PermissiveExcept extends OutputEnabledRules {

    // START BEAN PROPERTIES
    /** Rejects these output-names in the first-level. */
    @BeanField @Getter @Setter private StringSet except;

    /** Rejects all these output-names in the second-level (for all first level output-names) */
    @BeanField @Getter @Setter
    private List<NamedBean<StringSet>> exceptSecondLevel = new ArrayList<>();
    // END BEAN PROPERTIES

    /**
     * Create to reject a specific set of first-level output-names.
     * 
     * @param except rejects these output-names in the first-level.
     */
    public PermissiveExcept(StringSet except) {
        this.except = except;
    }

    // We cache the second-level map here
    private Map<String, OutputEnabled> mapSecondLevel = null;

    @Override
    public OutputEnabled first() {
        return new SpecificDisabled(except);
    }

    @Override
    public OutputEnabled second(String outputName) {
        createSecondLevelMapIfNecessary();
        return mapSecondLevel.getOrDefault(outputName, All.INSTANCE);
    }

    private void createSecondLevelMapIfNecessary() {
        if (mapSecondLevel == null) {
            mapSecondLevel = createSecondLevelMap();
        }
    }

    private Map<String, OutputEnabled> createSecondLevelMap() {
        Map<String, OutputEnabled> map = new HashMap<>();
        for (NamedBean<StringSet> bean : exceptSecondLevel) {
            map.put(bean.getName(), new SpecificDisabled(bean.getItem()));
        }
        return map;
    }
}
