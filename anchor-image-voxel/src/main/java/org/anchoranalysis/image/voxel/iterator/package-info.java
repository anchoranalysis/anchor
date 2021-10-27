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
/**
 * Utilities to iterate over voxel-locations in images and sub-regions of images.
 *
 * <p>The classes are named by the space over which they iterate (all voxels, within a bounding box,
 * or object etc.)
 *
 * <p>They have static functions that indicate what kind of parameters are made available to the
 * function called for each voxel:
 *
 * <ul>
 *   <li>withPoint for when only a {@link org.anchoranalysis.spatial.point.Point3i} is provided.
 *   <li>withVoxelBuffer for when additionally a {@link
 *       org.anchoranalysis.image.voxel.buffer.VoxelBuffer} for the respective-slice is provided.
 *   <li>withBuffer for when additionally a buffer (as used within {@link
 *       org.anchoranalysis.image.voxel.buffer.VoxelBuffer}) for the respective-slice is provided.
 * </ul>
 *
 * <p>Conventionally, the static functions order their parameters.
 *
 * <ol>
 *   <li>The restriction on where to iterate (e.g. object-mask, bounding-box etc.)
 *   <li>One or more {@link org.anchoranalysis.image.voxel.Voxels} to provide buffers or {@link
 *       org.anchoranalysis.image.voxel.buffer.VoxelBuffer}.
 *   <li>A functional-interface called a <i>processor</i> that is applied to each matching voxel.
 * </ol>
 */
package org.anchoranalysis.image.voxel.iterator;
