/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.bean.proposer;

import java.util.Optional;
import org.anchoranalysis.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.mpp.proposer.ProposerContext;

/**
 * An abstract base class for proposing a selection of a single mark from a collection of marks.
 */
public abstract class MarkFromCollectionProposer extends ProposerBean<MarkFromCollectionProposer>
        implements CompatibleWithMark {

    /**
     * Selects a mark from a collection of marks.
     *
     * @param marks the collection of marks to select from
     * @param context the context for the proposal
     * @return an Optional containing the selected Mark, or empty if no selection is made
     * @throws ProposalAbnormalFailureException if the selection process fails abnormally
     */
    public abstract Optional<Mark> selectMarkFrom(MarkCollection marks, ProposerContext context)
            throws ProposalAbnormalFailureException;
}