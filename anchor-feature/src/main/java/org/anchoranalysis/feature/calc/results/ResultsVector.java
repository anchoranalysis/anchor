/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.calc.results;

import java.util.Collection;
import java.util.Optional;
import org.anchoranalysis.core.text.TypedValue;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A vector of results from feature-calculations. A result is either a pointer to a Double or an
 * exception (indicating an error).
 *
 * <p>Optionally, contains an identifier for the vector.
 *
 * @author Owen Feehan
 */
public class ResultsVector {

    private static final ArrayComparer DEFAULT_COMPARER = new ArrayComparer();
    
    // Each object is either a Double or an Exception
    private Object[] arr;

    public ResultsVector(int size) {
        arr = new Object[size];
    }

    public double total() {
        double sum = 0.0;
        for (int i = 0; i < arr.length; i++) {
            sum += get(i);
        }
        return sum;
    }

    public void set(int i, double val) {
        arr[i] = val;
    }

    public void set(int start, ResultsVector rv) {
        for (int i = 0; i < rv.length(); i++) {
            Object val = rv.arr[i];
            arr[start + i] = val;
        }
    }

    public void setError(int i, Exception e) {
        arr[i] = e;
    }

    public void setErrorAll(Exception e) {
        for (int i = 0; i < arr.length; i++) {
            setError(i, e);
        }
    }

    public Exception getException(int i) {
        return (Exception) arr[i];
    }

    // A string description of what is contained at an index
    private String getString(int i) {
        Object obj = arr[i];
        return obj.toString();
    }

    // Returns a double, or NULL if the object is an exception
    public Optional<Double> getDoubleOrNull(int i) {

        Object obj = arr[i];

        if (obj instanceof Exception) {
            return Optional.empty();
        }

        return Optional.of((Double) obj);
    }

    // Value is replaced with NaN if it's an exception
    public double get(int i) {

        Object obj = arr[i];

        if (obj instanceof Exception) {
            return Double.NaN;
        }

        assert obj instanceof Double;

        return (Double) obj;
    }

    /**
     * Copies a contiguous subset of results from another vector
     *
     * @param index start-index to start copying into
     * @param length number of items to copy
     * @param src vector to copy from
     * @param srcIndex index in the source away to start from
     * @return
     */
    public void copyFrom(int index, int length, ResultsVector src, int srcIndex) {

        for (int i = 0; i < length; i++) {
            int indexIn = srcIndex + i;
            int indexOut = index + i;

            arr[indexOut] = src.arr[indexIn];
        }
    }

    public void addToTypeValueCollection(Collection<TypedValue> listOut, int numDecimalPlaces) {

        for (int i = 0; i < arr.length; i++) {
            listOut.add(new TypedValue(get(i), numDecimalPlaces));
        }
    }

    public int length() {
        return arr.length;
    }

    public boolean hasNoNulls() {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null) {
                return false;
            }
        }
        return true;
    }

    // Checks if the ResultsVector has exactly the values passed
    public boolean equals(Object... vals) {
        return DEFAULT_COMPARER.compareArrays(arr, vals);
    }

    // Checks if the ResultsVector has exactly the values passed to a particular precision
    public boolean equalsPrecision(double eps, Object... vals) {
        return new ArrayComparerPrecision(eps).compareArrays(arr, vals);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }

        if (!(obj instanceof ResultsVector)) {
            return false;
        }

        ResultsVector other = (ResultsVector) obj;

        return DEFAULT_COMPARER.compareArrays(arr, other.arr);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(arr).toHashCode();
    }

    // Exceptions are shown as NA
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(getString(i));
        }
        return sb.toString();
    }
}
