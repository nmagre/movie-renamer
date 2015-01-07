/*
 * Movie Renamer
 * Copyright (C) 2014 Nicolas Magré
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
package fr.free.movierenamer.ui.bean;

import com.alee.utils.ImageUtils;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.worker.impl.RenamerWorker;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class UIRenamer
 *
 * @author Nicolas Magré
 */
public class UIRenamer {

  private static final String renamedFileName = "renamed" + ".xml";
  private static final UIRenamer instance = new UIRenamer();
  private static final String renamedNodeName = "renamed";
  private final Document renamedDocument;

  /**
   * Access to the Settings instance
   *
   * @return The only instance of UIRenamer
   */
  public static UIRenamer getInstance() {
    return instance;
  }

  private UIRenamer() {
    // renamed file
    Document renamedDocument;
    try {
      File file = new File(Settings.APPFOLDER, renamedFileName);
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
      File renamedFile = new File(Settings.APPFOLDER, renamedFileName);
      FileUtils.writeXmlFile(renamedDocument, renamedFile);
      saveSuccess = true;
    } catch (Exception e) {
      Settings.LOGGER.log(Level.SEVERE, e.getMessage());
      saveSuccess = false;
    }
    return saveSuccess;
  }

  public boolean wasRenamed(UIFile file) {
    String checksum = file.getMd5Hash();
    Node node = getRenamedMediaNode(checksum);
    return !checksum.isEmpty() && node != null;
  }

  public boolean addRenamed(UIFile file, File mediaSourceFile, File newMediaFile, List<RenamerWorker.RenameFile> movedFiles, String thumb) {

    if (file == null) {
      return false;
    }

    Node root = XPathUtils.selectNode(renamedNodeName, this.renamedDocument);
    Node renamedMedia = getRenamedMediaNode(file.getMd5Hash());

    if (renamedMedia == null) {
      Element renamedMediaElement = renamedDocument.createElement("renamedMedia");
      renamedMediaElement.setAttribute("type", file.getMtype().name());
      renamedMediaElement.setAttribute("checksum", file.getMd5Hash());
      renamedMediaElement.setAttribute("thumb", thumb != null ? "true" : "false");
      renamedMedia = renamedMediaElement;
    } else {
      while (renamedMedia.hasChildNodes()) {
        renamedMedia.removeChild(renamedMedia.getFirstChild());
      }
    }

    Node files = renamedDocument.createElement("files");

    Element filename = renamedDocument.createElement("oldfilename");
    filename.setTextContent(mediaSourceFile.getName());
    renamedMedia.appendChild(filename);
    filename = renamedDocument.createElement("newfilename");
    filename.setTextContent(newMediaFile.getName());
    renamedMedia.appendChild(filename);
    Element date = renamedDocument.createElement("date");
    date.setTextContent(Calendar.getInstance().getTime().toString());
    renamedMedia.appendChild(date);

    for (RenamerWorker.RenameFile moveFile : movedFiles) {
      Element move = renamedDocument.createElement("historic");
      Element from = renamedDocument.createElement("from");
      from.setTextContent(moveFile.isTmpFile() ? "" : moveFile.getSource().toString());
      move.appendChild(from);
      Element to = renamedDocument.createElement("to");
      to.setTextContent(moveFile.getDestination().toString());
      move.appendChild(to);
      Element replace = renamedDocument.createElement("replace");
      replace.setTextContent(moveFile.isReplace() ? "true" : "false");
      move.appendChild(replace);
      files.appendChild(move);
    }
    renamedMedia.appendChild(files);

    // add it
    root.appendChild(renamedMedia);

    return saveRenamed();
  }
  
  public List<UIHistory> getHistoric() {
    List<UIHistory> historics = new ArrayList<>();
    List<Node> nodes = XPathUtils.selectNodes("//renamedMedia", renamedDocument);
    for(Node node : nodes) {
      String thumb = XPathUtils.getAttribute("thumb", node);
      Icon icon = null;
      if(thumb != null && thumb.equals("true")) {
        icon = ImageUtils.getImageIcon(new File(Settings.APPFOLDER, "/cache/images/thumb/" + XPathUtils.getAttribute("checksum", node)), false);// FIXME
      }
      
      historics.add(new UIHistory(icon, XPathUtils.selectNode("oldfilename", node).getTextContent(), XPathUtils.selectNode("newfilename", node).getTextContent()));
    }
    return historics;
  }

  private Node getRenamedMediaNode(String fileKey) {
    return XPathUtils.selectNode("//renamedMedia[@checksum='" + fileKey + "']", this.renamedDocument);
  }
}
