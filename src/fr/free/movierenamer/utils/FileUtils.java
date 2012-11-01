/*
 * movie-renamer
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
package fr.free.movierenamer.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 * Class FileUtils
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class FileUtils {
  /**
   * Pattern used for matching file extensions.
   */
  public static final Pattern EXTENSION = Pattern.compile("(?<=.[.])\\p{Alnum}+$");

  public static String getExtension(File file) {
    if (file.isDirectory())
      return null;

    return getExtension(file.getName());
  }

  public static String getExtension(String name) {
    Matcher matcher = EXTENSION.matcher(name);

    if (matcher.find()) {
      // extension without '.'
      return matcher.group();
    }

    // no extension
    return null;
  }

  public static boolean hasExtension(File file, String... extensions) {
    return hasExtension(file.getName(), extensions) && !file.isDirectory();
  }

  public static boolean hasExtension(String filename, String... extensions) {
    String extension = getExtension(filename);

    for (String value : extensions) {
      if ((extension == null && value == null) || (extension != null && extension.equalsIgnoreCase(value)))
        return true;
    }

    return false;
  }

  public static String getNameWithoutExtension(String name) {
    Matcher matcher = EXTENSION.matcher(name);

    if (matcher.find()) {
      return name.substring(0, matcher.start() - 1);
    }

    // no extension, return given name
    return name;
  }

  public static String getName(File file) {
    if (file.getName().isEmpty())
      return getFolderName(file);

    return getNameWithoutExtension(file.getName());
  }

  public static String getFolderName(File file) {
    String name = file.getName();

    if (!name.isEmpty())
      return name;

    return replacePathSeparators(file.toString(), "");
  }

  private static String replacePathSeparators(CharSequence path, String replacement) {
    return Pattern.compile("\\s*[\\\\/]+\\s*").matcher(path).replaceAll(replacement);
  }

  /**
   * Check if file have a good extension
   * 
   * @param fileName File to check extension
   * @param extensions Array of extensions
   * @return True if file extension is in array
   */
  public static boolean checkFileExt(String fileName, String[] extensions) {

    if (extensions == null | extensions.length == 0) {
      return false;
    }

    if (!fileName.contains(StringUtils.DOT)) {
      return false;
    }

    String ext = fileName.substring(fileName.lastIndexOf(StringUtils.DOT) + 1);
    for (int i = 0; i < extensions.length; i++) {
      if (ext.equalsIgnoreCase(extensions[i])) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if dir is a root directory
   * 
   * @param dir Directory
   * @return True if it is a directory
   */
  public static boolean isRootDir(File dir) {
    if (!dir.isDirectory()) {
      return false;
    }

    File[] roots = File.listRoots();
    for (File root : roots) {
      if (root.equals(dir)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method writes a DOM document to a file
   */
  public static void writeXmlFile(Document doc, File file) {
    try {
      // Prepare the DOM document for writing
      Source source = new DOMSource(doc);

      // Prepare the output file
      Result result = new StreamResult(file.toURI().getPath());

      // Write the DOM document to the file
      Transformer xformer = TransformerFactory.newInstance().newTransformer();
      xformer.transform(source, result);
    } catch (TransformerConfigurationException e) {
    } catch (TransformerException e) {
    }
  }

  public static class ExtensionFileFilter implements FileFilter {

    private final String[] extensions;

    public ExtensionFileFilter(String... extensions) {
      this.extensions = extensions;
    }

    public ExtensionFileFilter(Collection<String> extensions) {
      this.extensions = extensions.toArray(new String[0]);
    }

    @Override
    public boolean accept(File file) {
      return hasExtension(file, extensions);
    }

    public boolean accept(String name) {
      return hasExtension(name, extensions);
    }

    public boolean acceptExtension(String extension) {
      for (String other : extensions) {
        if (other.equalsIgnoreCase(extension))
          return true;
      }

      return false;
    }

    public String extension() {
      return extensions[0];
    }

    public String[] extensions() {
      return extensions.clone();
    }
  }

  private FileUtils() {
    throw new UnsupportedOperationException();
  }
}
