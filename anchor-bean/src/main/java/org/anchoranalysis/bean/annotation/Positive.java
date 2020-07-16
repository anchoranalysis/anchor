/* (C)2020 */
package org.anchoranalysis.bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// Indicates that a number should be greater than 0
@Retention(RetentionPolicy.RUNTIME)
public @interface Positive {}
