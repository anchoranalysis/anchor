package org.anchoranalysis.core.log.error;

import org.apache.commons.lang3.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Decorative header and footer messages outputted to file for errors and warnings.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
class DecorateMessage {

    /** Margin from the left before message. */
    private static final int LEFT_MARGIN = 5;
    
    /** Padded before and after the main message in a banner. */
    private static final char DECORATIVE_CHARACTER = '-';

    /** One character is palced before and after the main message in a banner. */
    private static final char WHITESPACE_CHARACTER = ' ';
    
    /** Message at beginning of an error. */
    private static final String START_ERROR = "BEGIN ERROR";
    
    /** Message at conclusion of an error. */
    private static final String END_ERROR = "END ERROR";

    /** Message at beginning of a warning. */
    private static final String START_WARNING = "BEGIN WARNING";

    /** Message at conclusion of a warning. */
    private static final String END_WARNING = "END WARNING";
    
    /**
     * 
     * @param message
     * @param warning
     * @return
     */
    public static String decorate(String message, boolean warning) {
        
        int bannerSize = message.length();
        
        StringBuilder builder = new StringBuilder();
        
        addBanner(builder, warning ? START_WARNING : START_ERROR, bannerSize);
        builder.append(System.lineSeparator());
        builder.append(message);
        builder.append(System.lineSeparator());        
        addBanner(builder, warning ? END_WARNING : END_ERROR, bannerSize);
        
        return builder.toString();
    }
    
    /** 
     * Builds a string with the text surrounded on each size by decorative characters, and one whitespace padding.
     */
    private static void addBanner(StringBuilder builder, String bannerText, int bannerSize) {
        
        int remaining = bannerSize - bannerText.length() - 2;
        
        builder.append( repeatedDecorativeCharacters(LEFT_MARGIN) );
        builder.append( WHITESPACE_CHARACTER );
        builder.append( bannerText );
        builder.append( WHITESPACE_CHARACTER );
        builder.append( repeatedDecorativeCharacters(remaining - LEFT_MARGIN) );
    }
    
    private static String repeatedDecorativeCharacters(int numberRepeats) {
        return StringUtils.repeat(DECORATIVE_CHARACTER, numberRepeats);
    }
}
