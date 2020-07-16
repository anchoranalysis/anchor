/* (C)2020 */
package org.anchoranalysis.image.object.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Like an {@ObjectCollection} but each object has associated properties.
 *
 * @author Owen Feehan
 */
public class ObjectCollectionWithProperties implements Iterable<ObjectWithProperties> {

    private final List<ObjectWithProperties> delegate;

    public ObjectCollectionWithProperties() {
        delegate = new ArrayList<>();
    }

    public ObjectCollectionWithProperties(ObjectMask object) {
        this(ObjectCollectionFactory.from(object));
    }

    public ObjectCollectionWithProperties(ObjectCollection objects) {
        delegate = objects.stream().mapToList(ObjectWithProperties::new);
    }

    public boolean add(ObjectMask object) {
        return delegate.add(new ObjectWithProperties(object));
    }

    public boolean add(ObjectWithProperties object) {
        return delegate.add(object);
    }

    public ObjectWithProperties get(int index) {
        return delegate.get(index);
    }

    @Override
    public Iterator<ObjectWithProperties> iterator() {
        return delegate.iterator();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    /**
     * Returns the contained-objects without corresponding properties
     *
     * <p>This is an IMMUTABLE operation.
     */
    public ObjectCollection withoutProperties() {
        return ObjectCollectionFactory.mapFrom(delegate, ObjectWithProperties::getMask);
    }
}
