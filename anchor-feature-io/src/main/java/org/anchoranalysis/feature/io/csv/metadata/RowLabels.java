/*-
 * #%L
 * anchor-feature-io
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

package org.anchoranalysis.feature.io.csv.metadata;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.value.TypedValue;
import org.anchoranalysis.feature.io.name.MultiName;

/**
 * Labels for each row of feature-results in the outputted CSV row.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class RowLabels {

    /** Unique identifier for the row taking all elements together (together a primary key). */
    private final Optional<String[]> identifier;

    /** an identifier for a higher-level group which the row belongs to (foreign key) */
    @Getter private final Optional<MultiName> group;

    /**
     * Creates with a single string as an identifier.
     *
     * @param identifier the identifier.
     */
    public RowLabels(String identifier) {
        this(Optional.of(new String[] {identifier}), Optional.empty());
    }

    /**
     * Adds the identifiers to a row to be outputted in a features CSV.
     *
     * @param csvRow the row of typed-values that will be outputted
     */
    public void addToRow(List<TypedValue> csvRow) {
        identifier.ifPresent(array -> addStringArrayToRow(array, csvRow));
        group.ifPresent(multiName -> addStringIterableToRow(multiName, csvRow));
    }

    private static void addStringArrayToRow(String[] array, List<TypedValue> csvRow) {
        Arrays.stream(array).forEach(str -> csvRow.add(new TypedValue(str)));
    }

    private static void addStringIterableToRow(Iterable<String> iterable, List<TypedValue> csvRow) {
        for (String string : iterable) {
            csvRow.add(new TypedValue(string));
        }
    }
}
