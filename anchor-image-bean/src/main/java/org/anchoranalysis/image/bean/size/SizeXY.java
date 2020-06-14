package org.anchoranalysis.image.bean.size;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;

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
	
	public SizeXY(int width, int height) {
		super();
		this.width = width;
		this.height = height;
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
