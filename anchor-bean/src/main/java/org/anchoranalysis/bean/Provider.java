/* (C)2020 */
package org.anchoranalysis.bean;

import org.anchoranalysis.core.error.CreateException;

/**
 * Indicates a particular type of bean that creates another object
 *
 * @author Owen Feehan
 * @param <T> the item the bean creates
 */
@FunctionalInterface
public interface Provider<T> {

    T create() throws CreateException;
}
