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
/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.nrg.cfg;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoList;
import org.anchoranalysis.anchor.mpp.feature.nrg.saved.NRGSavedAll;
import org.anchoranalysis.anchor.mpp.feature.nrg.saved.NRGSavedInd;
import org.anchoranalysis.anchor.mpp.feature.nrg.saved.NRGSavedPairs;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGSchemeWithSharedFeatures;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.set.UpdateMarkSetException;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

@AllArgsConstructor
public class CfgNRG implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    // START REQUIRED ARGUMENTS
    private final CfgWithNRGTotal delegate;

    private NRGSavedInd calcMarkInd;

    // We store every combination of interactions between marks
    private transient NRGSavedPairs calcMarkPair;

    // Certain features are stored for every object, so that we can reference
    //  them in our calculations for the 'all' component
    private transient NRGSavedAll calcMarkAll;
    // END REQUIRED ARGUMENTS

    public CfgNRG(CfgWithNRGTotal delegate) {
        this.delegate = delegate;
        this.calcMarkInd = null;
        this.calcMarkPair = null;
        this.calcMarkAll = null;
    }

    // The initial calculation of the NRG, thereafter it can be updated
    public void init() throws FeatureCalcException {
        this.calcMarkInd = new NRGSavedInd();
        try {
            this.calcMarkPair = new NRGSavedPairs(delegate.getNrgScheme().createAddCriteria());
            this.calcMarkAll = new NRGSavedAll();
        } catch (CreateException e) {
            throw new FeatureCalcException(e);
        }
    }

    public void assertValid() {
        if (this.calcMarkInd != null) {
            this.calcMarkInd.assertValid();
        }
        if (this.calcMarkPair != null) {
            this.calcMarkPair.assertValid();
        }
        if (this.calcMarkInd != null && this.calcMarkPair != null) {
            assert (Math.abs(
                            delegate.getNrgTotal()
                                    - calcMarkInd.getNrgTotal()
                                    - calcMarkPair.getNRGTotal())
                    < 1e-3);
        }
    }

    // This should be accessed read-only
    public CfgWithNRGTotal getCfgWithTotal() {
        return delegate;
    }

    public void updateTotal(MemoCollection pxlMarkMemoList, NRGStack stack)
            throws FeatureCalcException {

        // We calculate an all value
        this.calcMarkAll.calc(pxlMarkMemoList, delegate.getNrgScheme(), stack);

        double total =
                this.calcMarkInd.getNrgTotal()
                        + this.calcMarkPair.getNRGTotal()
                        + this.calcMarkAll.getNRGTotal();
        assert (!Double.isNaN(total));
        delegate.setNrgTotal(total);
    }

    public CfgNRG shallowCopy() {
        return new CfgNRG(
                delegate.shallowCopy(),
                calcMarkInd.shallowCopy(),
                calcMarkPair.shallowCopy(),
                calcMarkAll.shallowCopy());
    }

    public CfgNRG deepCopy() {
        return new CfgNRG(
                delegate.deepCopy(),
                calcMarkInd.deepCopy(),
                calcMarkPair.deepCopy(),
                calcMarkAll.deepCopy());
    }

    public double getNrgTotal() {
        return delegate.getNrgTotal();
    }

    public Cfg getCfg() {
        return delegate.getCfg();
    }

    public NRGSchemeWithSharedFeatures getNrgScheme() {
        return delegate.getNrgScheme();
    }

    public void add(MemoCollection wrapperInd, VoxelizedMarkMemo newPxlMarkMemo, NRGStack stack)
            throws FeatureCalcException {

        delegate.add(newPxlMarkMemo);

        // Individuals
        wrapperInd.add(getCalcMarkInd(), newPxlMarkMemo, stack, delegate.getNrgScheme());

        // Pairs
        try {
            getCalcMarkPair().add(wrapperInd, newPxlMarkMemo);
        } catch (UpdateMarkSetException e) {
            throw new FeatureCalcException(e);
        }

        updateTotal(wrapperInd, stack);
    }

    public void rmv(MemoCollection wrapperInd, VoxelizedMarkMemo memoRmv, NRGStack stack)
            throws FeatureCalcException {

        int index = wrapperInd.getIndexForMemo(memoRmv);
        assert (index != -1);
        rmv(wrapperInd, index, memoRmv, stack);
    }

    public void rmv(MemoCollection wrapperInd, int index, VoxelizedMarkMemo memoRmv, NRGStack stack)
            throws FeatureCalcException {
        try {
            getCalcMarkPair().rmv(wrapperInd, memoRmv);
            wrapperInd.rmv(getCalcMarkInd(), index);

            rmv(index);

            updateTotal(wrapperInd, stack);
        } catch (UpdateMarkSetException e) {
            throw new FeatureCalcException(e);
        }
    }

    public void rmvTwo(MemoCollection wrapperInd, int index1, int index2, NRGStack nrgStack)
            throws FeatureCalcException {

        VoxelizedMarkMemo memoRmv1 = wrapperInd.getMemoForIndex(index1);
        VoxelizedMarkMemo memoRmv2 = wrapperInd.getMemoForIndex(index2);

        wrapperInd.rmvTwo(getCalcMarkInd(), index1, index2);

        Cfg newCfg = delegate.getCfg().shallowCopy();

        MemoList memoList = new MemoList();
        memoList.addAll(wrapperInd);

        try {
            getCalcMarkPair().rmv(memoList, memoRmv1);
            memoList.remove(memoRmv1);

            getCalcMarkPair().rmv(memoList, memoRmv2);
        } catch (UpdateMarkSetException e) {
            throw new FeatureCalcException(e);
        }

        newCfg.removeTwo(index1, index2);

        delegate.setCfg(newCfg);

        updateTotal(wrapperInd, nrgStack);
    }

    // calculates a new energy and configuration based upon a mark at a particular index
    //   changing into new mark
    public void exchange(
            MemoCollection wrapperInd,
            int index,
            VoxelizedMarkMemo newMark,
            NRGStackWithParams nrgStack)
            throws FeatureCalcException {

        Mark oldMark = delegate.getCfg().get(index);

        delegate.exchange(index, newMark);

        // We do the exchange on the calcMarkInd first, as our calcMarkPair is expressed relative
        VoxelizedMarkMemo newPxlMarkMemo =
                wrapperInd.exchange(
                        getCalcMarkInd(),
                        index,
                        newMark,
                        nrgStack.getNrgStack(),
                        delegate.getNrgScheme());
        try {
            VoxelizedMarkMemo oldPxlMarkMemo = wrapperInd.getMemoForMark(getCfg(), oldMark);
            getCalcMarkPair().exchange(wrapperInd, oldPxlMarkMemo, index, newPxlMarkMemo);
        } catch (UpdateMarkSetException e) {
            throw new FeatureCalcException(e);
        }

        assert (getCalcMarkPair().isCfgSpan(delegate.getCfg()));

        // we can also calculate both afresh, but slower
        updateTotal(wrapperInd, nrgStack.getNrgStack());

        assert ((delegate.getNrgTotal()
                        - getCalcMarkInd().getNrgTotal()
                        - getCalcMarkPair().getNRGTotal())
                < 1e-6);
    }

    public void rmv(int index) {
        delegate.rmv(index);
    }

    public void exchange(int index, VoxelizedMarkMemo newMark) {
        delegate.exchange(index, newMark);
    }

    public void setCfg(Cfg cfg) {
        delegate.setCfg(cfg);
    }

    public NRGSavedInd getCalcMarkInd() {
        return calcMarkInd;
    }

    public NRGSavedPairs getCalcMarkPair() {
        return calcMarkPair;
    }

    @Override
    public String toString() {

        String newLine = System.getProperty("line.separator");

        StringBuilder s = new StringBuilder("{");

        s.append(
                String.format(
                        "size=%d, total=%e, ind=%e, pair=%e%n",
                        getCfg().size(),
                        getNrgTotal(),
                        getCalcMarkInd().getNrgTotal(),
                        getCalcMarkPair().getNRGTotal()));

        s.append(getCalcMarkInd().stringCfgNRG(getCfg()));
        s.append(getCalcMarkPair().toString());

        s.append("}");
        s.append(newLine);

        return s.toString();
    }

    public NRGSavedAll getCalcMarkAll() {
        return calcMarkAll;
    }

    public void setCalcMarkAll(NRGSavedAll calcMarkAll) {
        this.calcMarkAll = calcMarkAll;
    }
}
