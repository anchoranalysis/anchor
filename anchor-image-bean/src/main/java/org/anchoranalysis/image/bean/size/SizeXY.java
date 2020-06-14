package org.anchoranalysis.image.bean.size;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.extent.Extent;

public class SizeXY extends AnchorBean<SizeXY>{

	// START BEAN PROPERTIES
	@BeanField
	private int width;
	
	@BeanField
	private int height;
	// END BEAN PROPERTIES

	public SizeXY() {
		// STANDARD BEAN CONSTRUCTOR
	}

	/**
	 * Constructor
	 * 
	 * <p>Note the z-dimension in extent is ignored.</p>
	 * 
	 * @param extent extent
	 */
	public SizeXY(Extent extent) {
		this( extent.getX(), extent.getY() );
	}
	
	public SizeXY(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public Extent asExtent() {
		return new Extent(width,height,1);
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SizeXY other = (SizeXY) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		return true;
	}
}
