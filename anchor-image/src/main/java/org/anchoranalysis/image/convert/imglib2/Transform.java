package org.anchoranalysis.image.convert.imglib2;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import lombok.AccessLevel;

import lombok.NoArgsConstructor;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class Transform {
    
    public static ByteArray asArray(ByteBuffer buffer) {
        return new ByteArray(buffer.array());
    }
    
    public static ShortArray asArray(ShortBuffer buffer) {
        return new ShortArray(buffer.array());
    }
    
    public static FloatArray asArray(FloatBuffer buffer) {
        return new FloatArray(buffer.array());
    }
}
