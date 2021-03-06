/*-
 * #%L
 * anchor-image-feature
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

package org.anchoranalysis.image.feature.input;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.input.FeatureInputEnergy;
import org.anchoranalysis.image.core.merge.ObjectMaskMerger;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * A pair of objects (first and second) and maybe a merged version of both
 *
 * <p>Note that left and right simply identify two parts of the pair (tuple). It has no physical
 * meaning related to where the objects are located in the scene..
 *
 * <p>If a merged version doesn't exist, it is created and cached on demand.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public class FeatureInputPairObjects extends FeatureInputEnergy {

    private ObjectMask first;
    private ObjectMask second;

    private Optional<ObjectMask> merged = Optional.empty();

    public FeatureInputPairObjects(ObjectMask first, ObjectMask second) {
        this(first, second, Optional.empty());
    }

    public FeatureInputPairObjects(ObjectMask first, ObjectMask second, EnergyStack energyStack) {
        this(first, second, Optional.of(energyStack));
    }

    public FeatureInputPairObjects(
            ObjectMask first, ObjectMask second, Optional<EnergyStack> energyStack) {
        this(first, second, energyStack, Optional.empty());
    }

    public FeatureInputPairObjects(
            ObjectMask first,
            ObjectMask second,
            Optional<EnergyStack> energyStack,
            Optional<ObjectMask> merged) {
        super(energyStack);
        this.first = first;
        this.second = second;
        this.merged = merged;
    }

    protected FeatureInputPairObjects(FeatureInputPairObjects src) {
        super(src.getEnergyStackOptional());
        this.first = src.first;
        this.second = src.second;
        this.merged = src.merged;
    }

    public ObjectMask getFirst() {
        return first;
    }

    public ObjectMask getSecond() {
        return second;
    }

    /**
     * Returns a merged version of the two-objects available (or null if not available)
     *
     * @return the merged object-mask
     */
    public ObjectMask getMerged() {
        if (!merged.isPresent()) {
            merged = Optional.of(ObjectMaskMerger.merge(first, second));
        }
        return merged.get();
    }

    public Optional<ObjectMask> getMergedOptional() {
        return merged;
    }

    @Override
    public String toString() {
        return String.format("%s vs %s", first.centerOfGravity(), second.centerOfGravity());
    }
}
