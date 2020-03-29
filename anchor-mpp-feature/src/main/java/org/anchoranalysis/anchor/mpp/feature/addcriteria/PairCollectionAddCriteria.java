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
import java.util.Set;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.mark.PxlMarkMemoList;
import org.anchoranalysis.anchor.mpp.feature.session.FeatureSessionCreateParamsMPP;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.set.UpdateMarkSetException;
import org.anchoranalysis.anchor.mpp.pair.PairCollection;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.MemoForIndex;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.graph.GraphWithEdgeTypes;
import org.anchoranalysis.core.graph.GraphWithEdgeTypes.EdgeTypeWithVertices;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

// PairType is how we store the pair information

/**
 * An implementation of a PairCollection that uses IAddCriteria to determine
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

	/**
	 * 
	 */
	private static final long serialVersionUID = -3740934203419386237L;
	
	private GraphWithEdgeTypes<Mark,T> graph;
	
	// START BEAN PROPERTIES
	@BeanField
	private Class<?> pairTypeClass;
	
	@BeanField
	private transient AddCriteria<T> addCriteria;
	// END BEAN PROPERTIES
	
	private transient boolean hasInit = false;
	private transient NRGStackWithParams nrgStack;
	private transient LogErrorReporter logger;
	private transient SharedFeatureSet sharedFeatures;
	
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
	public void initUpdatableMarkSet( MemoForIndex marks, NRGStackWithParams stack, LogErrorReporter logger, SharedFeatureSet sharedFeatures ) throws InitException {
		assert( sharedFeatures!=null );
		assert( logger!=null );
		this.logger = logger;
		this.sharedFeatures = sharedFeatures;
		
		try {
			this.graph = new GraphWithEdgeTypes<>(true);
			
			// Add all marks as vertices
			for( int i=0; i< marks.size(); i++ ) {
				graph.addVertex( marks.getMemoForIndex(i).getMark() );
			}
			
			FeatureList features = addCriteria.orderedListOfFeatures();
			if (features==null) {
				features = new FeatureList();
			}
			
			FeatureSessionCreateParamsMPP session = new FeatureSessionCreateParamsMPP( features, stack.getNrgStack(), stack.getParams() );
			try {
				session.start( new FeatureInitParams(stack.getParams()), sharedFeatures, logger );
			} catch (InitException e) {
				throw new CreateException(e);
			}

			
			// Some nrg components need to be calculated individually
			for( int i=0; i< marks.size(); i++ ) {
				
				PxlMarkMemo srcMark = marks.getMemoForIndex(i); 
					
				for( int j=0; j<i; j++ ) {
					
					PxlMarkMemo destMark = marks.getMemoForIndex(j);
					
					T pair = addCriteria.generateEdge(srcMark, destMark, stack, session, stack.getDimensions().getZ()>1 );
					if( pair != null ) {
						graph.addEdge( srcMark.getMark(), destMark.getMark(), pair );
					}
				}
			}
			
			this.hasInit = true;
			this.nrgStack = stack;
			
		} catch (CreateException e) {
			throw new InitException(e);
		}
	}
	
	@Override
	public void add( MemoForIndex marksExisting, PxlMarkMemo newMark ) throws UpdateMarkSetException {
		assert hasInit;
		try {
			this.graph.addVertex( newMark.getMark() );
			calcPairsForMark( marksExisting, newMark, nrgStack );
		} catch (CreateException e) {
			throw new UpdateMarkSetException(e);
		}
	}
	
	private void calcPairsForMark( MemoForIndex pxlMarkMemoList, PxlMarkMemo newMark, NRGStackWithParams nrgStack ) throws CreateException {
		assert sharedFeatures!=null;
		assert logger!=null;
		FeatureSessionCreateParamsMPP session = new FeatureSessionCreateParamsMPP( addCriteria.orderedListOfFeatures(), nrgStack.getNrgStack(), nrgStack.getParams() );
		try {
			session.start( new FeatureInitParams(nrgStack.getParams()), sharedFeatures, logger );
		} catch (InitException e) {
			throw new CreateException(e);
		}
		
		// We calculate how the new mark interacts with all the other marks
		for( int i=0; i<pxlMarkMemoList.size(); i++) {

			PxlMarkMemo otherMark = pxlMarkMemoList.getMemoForIndex(i);
			if (!otherMark.getMark().equals( newMark.getMark() )) {
			
				T pair = addCriteria.generateEdge(otherMark,newMark, nrgStack, session, nrgStack.getDimensions().getZ()>1 );
				if (pair!=null) {
					assert containsMark( otherMark.getMark());
					assert containsMark( newMark.getMark());
					
					assert graph.getEdge(otherMark.getMark(), newMark.getMark())==null; 
					assert graph.getEdge(newMark.getMark(), otherMark.getMark())==null;

					this.graph.addEdge( otherMark.getMark(), newMark.getMark(), pair );
				}
			} 
		}
	}
	
	@Override
	public void exchange( MemoForIndex pxlMarkMemoList, PxlMarkMemo oldMark, int indexOldMark, PxlMarkMemo newMark ) throws UpdateMarkSetException {
		
		assert hasInit;
		
		// We need to make a copy of the list, so we can perform the removal operation after the add
		PxlMarkMemoList memoList = new PxlMarkMemoList(); 
		memoList.addAll( pxlMarkMemoList );
		
		rmv(pxlMarkMemoList, oldMark);
		
		memoList.remove(indexOldMark);
		
		// TODO, shouldn't this be memoList
		add(memoList, newMark );
	}
	
	@Override
	public void rmv( MemoForIndex marksExisting, PxlMarkMemo mark ) {
		
		assert(hasInit);
		
		assert this.graph.containsVertex(mark.getMark() );
		this.graph.removeVertex(mark.getMark());
		assert !this.graph.containsVertex(mark.getMark() );
	}
	
	// START DELEGATES
	public T getPair( Mark mark1, Mark mark2) {
		return graph.getEdge( mark1, mark2 );
	}
	
	// Each edge can appear many times
	public Collection<EdgeTypeWithVertices<Mark,T>> getPairsWithPossibleDuplicates() {
		return graph.edgeSetWithPossibleDuplicates();
	}
	
	// Each pair appears twice
	public Set<T> createPairsUnique() {
		HashSet<T> setOut = new HashSet<T>();
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
	public T randomPairNonUniform( RandomNumberGenerator re ) {
		
		int count = this.graph.edgeSetWithPossibleDuplicates().size();
		
		if (count==0) {
			return null;
		}
		
		// Pick an element from the existing configuration
		int index = (int) (re.nextDouble() * count);
		
		int i =0;
		for( GraphWithEdgeTypes.EdgeTypeWithVertices<Mark,T> di : getPairsWithPossibleDuplicates()) {
			if (i++==index) {
				return di.getEdge();
			}
		}
		
		throw new RuntimeException("Invalid index chosen for randomPair");
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
}
