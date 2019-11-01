package ch.ethz.biol.cell.mpp.gui.videostats.internalframe.markredraw;

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


import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;

import ch.ethz.biol.cell.mpp.cfg.Cfg;

public class ColoredCfg implements Iterable<Mark> {

	private Cfg cfg;
	private ColorList colorList;
	
	public ColoredCfg() {
		this( new Cfg(), new ColorList() );
	}
	
	public ColoredCfg(Cfg cfg, ColorList colorList) {
		super();
		assert(cfg!=null);
		this.cfg = cfg;
		this.colorList = colorList;
	}
	

	
	// NB, this changes the IDs of the marks
	public ColoredCfg(Cfg cfg, ColorIndex colorIndex, IDGetter<Mark> colorIDGetter ) {
		super();
		this.cfg = cfg;
		assert(cfg!=null);
		assert( colorIndex!= null );
		
		this.colorList = new ColorList();
		
		if (cfg!=null) {
			for( int i=0; i<cfg.size(); i++) {
				Mark m = cfg.get(i); 
				this.colorList.add( colorIndex.get( colorIDGetter.getID(m, i)) );
			}
		}
	}
	
	public ColoredCfg(Mark mark, RGBColor color) {
		super();
		this.cfg = new Cfg(mark);
		this.colorList = new ColorList(color);
	}

	public Cfg getCfg() {
		return cfg;
	}

	public ColorList getColorList() {
		return colorList;
	}
	
	public void addChangeID( Mark m, RGBColor color ) {
		cfg.add(m);
		m.setId( colorList.addWithIndex( color ));
	}
	
	public void add( Mark m, RGBColor color ) {
		cfg.add(m);
		colorList.add( color );
	}
	
	public void addAll( Cfg cfg, RGBColor color ) {
		for( Mark m : cfg ) {
			add( m, color );
		}
	}
	
	public void addAll( ColoredCfg cfg ) {
		for( int i=0; i<cfg.size(); i++) {
			add( cfg.getCfg().get(i), cfg.colorList.get(i) );
		}
	}

	@Override
	public Iterator<Mark> iterator() {
		return cfg.iterator();
	}

	public final int size() {
		return cfg.size();
	}
	
	public ColoredCfg deepCopy() {
		
		ColoredCfg newCfg = new ColoredCfg();
		newCfg.cfg = cfg.deepCopy();
		newCfg.colorList = colorList.deepCopy();
		return newCfg;
	}
	
	public ColoredCfg shallowCopy() {
		
		ColoredCfg newCfg = new ColoredCfg();
		newCfg.cfg = cfg.shallowCopy();
		newCfg.colorList = colorList.shallowCopy();
		return newCfg;
	}
	
	public ColoredCfg createMerged( ColoredCfg toMerge ) {
		
		ColoredCfg mergedNew = shallowCopy();
		//HashMap<Integer,Mark> mergedHash = cfg1.createIdHashMap();
		
		Set<Mark> set = mergedNew.getCfg().createSet();
		
		for (int i=0; i<toMerge.size(); i++) {
			Mark m = toMerge.getCfg().get(i);
			
			if (!set.contains(m)) {
				mergedNew.getCfg().add( m );
				mergedNew.getColorList().add( toMerge.getColorList().get(i) );
			}
		}
		return mergedNew;
	}
	
	
	
	
	// Calculates mask
	public ColoredCfg subsetWhereBBoxIntersects( ImageDim bndScene, int regionID, List<BoundingBox> intersectList ) {
		
		ColoredCfg intersectCfg = new ColoredCfg();
		
		for (int i=0; i<getCfg().size(); i++) {
			Mark mark = getCfg().get(i);
			
			if (mark.bbox(bndScene,regionID).intersect(intersectList)) {
				intersectCfg.add(mark.duplicate(), getColorList().get(i));
			}
		}
		
		//log.info( String.format("intersect size size=%d", intersectCfg.size() ));
		
		return intersectCfg;
	}

	public void remove(int index) {
		colorList.remove(index);
		cfg.remove(index);
	}

	

}
