/*-
 * #%L
 * anchor-mpp-sgmn
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

package org.anchoranalysis.mpp.sgmn.bean.optscheme.termination;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.log.MessageLogger;

// An OR list of termination conditions
@NoArgsConstructor
public class TerminationConditionListOr extends TerminationCondition {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<TerminationCondition> list = new ArrayList<>();
    // END BEAN PROPERTIES

    public TerminationConditionListOr(TerminationCondition cond1, TerminationCondition cond2) {
        this();
        list.add(cond1);
        list.add(cond2);
    }

    public boolean add(TerminationCondition tc) {
        return list.add(tc);
    }

    public int size() {
        return list.size();
    }

    @Override
    public boolean continueIterations(int crntIter, double score, int size, MessageLogger logger) {

        for (TerminationCondition tc : this.list) {
            if (!tc.continueIterations(crntIter, score, size, logger)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void init() {

        for (TerminationCondition tc : this.list) {
            tc.init();
        }
    }
}
