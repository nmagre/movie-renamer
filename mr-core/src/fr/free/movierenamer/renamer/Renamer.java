/*
 * mr-core
 * Copyright (C) 2012-2013 Nicolas Magré
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
package fr.free.movierenamer.renamer;

import java.io.File;
import java.net.URI;
import java.util.Calendar;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;

/**
 * Class Renamer
 *
 * @author Simon QUÉMÉNEUR
 */
public class Renamer {

  private static final String renamedFileName = "renamed" + ".xml";

  // Settings instance
  private static final Renamer instance = new Renamer();

  private static final String renamedNodeName = "renamed";

  // renamed document
  private final Document renamedDocument;

  /**
   * Access to the Settings instance
   *
   * @return The only instance of MR Settings
   */
  public static Renamer getInstance() {
    return instance;
  }

  private Renamer() {

    // renamed file
    Document renamedDocument;
    try {
      File file = new File(Settings.appFolder, renamedFileName);
      renamedDocument = URIRequest.getXmlDocument(file.toURI());
    } catch (Exception ex) {
      try {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        docBuilder = docFactory.newDocumentBuilder();

        // root element
        renamedDocument = docBuilder.newDocument();
        Element rootElement = renamedDocument.createElement(renamedNodeName);
        renamedDocument.appendChild(rootElement);
      } catch (ParserConfigurationException ex1) {
        renamedDocument = null;
      }
    }
    this.renamedDocument = renamedDocument;
    saveRenamed();
  }

  /**
   * Save renamed
   *
   * @return True if renamed was saved, False otherwise
   */
  private boolean saveRenamed() {
    boolean saveSuccess;
    Settings.LOGGER.log(Level.INFO, String.format("Save renamed files to %s", renamedFileName));
    try {
      // write it to file
      File renamedFile = new File(Settings.appFolder, renamedFileName);
      FileUtils.writeXmlFile(renamedDocument, renamedFile);
      saveSuccess = true;
    } catch (Exception e) {
      Settings.LOGGER.log(Level.SEVERE, e.getMessage());
      saveSuccess = false;
    }
    return saveSuccess;
  }

  public boolean wasRenamed(FileInfo fileInfo) {
    // return XPathUtils.selectNode("renamed/renamedMedia[@type='"+fileInfo.getType().name()+"']/media[@to='"+fileInfo.getURI().toString()+"']", this.renamedDocument) != null;
    String checksum = FileUtils.getFileChecksum(new File(fileInfo.getURI()));
    Node node = getRenamedMediaNode(checksum);
    return !checksum.isEmpty() && node != null;
  }

  private Node getRenamedMediaNode(String fileKey) {
    return XPathUtils.selectNode("//renamedMedia[@checksum='" + fileKey + "']", this.renamedDocument);
  }

  /**
   * @param fileInfo
   * @param toURI
   * @param fromURI
   */
  public boolean addRenamed(FileInfo fileInfo, URI fromURI, URI toURI) {
    boolean saved;
    if (fileInfo != null) {
      String checksum = FileUtils.getFileChecksum(new File(toURI));
      if (checksum.isEmpty()) {
        saved = false;
      } else {
        Node root = XPathUtils.selectNode(renamedNodeName, this.renamedDocument);

        Node renamedMedia = getRenamedMediaNode(checksum);

        if (renamedMedia == null) {
          Element renamedMediaElement = renamedDocument.createElement("renamedMedia");
          // renamedMedia.setAttribute("type", fileInfo.getType().name());
          renamedMediaElement.setAttribute("checksum", checksum);
          renamedMedia = renamedMediaElement;
        }

        Element move = renamedDocument.createElement("historic");
        Element from = renamedDocument.createElement("from");
        from.setTextContent(fromURI.toString());
        move.appendChild(from);
        Element to = renamedDocument.createElement("to");
        to.setTextContent(toURI.toString());
        move.appendChild(to);
        Element date = renamedDocument.createElement("date");
        date.setTextContent(Calendar.getInstance().getTime().toString());
        move.appendChild(date);

        renamedMedia.appendChild(move);

        // add it
        root.appendChild(renamedMedia);
        saved = saveRenamed();
      }
    } else {
      saved = false;
    }
    return saved;
  }

}
