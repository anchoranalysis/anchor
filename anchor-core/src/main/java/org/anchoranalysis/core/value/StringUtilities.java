package org.anchoranalysis.core.value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.OptionalUtilities;

/**
 * Utility functions for manipualting strings.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtilities {

    /**
     * Like {@link String#join} but ignores any empty strings, and returns {@link Optional#empty} if
     * all components are empty.
     *
     * @param delimeter delimeter to use when joining strings
     * @param stringMaybeEmpty strings that may or may not be empty.
     * @return a joined string including only the non-empty strings, and with a delimeter between
     *     them.
     */
    public static Optional<String> joinNonEmpty(String delimeter, String... stringMaybeEmpty) {
        List<String> components = listOfNonEmptyStrings(stringMaybeEmpty);
        return OptionalUtilities.createFromFlag(
                !components.isEmpty(), () -> String.join(delimeter, components));
    }

    /** Creates a list of strings, where strings are included only if they are non-empty. */
    private static List<String> listOfNonEmptyStrings(String... stringMaybeEmpty) {
        return Arrays.stream(stringMaybeEmpty)
                .filter(string -> !string.isEmpty())
                .collect(Collectors.toList());
    }
}
