package org.anchoranalysis.bean.xml.factory;

/*
 * #%L
 * anchor-bean
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * Describes an XMLNode so a user can identify it in a document
 * 
 * @author Owen Feehan
 *
 */
class HelperDescribeXmlNode {

	private HelperDescribeXmlNode() {}
	
	/**
	 * Do we consider an attribute a configuration attribute or not
	 * 
	 * @param node node we consider
	 * @return true if it's a configuration node, false otherwise
	 */
	private static boolean isConfigAttribute( ConfigurationNode node  ) {
		return node.getName().startsWith("config-");
	}
	
	private static boolean isRegularAttribute( ConfigurationNode node  ) {
		return !isConfigAttribute(node);
	}

	/**
	 * Describe an attribute key-value pair
	 * 
	 * @param node attribute
	 * @return a description in form of KEY=VALUE
	 */
	private static String describeAttributeKeyValue( ConfigurationNode node ) {
		return String.format("%s=%s", node.getName(), node.getValue().toString() );
	}
	
	/**
	 * Appends a description of an attribute-collection to a stringbuilder
	 * 
	 * @param nodes collection of attribute nodes
	 * @param sb string-builder
	 * @return a string in the format [ATTRIBUTE1_DESCRIPTION, ATTRIBUTE2_DESCRIPTION, ATTRIBUTE3_DESCRIPTION]
	 */
	private static void describeAttributes( Collection<ConfigurationNode> nodes, StringBuilder sb ) {
		// We show nothing if it's empty
		if (nodes.isEmpty()) {
			return;
		}
		sb.append(" ");
		String attributeDesc = nodes.stream().map( HelperDescribeXmlNode::describeAttributeKeyValue ).collect( Collectors.joining(", ", "[", "]") );
		sb.append( attributeDesc );
	}
	
	/**
	 * Describes a single-line (representing a XML bean) in the output, indicating its name and its attributes
	 * 
	 * @param node a XML node
	 * @return a string description as above
	 */
	private static String describeLine( ConfigurationNode node ) {
		StringBuilder sb = new StringBuilder();
		sb.append("");
		
		sb.append( node.getName() );
		
		// Regular attributes
		List<ConfigurationNode> regularAttributes = node.getAttributes().stream().filter( HelperDescribeXmlNode::isRegularAttribute ).collect(Collectors.toList());
		describeAttributes(regularAttributes,sb);

		// Config attributes
		List<ConfigurationNode> configAttributes = node.getAttributes().stream().filter( HelperDescribeXmlNode::isConfigAttribute ).collect(Collectors.toList());
		describeAttributes(configAttributes,sb);
		
		return sb.toString();
	}
	
	/**
	 * Gives a multi-line description of a XML node, by traversing a path from XML's root element until the node itself
	 * Each node is represented by a line, and is described by its name and its attributes
	 * 
	 * Collectively this helps to describe a particular XML node in a document
	 * 
	 * @param node the XML-node to describe
	 * @return a multi-line string as above
	 */
	public static String describeXMLNode( ConfigurationNode node ) {
		// We recurse back to the top, putting each item at the front of a linked list
		List<ConfigurationNode> listNames = new ArrayList<ConfigurationNode>();
		
		ConfigurationNode nodeCurrent = node;
		while( nodeCurrent!= null ) {
			listNames.add( 0, nodeCurrent );
			nodeCurrent = nodeCurrent.getParentNode();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(" /---- Identifying XML Element ");
		sb.append( System.lineSeparator() );
		
		// Concatenate with newline and bullet in between 
		String sep = System.lineSeparator() + "-| ";
		sb.append( "-| " + listNames.stream()
							   .map( HelperDescribeXmlNode::describeLine )
							   .collect(Collectors.joining(sep)) );
		sb.append( System.lineSeparator() );
		sb.append(" \\----------- ");
		return sb.toString();
	}
}
