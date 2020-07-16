/* (C)2020 */
package org.anchoranalysis.image.histogram;

public class HistogramCreatorSimple implements HistogramCreator {

    @Override
    public Histogram create() {
        return new HistogramArray(255);
    }
}
