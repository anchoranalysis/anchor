/* (C)2020 */
package org.anchoranalysis.feature.calc.results;

class ArrayComparer {

    public boolean compareArrays(Object[] objects1, Object[] objects2) {
        if (objects1 == null) {
            return (objects2.length == 0);
        }

        if (objects2 == null) {
            return (objects1.length == 0);
        }

        if (objects1.length != objects2.length) {
            return false;
        }

        for (int i = 0; i < objects1.length; i++) {

            if (!compareItem(objects1[i], objects2[i])) {
                return false;
            }
        }
        return true;
    }

    protected boolean compareItem(Object object1, Object object2) {
        return object1.equals(object2);
    }
}
