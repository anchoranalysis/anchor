package org.anchoranalysis.feature.io.csv;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.feature.io.csv.name.MultiName;

/**
 * Labels associated with feature-results in an outputted CSV row
 * 
 * @author Owen Feehan
 *
 */
public class StringLabelsForCsvRow {

	private final Optional<String[]> identifier;
	private final Optional<MultiName> group;
	
	/**
	 * Constructor
	 * 
	 * @param identifier unique identifier for the row taking all elements together (together a primary key)
	 * @param group an identifier for a higher-level group which the row belongs to (foreign key)
	 */
	public StringLabelsForCsvRow(Optional<String[]> identifier, Optional<MultiName> group) {
		super();
		this.identifier = identifier;
		this.group = group;
	}
	
	public void addToRow(List<TypedValue> csvRow) {
		identifier.ifPresent( stringArr->
			addStringArrayToRow(stringArr, csvRow)
		);
		group.ifPresent( stringArr->
			addStringIterableToRow(stringArr, csvRow)
		);
	}

	public Optional<MultiName> getGroup() {
		return group;
	}
	
	private static void addStringArrayToRow(String[] arr, List<TypedValue> csvRow) {
		Arrays.stream(arr).forEach( str->
			csvRow.add( new TypedValue(str) )
		);
	}
	
	private static void addStringIterableToRow(Iterable<String> itr, List<TypedValue> csvRow) {
		for(String str: itr) {
			csvRow.add( new TypedValue(str) );
		}
	}
}
