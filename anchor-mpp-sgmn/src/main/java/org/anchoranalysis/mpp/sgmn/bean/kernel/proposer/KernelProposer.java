package org.anchoranalysis.mpp.sgmn.bean.kernel.proposer;

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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.mpp.sgmn.bean.kernel.Kernel;
import org.anchoranalysis.mpp.sgmn.kernel.KernelCalcContext;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelWithID;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.WeightedKernel;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.WeightedKernelList;

public class KernelProposer<T> extends AnchorBean<KernelProposer<T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3588049652758462324L;
	
	// START BEAN
	@BeanField
	private List<KernelProposerOption<T>> optionList = new ArrayList<>();
	
	@BeanField
	private Kernel<T> initialKernel;
	// END BEAN

	private double[] cumProbArr;
	private ArrayList<WeightedKernel<T>> lstKernelFactories = null;
	
	// We need a list of options
	public KernelProposer() {
	}
			
	public void init() throws InitException {
		calcCumProb( optionList );
		
		// Ensure no two kernel factories have the same name
		EnsureUniqueNames.apply( lstKernelFactories );		
	}
		
	public void initWithProposerSharedObjects( MPPInitParams so, LogErrorReporter logger ) throws InitException {
		
		for (WeightedKernel<T> wkf : lstKernelFactories) {
			wkf.getKernel().initRecursive(so, logger);
		}
	}
		
	public void initBeforeCalc( KernelCalcContext context ) throws InitException {
		for (WeightedKernel<T> wkf : lstKernelFactories) {
			wkf.getKernel().initBeforeCalc(context);
		}
	}
	
	public KernelWithID<T> initialKernel( RandomNumberGenerator re ) {
		return new KernelWithID<T>(lstKernelFactories.get(0).getKernel(),0);
	}
	
	// Proposes a kernel
	public KernelWithID<T> proposeKernel( RandomNumberGenerator re ) {
		
		double rand = re.nextDouble();
		return proposeKernel( rand );
	}
	
	public int getNumKernel() {
		assert lstKernelFactories!= null;
		return lstKernelFactories.size();
	}
	
	public WeightedKernelList<T> getAllKernelFactories() {
		
		WeightedKernelList<T> listOut = new WeightedKernelList<T>();
		for( int i=0; i<getNumKernel(); i++) {
			WeightedKernel<T> wkf = lstKernelFactories.get(i);
			listOut.add( wkf );
		}
		return listOut;
	}
	
	public String[] createKernelFactoryNames() {
		
		String[] namesOut = new String[ getNumKernel() ];
		for( int i=0; i<getNumKernel(); i++) {
			namesOut[i] = lstKernelFactories.get(i).getKernel().getBeanName();
		}
		return namesOut;
	}
	
	public WeightedKernel<T> getWeightedKernelFactory( int i ) {
		return this.lstKernelFactories.get(i);
	}
	
	// View of the kernel proposer
	@Override
	public String toString() {
		
		String newLine = System.getProperty("line.separator");
		
		StringBuilder sb = new StringBuilder();
		
		sb.append( "{size=" );
		sb.append( getNumKernel());
		sb.append( newLine );
		for ( int i=0; i<getNumKernel(); i++ ) {
			sb.append( String.format("%d: %s%n", i, getWeightedKernelFactory(i).toString()) );
		}
		sb.append( "}" );
		
		return sb.toString();
	}

	public void checkCompatibleWith(Mark testMark) {

		for (WeightedKernel<T> wkf : lstKernelFactories) {
			if (!wkf.getKernel().isCompatibleWith(testMark)) {
				throw new UnsupportedOperationException( String.format("Kernel %s is not compatible with templateMark", wkf.getName()) );
			}
		}
	}

	public Kernel<T> getInitialKernel() {
		return initialKernel;
	}

	public void setInitialKernel(Kernel<T> initialKernel) {
		this.initialKernel = initialKernel;
	}

	public List<KernelProposerOption<T>> getOptionList() {
		return optionList;
	}

	public void setOptionList(List<KernelProposerOption<T>> optionList) {
		this.optionList = optionList;
	}

	private void calcCumProb( List<KernelProposerOption<T>> options ) throws InitException {
		
		if (options.size()==0) {
			throw new InitException("At least one option must be specified");
		}
		
		lstKernelFactories = new ArrayList<>();
		
		// We add our initial kernel to the list, but weight it with 0, so it cannot
		// ordinarily be chosen
		// THIS MUST BE THE FIRST ITEM OF THE LIST, so we can pick from it later
		//   see proposeKernel
		lstKernelFactories.add( new WeightedKernel<T>(initialKernel, 0.0));
		
		// First we get a sum of all prob for normalization
		// and we population the lst kernel factories
		double total = 0;
		for( KernelProposerOption<T> opt : options ) {
			total += opt.addWeightedKernelFactories(lstKernelFactories);
		}		

		if (total==0) {
			throw new InitException("The total weights of the kernel-factories must be > 0");
		}
		
		// We a derived array with the cumulative probabilities
		cumProbArr = new double[ lstKernelFactories.size() ];
		double running = 0;
		for( int i=0; i<lstKernelFactories.size(); i++ ) {
			running += (lstKernelFactories.get(i).getWeight() / total);
			cumProbArr[i] = running;
		}
	}
	
	// Proposes a kernel
	private KernelWithID<T> proposeKernel( double rand ) {
		
		for( int i=0; i<getNumKernel(); i++) {

			WeightedKernel<T> wkf = getWeightedKernelFactory(i);
			
			if (rand<cumProbArr[i] ) {
				return new KernelWithID<T>( wkf.getKernel(), i );
			} 
		}
		
		assert false;
		return null;
	}
}
