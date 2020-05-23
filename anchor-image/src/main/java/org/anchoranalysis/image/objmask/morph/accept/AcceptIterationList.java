package org.anchoranalysis.image.objmask.morph.accept;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class AcceptIterationList implements AcceptIterationConditon {
	private List<AcceptIterationConditon> list = new ArrayList<>();

	@Override
	public boolean acceptIteration(VoxelBox<ByteBuffer> buffer, BinaryValues bvb) throws OperationFailedException {
		for (AcceptIterationConditon ai : list) {
			if (!ai.acceptIteration(buffer, bvb)) {
				return false;
			}
		}
		return true;
	}

	public boolean add(AcceptIterationConditon e) {
		return list.add(e);
	}
	
	
}