package org.anchoranalysis.io.xml;

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


import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.anchoranalysis.io.xml.XmlOutputter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import anchor.test.TestLoader;

public class XmlOutputterTest {
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void test() throws ParserConfigurationException, SAXException, IOException, TransformerException, URISyntaxException {
		String testPathIn = "simpleXML01.xml";
		Path pathOut = folder.newFile("a file name with_spaces_and_underscores_.xml").toPath();
		
		TestLoader loader = TestLoader.createFromDefaultSystemProperty();
		
		Document docIn = loader.openXmlFromTestPath( testPathIn );
		
		XmlOutputter.writeXmlToFile(docIn, pathOut);
	
		Document docOut = TestLoader.openXmlAbsoluteFilePath( pathOut );

		assertTrue( TestLoader.areXmlEqual(docIn, docOut) );
	}

}
