package org.anchoranalysis.feature.io.csv.writer;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * What's needed to output a CSV other than the actual data items.
 * 
 * <p>i.e. headers, the output-name etc.
 * 
 * @author Owen Feehan
 *
 */
@Value @AllArgsConstructor
public class FeatureCSVMetadata {

    /**
     * The output name for the features CSV.
     */
    private final String outputName;
            
    /**
     * Headers for the CSV file
     */
    private final List<String> headers;
}
