/* (C)2020 */
package org.anchoranalysis.anchor.overlay;

import java.util.Iterator;
import java.util.function.DoubleUnaryOperator;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.core.unit.SpatialConversionUtilities;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;

public class OverlayProperties implements Iterable<NameValue<String>> {

    private NameValueSet<String> nameValueSet;

    public OverlayProperties() {
        super();
        this.nameValueSet = new NameValueSet<>();
    }

    public void add(String name, String value) {
        nameValueSet.add(new SimpleNameValue<>(name, value));
    }

    public void add(String name, int value) {
        add(name, Integer.toString(value));
    }

    public void addWithUnits(
            String name, double value, DoubleUnaryOperator convertToUnits, UnitSuffix unitSuffix) {
        double valueUnits =
                SpatialConversionUtilities.convertToUnits(
                        convertToUnits.applyAsDouble(value), unitSuffix);

        add(
                name,
                String.format(
                        "%2.2f (%.2f%s)",
                        value,
                        valueUnits,
                        SpatialConversionUtilities.unitMeterStringDisplay(unitSuffix)));
    }

    public void addDoubleAsString(String name, double val) {
        addDoubleAsString(name, val, "%1.2f");
    }

    public NameValueSet<String> getNameValueSet() {
        return nameValueSet;
    }

    @Override
    public Iterator<NameValue<String>> iterator() {
        return nameValueSet.iterator();
    }

    private void addDoubleAsString(String name, double val, String precision) {
        add(name, String.format(precision, val));
    }
}
