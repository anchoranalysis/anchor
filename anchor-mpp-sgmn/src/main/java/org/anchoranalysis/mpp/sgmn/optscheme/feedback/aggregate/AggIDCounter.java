/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate;

import java.io.PrintWriter;

public class AggIDCounter {

    private double[] idArr;

    public AggIDCounter(int size) {
        idArr = new double[size];
    }

    public AggIDCounter deepCopy() {
        AggIDCounter aic = new AggIDCounter(idArr.length);
        System.arraycopy(idArr, 0, aic.idArr, 0, idArr.length);
        return aic;
    }

    public double get(int id) {
        return idArr[id];
    }

    public void incr(int id) {

        // We ignore negative Ids
        if (id >= 0) {
            idArr[id]++;
        }
    }

    public void reset() {

        for (int i = 0; i < this.idArr.length; i++) {
            idArr[i] = 0;
        }
    }

    public double sum() {
        double v = 0;
        for (int i = 0; i < this.idArr.length; i++) {
            v += idArr[i];
        }
        return v;
    }

    // divison
    public void div(int divider) {
        for (int i = 0; i < this.idArr.length; i++) {
            idArr[i] /= divider;
        }
    }

    // division
    public void div(AggIDCounter divider) {

        if (this.idArr.length != divider.idArr.length) {
            throw new IllegalArgumentException("AggIDCounter sizes are not equal");
        }

        for (int i = 0; i < this.idArr.length; i++) {
            idArr[i] /= divider.idArr[i];
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[");

        for (int i = 0; i < this.idArr.length; i++) {
            s.append(String.format(" %3.6f ", idArr[i]));
        }
        s.append("]");
        return s.toString();
    }

    public void outputToWriter(PrintWriter writer) {
        for (int i = 0; i < this.idArr.length; i++) {

            if (i != 0) {
                writer.print(",");
            }

            writer.printf("%e", idArr[i]);
        }
    }

    public void outputHeaderToWriter(PrintWriter writer, String prefix) {
        for (int i = 0; i < this.idArr.length; i++) {

            if (i != 0) {
                writer.print(",");
            }

            writer.printf("%s%d", prefix, i);
        }
    }
}
