/*
 * mr-core
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
import fr.free.movierenamer.utils.WebRequest;
import fr.free.movierenamer.utils.XPathUtils;

/**
 * Class Renamer
 * 
 * @author Simon QUÉMÉNEUR
 */
public class Renamer {
  private static final String renamedFileName = "renamed" + ".xml";

  // Settings instance
  private static Renamer instance;

  private static final String renamedNodeName = "renamed";

  // renamed document
  private final Document renamedDocument;

  /**
   * Private build for singleton fix
   * 
   * @return
   */
  private static synchronized Renamer newInstance() {
    if (instance == null) {
      instance = new Renamer();
    }
    return instance;
  }

  /**
   * Access to the Settings instance
   * 
   * @return The only instance of MR Settings
   */
  public static synchronized Renamer getInstance() {
    if (instance == null) {
      instance = newInstance();
    }
    return instance;
  }

  private Renamer() {

    // renamed file
    Document renamedDocument;
    try {
      File file = new File(Settings.appFolder, renamedFileName);
      renamedDocument = WebRequest.getXmlDocument(file.toURI());
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
    Settings.LOGGER.log(Level.INFO, "Save renamed files to {0}", renamedFileName);
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
//    return XPathUtils.selectNode("renamed/renamedMedia[@type='"+fileInfo.getType().name()+"']/media[@to='"+fileInfo.getURI().toString()+"']", this.renamedDocument) != null;
    return XPathUtils.selectNode("//media[@to='"+fileInfo.getURI().toString()+"']", this.renamedDocument) != null;
  }

  /**
   * @param fileInfo
   * @param toURI
   * @param fromURI
   */
  public boolean addRenamed(FileInfo fileInfo, URI fromURI, URI toURI) {
    boolean saved;
    if (fileInfo != null) {
      Node root = XPathUtils.selectNode(renamedNodeName, this.renamedDocument);

      Element renamedMedia = renamedDocument.createElement("renamedMedia");
      renamedMedia.setAttribute("type", fileInfo.getType().name());

      Element media = renamedDocument.createElement("media");
      media.setAttribute("from", fromURI.toString());
      media.setAttribute("to", toURI.toString());
      renamedMedia.appendChild(media);

      Element date = renamedDocument.createElement("date");
      date.setTextContent(Calendar.getInstance().getTime().toString());
      renamedMedia.appendChild(date);

      // add it
      root.appendChild(renamedMedia);
      saved = saveRenamed();

    } else {
      saved = false;
    }
    // res += "<renamedMovie title=\"" + title.replace("\"", "") + "\">";
    // res += "  <tmdbId>" + tmdbId + "</tmdbId>";
    // res += "  <movie src=\"" + movieFileSrc.replace("\"", "") + "\" dest=\"" + movieFileDest.replace("\"", "") + "\" />";
    // res += "  <thumb>" + thumb + "</thumb>";
    // res += "  <date>" + date + "</date>";
    // res += "  <failed>" + (renameFailed ? "1":"0") + "</failed>";
    // res += "</renamedMovie>";
    return saved;
  }

}
