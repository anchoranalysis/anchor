/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean.define;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.configuration.SubnodeConfiguration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class SubNodeKeysHelper {

    /**
     * A list of sub-nodes of the configuration.
     *
     * <p>We rely on an API call that returns multiple entries per bean including for each {@code
     * config-class} and {@code config-factory}.
     *
     * <p>It only finds direct sub-nodes (i.e. it is not recursive).
     *
     * <p>e.g. for an example with two children ({@code namedChannelProviderList} and {@code
     * namedStackProviderList}) the following is returned:
     *
     * <pre>
     * namedChannelProviderList[@config-class]
     * namedChannelProviderList[@config-factory]
     * namedChannelProviderList[@filePath]
     * namedStackProviderList[@config-class]
     * namedStackProviderList[@config-factory]
     * namedStackProviderList[@filePath]
     * </pre>
     *
     * <p>We remove the [] component, and combine into a set for uniqueness to retrieve the desired
     * two items.
     *
     * @param config the subnode configuration
     * @return a newly created set of the extracted keys.
     */
    public static Set<String> extractKeys(SubnodeConfiguration config) {

        // TreeSet is good for outputting a sorted list (just to be neat!)
        TreeSet<String> out = new TreeSet<>();

        // We iterate through all beans defined, and assume each is a list of NamedBeans
        Iterator<String> itr = config.getKeys();
        while (itr.hasNext()) {
            String keyShort = stripLongName(itr.next());

            // We skip empty short keys
            // We skip any key with a . as this means we've iterated onto the next level (i.e. it is
            // no longer a direct child)
            if (!keyShort.isEmpty() && !keyShort.contains(".")) {
                out.add(keyShort);
            }
        }
        return out;
    }

    /**
     * Strips a long key name of the bit in square-brackets at the end
     *
     * @param keyLong a key name with square-brackets at the end e.g. someName[bitInSquareBrackets]
     * @return the string without the square-brackets part e.g. someName
     */
    private static String stripLongName(String keyLong) {

        int indexBracket = keyLong.indexOf('[');

        // We assume the left-bracket always exists
        assert (indexBracket != -1);

        return keyLong.substring(0, indexBracket);
    }
}
