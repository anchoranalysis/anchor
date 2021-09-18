/*-
 * #%L
 * anchor-annotation-io
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.annotation.io.comparer;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.core.value.TypedValue;
import org.anchoranalysis.math.arithmetic.RunningSumExtrema;
import org.apache.commons.lang.StringUtils;
import lombok.Getter;

/**
 * Statistics to be exported, comprised of name-value pairs.
 * 
 * <p>The values may be of type {@code int}, {@code double} or {@link String}.
 * 
 * <p>Typically, they are later written to a CSV file. 
 * 
 * @author Owen Feehan
 *
 */
public class StatisticsToExport {

    /** Number of decimal places when storing a double. */
    private static final int DECIMAL_PLACES = 4;
    
    /**
     * The names of statistics describing the annotation-comparison, as produced by {@link #getValues()}.
     */
    @Getter List<String> names = new ArrayList<>();

    /**
     * The values of statistics describing the annotation-comparison, corresponding exactly to the names in {@link #getNames()}.
     */
    @Getter List<TypedValue> values = new ArrayList<>();
    
    /**
     * Appends the names and values from another {@code AnnotationComparison} to the existing.
     * 
     * <p>Items are added to the end of their respective lists.
     * 
     * <p>This is a <i>mutable</i> operation.
     * 
     * @param toAppend the comparison to append.
     */
    public void append(StatisticsToExport toAppend) {
        this.names.addAll(toAppend.names);
        this.values.addAll(toAppend.values);
    }
    
    /**
     * Adds a statistic that is a {@link String}.
     * 
     * @param name the name of the statistic.
     * @param value the value of the statistic.
     */
    public void addString(String name, String value) {
        add(name, new TypedValue(value));
    }
    
    /**
     * Adds a statistic that is a {@code double}.
     * 
     * @param name the name of the statistic.
     * @param value the value of the statistic.
     */
    public void addDouble(String name, double value) {
        add(name, new TypedValue(value, DECIMAL_PLACES));
    }

    /**
     * Adds a statistic that is an {@code int}.
     * 
     * @param name the name of the statistic.
     * @param value the value of the statistic.
     */
    public void addInt(String name, int value) {
        add(name, new TypedValue(value));
    }

    /**
     * Adds three statistics a mean, min and max.
     * 
     * @param name the name of the statistic, which is capitalized and prefixed with {@code mean}, {@code min} and {@code max} respectively. 
     * @param mean the mean.
     * @param min the min.
     * @param max the max.
     */
    public void addMeanExtrema(String name, double mean, double min, double max) {
        String nameCapitalized = StringUtils.capitalize(name);
        addDouble("mean" + nameCapitalized, mean);
        addDouble("min" + nameCapitalized, min);
        addDouble("max" + nameCapitalized, max);
    }
    
    /**
     * Like {@link #addMeanExtrema(String, double, double, double)} but determines the statistics from a {@link RunningSumExtrema}.
     * 
     * @param name the name of the statistic, which is capitalized and prefixed with {@code mean}, {@code min} and {@code max} respectively. 
     * @param runningSum the running-sum, also remembering extrema.
     */
    public void addMeanExtrema(String name, RunningSumExtrema runningSum) {
        addMeanExtrema(name, runningSum.mean(), runningSum.min(), runningSum.max());
    }
    
    private void add(String name, TypedValue value) {
        this.names.add(name);
        this.values.add(value);
    }
}
