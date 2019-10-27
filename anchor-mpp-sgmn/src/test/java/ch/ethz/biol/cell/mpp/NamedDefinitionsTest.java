package ch.ethz.biol.cell.mpp;

/*-
 * #%L
 * anchor-mpp-sgmn
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

import static org.junit.Assert.*;

import java.nio.file.Path;

import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.xml.BeanXmlLoader;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.image.bean.provider.ChnlProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.junit.Before;
import org.junit.Test;

import anchor.test.TestLoader;

public class NamedDefinitionsTest {

	private TestLoader loader = TestLoader.createFromExecutingJARDirectory(NamedDefinitionsTest.class);
	
	@Before
    public void setUp() {
		RegisterBeanFactories.registerAllPackageBeanFactories(false);
    }
	
	private void checkNamedDefinitions( Define namedDefinitions ) {
		// We assume an order of chnl1 before chnl2
		assertTrue( namedDefinitions.getList(ChnlProvider.class).size()==2 );
		assertTrue( namedDefinitions.getList(ChnlProvider.class).get(0).getName().equals("chnl1") );
		assertTrue( namedDefinitions.getList(ChnlProvider.class).get(1).getName().equals("chnl2") );
		
		// We assume an order of stack1 before stack2
		assertTrue( namedDefinitions.getList(StackProvider.class).size()==2 );
		assertTrue( namedDefinitions.getList(StackProvider.class).get(0).getName().equals("stack1") );
		assertTrue( namedDefinitions.getList(StackProvider.class).get(1).getName().equals("stack2") );
	}
	
	@Test
	public void testStatic() throws BeanXmlException {
		Path pathStatic = loader.resolveTestPath("namedDefinitionsStatic.xml");
		
		Define namedDefinitions = BeanXmlLoader.loadBean( pathStatic );
		checkNamedDefinitions(namedDefinitions);
	}
	
	
	@Test
	public void testDynamic() throws BeanXmlException {
		Path pathDynamic = loader.resolveTestPath("namedDefinitionsDynamic.xml");
		Define namedDefinitions = BeanXmlLoader.loadBean( pathDynamic );
		checkNamedDefinitions(namedDefinitions);
	}

}
