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

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.bean.NullParametersBean;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;

/** An abstract base class for proposing a list of points based on a given point and mark. */
public abstract class PointsProposer extends NullParametersBean<PointsProposer>
        implements CompatibleWithMark {

    /**
     * Proposes a list of points based on the given parameters.
     *
     * @param point the reference point for the proposal
     * @param mark the mark associated with the proposal
     * @param dimensions the dimensions of the space in which the points are proposed
     * @param randomNumberGenerator a random number generator for any stochastic processes
     * @param errorNode an error node for reporting any errors during the proposal process
     * @return an Optional containing a List of proposed Point3i, or empty if no proposal is made
     * @throws ProposalAbnormalFailureException if the proposal fails abnormally
     */
    public abstract Optional<List<Point3i>> propose(
            Point3d point,
            Mark mark,
            Dimensions dimensions,
            RandomNumberGenerator randomNumberGenerator,
            ErrorNode errorNode)
            throws ProposalAbnormalFailureException;
}
