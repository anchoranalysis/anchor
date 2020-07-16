/* (C)2020 */
package org.anchoranalysis.math.rotation;

// Assigns values for a rotation about a particular axis (in radians)
// axisShift= 0 for x-axis, 1 for y-axis, 2 for z-axis
public class RotMatrixIndxCalc {

    private int shift;
    private int matNumDim;

    public RotMatrixIndxCalc(int shift, int matNumDim) {
        super();
        this.shift = shift;
        this.matNumDim = matNumDim;
    }

    public int calc(int orig) {
        return (orig + shift) % matNumDim;
    }
}
