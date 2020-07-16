/* (C)2020 */
package org.anchoranalysis.image.bean.provider.stack;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.image.stack.Stack;

@NoArgsConstructor
public class StackProviderReference extends StackProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String id = "";
    // END BEAN PROPERTIES

    private Stack stack;

    public StackProviderReference(String id) {
        this.id = id;
    }

    @Override
    public Stack create() throws CreateException {
        if (stack == null) {
            try {
                this.stack = getInitializationParameters().getStackCollection().getException(id);
            } catch (NamedProviderGetException e) {
                throw new CreateException(e);
            }
        }
        return stack;
    }
}
