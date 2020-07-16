/* (C)2020 */
package org.anchoranalysis.bean.shared.regex;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;

public class RegExSimple extends RegEx {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String matchString;
    // END BEAN PROPERTIES

    @Override
    public Optional<String[]> match(String str) {

        Pattern p = Pattern.compile(matchString);

        Matcher matcher = p.matcher(str);

        if (!matcher.matches()) {
            return Optional.empty();
        }
        return Optional.of(arrayFromMatcher(matcher));
    }

    private String[] arrayFromMatcher(Matcher matcher) {
        String[] arr = new String[matcher.groupCount()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = matcher.group(i + 1);
        }
        return arr;
    }

    @Override
    public String toString() {
        return String.format("regEx(%s)", matchString);
    }
}
