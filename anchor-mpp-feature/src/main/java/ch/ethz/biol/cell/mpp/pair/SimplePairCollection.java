package ch.ethz.biol.cell.mpp.pair;

import org.anchoranalysis.anchor.mpp.mark.Mark;


// A simple pair collection where the underlying PairType is a simple Pair
public class SimplePairCollection extends PairCollectionAddCriteria<Pair<Mark>> {

	private static final long serialVersionUID = 8228326469200660736L;

	public SimplePairCollection() {
		super( Pair.class );
	}

	@Override
	public String getBeanDscr() {
		return getBeanName();
	}


}
