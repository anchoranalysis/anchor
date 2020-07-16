/* (C)2020 */
package org.anchoranalysis.io.output.bean.allowed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.bean.annotation.BeanField;

@NoArgsConstructor
@AllArgsConstructor
public class SpecificOutputDisallowed extends OutputAllowed {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private StringSet outputsDisallowed;
    // END BEAN PROPERTIES

    @Override
    public boolean isOutputAllowed(String outputName) {
        return !outputsDisallowed.contains(outputName);
    }
}
