/* (C)2020 */
package org.anchoranalysis.bean.init.params;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NullInitParams implements BeanInitParams {

    private static final NullInitParams INSTANCE = new NullInitParams();

    public static NullInitParams instance() {
        return INSTANCE;
    }
}
