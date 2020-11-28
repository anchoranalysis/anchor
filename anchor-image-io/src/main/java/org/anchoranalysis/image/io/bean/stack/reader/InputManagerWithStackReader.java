package org.anchoranalysis.image.io.bean.stack.reader;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.input.bean.InputManager;

public abstract class InputManagerWithStackReader<T extends InputFromManager>
        extends InputManager<T> {

    // START BEAN PROPERTIES
    @BeanField @DefaultInstance @Getter @Setter private StackReader stackReader;
    // END BEAN PROPERTIES
}
