package ch.ethz.biol.cell.imageprocessing.seed.finder;

import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;

/*-
 * #%L
 * anchor-mpp-sgmn
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.experiment.bean.seed.SeedFinder;
import org.anchoranalysis.image.experiment.identifiers.ImgStackIdentifiers;
import org.anchoranalysis.image.experiment.seed.SeedFinderException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.seed.SeedCollection;
import org.anchoranalysis.image.seed.SeedsFactory;
import org.anchoranalysis.image.sgmn.SgmnFailedException;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.voxel.kernel.ApplyKernel;
import org.anchoranalysis.image.voxel.kernel.dilateerode.DilationKernel3;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.sgmn.cfg.CfgSgmn;
import ch.ethz.biol.cell.sgmn.cfg.ExperimentState;

public class SeedFinderCfgSgmn extends SeedFinder<ExperimentState> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private CfgSgmn cfgSgmn;
	// END BEAN PROPERTIES
	
	@Override
	public SeedCollection findSeeds(NamedImgStackCollection stackCollection, INamedProvider<ObjMaskCollection> objMaskProvider, ExperimentExecutionArguments expArgs, KeyValueParams params, LogErrorReporter logger, BoundOutputManagerRouteErrors outputManager, ExperimentState sharedState) throws SeedFinderException {

		try {
			Cfg cfg = cfgSgmn.sgmn(stackCollection, objMaskProvider, expArgs, params, logger, outputManager);
			return createSeedCollection(cfg, new NRGStack(stackCollection.getException(ImgStackIdentifiers.INPUT_IMAGE)).getDimensions() );
			
		} catch (SgmnFailedException | GetOperationFailedException | CreateException e) {
			throw new SeedFinderException(e);
		}
	}
	
	private static ObjMaskCollection createDilatedMasks( ObjMaskCollection objMasks, ImageDim dim ) throws CreateException {
		int growZ = dim.getZ() > 1 ? 1 : 0;
		Point3i growMargin = new Point3i(1,1, growZ);
		ObjMaskCollection objMasksDilated = objMasks.growBuffer(growMargin, growMargin, dim.getExtnt());
		
		
		for( ObjMask om : objMasksDilated ) {
			// Dilate the voxel box
			// We form a dilation on the entireMaskBox
			DilationKernel3 kernelDilation = new DilationKernel3( om.getBinaryValuesByte(), false, dim.getZ() > 1, false);	
			om.setVoxelBox( ApplyKernel.apply(kernelDilation, om.getVoxelBox() ) );
		}
		return objMasksDilated;
	}
	


	
	public static SeedCollection createSeedCollection( Cfg cfg, ImageDim dim  ) throws CreateException {
		
		// We create object masks for each mark
		ObjMaskCollection objMasks = cfg.calcMask(
			dim,
			RegionMapSingleton.instance().membershipWithFlagsForIndex( GlobalRegionIdentifiers.SUBMARK_INSIDE ),
			BinaryValuesByte.getDefault(),
			null
		).collectionObjMask();
		
		// We create another copy of the masks which are dilated by 1, this helps us catch
		//  neighbouring pixels
		ObjMaskCollection objMasksDilated = createDilatedMasks(objMasks, dim);
		
		// We loop through each mask, and find any other masks that intersect
		for( int i=0; i<objMasks.size(); i++ ) {
			ObjMask omDest = objMasks.get(i);
			
			for( int j=0; j<objMasksDilated.size(); j++ ) {
				
				if (i==j) {
					continue;
				}
				
				ObjMask omOthr1 = objMasksDilated.get(i);
				ObjMask omOthr2 = objMasksDilated.get(j);
				if (omOthr1.getBoundingBox().hasIntersection(omOthr2.getBoundingBox())) {
					// Unsure if this is correct?
					
					ObjMask omIntersectOthers = omOthr1.intersect(omOthr2, dim);
					
					// Now we intersect bounding boxes with what we are writing out
					BoundingBox boxWriteOut = omIntersectOthers.getBoundingBox().intersectCreateNew(omDest.getBoundingBox(), dim.getExtnt() );
					if (boxWriteOut==null) {
						continue;
					}
					
					Point3i maskRelPnt = boxWriteOut.relPosTo( omIntersectOthers.getBoundingBox() );
										
					boxWriteOut.getCrnrMin().sub(omDest.getBoundingBox().getCrnrMin());
					
					BoundingBox maskBox = new BoundingBox(maskRelPnt, boxWriteOut.extnt());
					
					omDest.getVoxelBox().setPixelsCheckMask(boxWriteOut, omIntersectOthers.getVoxelBox(), maskBox, omDest.getBinaryValues().getOffInt(), omIntersectOthers.getBinaryValuesByte().getOnByte());
				}
				
			}
		}
		
		return SeedsFactory.createSeedsWithoutMask(objMasks);
	}

	public CfgSgmn getCfgSgmn() {
		return cfgSgmn;
	}

	public void setCfgSgmn(CfgSgmn cfgSgmn) {
		this.cfgSgmn = cfgSgmn;
	}

	@Override
	public ExperimentState beforeAnySeedFinding(
			BoundOutputManagerRouteErrors outputManager)
			throws ExperimentExecutionException {
		return cfgSgmn.createExperimentState();
	}

	@Override
	public void afterAllSeedFinding(
			BoundOutputManagerRouteErrors outputManager, ExperimentState sharedState)
			throws ExperimentExecutionException {
		sharedState.outputAfterAllTasksAreExecuted(outputManager);
	}
}
