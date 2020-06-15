package org.anchoranalysis.feature.io.csv;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Optional;
import org.anchoranalysis.core.collection.MapCreate;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.name.MultiName;
import org.apache.commons.math3.util.Pair;

import com.google.common.collect.Comparators;

/**
 * A map which stores an aggregate structure for all entries (based on their unique names) and also on an aggregation-key extracted from the name
 * 
 * <p>New aggregate-structures are as needed, when an unseed key is referenced</p>.
 * 
 * @author Owen Feehan
 *
 * @param <K> name
 * @param <V> aggregate-stucture
 */
class AggregateMap<K extends MultiName,V> {

	/**
	 * A map which stores all entries and a corresponding results-vector collection
	 */
	private MapCreate<MultiName,V> nameMap;
	
	/**
	 * A map which stores a higher-level of grouping aggregation corresponding results-vector collection
	 */
	private MapCreate<Optional<String>,V> aggregationMap;
	
	/**
	 * Constructor
	 * 
	 * @param opCreateAggregateStructure creates a new aggregate-structure
	 */
	public AggregateMap(Operation<V,AnchorNeverOccursException> opCreateAggregateStructure) {
		nameMap = new MapCreate<>(opCreateAggregateStructure);
		
		// We need to specify an explicit comparator for the aggregation, as Optional does not have a natural order
		aggregationMap = new MapCreate<>(
			opCreateAggregateStructure,
			Comparators.emptiesFirst(Comparator.naturalOrder())
		);
	}
	
	/**
	 * Gets the aggregate-structure(s) corresponding to a key, reusing or creating new structures as needed
	 * 
	 * @param identifier name of item
	 * @return a pair, the left-item is the structure for the entire name, the right-item is the structure corresponding to the aggregation-key
	 */
	public Pair<V,V> getOrCreate(MultiName identifier) {
		return new Pair<>(
			nameMap.getOrCreateNew(identifier),
			aggregationMap.getOrCreateNew(identifier.deriveAggregationKey())
		);
	}

	/** Entries corresponding to the entire-names */
	public Collection<Entry<MultiName, V>> byEntireName() {
		return nameMap.entrySet();
	}
	
	/** Eentries corresponding to the aggreagtion-keys */
	public Collection<Entry<Optional<String>, V>> byAggregationKeys() {
		return aggregationMap.entrySet();
	}
}
