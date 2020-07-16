/* (C)2020 */
package org.anchoranalysis.anchor.mpp.proposer;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemoFactory;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.extent.ImageDimensions;

public class ProposerContext {

    private RandomNumberGenerator randomNumberGenerator;
    private NRGStackWithParams nrgStack;
    private RegionMap regionMap;
    private ErrorNode errorNode;

    public ProposerContext(
            RandomNumberGenerator randomNumberGenerator,
            NRGStackWithParams nrgStack,
            RegionMap regionMap,
            ErrorNode errorNode) {
        super();
        this.randomNumberGenerator = randomNumberGenerator;
        this.nrgStack = nrgStack;
        this.regionMap = regionMap;
        this.errorNode = errorNode;
    }

    public ProposerContext replaceError(ErrorNode errorNode) {
        return new ProposerContext(randomNumberGenerator, nrgStack, regionMap, errorNode);
    }

    public ProposerContext addErrorLevel(String errorMessage) {
        return new ProposerContext(
                randomNumberGenerator, nrgStack, regionMap, errorNode.add(errorMessage));
    }

    public ProposerContext addErrorLevel(String errorMessage, AnchorBean<?> bean) {
        return new ProposerContext(
                randomNumberGenerator, nrgStack, regionMap, errorNode.addBean(errorMessage, bean));
    }

    /** Samples an integer uniformally between [0..maxVal) */
    public int sampleInteger(int maxValExclusive) {
        return (int) (randomNumberGenerator.sampleDoubleZeroAndOne() * maxValExclusive);
    }

    public RandomNumberGenerator getRandomNumberGenerator() {
        return randomNumberGenerator;
    }

    public ImageDimensions getDimensions() {
        return nrgStack.getNrgStack().getDimensions();
    }

    public VoxelizedMarkMemo create(Mark mark) {
        return PxlMarkMemoFactory.create(mark, nrgStack.getNrgStack(), regionMap);
    }

    public RegionMap getRegionMap() {
        return regionMap;
    }

    public ErrorNode getErrorNode() {
        return errorNode;
    }

    public NRGStackWithParams getNrgStack() {
        return nrgStack;
    }
}
