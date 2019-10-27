package org.anchoranalysis.io.bean.color.generator;

import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;

/*
 * #%L
 * anchor-io
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


public class VeryBrightColorSetGenerator extends ColorSetGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	// From http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines
	private static String[] hexCodes = new String[] {
		"#fce94f",	// Butter
		"#fcaf3e",	// Orange
		"#e9b96e",	// Chocolate
		"#ad7fa8",	// Plum

		"#edd400",	// Butter
		"#f57900",	// Orange
		"#c17d11",	// Chocolate
		"#75507b",	// Plum
		
		"#c4a000",	// Butter
		"#ce5c00",	// Orange
		"#8f5902",	// Chocolate
		"#5ce566",	// Plum
		"#692DAC",
		"#FF0000",
		"#00FF00",
		"#0000FF",
		
		"#6600CC",		// Purple		
		
		
		"#F20056",
		"#AAF200",

		"#33FF99",		// Greenish-blue
		
		"#FFFF33",		// Yellow
		"#FF9900",		// Orange
		
		"#33FFFF"
	};
	// Previously "#F20056",
	
	private static RGBColor hex2Rgb(String colorStr) {
	    return new RGBColor(
            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
            Integer.valueOf( colorStr.substring( 5, 7 ), 16 )
	    );
	}

	@Override
	public ColorList genColors(int num_colors) {

		int hexCodesSize = hexCodes.length;
		
		ColorList out = new ColorList();
		for( int i=0; i<num_colors; i++) {
			int hexCodeIndex = i % hexCodesSize;
			out.add( hex2Rgb(hexCodes[hexCodeIndex]));
		}
		
		return out;
	}
}
