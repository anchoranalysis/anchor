/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.object.properties;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * An {@link ObjectMask} with associated key-value properties.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ObjectWithProperties {

    private final ObjectMask object;

    @Getter private final Map<String, Object> properties;

    public ObjectWithProperties(BoundingBox box) {
        this(new ObjectMask(box));
    }

    public ObjectWithProperties(ObjectMask object) {
        this(object, new HashMap<>());
    }

    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    /**
     * Maps the underlying object-mask to another object-mask, reusing the same properties object.
     *
     * <p>Note the properties are not duplicated, and the new object will reference the same
     * properties.
     *
     * <p>This is an <b>immutable</b> operation
     *
     * @param funcMap
     * @return the mapped object (with identical properties) to previously.
     */
    public ObjectWithProperties map(UnaryOperator<ObjectMask> funcMap) {
        return new ObjectWithProperties(funcMap.apply(object), properties);
    }

    public ObjectWithProperties duplicate() {
        ObjectWithProperties out = new ObjectWithProperties(object.duplicate());
        for (Entry<String, Object> entry : properties.entrySet()) {
            out.properties.put(entry.getKey(), entry.getValue());
        }
        return out;
    }

    public boolean equals(Object obj) {
        return object.equals(obj);
    }

    public BoundingBox boundingBox() {
        return object.boundingBox();
    }

    public int hashCode() {
        return object.hashCode();
    }

    public String toString() {
        return object.toString();
    }

    public ObjectMask withoutProperties() {
        return object;
    }

    public ByteBuffer sliceBufferLocal(int sliceIndexRelative) {
        return object.sliceBufferLocal(sliceIndexRelative);
    }
}
