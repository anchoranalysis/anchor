package org.anchoranalysis.image.bean.sgmn.binary;

/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.voxel.box.VoxelBox;

// Paramters that are optionally associated with BinarySgmn
public class BinarySgmnParameters {

	private VoxelBox<?> voxelBoxGradient;
	private Histogram intensityHistogram;
	private ImageRes res;
	private KeyValueParams keyValueParams;

	public VoxelBox<?> getVoxelBoxGradient() {
		return voxelBoxGradient;
	}

	public void setVoxelBoxGradient(VoxelBox<?> voxelBoxGradient) {
		this.voxelBoxGradient = voxelBoxGradient;
	}

	public Histogram getIntensityHistogram() {
		return intensityHistogram;
	}

	public void setIntensityHistogram(Histogram intensityHistogram) {
		this.intensityHistogram = intensityHistogram;
	}

	public ImageRes getRes() {
		return res;
	}

	public void setRes(ImageRes res) {
		this.res = res;
	}

	public KeyValueParams getKeyValueParams() {
		return keyValueParams;
	}

	public void setKeyValueParams(KeyValueParams keyValueParams) {
		this.keyValueParams = keyValueParams;
	}
	
	
}
