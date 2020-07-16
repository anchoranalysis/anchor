/* (C)2020 */
package org.anchoranalysis.bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// Indicates that a field of a class should be included in a equals() check
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionalBean {}
