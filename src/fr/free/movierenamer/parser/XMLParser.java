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
package fr.free.movierenamer.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XML parser
 * 
 * @param <T> XML object to parse
 * @author Nicolas Magré
 */
public class XMLParser<T> {

  private final File file;
  private MrParser<T> itp = null;

  public XMLParser(String file) {
    this(new File(file));
  }

  public XMLParser(File file) {
    this.file = file;
  }

  public void setParser(MrParser<T> itp) {
    this.itp = itp;
  }

  public T parseXml() throws IOException, InterruptedException, ParserConfigurationException, SAXException, SAXParseException {

    if (itp == null) {
      throw new NullPointerException("MrParser null");
    }

    SAXParser parseur;
    InputSource in = null;

    SAXParserFactory sparser = SAXParserFactory.newInstance();
    parseur = sparser.newSAXParser();

    in = new InputSource(new FileInputStream(file));

    try {
      parseur.parse(in, itp);
    } catch (NOSAXException mye) {
      // nothing to catch !!!
    } catch (ParserBugException ex) {
      throw new NullPointerException("Parse failed, bug in parser");
    }

    if (itp == null) {
      throw new NullPointerException("MrParser null");
    }

    return itp.getObject();
  }
}
