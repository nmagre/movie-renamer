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

import fr.free.movierenamer.utils.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
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

  private final String file;
  private final String innerFile;
  private MrParser<T> itp = null;

  public XMLParser(String XMLFile) {
    this.file = XMLFile;
    this.innerFile = null;
  }

  public XMLParser(String ZIPFile, String XMLFile) {
    this.file = ZIPFile;
    this.innerFile = XMLFile;
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

    ZipFile zf = null;
    if (Utils.isUrl(file)) {
      URL url = new URL(file);
      in = new InputSource(url.openStream());
    } else {
      if (file != null && Utils.isZIPFile(file)) {

        zf = new ZipFile(file);
        ZipEntry zipEntry;
        ZipInputStream zipIn;

        zipIn = new ZipInputStream(new FileInputStream(file));

        while ((zipEntry = zipIn.getNextEntry()) != null) {
          if (zipEntry.getName().equals(innerFile)) {
            in = new InputSource(zf.getInputStream(zipEntry));
            break;
          }
          zipIn.closeEntry();
        }
        zipIn.close();

        if (in == null) {
          throw new IOException(innerFile + " not found in zipFile " + file);
        }
      } else {
        File f = new File(file);
        in = new InputSource(new FileInputStream(f));
      }
    }

    try {
      parseur.parse(in, itp);
    } catch (NOSAXException mye) {
      // nothing to catch !!!
    } catch (ParserBugException ex) {
      throw new NullPointerException("Parse  failed, bug in parserl");
    }

    if (zf != null) {
      zf.close();
    }

    if (itp == null) {
      throw new NullPointerException("MrParser null");
    }

    return itp.getObject();
  }
}
