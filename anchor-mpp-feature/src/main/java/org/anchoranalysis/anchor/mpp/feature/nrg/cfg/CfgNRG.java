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

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoList;
import org.anchoranalysis.anchor.mpp.feature.nrg.saved.NRGSavedAll;
import org.anchoranalysis.anchor.mpp.feature.nrg.saved.NRGSavedInd;
import org.anchoranalysis.anchor.mpp.feature.nrg.saved.NRGSavedPairs;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGSchemeWithSharedFeatures;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.set.UpdateMarkSetException;
import org.anchoranalysis.anchor.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

@AllArgsConstructor
public class CfgNRG implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    // START REQUIRED ARGUMENTS
    private final CfgWithNRGTotal delegate;

    @Getter private NRGSavedInd individual;

    // We store every combination of interactions between marks
    @Getter private transient NRGSavedPairs pair;

    // Certain features are stored for every object, so that we can reference
    //  them in our calculations for the 'all' component
    @Getter @Setter private transient NRGSavedAll all;
    // END REQUIRED ARGUMENTS

    public CfgNRG(CfgWithNRGTotal delegate) {
        this.delegate = delegate;
        this.individual = null;
        this.pair = null;
        this.all = null;
    }

    // The initial calculation of the NRG, thereafter it can be updated
    public void init() throws NamedFeatureCalculateException {
        this.individual = new NRGSavedInd();
        try {
            this.pair = new NRGSavedPairs(delegate.getNrgScheme().createAddCriteria());
            this.all = new NRGSavedAll();
        } catch (CreateException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }

    public void assertValid() {
        if (this.individual != null) {
            this.individual.assertValid();
        }
        if (this.pair != null) {
            this.pair.assertValid();
        }
        if (this.individual != null && this.pair != null) {
            assert (Math.abs(
                            delegate.getNrgTotal()
                                    - individual.getNrgTotal()
                                    - pair.getNRGTotal())
                    < 1e-3);
        }
    }

    // This should be accessed read-only
    public CfgWithNRGTotal getCfgWithTotal() {
        return delegate;
    }

    public void updateTotal(MemoCollection pxlMarkMemoList, NRGStack stack)
            throws NamedFeatureCalculateException {

        // We calculate an all value
        this.all.calc(pxlMarkMemoList, delegate.getNrgScheme(), stack);

        double total =
                this.individual.getNrgTotal()
                        + this.pair.getNRGTotal()
                        + this.all.getNRGTotal();
        assert (!Double.isNaN(total));
        delegate.setNrgTotal(total);
    }

    public CfgNRG shallowCopy() {
        return new CfgNRG(
                delegate.shallowCopy(),
                individual.shallowCopy(),
                pair.shallowCopy(),
                all.shallowCopy());
    }

    public CfgNRG deepCopy() {
        return new CfgNRG(
                delegate.deepCopy(),
                individual.deepCopy(),
                pair.deepCopy(),
                all.deepCopy());
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
            throws NamedFeatureCalculateException {

        delegate.add(newPxlMarkMemo);

        // Individuals
        wrapperInd.add(getIndividual(), newPxlMarkMemo, stack, delegate.getNrgScheme());

        // Pairs
        try {
            getPair().add(wrapperInd, newPxlMarkMemo);
        } catch (UpdateMarkSetException e) {
            throw new NamedFeatureCalculateException(e);
        }

        updateTotal(wrapperInd, stack);
    }

    public void rmv(MemoCollection wrapperInd, VoxelizedMarkMemo memoRmv, NRGStack stack)
            throws NamedFeatureCalculateException {

        int index = wrapperInd.getIndexForMemo(memoRmv);
        assert (index != -1);
        rmv(wrapperInd, index, memoRmv, stack);
    }

    public void rmv(MemoCollection wrapperInd, int index, VoxelizedMarkMemo memoRmv, NRGStack stack)
            throws NamedFeatureCalculateException {
        try {
            getPair().rmv(wrapperInd, memoRmv);
            wrapperInd.rmv(getIndividual(), index);

            rmv(index);

            updateTotal(wrapperInd, stack);
        } catch (UpdateMarkSetException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }

    public void rmvTwo(MemoCollection wrapperInd, int index1, int index2, NRGStack nrgStack)
            throws NamedFeatureCalculateException {

        VoxelizedMarkMemo memoRmv1 = wrapperInd.getMemoForIndex(index1);
        VoxelizedMarkMemo memoRmv2 = wrapperInd.getMemoForIndex(index2);

        wrapperInd.rmvTwo(getIndividual(), index1, index2);

        Cfg newCfg = delegate.getCfg().shallowCopy();

        MemoList memoList = new MemoList();
        memoList.addAll(wrapperInd);

        try {
            getPair().rmv(memoList, memoRmv1);
            memoList.remove(memoRmv1);

            getPair().rmv(memoList, memoRmv2);
        } catch (UpdateMarkSetException e) {
            throw new NamedFeatureCalculateException(e);
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
            throws NamedFeatureCalculateException {

        Mark oldMark = delegate.getCfg().get(index);

        delegate.exchange(index, newMark);

        // We do the exchange on the individual first, as our pair is expressed relative
        VoxelizedMarkMemo newPxlMarkMemo =
                wrapperInd.exchange(
                        getIndividual(),
                        index,
                        newMark,
                        nrgStack.getNrgStack(),
                        delegate.getNrgScheme());
        try {
            VoxelizedMarkMemo oldPxlMarkMemo = wrapperInd.getMemoForMark(getCfg(), oldMark);
            getPair().exchange(wrapperInd, oldPxlMarkMemo, index, newPxlMarkMemo);
        } catch (UpdateMarkSetException e) {
            throw new NamedFeatureCalculateException(e);
        }

        assert (getPair().isCfgSpan(delegate.getCfg()));

        // we can also calculate both afresh, but slower
        updateTotal(wrapperInd, nrgStack.getNrgStack());

        assert ((delegate.getNrgTotal()
                        - getIndividual().getNrgTotal()
                        - getPair().getNRGTotal())
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

    @Override
    public String toString() {

        String newLine = System.getProperty("line.separator");

        StringBuilder s = new StringBuilder("{");

        s.append(
                String.format(
                        "size=%d, total=%e, ind=%e, pair=%e%n",
                        getCfg().size(),
                        getNrgTotal(),
                        getIndividual().getNrgTotal(),
                        getPair().getNRGTotal()));

        s.append(getIndividual().stringCfgNRG(getCfg()));
        s.append(getPair().toString());

        s.append("}");
        s.append(newLine);

        return s.toString();
    }
}
