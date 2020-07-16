/* (C)2020 */
package org.anchoranalysis.feature.io.csv;

import org.apache.commons.lang3.ArrayUtils;

public class MetadataHeaders {

    /**
     * Headers describing the first few non-feature columns outputted in the CSV (2-3 columns with
     * group and ID information)
     */
    private String[] identifiers;

    private String[] group;

    /**
     * This constructor will include two group names in the outputting CSV file, but NO id column
     *
     * @param group headers for the group
     * @param identifiers headers for identification
     */
    public MetadataHeaders(String[] group, String[] identifiers) {
        super();
        this.group = group;
        this.identifiers = identifiers;
    }

    public String[] groupHeaders() {
        return group;
    }

    public String[] allHeaders() {
        return ArrayUtils.addAll(identifiers, group);
    }
}
