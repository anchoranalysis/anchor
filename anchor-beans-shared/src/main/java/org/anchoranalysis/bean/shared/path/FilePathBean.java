package org.anchoranalysis.bean.shared.path;

import org.anchoranalysis.bean.initializable.InitializableBean;
import org.anchoranalysis.bean.initializable.property.PropertyInitializer;
import org.anchoranalysis.bean.initializable.property.SimplePropertyDefiner;

public abstract class FilePathBean<T> extends InitializableBean<T, FilePathInitialization> {

    protected FilePathBean() {
        super(
                new PropertyInitializer<>(FilePathInitialization.class),
                new SimplePropertyDefiner<>(FilePathInitialization.class));
    }
}
