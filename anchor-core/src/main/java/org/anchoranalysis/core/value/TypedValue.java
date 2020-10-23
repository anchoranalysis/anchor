/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.core.value;

import java.text.DecimalFormat;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor @Value
public class TypedValue {

    private final String value;
    private final boolean isNumeric;

    public TypedValue(String value) {
        this( value, false );
    }

    public TypedValue(int value) {
        this( Integer.toString(value), true );
    }

    public TypedValue(double value, int numberDecimalPlaces) {
        this( decimalValueOrNaN(value, numberDecimalPlaces), true );
    }
    
    private static String decimalValueOrNaN(double value, int numberDecimalPlaces) {
        if (Double.isNaN(value)) {
            return "NaN";
        } else {
            return createDecimalFormat(numberDecimalPlaces).format(value);
        }
    }
    
    private static DecimalFormat createDecimalFormat(int numberDecimalPlaces) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumFractionDigits(numberDecimalPlaces);
        decimalFormat.setGroupingUsed(false);
        return decimalFormat;
    }
}
