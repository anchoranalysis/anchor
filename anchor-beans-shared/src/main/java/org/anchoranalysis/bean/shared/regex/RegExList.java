/* (C)2020 */
package org.anchoranalysis.bean.shared.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;

public class RegExList extends RegEx {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<RegEx> list = new ArrayList<>();
    // END BEAN PROPERTIES

    @Override
    public Optional<String[]> match(String str) {

        for (RegEx re : list) {
            Optional<String[]> matches = re.match(str);
            if (matches.isPresent()) {
                return matches;
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (RegEx re : list) {
            sb.append(re.toString());

            if (first) {
                first = false;
            } else {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
