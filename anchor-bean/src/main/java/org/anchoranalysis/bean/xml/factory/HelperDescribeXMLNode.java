/* (C)2020 */
package org.anchoranalysis.bean.xml.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalList;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * Describes an XMLNode so a user can identify it in a document
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperDescribeXmlNode {

    /**
     * Do we consider an attribute a configuration attribute or not
     *
     * @param node node we consider
     * @return true if it's a configuration node, false otherwise
     */
    private static boolean isConfigAttribute(ConfigurationNode node) {
        return node.getName().startsWith("config-");
    }

    private static boolean isRegularAttribute(ConfigurationNode node) {
        return !isConfigAttribute(node);
    }

    /**
     * Describe an attribute key-value pair
     *
     * @param node attribute
     * @return a description in form of KEY=VALUE
     */
    private static String describeAttributeKeyValue(ConfigurationNode node) {
        return String.format("%s=%s", node.getName(), node.getValue().toString());
    }

    /**
     * Appends a description of an attribute-collection to a stringbuilder
     *
     * @param nodes collection of attribute nodes
     * @param sb string-builder
     * @return a string in the format [ATTRIBUTE1_DESCRIPTION, ATTRIBUTE2_DESCRIPTION,
     *     ATTRIBUTE3_DESCRIPTION]
     */
    private static void describeAttributes(
            Collection<ConfigurationNode> nodes,
            Predicate<ConfigurationNode> predicate,
            StringBuilder sb) {

        List<ConfigurationNode> nodesFiltered = FunctionalList.filterToList(nodes, predicate);

        // We show nothing if it's empty
        if (nodesFiltered.isEmpty()) {
            return;
        }
        sb.append(" ");
        String attributeDesc =
                nodesFiltered.stream()
                        .map(HelperDescribeXmlNode::describeAttributeKeyValue)
                        .collect(Collectors.joining(", ", "[", "]"));
        sb.append(attributeDesc);
    }

    /**
     * Describes a single-line (representing a XML bean) in the output, indicating its name and its
     * attributes
     *
     * @param node a XML node
     * @return a string description as above
     */
    private static String describeLine(ConfigurationNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("");

        sb.append(node.getName());

        // Regular attributes
        describeAttributes(node.getAttributes(), HelperDescribeXmlNode::isRegularAttribute, sb);

        // Config attributes
        describeAttributes(node.getAttributes(), HelperDescribeXmlNode::isConfigAttribute, sb);

        return sb.toString();
    }

    /**
     * Gives a multi-line description of a XML node, by traversing a path from XML's root element
     * until the node itself Each node is represented by a line, and is described by its name and
     * its attributes
     *
     * <p>Collectively this helps to describe a particular XML node in a document
     *
     * @param node the XML-node to describe
     * @return a multi-line string as above
     */
    public static String describeXMLNode(ConfigurationNode node) {
        // We recurse back to the top, putting each item at the front of a linked list
        List<ConfigurationNode> listNames = new ArrayList<>();

        ConfigurationNode nodeCurrent = node;
        while (nodeCurrent != null) {
            listNames.add(0, nodeCurrent);
            nodeCurrent = nodeCurrent.getParentNode();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(" /---- Identifying XML Element ");
        sb.append(System.lineSeparator());

        // Concatenate with newline and bullet in between
        String sep = System.lineSeparator() + "-| ";
        sb.append(
                "-| "
                        + listNames.stream()
                                .map(HelperDescribeXmlNode::describeLine)
                                .collect(Collectors.joining(sep)));
        sb.append(System.lineSeparator());
        sb.append(" \\----------- ");
        return sb.toString();
    }
}
