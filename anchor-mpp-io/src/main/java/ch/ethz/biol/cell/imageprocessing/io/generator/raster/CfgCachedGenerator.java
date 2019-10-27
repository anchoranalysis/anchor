package ch.ethz.biol.cell.imageprocessing.io.generator.raster;

import java.nio.file.Path;

import org.anchoranalysis.core.error.CreateException;

/*
 * #%L
 * anchor-mpp-io
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



import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.RasterWriterUtilities;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.io.bean.output.OutputWriteSettings;
import org.anchoranalysis.io.output.OutputWriteFailedException;

import ch.ethz.biol.cell.mpp.cfgtoobjmaskwriter.OverlayWriter;

public class CfgCachedGenerator extends CacheableCfgToRGBGenerator {

	private CachedRGB cachedRGB;
	private OverlayedDisplayStackUpdate element;
	private ErrorReporter errorReporter;
	
	public CfgCachedGenerator( CachedRGB cachedRGB, ErrorReporter errorReporter ) {
		super();
		this.cachedRGB = cachedRGB;
		this.errorReporter = errorReporter;
	}
	
	// THIS MUST BE CALLED before we do any drawing.
	@Override
	public void updateMaskWriter( OverlayWriter maskWriter ) {
		this.cachedRGB.updateMaskWriter( maskWriter );
	}

	@Override
	public DisplayStack generate() throws OutputWriteFailedException {
		try {
			return DisplayStack.create(cachedRGB.getRGB());
		} catch (CreateException e) {
			throw new OutputWriteFailedException(e);
		}
	}

	@Override
	public OverlayedDisplayStackUpdate getIterableElement() {
		return element;	// this.cachedRGB.createCfgWithStack();
	}
	
	@Override
	public void applyRedrawUpdate( OverlayedDisplayStackUpdate update) {
		try {
			setIterableElement(update);
		} catch (SetOperationFailedException e) {
			errorReporter.recordError(CfgCachedGenerator.class, e);
		}
	}
	
	@Override
	public void setIterableElement(OverlayedDisplayStackUpdate element) throws SetOperationFailedException {
		
		// We don't do any changes if is exactly the same, if some other
		//   factor has altered, to change how the generator would
		//   produce it's output then, it must be externally triggered
		if (element==getIterableElement()) {
			return;
		}
		
		this.element = element;

		try {
			cachedRGB.updateCfg( element );
		} catch (OperationFailedException e) {
			throw new SetOperationFailedException(e);
		}
	}

	@Override
	public DisplayStack getBackground() {
		return cachedRGB.getBackground();
	}

	@Override
	public void writeToFile(OutputWriteSettings outputWriteSettings,
			Path filePath) throws OutputWriteFailedException {
		RasterGenerator.writeToFile(cachedRGB.getRGB().asStack(), outputWriteSettings, filePath,true);
	}

	@Override
	public String getFileExtension(OutputWriteSettings outputWriteSettings) {
		return RasterWriterUtilities.getDefaultRasterFileExtension(outputWriteSettings);
	}
}
