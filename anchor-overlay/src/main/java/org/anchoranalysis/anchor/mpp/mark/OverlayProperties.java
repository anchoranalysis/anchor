package org.anchoranalysis.anchor.mpp.mark;

import java.util.Iterator;
import java.util.function.Function;

import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.value.INameValue;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.core.unit.SpatialConversionUtilities;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;

public class OverlayProperties implements Iterable<INameValue<String>> {

	private NameValueSet<String> nameValueSet;

	public OverlayProperties() {
		super();
		this.nameValueSet = new NameValueSet<>();
	}
	
	public void add( String name, String value ) {
		nameValueSet.add(
			new NameValue<>(name, value)
		);
	}
	
	public void add( String name, int value ) {
		add(name, Integer.toString(value)); 
	}
	
	public void addWithUnits(
		String name,
		double value,
		Function<Double,Double> convertToUnits,
		UnitSuffix unitSuffix
	) {
		double valueUnits = SpatialConversionUtilities.convertToUnits(
			convertToUnits.apply(value),
			unitSuffix
		);
		
		add(
			name,
			String.format(
				"%2.2f (%.2f%s)",
				value,
				valueUnits,
				SpatialConversionUtilities.unitMeterStringDisplay(
					unitSuffix
				)
			)
		);
	}
	
	public void addDoubleAsString( String name, double val ) {
		addDoubleAsString(name, val, "%1.2f");
	}

	public NameValueSet<String> getNameValueSet() {
		return nameValueSet;
	}
	
	@Override
	public Iterator<INameValue<String>> iterator() {
		return nameValueSet.iterator();
	}

	private void addDoubleAsString( String name, double val, String precision ) {
		add(name, String.format(precision, val)	);
	}
}
