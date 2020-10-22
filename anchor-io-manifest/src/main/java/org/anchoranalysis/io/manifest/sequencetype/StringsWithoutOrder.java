/*-
 * #%L
 * anchor-io-manifest
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

package org.anchoranalysis.io.manifest.sequencetype;

import java.util.HashSet;
import org.anchoranalysis.core.index.bounded.OrderProvider;

/**
 * A sequence of strings that has no order.
 *
 * <p>The only requirement is that each index is unique.
 *
 * @author Owen Feehan
 */
public class StringsWithoutOrder extends SequenceType<String> {

    /** */
    private static final long serialVersionUID = 8292809183713424555L;

    private final HashSet<String> set;

    private transient CollectionAsRange range;

    public StringsWithoutOrder() {
        set = new HashSet<>();
    }

    @Override
    public void assignMaximumIndex(int index) {
        // NOTHING TO DO, the maximum is not changed for this sequence-type
    }

    @Override
    public String getName() {
        return "setSequenceType";
    }

    @Override
    public void update(String element) throws SequenceTypeException {
        set.add(element);
    }

    @Override
    public int getNumberElements() {
        return set.size();
    }

    @Override
    public OrderProvider createOrderProvider() {
        OrderProviderHashMap map = new OrderProviderHashMap();
        map.addStringSet(set);
        return map;
    }

    @Override
    public IncompleteElementRange elementRange() {
        if (range==null) {
            // Lazy creation due to serialization
            range = new CollectionAsRange(set);
        }
        return range;
    }

}
