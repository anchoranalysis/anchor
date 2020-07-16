package org.anchoranalysis.anchor.mpp.feature.addcriteria;

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


import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoList;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.set.UpdateMarkSetException;
import org.anchoranalysis.anchor.mpp.pair.PairCollection;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.MemoForIndex;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.graph.EdgeTypeWithVertices;
import org.anchoranalysis.core.graph.GraphWithEdgeTypes;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

/**
 * An implementation of a PairCollection that uses {@link AddCriteria} to determine
 *   how pairs are formed.
 *   
 *   Note: this is not a valid-bean on its own, as there is no default public constructor
 *   So if we use it in BeanXML, we must sub-class it with such a constructor.
 *     
 *   However, it is useful to keep the class non-abstract, as when the copy methods
 *   (shallowCopy, deepCopy etc.) are called, we can instantiate an instance of this class
 * 
 * @author Owen Feehan
 *
 * @param <T> type of the pair
 */
public class PairCollectionAddCriteria<T> extends PairCollection<T> {
	
	private GraphWithEdgeTypes<Mark,T> graph;
	
	// START BEAN PROPERTIES
	@BeanField
	private Class<?> pairTypeClass;
	
	@BeanField
	private AddCriteria<T> addCriteria;
	// END BEAN PROPERTIES
	
	private boolean hasInit = false;
	private NRGStackWithParams nrgStack;
	private Logger logger;
	private SharedFeatureMulti sharedFeatures;
	
	public PairCollectionAddCriteria( Class<?> pairTypeClass ) {
		this.pairTypeClass = pairTypeClass;
		graph = new GraphWithEdgeTypes<>(true);
	}
	
	public PairCollectionAddCriteria<T> shallowCopy() {
		PairCollectionAddCriteria<T> out = new PairCollectionAddCriteria<>( this.pairTypeClass );
		out.graph = this.graph.shallowCopy();
		out.addCriteria = this.addCriteria;
		out.nrgStack = this.nrgStack;
		out.hasInit = this.hasInit;
		out.logger = this.logger;
		out.sharedFeatures = this.sharedFeatures;
		return out;
	}
	
	public PairCollectionAddCriteria<T> deepCopy() {
		PairCollectionAddCriteria<T> out = new PairCollectionAddCriteria<>( this.pairTypeClass );
		out.graph = this.graph.shallowCopy();
		out.addCriteria = this.addCriteria;
		out.nrgStack = nrgStack;
		out.hasInit = this.hasInit;
		out.logger = this.logger;
		out.sharedFeatures = this.sharedFeatures;
		return out;
	}
	
	@Override
	public void initUpdatableMarkSet( MemoForIndex marks, NRGStackWithParams stack, Logger logger, SharedFeatureMulti sharedFeatures ) throws InitException {
		this.logger = logger;
		this.sharedFeatures = sharedFeatures;
		
		try {
			this.graph = new GraphWithEdgeTypes<>(true);
			
			// Add all marks as vertices
			for( int i=0; i< marks.size(); i++ ) {
				graph.addVertex( marks.getMemoForIndex(i).getMark() );
			}
			
			Optional<FeatureList<FeatureInputPairMemo>> features = addCriteria.orderedListOfFeatures();
			
			Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session = OptionalUtilities.map( 
				features,
				f-> FeatureSession.with(
					f,
					new FeatureInitParams(stack.getParams()),
					sharedFeatures,
					logger
				)
			);
			
			initGraph( marks, stack, session );
			
			this.hasInit = true;
			this.nrgStack = stack;
			
		} catch (CreateException | FeatureCalcException e) {
			throw new InitException(e);
		}
	}
	
	@Override
	public void add( MemoForIndex marksExisting, VoxelizedMarkMemo newMark ) throws UpdateMarkSetException {
		checkInit();
		try {
			this.graph.addVertex( newMark.getMark() );
			calcPairsForMark( marksExisting, newMark, nrgStack );
		} catch (CreateException e) {
			throw new UpdateMarkSetException(e);
		}
	}
	
	@Override
	public void exchange( MemoForIndex pxlMarkMemoList, VoxelizedMarkMemo oldMark, int indexOldMark, VoxelizedMarkMemo newMark ) throws UpdateMarkSetException {
		checkInit();
		
		// We need to make a copy of the list, so we can perform the removal operation after the add
		MemoList memoList = new MemoList(); 
		memoList.addAll( pxlMarkMemoList );
		
		rmv(pxlMarkMemoList, oldMark);
		
		memoList.remove(indexOldMark);

		add(memoList, newMark );
	}
	
	@Override
	public void rmv( MemoForIndex marksExisting, VoxelizedMarkMemo mark ) throws UpdateMarkSetException {
		checkInit();
		this.graph.removeVertex(mark.getMark());
	}
	
