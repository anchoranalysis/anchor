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

package org.anchoranalysis.feature.results;

import java.util.Collection;
import java.util.Optional;
import org.anchoranalysis.core.value.TypedValue;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A vector of results of applying a feature-calculations to many entities.
 *
 * <p>Each position in the vector describes the result for a single entity. The result is either a
 * {@link Double} or an {@link Exception}.
 *
 * <p>An {@link Exception} indicates that the feature-calculation ended in failure, producing the
 * exception.
 *
 * @author Owen Feehan
 */
public class ResultsVector {

    private static final ArrayComparer DEFAULT_COMPARER = new ArrayComparer();

    /** Each object is either a {@link Double} or an {@link Exception}. */
    private Object[] vector;

    /**
     * Creates with a particular size.
     *
     * @param size the number of results the vector can store.
     */
    public ResultsVector(int size) {
        vector = new Object[size];
    }

    /**
     * The total value of all results.
     *
     * @return the sum of all results, or {@link Double#NaN} if any are errored.
     */
    public double total() {
        double sum = 0.0;
        for (int i = 0; i < vector.length; i++) {
            sum += get(i);
        }
        return sum;
    }

    /**
     * Assigns a result at a particular position.
     *
     * @param index the index of the position (zero-valued).
     * @param value the value to assign as a result.
     */
    public void set(int index, double value) {
        vector[index] = value;
    }

    /**
     * Assigns many results, starting at a particular position, and incrementing thereafter.
     *
     * @param startIndex the index of the initial position (zero-valued) for the first result.
     * @param resultsToAssign the values to assign in {@code startIndex} and subsequent positions.
     */
    public void set(int startIndex, ResultsVector resultsToAssign) {
        for (int index = 0; index < resultsToAssign.size(); index++) {
            Object value = resultsToAssign.vector[index];
            vector[startIndex + index] = value;
        }
    }

    /**
     * Set an error state at a particular position.
     *
     * @param index the index of the position (zero-valued).
     * @param exception the error state.
     */
    public void setError(int index, Exception exception) {
        vector[index] = exception;
    }

    /**
     * Set an error state at all positions.
     *
     * @param exception the error state.
     */
    public void setErrorAll(Exception exception) {
        for (int index = 0; index < vector.length; index++) {
            setError(index, exception);
        }
    }

    /**
     * Gets an error state at a particular position.
     *
     * <p>This should <b>only</b> be called at positions which for sure are errored.
     *
     * @param index the position (zero-indexed).
     * @return the exception for the error.
     */
    public Exception getError(int index) {
        return (Exception) vector[index];
    }

    /**
     * Gets a result value at a particular position.
     *
     * @param index the position (zero-indexed).
     * @return the result-value if unerrored, or {@link Optional#empty} if the position is errored.
     */
    public Optional<Double> getResult(int index) {

        Object obj = vector[index];

        if (obj instanceof Exception) {
            return Optional.empty();
        }

        return Optional.of((Double) obj);
    }

    /**
     * The result of a feature-calculation stored at a particular {@code index}.
     *
     * @param index the index (zero-indexed). It should be {@code >= 0} and {@code < size()}.
     * @return the value corresponding to the feature-calculation or {@link Double#NaN} if an
     *     exception occurred during calculation.
     */
    public double get(int index) {

        Object obj = vector[index];

        if (obj instanceof Exception) {
            return Double.NaN;
        }

        assert obj instanceof Double;

        return (Double) obj;
    }

    /**
     * Copies a contiguous subset of results from another vector.
     *
     * @param index start-position to start copying into (zero-indexed).
     * @param length number of items to copy.
     * @param source vector to copy from.
     * @param sourceIndex index in the source away to start from.
     */
    public void copyFrom(int index, int length, ResultsVector source, int sourceIndex) {

        for (int i = 0; i < length; i++) {
            int indexIn = sourceIndex + i;
            int indexOut = index + i;

            vector[indexOut] = source.vector[indexIn];
        }
    }

    /**
     * Adds {@link TypedValue} representations of the results to a {@link Collection}.
     *
     * @param addTo the collection to add the representations to.
     * @param numberDecimalPlaces the number of decimal places to use, or -1 to visually shorten as
     *     much as possible.
     */
    public void addTypedValuesTo(Collection<TypedValue> addTo, int numberDecimalPlaces) {

        for (int index = 0; index < vector.length; index++) {
            double value = get(index);
            TypedValue representation =
                    numberDecimalPlaces == -1
                            ? new TypedValue(value)
                            : new TypedValue(value, numberDecimalPlaces);
            addTo.add(representation);
        }
    }

    /**
     * The number of calculations stored in the vector.
     *
     * @return the total number of calculations in the vector.
     */
    public int size() {
        return vector.length;
    }

    /**
     * Does the instance have exactly these values?
     *
     * @param values the values to check for equality.
     * @return true, if the results in this object are exactly the same as {@code values}.
     */
    public boolean equals(Object... values) {
        return DEFAULT_COMPARER.compareArrays(vector, values);
    }

    /**
     * Like {@link #equals(Object)} but includes a tolerance for checking equality of the doubles.
     *
     * @param eps amount of allowed absolute error.
     * @param values the values to check for equality.
     * @return true, if the results in this object are the same as {@code values}, within the
     *     tolerance.
     */
    public boolean equalsPrecision(double eps, Object... values) {
        return new ArrayComparerPrecision(eps).compareArrays(vector, values);
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

        return DEFAULT_COMPARER.compareArrays(vector, other.vector);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(vector).toHashCode();
    }

    // Exceptions are shown as NA
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < vector.length; index++) {
            if (index != 0) {
                builder.append(", ");
            }
            builder.append(getString(index));
        }
        return builder.toString();
    }

    /** A textual description of what is contained in the vector at a particular position. */
    private String getString(int index) {
        Object obj = vector[index];
        return obj.toString();
    }
}
