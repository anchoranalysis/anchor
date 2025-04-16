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

package org.anchoranalysis.mpp.proposer;

import lombok.Value;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemoFactory;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.mpp.proposer.error.ErrorNode;

/**
 * Context for proposing operations in the MPP framework.
 * <p>
 * This class encapsulates various components needed for proposing operations,
 * including random number generation, energy stack, region map, and error handling.
 * </p>
 */
@Value
public class ProposerContext {

    /** Random number generator for sampling operations. */
    private RandomNumberGenerator randomNumberGenerator;

    /** Energy stack for the current context. */
    private EnergyStack energyStack;

    /** Region map for the current context. */
    private RegionMap regionMap;

    /** Operation context for the current operation. */
    private OperationContext operationContext;

    /** Error node for tracking and managing errors. */
    private ErrorNode errorNode;

    /**
     * Creates a new ProposerContext with a replaced error node.
     *
     * @param errorNode The new error node to use
     * @return A new ProposerContext with the updated error node
     */
    public ProposerContext replaceError(ErrorNode errorNode) {
        return new ProposerContext(
                randomNumberGenerator, energyStack, regionMap, operationContext, errorNode);
    }

    /**
     * Creates a new ProposerContext with an additional error level.
     *
     * @param errorMessage The error message to add
     * @return A new ProposerContext with the added error level
     */
    public ProposerContext addErrorLevel(String errorMessage) {
        return new ProposerContext(
                randomNumberGenerator,
                energyStack,
                regionMap,
                operationContext,
                errorNode.add(errorMessage));
    }

    /**
     * Samples an integer uniformly between [0..maxValExclusive).
     *
     * @param maxValExclusive The exclusive upper bound for the sampled integer
     * @return A randomly sampled integer
     */
    public int sampleInteger(int maxValExclusive) {
        return (int) (randomNumberGenerator.sampleDoubleZeroAndOne() * maxValExclusive);
    }

    /**
     * Gets the dimensions of the energy stack.
     *
     * @return The dimensions of the energy stack
     */
    public Dimensions dimensions() {
        return energyStack.dimensions();
    }

    /**
     * Creates a VoxelizedMarkMemo for the given mark.
     *
     * @param mark The mark to create a VoxelizedMarkMemo for
     * @return A new VoxelizedMarkMemo
     */
    public VoxelizedMarkMemo create(Mark mark) {
        return VoxelizedMarkMemoFactory.create(mark, energyStack.withoutParameters(), regionMap);
    }
}