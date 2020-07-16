/* (C)2020 */
package org.anchoranalysis.bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//
//
//

/**
 * Indicates that a set of definitions of classes can be grouped using this class
 *
 * <p>It is typically set on the abstract parent class of a set of classes
 *
 * @see org.anchoranalysis.bean.define.Define
 * @author Owen Feehan
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GroupingRoot {}
