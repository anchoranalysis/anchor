/* (C)2020 */
package org.anchoranalysis.core.text;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LanguageUtilities {

    /**
     * Returns something or somethings depending on the number
     *
     * @param number the number n
     * @param word the something
     * @return the string as above
     */
    public static String pluralizeMaybe(long number, String word) {
        if (number == 1) {
            return word;
        } else {
            return pluralize(word);
        }
    }

    /**
     * Returns 1 something or n somethings as is appropriate
     *
     * @param number the number n
     * @param word the something
     * @return the string as above
     */
    public static String prefixPluralizeMaybe(long number, String word) {
        if (number == 1) {
            return "1 " + word;
        } else {
            return prefixPluralize(number, word);
        }
    }

    /**
     * Returns n somethings
     *
     * @param number the number n
     * @param word the something
     * @return the string as above
     */
    public static String prefixPluralize(long number, String word) {
        return String.format("%d %s", number, pluralize(word));
    }

    /**
     * Given something, returns somethings
     *
     * @param word the something
     * @return the string as above
     */
    public static String pluralize(String word) {
        return String.format("%ss", word);
    }
}
