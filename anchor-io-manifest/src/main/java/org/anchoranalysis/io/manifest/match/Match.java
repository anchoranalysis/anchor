/* (C)2020 */
package org.anchoranalysis.io.manifest.match;

public interface Match<T> {

    boolean matches(T obj);
}
