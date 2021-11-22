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

package org.anchoranalysis.image.core.object.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesInt;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * An {@link ObjectMask} with associated key-value properties.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ObjectWithProperties {

    private final ObjectMask object;

    /** A mapping between keys and corresponding values. */
    @Getter private final Map<String, Object> properties;

    /**
     * Creates as a bounding-box with all corresponding mask voxels set to <i>off</i>.
     *
     * <p>Default {@link BinaryValuesInt} of (off=0, on=255) are used for the mask.
     *
     * @param box bounding-box.
     */
    public ObjectWithProperties(BoundingBox box) {
        this(new ObjectMask(box));
    }

    /**
     * Creates with an existing {@link ObjectMask} and empty properties.
     *
     * @param object the object.
     */
    public ObjectWithProperties(ObjectMask object) {
        this(object, new HashMap<>());
    }

    /**
     * Assigns a value to a property with a particular name.
     *
     * <p>Any existing value with the same name is replaced.
     *
     * @param name the name of the property.
     * @param value the value of the property.
     * @param <T> type of property-value.
     */
    public <T> void setProperty(String name, T value) {
        properties.put(name, value);
    }

    /**
     * Retrieves the value of a property corresponding to a particular name.
     *
     * <p>Note that the user must be careful to retreieve this property with the correct-type as it
     * is cast to {@code <T>} without any checks.
     *
     * @param <T> type of property-value.
     * @param name the name of the property.
     * @return the corresponding property-value to {@code name} or {@code null} if no such value
     *     exists.
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String name) {
        return (T) properties.get(name);
    }

    /**
     * Whether a particular property exists with a particular name.
     *
     * @param name the name.
     * @return true iff the property exists.
     */
    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    /**
     * Executes a consumer on each property.
     *
     * @param consumer a consumer accepting the name and the value of the property.
     */
    public void forEachProperty(BiConsumer<String, Object> consumer) {
        properties.forEach(consumer);
    }

    /**
     * Maps the underlying object-mask to another object-mask, reusing the same properties object.
     *
     * <p>Note the properties are not duplicated, and the new object will reference the same
     * properties.
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @param operator the operator that performs the mapping.
     * @return the mapped object (with identical properties) to previously.
     */
    public ObjectWithProperties map(UnaryOperator<ObjectMask> operator) {
        return new ObjectWithProperties(operator.apply(object), properties);
    }

    /**
     * Deep copies the current instance.
     *
     * @return a deep copy.
     */
    public ObjectWithProperties duplicate() {
        ObjectWithProperties out = new ObjectWithProperties(object.duplicate());
        for (Entry<String, Object> entry : properties.entrySet()) {
            out.properties.put(entry.getKey(), entry.getValue());
        }
        return out;
    }

    /**
     * The bounding-box which gives a location for the object-mask on an image.
     *
     * @return the bounding-box.
     */
    public BoundingBox boundingBox() {
        return object.boundingBox();
    }

    @Override
    public boolean equals(Object obj) {
        return object.equals(obj);
    }

    @Override
    public int hashCode() {
        return object.hashCode();
    }

    @Override
    public String toString() {
        return object.toString();
    }

    /**
     * Exposes the underlying {@link ObjectMask} ignoring any properties.
     *
     * @return the underlying {@link ObjectMask} reusing the existing data object.
     */
    public ObjectMask asObjectMask() {
        return object;
    }

    /**
     * A slice buffer with <i>local</i> coordinates.
     *
     * <p>i.e. with coordinates relative to the bounding-box corner.
     *
     * @param sliceIndexRelative sliceIndex (z) relative to the bounding-box of the object-mask.
     * @return the buffer.
     */
    public UnsignedByteBuffer sliceBufferLocal(int sliceIndexRelative) {
        return object.sliceBufferLocal(sliceIndexRelative);
    }
}
