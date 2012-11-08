/*
 * movie-renamer-core
 * Copyright (C) 2012 Nicolas Magré
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.utils;

import java.io.StringWriter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * Class XPathUtils
 * 
 * @see http://fr.wikipedia.org/wiki/XPath
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class XPathUtils {

  /**
   * Select a child node of a xpath
   * 
   * @param xpath
   * @param node
   * @return
   */
  public static Node selectNode(String xpath, Object node) {
    try {
      return (Node) getXPath(xpath).evaluate(node, XPathConstants.NODE);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Select all children node of a xpath
   * 
   * @param xpath
   * @param node
   * @return
   */
  public static List<Node> selectNodes(String xpath, Object node) {
    try {
      return new CustomNodeList((NodeList) getXPath(xpath).evaluate(node, XPathConstants.NODESET));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String selectString(String xpath, Object node) {
    try {
      return ((String) getXPath(xpath).evaluate(node, XPathConstants.STRING)).trim();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String getTextContent(String childName, Node parentNode) {
    Node child = getChild(childName, parentNode);

    if (child == null) {
      return null;
    }

    return getTextContent(child);
  }

  /**
   * Select all children node of a xpath single node
   * 
   * @param xpath
   * @param node
   * @return
   */
  public static List<Node> selectChildren(String xpath, Object node) {
    try {
      List<Node> children = new ArrayList<Node>();
      for (Node child : new CustomNodeList(selectNode(xpath, node).getChildNodes())) {
        if (!"#text".equals(child.getNodeName())) {
          children.add(child);
        }
      }
      return children;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * get a child node of parentNode with a nodeName
   * 
   * @param nodeName
   * @param parentNode
   * @return
   */
  public static Node getChild(String nodeName, Node parentNode) {
    for (Node child : new CustomNodeList(parentNode.getChildNodes())) {
      if (nodeName.equals(child.getNodeName()))
        return child;
    }

    return null;
  }

  /**
   * get all children nodes of parentNode with a nodeName
   * 
   * @param nodeName
   * @param parentNode
   * @return
   */
  public static List<Node> getChildren(String nodeName, Node parentNode) {
    List<Node> children = new ArrayList<Node>();

    for (Node child : new CustomNodeList(parentNode.getChildNodes())) {
      if (nodeName.equals(child.getNodeName()))
        children.add(child);
    }

    return children;
  }

  public static String getAttribute(String attribute, Node node) {
    Node attributeNode = node.getAttributes().getNamedItem(attribute);

    if (attributeNode != null)
      return attributeNode.getNodeValue().trim();

    return null;
  }

  public static String getTextContent(Node node) {
    StringBuilder sb = new StringBuilder();

    for (Node textNode : getChildren("#text", node)) {
      sb.append(textNode.getNodeValue());
    }

    return sb.toString().trim();
  }

  public static Integer getIntegerContent(String childName, Node parentNode) {
    try {
      return new Integer(getTextContent(childName, parentNode));
    } catch (NumberFormatException e) {
      return null;
    }
  }

  public static Float getFloatContent(String childName, Node parentNode) {
    try {
      return new Float(getTextContent(childName, parentNode));
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static XPathExpression getXPath(String xpath) throws XPathExpressionException {
    return XPathFactory.newInstance().newXPath().compile(xpath);
  }
  
  public static String convertToString(Node node) {
    boolean withXMLDeclaration = true;
    String result;
    if(withXMLDeclaration) {
      Document document = node.getOwnerDocument();
      DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
      LSSerializer serializer = domImplLS.createLSSerializer();
      result = serializer.writeToString(node);
    } else {
      try {
      TransformerFactory transFactory = TransformerFactory.newInstance();
      Transformer transformer = transFactory.newTransformer();
      StringWriter buffer = new StringWriter();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.transform(new DOMSource(node), new StreamResult(buffer));
      result = buffer.toString();
      } catch (TransformerConfigurationException e) {
        result = "";
      } catch (TransformerException e) {
        result = "";
      }
    }
    return result;
  }

  private static class CustomNodeList extends AbstractList<Node> {

    private final NodeList nodes;

    public CustomNodeList(NodeList nodes) {
      this.nodes = nodes;
    }

    @Override
    public Node get(int index) {
      return nodes.item(index);
    }

    @Override
    public int size() {
      return nodes.getLength();
    }

  }
}
