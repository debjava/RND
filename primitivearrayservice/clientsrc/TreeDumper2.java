

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TreeDumper2
{
  public void dump(Document doc) 
  {
    dumpLoop((Node) doc, "");
  }

  private void dumpLoop(Node node, String indent)
  {
    switch (node.getNodeType()) 
    {
    case Node.ATTRIBUTE_NODE:
      dumpAttributeNode((Attr) node, indent);
      break;
    case Node.CDATA_SECTION_NODE:
      dumpCDATASectionNode((CDATASection) node, indent);
      break;
    case Node.COMMENT_NODE:
      dumpCommentNode((Comment) node, indent);
      break;
    case Node.DOCUMENT_NODE:
      dumpDocument((Document) node, indent);
      break;
    case Node.DOCUMENT_FRAGMENT_NODE:
      dumpDocumentFragment((DocumentFragment) node, indent);
      break;
    case Node.DOCUMENT_TYPE_NODE:
      dumpDocumentType((DocumentType) node, indent);
      break;
    case Node.ELEMENT_NODE:
      dumpElement((Element) node, indent);
      break;
    case Node.ENTITY_NODE:
      dumpEntityNode((Entity) node, indent);
      break;
    case Node.ENTITY_REFERENCE_NODE:
      dumpEntityReferenceNode((EntityReference) node, indent);
      break;
    case Node.NOTATION_NODE:
      dumpNotationNode((Notation) node, indent);
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
      dumpProcessingInstructionNode((ProcessingInstruction) node, indent);
      break;
    case Node.TEXT_NODE:
      dumpTextNode((Text) node, indent);
      break;
    default:
      System.out.println(indent + "Unknown node");
      break;
    }

    NodeList list = node.getChildNodes();
    for (int i = 0; i < list.getLength(); i++)
      dumpLoop(list.item(i), indent + "   ");
  }

  /* Display the contents of a ATTRIBUTE_NODE */
  private void dumpAttributeNode(Attr node, String indent) {
    System.out.println(indent + "ATTRIBUTE " + node.getName() + "=\""
        + node.getValue() + "\"");
  }

  /* Display the contents of a CDATA_SECTION_NODE */
  private void dumpCDATASectionNode(CDATASection node, String indent) {
    System.out.println(indent + "CDATA SECTION length=" + node.getLength());
    System.out.println(indent + "\"" + node.getData() + "\"");
  }

  /* Display the contents of a COMMENT_NODE */
  private void dumpCommentNode(Comment node, String indent) {
    System.out.println(indent + "COMMENT length=" + node.getLength());
    System.out.println(indent + "  " + node.getData());
  }

  /* Display the contents of a DOCUMENT_NODE */
  private void dumpDocument(Document node, String indent) {
    System.out.println(indent + "DOCUMENT");
  }

  /* Display the contents of a DOCUMENT_FRAGMENT_NODE */
  private void dumpDocumentFragment(DocumentFragment node, String indent) {
    System.out.println(indent + "DOCUMENT FRAGMENT");
  }

  /* Display the contents of a DOCUMENT_TYPE_NODE */
  private void dumpDocumentType(DocumentType node, String indent) {
    System.out.println(indent + "DOCUMENT_TYPE: " + node.getName());
    if (node.getPublicId() != null)
      System.out.println(indent + " Public ID: " + node.getPublicId());
    if (node.getSystemId() != null)
      System.out.println(indent + " System ID: " + node.getSystemId());
    NamedNodeMap entities = node.getEntities();
    if (entities.getLength() > 0) {
      for (int i = 0; i < entities.getLength(); i++) {
        dumpLoop(entities.item(i), indent + "  ");
      }
    }
    NamedNodeMap notations = node.getNotations();
    if (notations.getLength() > 0) {
      for (int i = 0; i < notations.getLength(); i++)
        dumpLoop(notations.item(i), indent + "  ");
    }
  }

  /* Display the contents of a ELEMENT_NODE */
  private void dumpElement(Element node, String indent) {
    System.out.println(indent + "ELEMENT: " + node.getTagName());
    NamedNodeMap nm = node.getAttributes();
    for (int i = 0; i < nm.getLength(); i++)
      dumpLoop(nm.item(i), indent + "  ");
  }

  /* Display the contents of a ENTITY_NODE */
  private void dumpEntityNode(Entity node, String indent) {
    System.out.println(indent + "ENTITY: " + node.getNodeName());
  }

  /* Display the contents of a ENTITY_REFERENCE_NODE */
  private void dumpEntityReferenceNode(EntityReference node, String indent) {
    System.out.println(indent + "ENTITY REFERENCE: " + node.getNodeName());
  }

  /* Display the contents of a NOTATION_NODE */
  private void dumpNotationNode(Notation node, String indent) {
    System.out.println(indent + "NOTATION");
    System.out.print(indent + "  " + node.getNodeName() + "=");
    if (node.getPublicId() != null)
      System.out.println(node.getPublicId());
    else
      System.out.println(node.getSystemId());
  }

  /* Display the contents of a PROCESSING_INSTRUCTION_NODE */
  private void dumpProcessingInstructionNode(ProcessingInstruction node,
      String indent) {
    System.out.println(indent + "PI: target=" + node.getTarget());
    System.out.println(indent + "  " + node.getData());
  }

  /* Display the contents of a TEXT_NODE */
  private void dumpTextNode(Text node, String indent) 
  {
    System.out.println(indent + "TEXT length=" + node.getLength());
    System.out.println(indent + "  " + node.getData());
  }

  static public void main(String[] arg) {
    String filename = "soapxml/request4.xml";
    boolean validate = false;

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(validate);
    dbf.setNamespaceAware(true);
    dbf.setIgnoringElementContentWhitespace(true);

    // Parse the input to produce a parse tree with its root
    // in the form of a Document object
    Document doc = null;
    try {
      DocumentBuilder builder = dbf.newDocumentBuilder();
      builder.setErrorHandler(new MyErrorHandler());
      InputSource is = new InputSource(filename);
      doc = builder.parse(is);
    } catch (SAXException e) {
      System.exit(1);
    } catch (ParserConfigurationException e) {
      System.err.println(e);
      System.exit(1);
    } catch (IOException e) {
      System.err.println(e);
      System.exit(1);
    }

    // Use a TreeDumper to list the tree
    TreeDumper2 td = new TreeDumper2();
    td.dump(doc);
  }

//  private static void usage() 
//  {
//    System.err.println("Usage: DOMCheck [-v] <filename>");
//    System.exit(1);
//  }
}



         