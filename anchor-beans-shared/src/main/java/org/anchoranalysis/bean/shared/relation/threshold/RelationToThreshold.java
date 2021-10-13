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

package org.anchoranalysis.bean.shared.relation.threshold;

import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.GenerateUniqueParameterization;
import org.anchoranalysis.math.relation.DoubleBiPredicate;

/**
 * A base class specifying threshold and a relation to it, allowing for tests of a value in relation to a threshold.
 *
 * @author Owen Feehan
 */
public abstract class RelationToThreshold extends AnchorBean<RelationToThreshold>
        implements GenerateUniqueParameterization {

    /** 
     * The threshold-value.
     *
     * @return the value.
     */
    public abstract double threshold();

    /** 
     * The relation to the threshold.
     * 
     * @return the predicate, where the threshold will form the second operand.
     */
    public abstract DoubleBiPredicate relation();

    /**
     * Expresses the relation as a predicate on values, where only values that fulfill the relation
     * to threshold are true.
     *
     * @return a newly-created predicate that accepts an double-value.
     */
    public DoublePredicate asPredicateDouble() {
        double thresholdInstance = threshold();
        DoubleBiPredicate relationInstance = relation();
        return value -> relationInstance.test(value, thresholdInstance);
    }

    /**
     * Like {@link #asPredicateDouble()} but expresses the relationship for an integer.
     *
     * @return a newly-created predicate that accepts an integer-value.
     */
    public IntPredicate asPredicateInt() {
        double thresholdInstance = threshold();
        DoubleBiPredicate relationInstance = relation();
        return value -> relationInstance.test(value, thresholdInstance);
    }
}
