/*-
 * #%L
 * anchor-image-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.anchoranalysis.image.core.stack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    public static String[] asArray() {
        return new String[] {RED, GREEN, BLUE};
    }

    /** Creates a list with all channel-names in R-G-B order. */
    public static List<String> asList() {
        return Arrays.asList(RED, GREEN, BLUE);
    }
    
    /** Creates a set of the channel names. */
    public static Set<String> asSet() {
        return new HashSet<>(asList());
    }
    
    /**
     * Derives the index of a channel for a channel name.
     * 
     * <p>Names match only if lower-case.
     * 
     * @param channelName the name of the channel
     * @return 0 for red, 1 for green, 2 for blue or {@link Optional#empty} if name is anything else.
     */
    public static Optional<Integer> deriveIndex(String channelName) {
        switch(channelName) {
            case RED:
                return Optional.of(0);
            case GREEN:
                return Optional.of(1);                
            case BLUE:
                return Optional.of(2);
            default:
                return Optional.empty();
        }
    }

    /**
     * Whether the channel-name is one of red, green, or blue.
     * 
     * <p>Names match only if lower-case.
     * 
     * @param channelName name to check if it is valid.
     * @return true iff {@code channelName} is red, green, or blue.
     */
    public static boolean isValidName(String channelName) {
        return channelName.equals(RED) || channelName.equals(GREEN) || channelName.equals(BLUE); 
    }
}
