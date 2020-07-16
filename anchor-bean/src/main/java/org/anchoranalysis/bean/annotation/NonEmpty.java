/* (C)2020 */
package org.anchoranalysis.bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// Indicates that a BeanField is non-empty (a collection must have at least-one item).
@Retention(RetentionPolicy.RUNTIME)
public @interface NonEmpty {}
