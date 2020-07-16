/* (C)2020 */
package org.anchoranalysis.io.generator.histogram;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.output.csv.CSVWriter;

class HistogramCSVWriter {

    private HistogramCSVWriter() {}

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
