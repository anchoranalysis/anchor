/* (C)2020 */
package org.anchoranalysis.bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// Indicates that a field of a class should be ignored during the normal recursive initialisation
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipInit {}
