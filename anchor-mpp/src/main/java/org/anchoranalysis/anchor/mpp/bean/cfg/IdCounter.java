/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.cfg;

class IdCounter {

    private int id = 0;

    public IdCounter(int num) {
        this.id = num;
    }

    public void increment() {
        id++;
    }

    public int getId() {
        return id;
    }

    public int getIdAndIncrement() {
        return id++;
    }
}
