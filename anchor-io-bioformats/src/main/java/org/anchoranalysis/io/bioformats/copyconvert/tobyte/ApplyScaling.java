/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

class ApplyScaling {

    private float convertRatio = 1;
    private float sub = 0;

    public ApplyScaling() {}

    public ApplyScaling(float convertRatio, float sub) {
        super();
        this.convertRatio = convertRatio;
        this.sub = sub;
    }

    public float apply(float in) {
        return (in - sub) * convertRatio;
    }

    public short apply(short in) {
        return (short) ((in - sub) * convertRatio);
    }

    public int apply(int in) {
        return (int) ((in - sub) * convertRatio);
    }
}
