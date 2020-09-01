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

package org.anchoranalysis.bean.permute.property;

import java.util.Iterator;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;

/**
 * A sequence of integers.
 *
 * <p>The sequence will step from {@code start} to {@code end} with step-sizes of {@code increment}.
 *
 * <p>There is no guarantee that {@code end} will be included, if it doesn't align with the
 * step-size.
 *
 * @author Owen Feehan
 */
public class SequenceInteger extends AnchorBean<SequenceInteger> {

    // START BEAN PROPERTIES
    /** The index to begin iteration from */
    @BeanField @Getter @Setter private int start = 0;

    /** An index beyond which we cannot iterate (inclusive) */
    @BeanField @Getter @Setter private int end = 0;

    /** Size of step to use when incrementing */
    @BeanField @Getter @Setter private int increment = 1;
    // END BEAN PROPERTIES

    @Override
    public void checkMisconfigured(BeanInstanceMap defaultInstances)
            throws BeanMisconfiguredException {
        super.checkMisconfigured(defaultInstances);
        if (end < start) {
            throw new BeanMisconfiguredException(
                    String.format("end (%d) cannot be less than start (%d)", start, end));
        }
    }

    /**
     * An iterator for each element in the sequence
     *
     * @return the iterator
     */
    public Iterator<Integer> iterator() {
        return new SequenceIntegerIterator(start, end, increment);
    }
}
