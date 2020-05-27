package org.anchoranalysis.image.voxel.iterator;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Optional;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.anchoranalysis.image.voxel.iterator.changed.ProcessVoxelNeighbour;
import org.anchoranalysis.image.voxel.nghb.Nghb;

/**
 * Iterate over voxels in an extent/bounding-box/mask calling a processor on each selected voxel
 * 
 * @author Owen Feehan
 *
 */
public class IterateVoxels {
	
	private IterateVoxels() {}

	/** 
	 * Iterate over each voxel that is located on a mask AND optionally a second-mask
	 * 
	 * <p>If a second-mask is defined, it is a logical AND condition. A voxel is only processed if it exists in both masks</p>.
	 * 
	 * @param firstMask the first-mask that is used as a condition on what voxels to iterate
	 * @param secondMask an optional second-mask that can be a further condition
	 * @param process is called for each voxel with that satisfies the conditions using GLOBAL co-ordinates for each voxel.
	 **/
	public static void overMasks( ObjMask firstMask, Optional<ObjMask> secondMask, ProcessVoxel process ) {
		if (secondMask.isPresent()) {
			Optional<BoundingBox> intersection = firstMask.getBoundingBox().intersection().with(secondMask.get().getBoundingBox());
			intersection.ifPresent( bbox->
				callEachPoint(
					bbox,
					requireIntersectionTwice(process, firstMask, secondMask.get())
				)
			);
		} else {
			callEachPoint( firstMask, process );
		}
	}
	
	
	/**
	 * Iterate over each voxel in a sliding-buffer, optionally restricting it to be only voxels in a certain mask
	 *
	 * @param buffer a sliding-buffer whose voxels are iterated over, parially (if a mask is defined) as a whole (if no mask is defined)
	 * @param mask an optional mask that is used as a condition on what voxels to iterate
	 * @param process process is called for each voxel (on the entire {@link SlidingBuffer} or on the object-mask depending) using GLOBAL coordinates.
	 */
	public static void callEachPoint( Optional<ObjMask> mask, SlidingBuffer<?> buffer, ProcessVoxel process ) {
		
		buffer.seek(
			mask.map( om->
				om.getBoundingBox().getCrnrMin().getZ()
			).orElse(0)
		);
		
		callEachPoint(
			mask,
			buffer.extnt(),
			new ProcessVoxelSlide(buffer, process)
		);
	}
	
	
	/**
	 * Iterate over each voxel that is located on a mask if it exists, otherwise iterate over the entire extent
	 * 
	 * @param mask an optional mask that is used as a condition on what voxels to iterate
	 * @param extent if mask isn't defined, then all the voxels in this {@link Extent} are iterated over instead
	 * @param process process is called for each voxel (on the entire {@link Extent} or on the object-mask depending) using GLOBAL coordinates.
	 */
	public static void callEachPoint( Optional<ObjMask> mask, Extent extent, ProcessVoxel process ) {
		if (mask.isPresent()) {
			callEachPoint(mask.get(), process);
		} else {
			callEachPoint(extent, process);
		}
	}
	
	
	/**
	 * Iterate over each voxel that is located on a mask
	 * 
	 * @param mask the mask that is used as a condition on what voxels to iterate
	 * @param process process is called for each voxel with that satisfies the conditions using GLOBAL coordinates.
	 */
	public static void callEachPoint( ObjMask mask, ProcessVoxel process ) {
		callEachPoint(
			mask.getBoundingBox(),
			new RequireIntersectionWithMask(process, mask)
		);
	}

