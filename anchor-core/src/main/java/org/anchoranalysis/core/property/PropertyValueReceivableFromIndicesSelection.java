/* (C)2020 */
package org.anchoranalysis.core.property;

import org.anchoranalysis.core.index.IndicesSelection;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;

public class PropertyValueReceivableFromIndicesSelection
        implements IPropertyValueReceivable<IntArray> {

    private IndicesSelection indices;

    public PropertyValueReceivableFromIndicesSelection(IndicesSelection indices) {
        super();
        this.indices = indices;
    }

    @Override
    public void addPropertyValueChangeListener(PropertyValueChangeListener<IntArray> l) {
        indices.addListener(l);
    }

    @Override
    public void removePropertyValueChangeListener(PropertyValueChangeListener<IntArray> l) {
        indices.removeListener(l);
    }
}
