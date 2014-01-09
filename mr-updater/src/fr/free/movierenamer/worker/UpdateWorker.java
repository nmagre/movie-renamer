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
package fr.free.movierenamer.worker;

import fr.free.movierenamer.UpdateDialog;
import fr.free.movierenamer.UpdateFile;
import fr.free.movierenamer.utils.UpdateUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class UpdateWorker
 *
 * @author Nicolas Magré
 */
public class UpdateWorker extends SwingWorker<String, String> {

  private static final String host = "www.movie-renamer.fr";
  private final UpdateDialog uf;
  private final String version;
  private final File updateDir;
  private final File installDir;
  private final int RETRY = 3;
  private final int WAIT = 5000;

  public UpdateWorker(UpdateDialog uf, String version, File updateDir, File installDir) {
    this.uf = uf;
    this.version = version;
    this.updateDir = updateDir;
    this.installDir = installDir;
  }

  @Override
  protected String doInBackground() throws InterruptedException {
    Document dom = null;
    String message = "";

    publish("Get update info");

    for (int i = 0; i < RETRY; i++) {
      Thread.sleep(i * WAIT);

      try {
        dom = getUpdateXml();
        break;
      } catch (SAXException | ParserConfigurationException e) {
        Logger.getLogger(UpdateWorker.class.getName()).log(Level.SEVERE, null, e);
        message = e.getMessage();
        break;
      } catch (IOException ex) {
        Logger.getLogger(UpdateWorker.class.getName()).log(Level.SEVERE, null, ex);
        message = ex.getMessage();
      }
    }

    if (dom == null) {
      return message;
    }

    List<UpdateFile> files = new ArrayList<>();
    Node root = UpdateUtils.selectNode("/update", dom);
    NodeList nodes = UpdateUtils.selectNodes("files/file", root);
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      try {
        files.add(new UpdateFile(new URI(UpdateUtils.selectString("url", node)), UpdateUtils.selectString("path", node),
                UpdateUtils.selectString("md5", node)));
      } catch (URISyntaxException ex) {
        Logger.getLogger(UpdateWorker.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    // Create update directory
    if (!updateDir.exists()) {
      try {
        updateDir.mkdirs();
      } catch (SecurityException e) {
        return e.getMessage();
      }
    }

    // Remove all files in update directory
    UpdateUtils.removeFile(updateDir.listFiles());
    message = null;

    // download files and check md5
    int count = 1;
    int nbfiles = files.size();
    for (UpdateFile file : files) {
      publish("Download file " + count++ + "/" + nbfiles);
      for (int i = 0; i < RETRY; i++) {
        Thread.sleep(i * WAIT);
        try {
          downloadFile(file);
          break;
        } catch (Exception ex) {
          Logger.getLogger(UpdateWorker.class.getName()).log(Level.SEVERE, null, ex);
          message = ex.getMessage();
        }
      }
    }

    try {
      publish("Move files");
      UpdateUtils.move(updateDir.listFiles(), installDir);
    } catch (IOException ex) {
      Logger.getLogger(UpdateWorker.class.getName()).log(Level.SEVERE, null, ex);
      message = ex.getMessage();
    }
    return message;
  }

  private void downloadFile(UpdateFile file) throws Exception {

    InputStream input = null;
    FileOutputStream output = null;

    String outputfile = updateDir.getAbsolutePath();
    if (!file.getPath().equals("")) {
      outputfile += File.separator + file.getPath();
      new File(outputfile).mkdirs();
    }

    outputfile += File.separator;
    String filename = file.getUrl().toString().substring(file.getUrl().toString().lastIndexOf("/") + 1);
    outputfile += filename.replace("%20", " ");

    try {
      MessageDigest md = MessageDigest.getInstance("MD5");

      URLConnection connection = file.getUrl().toURL().openConnection();
      input = connection.getInputStream();
      output = new FileOutputStream(outputfile);
      DigestInputStream dis = new DigestInputStream(input, md);

      byte[] buffer = new byte[1024];
      int read;
      while ((read = dis.read(buffer)) > 0) {
        output.write(buffer, 0, read);
      }

      output.flush();

      String md5 = (new HexBinaryAdapter()).marshal(md.digest());
      if (!md5.equalsIgnoreCase(file.getMd5())) {
        throw new Exception("Wrong md5");
      }
    } catch (IOException e) {
      throw e;
    } catch (NoSuchAlgorithmException ex) {
      Logger.getLogger(UpdateWorker.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        if (output != null) {
          output.close();
        }

        if (input != null) {
          input.close();
        }
      } catch (IOException e) {
      }
    }

  }

  private Document getUpdateXml() throws MalformedURLException, ParserConfigurationException, SAXException, IOException {
    URL update = new URL("http", host, "/updater/updater.php?getfile&version=" + version);
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();

    return db.parse(update.openStream());
  }

  @Override
  protected void process(List<String> v) {
    uf.setStatus(v.get(0));
  }

}
