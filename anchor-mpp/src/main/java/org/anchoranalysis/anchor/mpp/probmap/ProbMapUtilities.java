package org.anchoranalysis.anchor.mpp.probmap;

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


import java.nio.ByteBuffer;

import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class ProbMapUtilities {
	
	private ProbMapUtilities() {}
	
	public static Point3d getUniformRandomPnt( RandomNumberGenerator re, ImageDimensions bndScene ) {
    	// Generate each point
    	Point3d pos = new Point3d();
    	pos.setX( re.nextDouble() * (bndScene.getX()-1) );
    	pos.setY( re.nextDouble() * (bndScene.getY()-1) );
    	pos.setZ( re.nextDouble() * (bndScene.getZ()-1) );
    	return pos;
    }
    
    public static boolean accptPosBinary( VoxelBox<ByteBuffer> vb, Point3d pnt ) {
    	return accptPosBinary(vb, (int) pnt.getX(), (int) pnt.getY(), (int) pnt.getZ());
    }
    
    public static boolean accptPosBinary( VoxelBox<ByteBuffer> vb, int x, int y, int z) {
    	
    	int prob = vb.getVoxel(x,y,z);
    	
    	if (prob==255) {
    		return true;
    	}

    	if (prob==0) {
    		return false;
    	}    	
    	
    	assert false;
    	return false;
    }
    

}
