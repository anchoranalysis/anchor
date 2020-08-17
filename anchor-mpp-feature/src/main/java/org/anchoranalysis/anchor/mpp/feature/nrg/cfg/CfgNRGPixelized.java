/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.anchor.mpp.feature.nrg.cfg;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.mark.ListUpdatableMarkSetCollection;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoList;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGSchemeWithSharedFeatures;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.set.UpdateMarkSetException;
import org.anchoranalysis.anchor.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

/**
 * Contains a particular energy configuration
 *
 * <p>i.e. A {@link CfgNRG} together with the cached voxelization calculations.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CfgNRGPixelized {

    @Getter private CfgNRG cfgNRG;

    /**
     * A cached version of the calculations for each energy component in the associated {@link
     * NRGScheme}
     */
    private MemoCollection memoMarks;

    private Logger logger;

    public CfgNRGPixelized(
            CfgNRG cfgNRG,
            NRGStackWithParams nrgStack,
            SharedFeatureMulti sharedFeatures,
            Logger logger)
            throws NamedFeatureCalculateException {
        this(cfgNRG, createMemoCollection(cfgNRG, nrgStack, sharedFeatures, logger), logger);
    }

    // Copy constructor - we do shallow copying of configuration
    public CfgNRGPixelized shallowCopy() {
        return new CfgNRGPixelized(cfgNRG.shallowCopy(), new MemoCollection(memoMarks), logger);
    }

    // Copy constructor - we do shallow copying of configuration
    public CfgNRGPixelized deepCopy() {
        return new CfgNRGPixelized(cfgNRG.deepCopy(), new MemoCollection(memoMarks), logger);
    }

    public Cfg getCfg() {
        return cfgNRG.getCfg();
    }

    public void clean() {
        memoMarks.clean();
    }

    public NRGSchemeWithSharedFeatures getNRGScheme() {
        return cfgNRG.getNrgScheme();
    }

    public void add(VoxelizedMarkMemo newPxlMark, NRGStack stack)
            throws NamedFeatureCalculateException {
        cfgNRG.add(memoMarks, newPxlMark, stack);
    }

    public void rmv(int index, NRGStack stack) throws NamedFeatureCalculateException {
        VoxelizedMarkMemo memoRmv = getMemoForIndex(index);
        cfgNRG.rmv(memoMarks, index, memoRmv, stack);
    }

    public void rmv(VoxelizedMarkMemo memoRmv, NRGStack stack)
            throws NamedFeatureCalculateException {
        cfgNRG.rmv(memoMarks, memoRmv, stack);
    }

    public void rmvTwo(int index1, int index2, NRGStack stack)
            throws NamedFeatureCalculateException {
        cfgNRG.rmvTwo(memoMarks, index1, index2, stack);
    }

    // Does the pairs hash only contains items contained in a particular configuration
    public boolean isCfgSpan() {
        return cfgNRG.getPair().isCfgSpan(cfgNRG.getCfg());
    }

    // calculates a new energy and configuration based upon a mark at a particular index
    //   changing into new mark
    public void exchange(int index, VoxelizedMarkMemo newMark, NRGStackWithParams nrgStack)
            throws NamedFeatureCalculateException {
        cfgNRG.exchange(memoMarks, index, newMark, nrgStack);
    }

    public double getTotal() {
        return cfgNRG.getNrgTotal();
    }

    // Adds all current marks to the updatable-pair list
    public void addAllToUpdatablePairList(ListUpdatableMarkSetCollection updatablePairList)
            throws UpdateMarkSetException {
        updatablePairList.add(memoMarks);
    }

    // Adds the particular memo to the updatable pair-list
    public void addToUpdatablePairList(
            ListUpdatableMarkSetCollection updatablePairList, VoxelizedMarkMemo memo)
            throws UpdateMarkSetException {
        updatablePairList.add(memoMarks, memo);
    }

    // Removes a memo from the updatable pair-list
    public void rmvFromUpdatablePairList(
            ListUpdatableMarkSetCollection updatablePairList, Mark mark)
            throws UpdateMarkSetException {
        VoxelizedMarkMemo memo = getMemoForMark(mark);
        updatablePairList.rmv(memoMarks, memo);
    }

    // Exchanges one mark with another on the updatable pair list
    public void exchangeOnUpdatablePairList(
            ListUpdatableMarkSetCollection updatablePairList,
            Mark markExst,
            VoxelizedMarkMemo memoNew)
            throws UpdateMarkSetException {
        VoxelizedMarkMemo memoExst = getMemoForMark(markExst);
        updatablePairList.exchange(memoMarks, memoExst, getCfg().indexOf(markExst), memoNew);
    }

    public MemoList createDuplicatePxlMarkMemoList() {
        MemoList list = new MemoList();
        list.addAll(memoMarks);
        return list;
    }

    public VoxelizedMarkMemo getMemoForMark(Mark mark) {
        return memoMarks.getMemoForMark(cfgNRG.getCfg(), mark);
    }

    public VoxelizedMarkMemo getMemoForIndex(int index) {
        return memoMarks.getMemoForIndex(index);
    }

    @Override
    public String toString() {

        String newLine = System.getProperty("line.separator");

        StringBuilder s = new StringBuilder("{");

        s.append(
                String.format(
                        "size=%d, total=%e, ind=%e, pair=%e%n",
                        cfgNRG.getCfg().size(),
                        cfgNRG.getNrgTotal(),
                        cfgNRG.getIndividual().getNrgTotal(),
                        cfgNRG.getPair().getNRGTotal()));

        s.append(cfgNRG.getIndividual().stringCfgNRG(cfgNRG.getCfg()));
        s.append(cfgNRG.getPair().toString());

        s.append("}");
        s.append(newLine);

        return s.toString();
    }

    // The initial calculation of the NRG, thereafter it can be updated
    private static MemoCollection createMemoCollection(
            CfgNRG cfgNRG,
            NRGStackWithParams nrgStack,
            SharedFeatureMulti sharedFeatures,
            Logger logger)
            throws NamedFeatureCalculateException {
        try {
            cfgNRG.init();

            MemoCollection memo =
                    new MemoCollection(
                            cfgNRG.getIndividual(),
                            nrgStack.getNrgStack(),
                            cfgNRG.getCfg(),
                            cfgNRG.getNrgScheme());

            cfgNRG.getPair().initUpdatableMarkSet(memo, nrgStack, logger, sharedFeatures);

            // Some nrg components need to be calculated in terms of interactions
            //  this we need to track in an intelligent way
            cfgNRG.updateTotal(memo, nrgStack.getNrgStack());

            return memo;
        } catch (InitException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }
}
