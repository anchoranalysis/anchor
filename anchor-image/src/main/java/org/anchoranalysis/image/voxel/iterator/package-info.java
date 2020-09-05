/**
 * Utilities to iterate over voxel-locations in images and sub-regions of images.
 * 
 * <p>The classes are named by the space over which they iterate (all voxels, within a bounding box, or object etc.)
 * 
 * <p>They have static functions that indicate what kind of parameters are made available to the function called for each voxel:
 * <ul>
 * <li>withPoint for when only a {@link org.anchoranalysis.core.geometry.Point3i} is provided.
 * <li>withVoxelBuffer for when additionally a {@link org.anchoranalysis.image.voxel.buffer.VoxelBuffer} for the respective-slice is provided.
 * <li>withBuffer for when additionally a buffer (as used within @link org.anchoranalysis.image.voxel.buffer.VoxelBuffer}) for the respective-slice is provided.
 * </ul>
 * 
 *  <p>Conventionally, the static functions order their parameters.
 *  <ol>
 *  <li>The restriction on where to iterate (e.g. object-mask, bounding-box etc.)
 *  <li>One or more {@link org.anchoranalysis.image.voxel.Voxels} to provide buffers or {@link org.anchoranalysis.image.voxel.buffer.VoxelBuffer}.
 *  <li>A functional-interface called a <i>processor</i> that is applied to each matching voxel.
 */
package org.anchoranalysis.image.voxel.iterator;