	// START DELEGATES
	// Each edge can appear many times
	public Collection<EdgeTypeWithVertices<Mark,T>> getPairsWithPossibleDuplicates() {
		return graph.edgeSetWithPossibleDuplicates();
	}
	
	// Each pair appears twice
	public Set<T> createPairsUnique() {
		HashSet<T> setOut = new HashSet<>();
		for( EdgeTypeWithVertices<Mark,T> pair : getPairsWithPossibleDuplicates()) {
			setOut.add(pair.getEdge());
		}
		return setOut;
	}
	
	public Collection<EdgeTypeWithVertices<Mark,T>> getPairsFor( Mark mark ) {
		return graph.edgesOf( mark );
	}
	
	public boolean containsMark( Mark mark ) {
		return graph.containsVertex( mark );
	}
	
	public Collection<Mark> getMarks() {
		return graph.vertexSet();
	}
	
	public boolean isCfgSpan( Cfg cfg ) {
		
		for (int i=0; i<cfg.size(); i++) {
			if (!containsMark( cfg.get(i) )) {
				return false;
			}
		}
		
		for( Mark m : getMarks() ) {
			if ( cfg.indexOf(m)==-1 ) {
				return false;
			}
		}
		
		return true;
	}
	
	// As pairs can be duplicated, this is not strictly UNIFORM sampling, but usually each item appears twice
	//  it's more efficient to sample from this, than to always insist upon uniqueness, as this involves
	//  creating a HashMap each query (due to the implementation)
	@Override
	public T sampleRandomPairNonUniform( RandomNumberGenerator randomNumberGenerator ) {
		
		int count = this.graph.edgeSetWithPossibleDuplicates().size();
		
		if (count==0) {
			throw new AnchorFriendlyRuntimeException("No edges exist to sample from");
		}
		
		// Pick an element from the existing configuration
		int index = randomNumberGenerator.sampleIntFromRange(count);
		
		int i =0;
		for( EdgeTypeWithVertices<Mark,T> di : getPairsWithPossibleDuplicates()) {
			if (i++==index) {
				return di.getEdge();
			}
		}
		
		throw new AnchorFriendlyRuntimeException("Invalid index chosen for randomPair");
	}
	
	public AddCriteria<T> getAddCriteria() {
		return addCriteria;
	}

	public void setAddCriteria(AddCriteria<T> addCriteria) {
		this.addCriteria = addCriteria;
	}

	public Class<?> getPairTypeClass() {
		return pairTypeClass;
	}

	public void setPairTypeClass(Class<?> pairTypeClass) {
		this.pairTypeClass = pairTypeClass;
	}
	
	private void initGraph( MemoForIndex marks, NRGStackWithParams stack, Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session ) throws CreateException {
		// Some nrg components need to be calculated individually
		for( int i=0; i< marks.size(); i++ ) {
			
			VoxelizedMarkMemo srcMark = marks.getMemoForIndex(i); 
				
			for( int j=0; j<i; j++ ) {
				
				VoxelizedMarkMemo destMark = marks.getMemoForIndex(j);
				
				addCriteria.generateEdge(
					srcMark,
					destMark,
					stack,
					session,
					stack.getDimensions().getZ()>1
				).ifPresent( pair->
					graph.addEdge( srcMark.getMark(), destMark.getMark(), pair )
				);
			}
		}
	}
	
	private void calcPairsForMark( MemoForIndex pxlMarkMemoList, VoxelizedMarkMemo newMark, NRGStackWithParams nrgStack ) throws CreateException {
		
		Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session;
		
		try {
			session = OptionalUtilities.map(
				addCriteria.orderedListOfFeatures(),
				f-> FeatureSession.with(
					f,
					new FeatureInitParams(nrgStack.getParams()),
					sharedFeatures,
					logger
				)
			);
		} catch (FeatureCalcException e) {
			throw new CreateException(e);
		}
				
		// We calculate how the new mark interacts with all the other marks
		for( int i=0; i<pxlMarkMemoList.size(); i++) {

			VoxelizedMarkMemo otherMark = pxlMarkMemoList.getMemoForIndex(i);
			if (!otherMark.getMark().equals( newMark.getMark() )) {
				addCriteria.generateEdge(
					otherMark,
					newMark,
					nrgStack,
					session,
					nrgStack.getDimensions().getZ()>1
				).ifPresent( pair->
					this.graph.addEdge( otherMark.getMark(), newMark.getMark(), pair )
				);
			} 
		}
	}
		
	private void checkInit() throws UpdateMarkSetException {
		if (!hasInit) {
			throw new UpdateMarkSetException("object has not been initialized");
		}
	}
}