	/**
	 * Iterate over each voxel that is located on a mask - with offsets
	 *
	 * <p>This is identical to the other {@link callEachPoint} but adds offsets, and is optimized for this circumstance.</p>
	 *  
	 * @param mask the mask that is used as a condition on what voxels to iterate
	 * @param extent the scene-size
	 * @param process process is called for each voxel with that satisfies the conditions using GLOBAL coordinates.
	 */
	private static <T extends Buffer> void callEachPoint( ObjMask mask, VoxelBox<T> voxels, ProcessVoxelSliceBuffer<T> process ) {
		// This is re-implemented in full, as reusing existing code with {@link AddOffsets} and
		//  {@link RequireIntersectionWithMask} was not inling using default JVM settings
		// Based on unit-tests, it seems to perform better emperically, even with the new Point3i() adding to the heap.

		Extent extent = voxels.extent();
		Extent extentMask = mask.getVoxelBox().extent();
		Point3i crnrMin = mask.getBoundingBox().getCrnrMin();
		byte valueOn = mask.getBinaryValuesByte().getOnByte();
		
		for (int z=0; z<extentMask.getZ(); z++) {

			// For 3d we need to translate the global index back to local
			int z1 = crnrMin.getZ() + z;
						
			T bb = voxels.getPixelsForPlane(z1).buffer();
			ByteBuffer bbOM = mask.getVoxelBox().getPixelsForPlane(z).buffer();
			process.notifyChangeZ(z1);
			
			for (int y=0; y<extentMask.getY(); y++) {
				int y1 = crnrMin.getY() + y;				
				int offset = extent.offset(crnrMin.getX(), y1);
				
				for (int x=0; x<extentMask.getX(); x++) {

					if (bbOM.get()==valueOn) {
						int x1 = crnrMin.getX() + x;
 
						process.process(
							new Point3i(x1,y1,z1),
							bb,
							offset
						);
					}
					offset++;
				}
			}
		}
	}
	
	/**
	 * Iterate over each voxel in an {@link Extent}
	 * 
	 * @param extent the extent to be iterated over
	 * @param process process is called for each voxel inside the extent using the same coordinates as the extent.
	 */
	public static void callEachPoint( Extent extent, ProcessVoxel process ) {
		callEachPoint(
			new BoundingBox(extent),
			process
		);
	}
	
	
	/**
	 * Iterate over each voxel in a bounding-box
	 * 
	 * @param bbox the box that is used as a condition on what voxels to iterate i.e. only voxels within these bounds
	 * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
	 */
	public static void callEachPoint( BoundingBox bbox, ProcessVoxel process ) {
		
		Point3i crnrMin = bbox.getCrnrMin();
		Point3i crnrMax = bbox.calcCrnrMax();
		
		Point3i pnt = new Point3i();
		
		for(pnt.setZ(crnrMin.getZ()); pnt.getZ()<=crnrMax.getZ(); pnt.incrZ()) {

			process.notifyChangeZ(pnt.getZ());
			
			for(pnt.setY(crnrMin.getY()); pnt.getY()<=crnrMax.getY(); pnt.incrY()) {
				
				process.notifyChangeY(pnt.getY());
				
				for(pnt.setX(crnrMin.getX()); pnt.getX()<=crnrMax.getX(); pnt.incrX()) {
					process.process(pnt);
				}
			}
		}
	}
	
	
	/**
	 * Iterate over each voxel - with an associated buffer for each slice from a voxel-bo
	 *
	 * @param voxels a voxel-box, all of whose voxels will be iterated over 
	 * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
	 * @param <T> buffer-type in voxel-box
	 */
	public static <T extends Buffer> void callEachPoint(VoxelBox<T> voxels, ProcessVoxelSliceBuffer<T> process ) {
		Extent extent = voxels.extent(); 
		callEachPoint(
			extent,
			new RetrieveBufferForSlice<>(voxels, process)
		);
	}

	/**
	 * Iterate over each voxel in a bounding-box - with an associated buffer for each slice from a voxel-bo
	 *
	 * @param voxels a voxel-box for which {@link} refers to a subregion. 
	 * @param bbox the box that is used as a condition on what voxels to iterate i.e. only voxels within these bounds
	 * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
	 * @param <T> buffer-type in voxel-box
	 */
	public static <T extends Buffer> void callEachPoint(VoxelBox<T> voxels, BoundingBox bbox, ProcessVoxelSliceBuffer<T> process ) {
		callEachPoint(
			bbox,
			new RetrieveBufferForSlice<>(voxels, process)
		);
	}
	
