/* (C)2020 */
package org.anchoranalysis.image.object.properties;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.idgetter.IDGetter;

@AllArgsConstructor
public class IDGetterObjectWithProperties implements IDGetter<ObjectWithProperties> {

    private final String propertyName;

    @Override
    public int getID(ObjectWithProperties m, int iter) {
        return (Integer) m.getProperty(propertyName);
    }
}
