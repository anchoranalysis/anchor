/* (C)2020 */
package org.anchoranalysis.bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// Indicates that a property is a field that is part of the bean
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanField {}
