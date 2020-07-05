package org.anchoranalysis.annotation.io.assignment.generator;

/*
 * #%L
 * anchor-annotation
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


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.anchoranalysis.anchor.overlay.bean.objmask.writer.ObjMaskWriter;
import org.anchoranalysis.annotation.io.assignment.Assignment;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.bean.provider.stack.StackProviderArrangeRaster;
import org.anchoranalysis.image.io.bean.stack.arrange.StackProviderTileWithLabels;
import org.anchoranalysis.image.io.bean.stack.arrange.StackProviderWithLabel;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.StackGenerator;
import org.anchoranalysis.image.io.generator.raster.obj.rgb.RGBObjMaskGenerator;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.bean.objmask.writer.IfElseWriter;
import org.anchoranalysis.io.bean.objmask.writer.RGBOutlineWriter;
import org.anchoranalysis.io.bean.objmask.writer.RGBSolidWriter;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class AssignmentGenerator extends RasterGenerator {
	
	private DisplayStack background;
	private Assignment assignment;
	private StackGenerator delegate;
	private boolean mipOutline;
	private int outlineWidth = 1;
	private ColorPool colorPool;
	
	private String leftName = "annotation";
	private String rightName = "result";
	
	/**
	 * 
	 * @param background
	 * @param assignment
	 * @param colorSetGeneratorPaired
	 * @param colorSetGeneratorUnpaired
	 * @param mipOutline
	 * @param factory
	 * @param replaceMatchesWithSolids if TRUE, then any matching objects are displayed as solids, rather than outlines. if FALSE, all objects are displayed as outlines.
	 */
	AssignmentGenerator(
			DisplayStack background,
			Assignment assignment,
			ColorPool colorPool,
			boolean mipOutline
		) {
		super();
		this.background = background;
		this.assignment = assignment;
		this.mipOutline = mipOutline;
		this.colorPool = colorPool;
		
		delegate = new StackGenerator(true, "assignmentComparison");
	}

	@Override
	public boolean isRGB() {
		return true;
	}
	
	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		StackProviderArrangeRaster stackProvider = createTiledStackProvider(
			createRGBOutlineStack(true),
			createRGBOutlineStack(false),
			leftName,
			rightName
		);
		
		try {
			Stack combined = stackProvider.create();
			delegate.setIterableElement(combined);
			return delegate.generate();
			
		} catch (CreateException e) {
			throw new OutputWriteFailedException(e);
		}
	}

	private static StackProviderArrangeRaster createTiledStackProvider(
		Stack stackLeft,
		Stack stackRight,
		String nameLeft,
		String nameRight
	) {
		List<StackProviderWithLabel> listProvider = new ArrayList<>();
		listProvider.add( new StackProviderWithLabel(stackLeft, nameLeft) );
		listProvider.add( new StackProviderWithLabel(stackRight, nameRight) );
		
		return StackProviderTileWithLabels.createStackProvider(
			listProvider,
			2,
			false,
			false,
			true
		);
	}
	
	private Stack createRGBOutlineStack(boolean left) throws OutputWriteFailedException {
		try {
			return createRGBOutlineStack(
				assignment.getListPaired(left),
				colorPool,
				assignment.getListUnassigned(left)
			);
		} catch (OperationFailedException e) {
			throw new OutputWriteFailedException(e);
		}
	}
	
	private Stack createRGBOutlineStack(List<ObjectMask> matchedObjs, ColorPool colorPool, final List<ObjectMask> otherObjs ) throws OutputWriteFailedException, OperationFailedException {
		
		ObjectCollection omc = ObjectCollectionFactory.from(matchedObjs, otherObjs);

		return createGenerator(
			otherObjs,
			colorPool.createColors(otherObjs.size()),
			omc
		).generate();
	}

	
	private RGBObjMaskGenerator createGenerator( List<ObjectMask> otherObjs, ColorList cols, ObjectCollection omc ) {
		
		ObjMaskWriter outlineWriter = createOutlineWriter();
		
		if (colorPool.isDifferentColorsForMatches()) {
			ObjMaskWriter objMaskWriter = createConditionalWriter(
				otherObjs,
				outlineWriter
			);
			return createGenerator( objMaskWriter, cols, omc );
		} else {
			return createGenerator( outlineWriter, cols, omc );
		}
	}
	
	private RGBObjMaskGenerator createGenerator( ObjMaskWriter objMaskWriter, ColorList cols, ObjectCollection omc ) {
		return new RGBObjMaskGenerator(
			objMaskWriter,
			new ObjectCollectionWithProperties(omc),
			background,
			cols
		);
	}
	
	
	private ObjMaskWriter createConditionalWriter( List<ObjectMask> otherObjs, ObjMaskWriter writer ) {
		
		IfElseWriter.Condition condition = new IfElseWriter.Condition() {

			@Override
			public boolean isTrue(ObjectWithProperties mask,
					RGBStack stack, int id) {
				return otherObjs.contains(mask.getMask());
			}
			
		};
		
		return new IfElseWriter(condition, writer, new RGBSolidWriter() );
	}
	
	private ObjMaskWriter createOutlineWriter() {
		return new RGBOutlineWriter(outlineWidth,mipOutline);
	}
	
	
	
	@Override
	public Optional<ManifestDescription> createManifestDescription() {
		return Optional.of(
			new ManifestDescription("raster", "assignment")
		);
	}

	public int getOutlineWidth() {
		return outlineWidth;
	}

	public void setOutlineWidth(int outlineWidth) {
		this.outlineWidth = outlineWidth;
	}

	public String getLeftName() {
		return leftName;
	}

	public void setLeftName(String annotationName) {
		this.leftName = annotationName;
	}

	public String getRightName() {
		return rightName;
	}

	public void setRightName(String resultName) {
		this.rightName = resultName;
	}

}
