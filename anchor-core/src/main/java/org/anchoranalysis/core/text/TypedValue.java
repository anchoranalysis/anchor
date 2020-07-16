/* (C)2020 */
package org.anchoranalysis.core.text;

import java.text.DecimalFormat;

public class TypedValue {

    private String value;
    private boolean isNumeric;

    public TypedValue(String value) {
        this.value = value;
        this.isNumeric = false;
    }

    public TypedValue(int value) {
        super();
        this.value = Integer.toString(value);
        this.isNumeric = true;
    }

    public TypedValue(double value, int numDecimalPlaces) {
        super();
        if (Double.isNaN(value)) {
            this.value = "NaN";
            this.isNumeric = true;
        } else {
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.setMinimumFractionDigits(numDecimalPlaces);
            decimalFormat.setGroupingUsed(false);
            this.value = decimalFormat.format(value);
            this.isNumeric = true;
        }
    }

    public TypedValue(String value, boolean isNumeric) {
        super();
        this.value = value;
        this.isNumeric = isNumeric;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isNumeric() {
        return isNumeric;
    }

    public void setNumeric(boolean isNumeric) {
        this.isNumeric = isNumeric;
    }
}
