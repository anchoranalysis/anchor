/* (C)2020 */
package org.anchoranalysis.anchor.mpp.plot;

import java.awt.Color;
import java.awt.Paint;

public class NRGGraphItem {

    private String objectID;
    private double nrg;
    private Paint paint = Color.BLUE;

    public NRGGraphItem() {}

    public NRGGraphItem(String objectID, double nrg) {
        super();
        this.objectID = objectID;
        this.nrg = nrg;
    }

    public double getNrg() {
        return nrg;
    }

    public void setNrg(double nrg) {
        this.nrg = nrg;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
