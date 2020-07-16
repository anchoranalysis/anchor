/* (C)2020 */
package org.anchoranalysis.image.bean.provider.stack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.stack.Stack;

// We don't really use this as a bean, convenient way of inserting stack into providers in bean
// parameters
// This is hack. When inserted into the usual framework, lots of standard bean behaviour wont work.
@NoArgsConstructor
@AllArgsConstructor
public class StackProviderHolder extends StackProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private Stack stack;
    // END BEAN PROPERTIES

    @Override
    public Stack create() throws CreateException {
        return stack;
    }
}
