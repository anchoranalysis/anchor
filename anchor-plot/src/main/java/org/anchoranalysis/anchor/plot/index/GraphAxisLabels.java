/* (C)2020 */
package org.anchoranalysis.anchor.plot.index;

/** Labels for x and y axis in graph */
public class GraphAxisLabels {

    private String x = "Index";
    private String y = "Value";

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setXY(String x, String y) {
        this.x = x;
        this.y = y;
    }

    public void setY(String y) {
        this.y = y;
    }
}
