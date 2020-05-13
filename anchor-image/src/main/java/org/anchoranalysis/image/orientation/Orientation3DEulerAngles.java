package org.anchoranalysis.image.orientation;

/*-
 * #%L
 * anchor-image
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

import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.math.rotation.RotationMatrix;
import org.anchoranalysis.math.rotation.RotationMatrix3DFromRadianCreator;
import org.apache.commons.lang.builder.HashCodeBuilder;

// Conventions taken from http://mathworld.wolfram.com/EulerAngles.html
public class Orientation3DEulerAngles extends Orientation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -850189653607136128L;

	private double rotXRadians;	// Alpha
	private double rotYRadians;	// Beta
	private double rotZRadians;	// Gamma
	
	public Orientation3DEulerAngles() {
		this(0.0, 0.0, 0.0);
	}
		
	public Orientation3DEulerAngles(double rotXRadians, double rotYRadians,
			double rotZRadians) {
		super();
		this.rotXRadians = rotXRadians;
		this.rotYRadians = rotYRadians;
		this.rotZRadians = rotZRadians;
	}

	@Override
	public Orientation3DEulerAngles duplicate() {
		
		Orientation3DEulerAngles copy = new Orientation3DEulerAngles();
		copy.rotXRadians = this.rotXRadians;
		copy.rotYRadians = this.rotYRadians;
		copy.rotZRadians = this.rotZRadians;
		return copy;
	}

	@Override
	public boolean equals(Object other) {
		
		if (other == null) { return false; }
		if (other == this) { return true; }
		
		if (!(other instanceof Orientation3DEulerAngles)) {
			return false;
		}
		
		Orientation3DEulerAngles otherCast = (Orientation3DEulerAngles) other;
		
		if (rotXRadians != otherCast.rotXRadians) {
			return false;
		}
		
		if (rotYRadians != otherCast.rotYRadians) {
			return false;
		}
		
		if (rotZRadians != otherCast.rotZRadians) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(rotXRadians)
				.append(rotYRadians)
				.append(rotZRadians)
				.toHashCode();
	}

	public double getRotXRadians() {
		return rotXRadians;
	}

	public void setRotXRadians(double rotXRadians) {
		this.rotXRadians = rotXRadians;
	}

	public double getRotYRadians() {
		return rotYRadians;
	}

	public void setRotYRadians(double rotYRadians) {
		this.rotYRadians = rotYRadians;
	}

	public double getRotZRadians() {
		return rotZRadians;
	}

	public void setRotZRadians(double rotZRadians) {
		this.rotZRadians = rotZRadians;
	}

	@Override
	public String toString() {
		return String.format("%3.3f, %3.3f, %3.3f", rotXRadians, rotYRadians, rotZRadians);
	}
	
	@Override
	public RotationMatrix createRotationMatrix() {
		return new RotationMatrix3DFromRadianCreator(rotXRadians, rotYRadians, rotZRadians).createRotationMatrix();	
	}

	@Override
	public Orientation negative() {
		Orientation3DEulerAngles dup = duplicate();
		dup.rotZRadians += Math.PI;
		dup.rotZRadians = dup.rotZRadians % (2*Math.PI);
		return dup;
	}

	@Override
	public void addProperties(NameValueSet<String> nvc) {
		addProperty(nvc, "X", rotXRadians);
		addProperty(nvc, "Y", rotYRadians);
		addProperty(nvc, "Z", rotZRadians);
	}
	
	private void addProperty(NameValueSet<String> nvc, String dimension, double radians) {
		nvc.add(
			new SimpleNameValue<>(
				String.format("Orientation Angle %s (radians)", dimension),
				String.format("%1.2f", rotXRadians)
			)
		);
	}

	@Override
	public void addPropertiesToMask(ObjMaskWithProperties mask) {
		// NOTHING TO ADD
	}

	@Override
	public int getNumDims() {
		return 3;
	}
}
