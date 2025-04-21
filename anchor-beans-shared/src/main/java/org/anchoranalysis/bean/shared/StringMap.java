/*-
 * #%L
 * anchor-beans-shared
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

package org.anchoranalysis.bean.shared;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.CreateException;

/**
 * A bean that defines a mapping between a set of {@link String}s to another set of {@link String}s.
 *
 * @author Owen Feehan
 */
public class StringMap extends AnchorBean<StringMap> {

    // START BEAN PROPERTIES
    /** A list of mappings between a single-items in the respective sets. */
    @Getter @Setter private List<StringMapItem> list;

    // END BEAN PROPERTIES

    /**
     * Derives a {@link Map} representation for the mapping.
     *
     * <p>Each {@link StringMapItem#getSource()} should be unique in {@code list}, otherwise an
     * exception is thrown.
     *
     * @return a newly created {@link Map} representing the items in {@code list}.
     * @throws CreateException if the values of {@link StringMapItem#getSource()} in {@code list}
     *     are not unique.
     */
    public Map<String, String> create() throws CreateException {

        HashMap<String, String> map = new HashMap<>();

        for (StringMapItem mapping : list) {
            if (map.putIfAbsent(mapping.getSource(), mapping.getTarget()) != null) {
                throw new CreateException(
                        String.format(
                                "More than one element in list contains %s as a source. Each source must be unique.",
                                mapping.getSource()));
            }
        }

        return map;
    }
}
