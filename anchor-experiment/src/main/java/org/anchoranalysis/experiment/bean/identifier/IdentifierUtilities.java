/* (C)2020 */
package org.anchoranalysis.experiment.bean.identifier;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class IdentifierUtilities {

    public static String identifierFromNameVersion(String name, Optional<String> version) {
        StringBuilder sb = new StringBuilder(name);
        version.ifPresent(v -> sb.append("_" + v));
        return sb.toString();
    }
}
