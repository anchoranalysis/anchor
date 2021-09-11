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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonNegative;
import org.anchoranalysis.bean.shared.relation.RelationBean;
import org.anchoranalysis.math.relation.DoubleBiPredicate;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RelationToConstant extends RelationToThreshold {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private RelationBean relation;

    @BeanField @NonNegative @Getter @Setter private double threshold = -1;
    // END BEAN PROPERTIES

    @Override
    public double threshold() {
        return threshold;
    }

    @Override
    public DoubleBiPredicate relation() {
        return relation.create();
    }

    @Override
    public String toString() {
        return String.format("%s %f", relation, threshold);
    }

    @Override
    public String uniqueName() {
        return String.format(
                "%s_%s_%d", getClass().getCanonicalName(), relation.uniqueName(), threshold);
    }
}
