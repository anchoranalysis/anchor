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

package org.anchoranalysis.bean.shared.relation;

import lombok.EqualsAndHashCode;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.GenerateUniqueParameterization;
import org.anchoranalysis.math.relation.DoubleBiPredicate;

/**
 * Base class that specifies a <a
 * href="https://en.wikipedia.org/wiki/Relation_(mathematics)">relation</a> between two {@code
 * double}s.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = false)
public abstract class RelationBean extends AnchorBean<RelationBean>
        implements GenerateUniqueParameterization {

    /**
     * Creates a {@link DoubleBiPredicate} that implements the relation.
     *
     * @return the relation as a predicate.
     */
    public abstract DoubleBiPredicate create();

    @Override
    public abstract String toString();

    // This is sufficient for all base-classes, as we rely on them not being further parameterized
    @Override
    public String uniqueName() {
        return getClass().getCanonicalName();
    }
}
