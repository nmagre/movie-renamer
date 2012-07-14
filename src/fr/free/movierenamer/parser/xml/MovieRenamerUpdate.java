/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.parser.xml;

import fr.free.movierenamer.utils.Update;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author Nicolas Magré
 */
public class MovieRenamerUpdate extends MrParser<Update> {

  private StringBuffer buffer;
  private Update update;
  private boolean updateXML;
  private boolean desc;

  public MovieRenamerUpdate(String version) {
    super();
    if (version.contains("_")) {
      version = version.substring(0, version.indexOf("_"));
    }
    update = new Update(version);
    updateXML = false;
    desc = false;
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("update")) {
      updateXML = true;
    }
    if (name.equalsIgnoreCase("description")) {
      desc = true;
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("update")) {
      updateXML = false;
    }

    if (updateXML) {
      if (name.equalsIgnoreCase("description")) {
        desc = false;
      }
      if (name.equalsIgnoreCase("version")) {
        update.setVersion(buffer.toString());
      }
      if (name.equalsIgnoreCase("url")) {
        update.setUrl(buffer.toString());
      }
      if (desc) {
        if (name.equalsIgnoreCase("en")) {
          update.setDescEN(buffer.toString());
        }
        if (name.equalsIgnoreCase("fr")) {
          update.setDescFR(buffer.toString());
        }
      }
    }
    buffer = null;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String lecture = new String(ch, start, length);
    if (buffer != null) {
      buffer.append(lecture);
    }
  }

  @Override
  public Update getObject() {
    return update;
  }
}
