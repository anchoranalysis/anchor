/*-
 * #%L
 * anchor-io-generator
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

package org.anchoranalysis.io.generator.histogram;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.output.csv.CSVWriter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HistogramCSVWriter {

    public static void writeHistogramToFile(Histogram histogram, Path filePath, boolean ignoreZeros)
            throws AnchorIOException {

        List<String> headers = new ArrayList<>();
        headers.add("intensity");
        headers.add("count");

        try (CSVWriter writer = CSVWriter.create(filePath)) {
            writer.writeHeaders(headers);

            for (int i = histogram.getMinBin(); i <= histogram.getMaxBin(); i++) {
                List<TypedValue> list = new ArrayList<>();

                int histVal = histogram.getCount(i);

                if (ignoreZeros && histVal == 0) {
                    continue;
                }

                list.add(new TypedValue(i, 0));
                list.add(new TypedValue(histVal, 0));
                writer.writeRow(list);
            }
        }
    }
}
