package org.anchoranalysis.image.seed;

/*
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.seed.Seed;
import org.anchoranalysis.image.seed.SeedsFactory;
import org.junit.Test;


public class SeedTest {

	@Test 
	public void testSerialization() throws IOException, ClassNotFoundException {
		
		Seed seed_in = SeedsFactory.create( new Point3d(14,15,16) );
		
		ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream(  );
		ObjectOutputStream serializer = new ObjectOutputStream(memoryOutputStream);
		serializer.writeObject( seed_in );
		serializer.flush();

		ByteArrayInputStream memoryInputStream = new ByteArrayInputStream( memoryOutputStream.toByteArray() );
		ObjectInputStream deserializer = new ObjectInputStream(memoryInputStream);
		
		Seed seed_out = (Seed) deserializer.readObject();

		assertTrue( seed_in.equalsDeep(seed_out) );
	}
}
