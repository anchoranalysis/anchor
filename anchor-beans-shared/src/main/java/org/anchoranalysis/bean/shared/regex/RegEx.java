/* (C)2020 */
package org.anchoranalysis.bean.shared.regex;

import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;

public abstract class RegEx extends AnchorBean<RegEx> {

    /**
     * Returns an array of string components it matches successfully, or empty() if it cannot match
     *
     * @param str string to match against the regular-expression
     * @return an array of string components representing each group in the match, or empty() if no
     *     match is possible
     */
    public abstract Optional<String[]> match(String str);

    /**
     * Returns a boolean whether the regular-expression successfully matches a string or not
     *
     * @param str string to match against the regular-expression
     * @return TRUE if successfully matched, FALSE otherwise
     */
    public boolean hasMatch(String str) {
        return match(str).isPresent();
    }
}
