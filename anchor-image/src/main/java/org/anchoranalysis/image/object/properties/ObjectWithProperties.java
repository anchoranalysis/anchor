/* (C)2020 */
package org.anchoranalysis.image.object.properties;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;

/**
 * An {@link ObjectMask} with associated key-value properties.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ObjectWithProperties {

    @Getter private final ObjectMask mask;

    @Getter private final Map<String, Object> properties;

    public ObjectWithProperties(BoundingBox bbox) {
        this(new ObjectMask(bbox));
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
     * <p>This is an IMMUTABLE operation
     *
     * @param funcMap
     * @return the mapped object (with identical properties) to previously.
     */
    public ObjectWithProperties map(UnaryOperator<ObjectMask> funcMap) {
        return new ObjectWithProperties(funcMap.apply(mask), properties);
    }

    public ObjectWithProperties duplicate() {
        ObjectWithProperties out = new ObjectWithProperties(mask.duplicate());
        for (Entry<String, Object> entry : properties.entrySet()) {
            out.properties.put(entry.getKey(), entry.getValue());
        }
        return out;
    }

    public boolean equals(Object obj) {
        return mask.equals(obj);
    }

    public BoundingBox getBoundingBox() {
        return mask.getBoundingBox();
    }

    public VoxelBox<ByteBuffer> getVoxelBox() {
        return mask.getVoxelBox();
    }

    public int hashCode() {
        return mask.hashCode();
    }

    public String toString() {
        return mask.toString();
    }

    public BinaryValuesByte getBinaryValues() {
        return mask.getBinaryValuesByte();
    }
}
