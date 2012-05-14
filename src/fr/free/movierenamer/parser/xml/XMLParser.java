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
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML parser
 *
 * @param <T> XML object to parse
 * @author Nicolas Magré
 */
public class XMLParser<T> {

  private String XMLFile;
  private String ZIPFile;
  private IParser<T> itp = null;

  public XMLParser(String XMLFile) {
    this.XMLFile = XMLFile;
  }

  public XMLParser(String ZIPFile, String XMLFile) {
    this.ZIPFile = ZIPFile;
    this.XMLFile = XMLFile;
  }

  public void setParser(IParser<T> itp) {
    this.itp = itp;
  }

  public T parseXml() throws IOException, InterruptedException, ParserConfigurationException, SAXException {

    if (itp == null) {
      throw new NullPointerException("IParser null");
    }

    SAXParser parseur;
    InputSource in = null;

    SAXParserFactory sparser = SAXParserFactory.newInstance();
    parseur = sparser.newSAXParser();

    ZipFile zf = null;
    if (Utils.isUrl(XMLFile)) {//A refaire ,openStream can failed
      URL url = new URL(XMLFile);
      in = new InputSource(url.openStream());
    } else {
      if (ZIPFile != null && Utils.isZIPFile(ZIPFile)) {

        zf = new ZipFile(ZIPFile);        
        ZipEntry zipEntry;
        ZipInputStream zipIn;

        zipIn = new ZipInputStream(new FileInputStream(ZIPFile));
        zipEntry = zipIn.getNextEntry();

        while (zipEntry != null) {
          if (XMLFile.equals(zipEntry.getName())) {
            in = new InputSource(zf.getInputStream(zipEntry));
            break;
          }
          zipIn.closeEntry();
          zipEntry = zipIn.getNextEntry();
        }
        zipIn.close();

        if (in == null) {
          throw new IOException(XMLFile + " not found in zipFile " + ZIPFile);
        }
      } else {
        File f = new File(XMLFile);
        in = new InputSource(new FileInputStream(f));
      }
    }

    parseur.parse(in, (DefaultHandler) itp);
    
    if(zf != null) {
      zf.close();
    }
    
    if (itp == null) {
      throw new NullPointerException("IParser null");
    }

    return itp.getObject();
  }
}
