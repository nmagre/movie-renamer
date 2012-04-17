/*
 * Movie Renamer
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
package fr.free.movierenamer.parser.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML parser
 * @param <T> XML object to parse
 * @author Nicolas Magré
 */
public class XMLParser<T> {

  private String XMLFile;
  private IParser<T> itp = null;
  private final int RETRY = 3;

  public XMLParser(String XMLFile) {
    this.XMLFile = XMLFile;
  }

  public void setParser(IParser<T> itp) {
    this.itp = itp;
  }

  public T parseXml() throws IOException, InterruptedException {

    if (itp == null) throw new NullPointerException("IParser null");
    SAXParserFactory sparser = SAXParserFactory.newInstance();
    SAXParser parseur;
    T obj;
    try {
      parseur = sparser.newSAXParser();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
      return null;
    } catch (SAXException e) {
      e.printStackTrace();
      return null;
    }

    for (int i = 0; i < RETRY; i++) {
      try {
        InputSource objFile;
        if (isUrl()) {
          URL url = new URL(XMLFile);
          objFile = new InputSource(url.openStream());
        } else {
          File f = new File(XMLFile);
          objFile = new InputSource(new FileInputStream(f));
        }
        parseur.parse(objFile, (DefaultHandler) itp);
        if (itp == null) throw new NullPointerException("IParser null");
        obj = itp.getObject();
      } catch (SAXException e) {
        break;
      } catch (IOException e) {
        Thread.sleep(500);
        continue;
      }
      return obj;
    }
    throw new IOException("Failed to read after " + RETRY + " attempts");
  }

  /**
   * Check if "XMLFILE" is an url
   * @return
   */
  private boolean isUrl() {
    try {
      new URL(XMLFile);
    } catch (MalformedURLException e) {
      return false;
    }
    return true;
  }
}
