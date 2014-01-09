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
package fr.free.movierenamer.utils;

import fr.free.movierenamer.Main;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class UpdateUtils
 *
 * @author Nicolas Magré
 */
public class UpdateUtils {

  private static final String javaBin = System.getProperty("java.home") + "/bin/java";
  private static final String OS = System.getProperty("os.name").toLowerCase();

  public static boolean isUnix() {
    return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
  }

  private static XPathExpression getXPath(String xpath) throws XPathExpressionException {
    return XPathFactory.newInstance().newXPath().compile(xpath);
  }

  /**
   * Select a child node of a xpath
   *
   * @param xpath
   * @param node
   * @return
   */
  public static Node selectNode(String xpath, Object node) {
    try {
      return (Node) getXPath(xpath).evaluate(node, XPathConstants.NODE);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Select all children node of a xpath
   *
   * @param xpath
   * @param node
   * @return
   */
  public static NodeList selectNodes(String xpath, Object node) {
    try {
      return (NodeList) getXPath(xpath).evaluate(node, XPathConstants.NODESET);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String selectString(String xpath, Object node) {
    try {
      return ((String) getXPath(xpath).evaluate(node, XPathConstants.STRING)).trim();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void move(File[] files, File destFolder) throws IOException {
    for (File file : files) {
      if (file.isFile()) {
        File newFile = new File(destFolder, file.getName());
        if (newFile.exists()) {
          newFile.delete();
        }

        if (!file.renameTo(newFile)) {
          Files.copy(file.toPath(), newFile.toPath(), REPLACE_EXISTING);
          file.delete();
        }
      } else {
        File dir = new File(destFolder, file.getName());
        if (!dir.exists()) {
          dir.mkdirs();
        }
        move(file.listFiles(), new File(destFolder, file.getName()));
      }
    }
  }

  public static void removeFile(File[] files) {
    for (File file : files) {
      if (file.isDirectory()) {
        removeFile(file.listFiles());
      }
      file.delete();
    }
  }

  public static void startMovieRenamer() throws IOException {
    try {
      final StringBuffer cmd = new StringBuffer("\"" + javaBin + "\" ");

      String maincmd = "-jar ";
      try {
        String property = System.getProperty("sun.java.command");
        if (property != null) {
          String[] properties = property.split(" ");
          if (properties[0].endsWith(".jar")) {
            maincmd += "\"" + new File(properties[0]).getParentFile().getPath() + File.separator + "Movie Renamer.jar\"";
          } else {
            maincmd = "-cp \"" + System.getProperty("java.class.path") + "\" " + new File(properties[0]).getParentFile().getPath() + File.separator + "Movie Renamer.class\"";
          }
        }
      } catch (Exception e) {
        File installDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        installDir = installDir.getParentFile().getParentFile();
        maincmd += "\"" + installDir.getAbsolutePath() + File.separator + "Movie Renamer.jar" + "\"";
      }

      cmd.append(maincmd);

      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          try {
            Runtime.getRuntime().exec(cmd.toString());
          } catch (IOException e) {
          }
        }
      });

      System.exit(0);
    } catch (URISyntaxException e) {
      throw new IOException("Error while trying to restart the application", e);
    }
  }

  private UpdateUtils() {
    throw new UnsupportedOperationException();
  }
}
