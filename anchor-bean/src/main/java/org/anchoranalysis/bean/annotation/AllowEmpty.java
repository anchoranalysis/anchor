/* (C)2020 */
package org.anchoranalysis.bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// Indicates that a String is allowed be empty. By default, it must be popopulated.
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowEmpty {}
