/* (C)2020 */
package org.anchoranalysis.bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates a default bean-instance for a value, which is used if user has not explicitly set
 * another value
 *
 * <p>A corresponding default must be set in the defaults passed to the initialize bean route in
 * {\link org.anchoranalysis.bean.AnchorBean}
 *
 * @author Owen Feehan
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultInstance {}
