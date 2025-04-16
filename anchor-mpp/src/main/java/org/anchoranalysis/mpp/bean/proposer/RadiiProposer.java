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
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.bean.MarksBean;
import org.anchoranalysis.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.spatial.orientation.Orientation;
import org.anchoranalysis.spatial.point.Point3d;

/**
 * An abstract base class for proposing radii in a 3D space.
 * <p>
 * This class extends MarksBean and implements CompatibleWithMark,
 * providing a foundation for creating radii proposers in the MPP (Marked Point Process) framework.
 * </p>
 */
public abstract class RadiiProposer extends MarksBean<RadiiProposer> implements CompatibleWithMark {

    /**
     * Proposes a new point representing radii based on the given parameters.
     * <p>
     * When no bounds are provided, bounds should be created from a bound calculator.
     * </p>
     *
     * @param position the position for which to propose radii
     * @param randomNumberGenerator a random number generator for any stochastic processes
     * @param dimensions the dimensions of the space in which the radii are proposed
     * @param orientation the orientation to consider when proposing radii
     * @return an Optional containing the proposed Point3d representing radii, or empty if no proposal is made
     * @throws ProposalAbnormalFailureException if the proposal fails abnormally
     */
    public abstract Optional<Point3d> propose(
            Point3d position,
            RandomNumberGenerator randomNumberGenerator,
            Dimensions dimensions,
            Orientation orientation)
            throws ProposalAbnormalFailureException;
}