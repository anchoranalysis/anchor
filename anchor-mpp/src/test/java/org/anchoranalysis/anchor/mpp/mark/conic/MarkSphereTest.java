/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.conic;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.anchoranalysis.core.geometry.Point3d;
import org.junit.Test;

public class MarkSphereTest {

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {

        MarkSphere ms_in = new MarkSphere();
        ms_in.setId(3);
        ms_in.setPos(new Point3d(4, 5, 6));
        ms_in.setRadius(7);

        ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream serializer = new ObjectOutputStream(memoryOutputStream);
        serializer.writeObject(ms_in);
        serializer.flush();

        ByteArrayInputStream memoryInputStream =
                new ByteArrayInputStream(memoryOutputStream.toByteArray());
        ObjectInputStream deserializer = new ObjectInputStream(memoryInputStream);

        MarkSphere ms_out = (MarkSphere) deserializer.readObject();

        assertTrue(ms_in.equalsDeep(ms_out));
    }
}
