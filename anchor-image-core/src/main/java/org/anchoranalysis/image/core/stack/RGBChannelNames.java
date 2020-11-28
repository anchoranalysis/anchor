package org.anchoranalysis.image.core.stack;

import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constants for referring to the channels in a RGB-stack.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RGBChannelNames {
    /** Name for the <b>red</b> channel. */
    public static final String RED = "red";

    /** Name for the <b>green</b> channel. */
    public static final String GREEN = "green";

    /** Name for the <b>blue</b> channel. */
    public static final String BLUE = "blue";

    /** Creates an array with all channel-names in R-G-B order. */
    public static String[] rgbArray() {
        return new String[] {RED, GREEN, BLUE};
    }

    /** Creates a list with all channel-names in R-G-B order. */
    public static List<String> rgbList() {
        return Arrays.asList(RED, GREEN, BLUE);
    }
}
