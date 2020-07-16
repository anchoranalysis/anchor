/*-
 * #%L
 * anchor-mpp
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
