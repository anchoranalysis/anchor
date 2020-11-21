package org.anchoranalysis.image.io.stack.output;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A combination of attributions describing how to write a particular stack and any suggestions from the user.
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class StackWriteOptions {

    /** The attributes of the particular stack to write. */
    @Getter private StackWriteAttributes attributes;
}
