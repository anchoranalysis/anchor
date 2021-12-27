package org.anchoranalysis.image.bean.spatial.arrange;

import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Tile}.
 * 
 * @author Owen Feehan
 *
 */
class TileTest {

	@Test
	void testHorizontal() throws ArrangeStackException {
		Tile tile = new Tile(2, -1);
		
		ColoredDualStacks.combine(tile, false);
	}
}
