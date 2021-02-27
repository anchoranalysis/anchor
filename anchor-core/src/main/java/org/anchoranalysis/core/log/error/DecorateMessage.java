package org.anchoranalysis.core.log.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Decorative header and footer messages outputted to file for errors and warnings.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DecorateMessage {

    /** Margin from the left before message. */
    private static final int LEFT_MARGIN = 5;

    /** Padded before and after the main message in a banner. */
    private static final char DECORATIVE_CHARACTER = '-';

    /** One character is palced before and after the main message in a banner. */
    private static final char WHITESPACE_CHARACTER = ' ';

    /** Describes an error. */
    private static final String ERROR = "ERROR";

    /** Describes a warning. */
    private static final String WARNING = "WARNING";

    /** Describes a begginning. */
    private static final String START = "START";

    /** Describes a conclusion. */
    private static final String END = "END";

    /**
     * Places a line of banner text before and after a message.
     *
     * @param message the message to embedded in a banner.
     * @param warning if true, the message is associated with a warning, otherwise an error.
     * @return a multi-line string with the existing message embedded within banner lines.
     */
    public static String decorate(String message, boolean warning) {

        int bannerSize = message.length();

        StringBuilder builder = new StringBuilder();

        addBannerMultiplex(builder, bannerSize, START, warning);
        addNewline(builder);
        builder.append(message);
        addNewline(builder);
        addBannerMultiplex(builder, bannerSize, END, warning);

        return builder.toString();
    }

    private static void addBannerMultiplex(
            StringBuilder builder, int bannerSize, String prefix, boolean warning) {
        String bannerText = prefix + " " + (warning ? WARNING : ERROR);
        addBanner(builder, bannerText, bannerSize);
    }

    /**
     * Builds a string with the text surrounded on each size by decorative characters, and one
     * whitespace padding.
     */
    private static void addBanner(StringBuilder builder, String bannerText, int bannerSize) {

        int remaining = bannerSize - bannerText.length() - 2;

        builder.append(repeatedDecorativeCharacters(LEFT_MARGIN));
        builder.append(WHITESPACE_CHARACTER);
        builder.append(bannerText);
        builder.append(WHITESPACE_CHARACTER);
        builder.append(repeatedDecorativeCharacters(remaining - LEFT_MARGIN));
    }

    private static void addNewline(StringBuilder builder) {
        builder.append(System.lineSeparator());
    }

    private static String repeatedDecorativeCharacters(int numberRepeats) {
        return StringUtils.repeat(DECORATIVE_CHARACTER, numberRepeats);
    }
}