	/**
	 * Iterate over each voxel in a mask - with an associated buffer for each slice from a voxel-bo
	 *
	 * @param voxels a voxel-box for which {@link} refers to a subregion. 
	 * @param mask the mask is used as a condition on what voxels to iterate i.e. only voxels within these bounds
	 * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
	 * @param <T> buffer-type in voxel-box
	 */
	public static <T extends Buffer> void callEachPoint( VoxelBox<T> voxels, ObjMask mask, ProcessVoxelSliceBuffer<T> process) {
		callEachPoint(
			mask,
			voxels,
			process
		);
	}
	
	/**
	 * Iterate over each voxel in a mask - with an associated buffer for each slice from a voxel-bo
	 *
	 * @param voxels a voxel-box for which {@link} refers to a subregion. 
	 * @param mask the mask is used as a condition on what voxels to iterate i.e. only voxels within these bounds
	 * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
	 * @param <T> buffer-type in voxel-box
	 */
	public static <T extends Buffer> void callEachPoint( VoxelBox<T> voxels, BinaryChnl mask, ProcessVoxelSliceBuffer<T> process) {
		// Treat it as one giant object box. This will involve some additions and subtractions of 0 during the processing of voxels
		// but after some quick emperical checks, it doesn't seem to make a performance difference. Probably the JVM is smart enough
		// to optimize away these redundant calcualations.
		callEachPoint(
			new ObjMask(mask.binaryVoxelBox()),
			voxels,
			process
		);
	}
	
	/**
	 * Iterate over each voxel that is located on a mask if it exists, otherwise iterate over the entire voxel-box.
	 * 
	 * <p>This is similar behaviour to {@link callEachPoint} but adds a buffer for each slice.</p>
	 */
	public static <T extends Buffer> void callEachPoint( Optional<ObjMask> mask, VoxelBox<T> voxels, ProcessVoxelSliceBuffer<T> process) {
		Extent extent = voxels.extent();
		
		// Note the offsets must be added before any additional restriction like a mask, to make sure they are calculate for EVERY process.
		// Therefore we {@link AddOffsets} must be interested as the top-most level in the processing chain
		// (i.e. {@link AddOffsets} must delegate to {@link RequireIntersectionWithMask} but not the other way round.
		if (mask.isPresent()) {
			callEachPoint(
				mask.get(),
				voxels,
				process
			);
		} else {
			callEachPoint(
				extent,
				new RetrieveBufferForSlice<T>(voxels, process)
			);
		}
	}
	
	/**
	 * Iterate over each point in the neighbourhood of an existing point - also setting the source of a delegate
	 * 
	 * @param sourcePnt the point to iterate over its neighbourhood
	 * @param nghb a definition of what constitutes the neighbourhood
	 * @param do3D whether to iterate in 2D or 3D
	 * @param process is called for each voxel in the neighbourhood of the source-point.
	 * @return the result after processing each point in the neighbourhood
	 */
	public static <T> T callEachPointInNghb( Point3i sourcePnt, Nghb nghb, boolean do3D, ProcessVoxelNeighbour<T> process, int sourceVal, int sourceOffsetXY) {
		process.initSource(sourcePnt, sourceVal, sourceOffsetXY);
		nghb.processAllPointsInNghb(do3D, process);
		return process.collectResult();
	}
		
	private static ProcessVoxel requireIntersectionTwice( ProcessVoxel processor, ObjMask mask1, ObjMask mask2 ) {
		ProcessVoxel inner = new RequireIntersectionWithMask(processor, mask2);
		return new RequireIntersectionWithMask(inner, mask1);
	}
}