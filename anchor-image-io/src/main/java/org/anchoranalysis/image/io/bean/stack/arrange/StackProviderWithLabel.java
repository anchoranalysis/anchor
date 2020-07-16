/* (C)2020 */
package org.anchoranalysis.image.io.bean.stack.arrange;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.NullParamsBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProviderHolder;
import org.anchoranalysis.image.stack.Stack;

@NoArgsConstructor
public class StackProviderWithLabel extends NullParamsBean<StackProviderWithLabel> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private StackProvider stackProvider;

    @BeanField @Getter @Setter private String label;
    // END BEAN PROPERTIES

    public StackProviderWithLabel(Stack stack, String label) {
        this.stackProvider = new StackProviderHolder(stack);
        this.label = label;
    }
}
