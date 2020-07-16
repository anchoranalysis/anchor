/* (C)2020 */
package org.anchoranalysis.bean.xml.factory;

import java.util.List;

@FunctionalInterface
public interface ICreateFromList<T> {

    Object create(List<T> list);
}
