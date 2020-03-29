package org.anchoranalysis.mpp.sgmn.bean.kernel;

/*
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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



import java.io.Serializable;

import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.anchor.mpp.feature.mark.ListUpdatableMarkSetCollection;
import org.anchoranalysis.anchor.mpp.mark.set.UpdateMarkSetException;
import org.anchoranalysis.anchor.mpp.params.ICompatibleWith;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.mpp.sgmn.kernel.KernelCalcContext;
import org.anchoranalysis.mpp.sgmn.kernel.KernelCalcNRGException;

/**
 * Modifies an Object by applying a kernel
 * 
 * @author FEEHANO
 *
 * @param <T> the type of object that is modified
 */
public abstract class Kernel<T> extends MPPBean<Kernel<T>> implements Serializable, ICompatibleWith {

	/**
	 * 
	 */
	private static final long serialVersionUID = 481147618512690557L;

	// START BEAN PROPERTIES
	@BeanField @AllowEmpty
	// This is only decorative. Currently it has no use.
	private String name = "";
	// END BEAN PROPERTIES
	
	public Kernel() {
	}
	
	// Call ONCE before calculating anything
	public abstract void initBeforeCalc(KernelCalcContext context) throws InitException;
		
	/**
	 * Calculates the NRG for a proposal
	 * @param exst the existing NRG
	 * @param context
	 * @return a proposal, or NULL if there is no proposal to make
	 * @throws KernelCalcNRGException
	 */
	public abstract T makeProposal(
		T exst,
		KernelCalcContext context
	) throws KernelCalcNRGException;
	
	public abstract double calcAccptProb( int exstSize, int propSize, double poissonIntens, ImageDim sceneSize, double densityRatio );
	
	public abstract String dscrLast();
	
	/**
	 * If the kernel is accepted, makes the necessary changes to a ListUpdatableMarkSetCollection
	 * 
	 * @param updatableMarkSetCollection where to make the changes
	 * @param nrgExst existing energy
	 * @param nrgNew accepted energy
	 * @throws UpdateMarkSetException
	 */
	public abstract void updateAfterAccpt( ListUpdatableMarkSetCollection updatableMarkSetCollection, T nrgExst, T nrgNew ) throws UpdateMarkSetException;
	
	// Returns an array of Mark IDs that were changed in the last calcNRGForProp for the kernel
	// Guaranteed only to be called, if calcNRGForProp did not return NULL
	public abstract int[] changedMarkIDArray();

	/**
	 * Called every time a proposal is accepted, so a kernel can potentially keep track of the state
	 *   of the current image
	 *  
	 * @param cfgNRG
	 */
	public abstract void informLatestState( T cfgNRG );
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
