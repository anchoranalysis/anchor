/* (C)2020 */
package org.anchoranalysis.bean;

public interface StringBeanCollection extends Iterable<String> {

    void add(String s);

    boolean contains(String s);
}
