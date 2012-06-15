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
package fr.free.movierenamer.utils;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.CMMException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Class Utils
 *
 * @author Nicolas Magré
 */
public class Utils {

  private static String OS = null;
  private static ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");
  public static final String SPACE = " ";
  public static final String ENDLINE = "\n";
  public static final String EMPTY = "";
  public static final String DOT = ".";
  public static final int FIRSTLO = 0;
  public static final int FIRSTLA = 1;
  public static final int UPPER = 2;
  public static final int LOWER = 3;
  public static final ResourceBundle rb = ResourceBundle.getBundle("fr/free/movierenamer/version");

  /**
   * Get token from version.properties
   *
   * @param propToken Token property name
   * @return Token value or an empty string
   */
  public static String getRbTok(String propToken) {
    String msg = "";
    try {
      // msg = rb.getString(propToken);
    } catch (MissingResourceException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }
    return msg;
  }

  /**
   * Get operating system name
   *
   * @return Operating system name
   */
  private static String getOsName() {
    if (OS == null) {
      OS = System.getProperty("os.name");
    }
    return OS;
  }

  /**
   * Check if operating system is windows
   *
   * @return True if OS is windows, false otherwhise
   */
  public static boolean isWindows() {
    return getOsName().startsWith("Windows");
  }

  public static String getFilteredName(String movieName, List<String> replaceBy) {
    String res = movieName.replaceAll("\\.", " ").replaceAll("_", " ");
    for (String regex : replaceBy) {
      res = res.replaceAll("(?i)" + regex, "");
    }
    return res;
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

    if (!fileName.contains(DOT)) {
      return false;
    }

    String ext = fileName.substring(fileName.lastIndexOf(DOT) + 1);
    for (int i = 0; i < extensions.length; i++) {
      if (ext.equalsIgnoreCase(extensions[i])) {
        return true;
      }
    }
    return false;
  }

  /**
   * Remove string from array at index
   *
   * @param array String array
   * @param index Index of string to remove
   * @return
   */
  public static String[] removeFromArray(String[] array, int index) {
    if (index >= array.length) {
      return null;
    }

    String[] newArray = new String[array.length - 1];
    int pos = 0;
    for (int i = 0; i < array.length; i++) {
      if (i != index) {
        newArray[pos++] = array[i];
      }
    }
    return newArray;
  }

  /**
   * Get a string from an array separated by separator and limited to limit
   *
   * @param array Object array
   * @param separator Separator
   * @param limit Limit
   * @return String separated by separator or empty
   */
  public static String arrayToString(Object[] array, String separator, int limit) {
    StringBuilder res = new StringBuilder();

    if (array.length == 0) {
      return res.toString();
    }

    for (int i = 0; i < array.length; i++) {
      if (limit != 0 && i == limit) {
        break;
      }

      res.append(array[i].toString());

      if ((i + 1) != limit) {
        res.append((i < (array.length - 1)) ? separator : "");
      }
    }
    return res.toString();
  }

  /**
   * Get a string from an array separated by separator and limited to limit
   *
   * @param array ArrayList
   * @param separator Separator
   * @param limit Limit
   * @return String separated by separator or empty
   */
  public static String arrayToString(ArrayList<?> array, String separator, int limit) {
    return arrayToString(array.toArray(new Object[array.size()]), separator, limit);
  }

  /**
   * Get an array from a string separated by separator
   *
   * @param str String
   * @param separator Separator
   * @return
   */
  public static ArrayList<String> stringToArray(String str, String separator) {
    ArrayList<String> array = new ArrayList<String>();
    if (str == null) {
      return array;
    }
    if (separator == null) {
      separator = ", ";
    }
    String[] res = str.split(separator);
    array.addAll(Arrays.asList(res));
    return array;
  }

  /**
   * Get string md5
   *
   * @param str String
   * @return md5
   */
  public static String md5(String str) {
    try {
      MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
      digest.update(str.getBytes());
      byte messageDigest[] = digest.digest();

      StringBuilder hexString = new StringBuilder();
      for (int i = 0; i < messageDigest.length; i++) {
        hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
      }
      return hexString.toString();

    } catch (NoSuchAlgorithmException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }
    return str;
  }

  /**
   * Get directory size
   *
   * @param dir Directory
   * @return Directory size or 0
   */
  public static long getDirSize(File dir) {
    long size = 0;
    if (dir == null) {
      return size;
    }
    if (dir.isFile()) {
      size = dir.length();
    } else {
      File[] subFiles = dir.listFiles();
      if (subFiles == null) {
        return 0;
      }
      for (File file : subFiles) {
        if (file.isFile()) {
          size += file.length();
        } else {
          size += getDirSize(file);
        }
      }
    }
    return size;
  }

  /**
   * Get directry size in Megabytes
   *
   * @param dir Directory
   * @return Directory size or 0
   */
  public static long getDirSizeInMegabytes(File dir) {
    return getDirSize(dir) / 1024 / 1024;
  }

  /**
   * Check if string is a digit
   *
   * @param str String
   * @return True if str is a digit, false otherwise
   */
  public static boolean isDigit(String str) {
    if (str == null || str.length() == 0) {
      return false;
    }

    for (int i = 0; i < str.length(); i++) {
      if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != '.') {
        return false;
      }
    }
    return true;
  }

  /**
   * Create file path
   *
   * @param fileName File
   * @param dir File is a directory
   * @return True on success, false otherwise
   */
  public static boolean createFilePath(String fileName, boolean dir) {
    boolean ret = true;
    File f = new File(fileName);
    if (!f.exists()) {
      if (dir) {
        ret = f.mkdirs();
      } else {
        File d = new File(f.getParent());
        ret = d.mkdirs();
      }
    }
    return ret;
  }

  /**
   * Download file from http server
   *
   * @param url File url
   * @param fileName Download filename
   * @throws IOException
   */
  public static void downloadFile(URL url, String fileName) throws IOException {
    InputStream is;
    OutputStream out;

    is = url.openStream();
    File f = new File(fileName);
    if (!f.exists()) {
      File d = new File(f.getParent());
      if (!d.exists()) {
        if (!d.mkdirs()) {
          throw new IOException(bundle.getString("unabletoCreate") + " : " + fileName);
        }
      }
      if (!f.createNewFile()) {
        throw new IOException(bundle.getString("unabletoCreate") + " : " + fileName);
      }
    }

    out = new FileOutputStream(f);
    byte buf[] = new byte[1024];
    int len;

    while ((len = is.read(buf)) > 0) {
      out.write(buf, 0, len);
    }
    is.close();
    out.close();
  }

  /**
   * Get image from jar file
   *
   * @param <T>
   * @param fileName Image filename
   * @param cls Class
   * @return Image or null
   */
  public static <T> Image getImageFromJAR(String fileName, Class<T> cls) {
    if (fileName == null) {
      return null;
    }

    Image image = null;
    byte[] thanksToNetscape;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    InputStream in = cls.getResourceAsStream(fileName);

    try {
      int length = in.available();
      thanksToNetscape = new byte[length];
      in.read(thanksToNetscape);
      image = toolkit.createImage(thanksToNetscape);

    } catch (Exception ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } finally {
      try {
        in.close();
      } catch (IOException ex) {
        Settings.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
    return image;
  }

  /**
   * Delete all file in directory
   *
   * @param dir Directory
   * @return True on succes, false otherwise
   */
  public static boolean deleteFileInDirectory(File dir) {
    boolean del = true;
    if (dir == null && !dir.isDirectory()) {
      return false;
    }
    for (File fils : dir.listFiles()) {
      if (fils.isDirectory()) {
        if (!deleteFileInDirectory(fils)) {
          del = false;
        } else if (!fils.delete()) {
          del = false;
        }
      } else if (!fils.delete()) {
        del = false;
      }
    }
    return del;
  }

  /**
   * Copy a file
   *
   * @param sourceFile Source file
   * @param destFile Destination file
   * @return True on susccess, false otherwise
   * @throws IOException
   */
  public static boolean copyFile(File sourceFile, File destFile) throws IOException {
    boolean cpFile = false;
    if (sourceFile == null || !sourceFile.canRead()) {
      return cpFile;
    }

    if (!destFile.exists()) {
      cpFile = destFile.createNewFile();
    }

    FileChannel source = null;
    FileChannel destination = null;
    try {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0, source.size());
    } finally {
      if (source != null) {
        source.close();
      }
      if (destination != null) {
        destination.close();
      }
    }
    return cpFile;
  }

  /**
   * Restart application
   *
   * @param jarFile Jar file to restart
   * @return True if success , false otherwise
   * @throws Exception
   */
  public static boolean restartApplication(File jarFile) throws Exception {
    String javaBin = System.getProperty("java.home") + "/bin/java";

    if (!jarFile.getName().endsWith(".jar")) {
      return false;
    }
    String toExec[] = new String[]{javaBin, "-jar", jarFile.getPath()};
    Process p = Runtime.getRuntime().exec(toExec);
    return true;
  }

  /**
   * Rotate string by 13 places
   *
   * @param text String
   * @return String rotate
   */
  public static String rot13(String text) {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if ((c >= 'a' && c <= 'm') || (c >= 'A' && c <= 'M')) {
        c += 13;
      } else if ((c >= 'n' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
        c -= 13;
      }
      res.append(c);
    }
    return res.toString();
  }

  /**
   * Capitalized first letter for each words or only first one
   *
   * @param str String
   * @param onlyFirst Only first word letter capitalized
   * @return String capitalized
   */
  public static String capitalizedLetter(String str, boolean onlyFirst) {
    StringBuilder res = new StringBuilder();
    char ch, prevCh;
    boolean toUpper = true;
    prevCh = '.';
    str = str.toLowerCase();
    for (int i = 0; i < str.length(); i++) {
      ch = str.charAt(i);
      if (toUpper && Character.isLetter(ch)) {
        if (!Character.isLetter(prevCh) || (prevCh == 'i' && ch == 'i')) {
          res.append(Character.toUpperCase(ch));
          if (onlyFirst) {
            toUpper = false;
          }
        } else {
          res.append(ch);
        }
      } else {
        res.append(ch);
      }

      prevCh = ch;
    }
    return res.toString();
  }

  /**
   * Escape XML special character
   *
   * @param str String to escape
   * @return String escaped
   */
  public static String escapeXML(String str) {
    if (str == null) {
      return null;
    }

    StringBuilder stringBuffer = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      boolean needEscape = (ch == '<' || ch == '&' || ch == '>');

      if (needEscape || (ch < 32) || (ch > 136)) {
        stringBuffer.append("&#").append((int) ch).append(";");
      } else {
        stringBuffer.append(ch);
      }
    }
    return stringBuffer.toString();
  }

  /**
   * Unescape XML special character
   *
   * @param str String
   * @param encode Encode type
   * @return Unescape string
   */
  public static String unEscapeXML(String str, String encode) {
    if (str == null) {
      return "";
    }

    try {
      str = str.replaceAll("&#x(\\w\\w);", "%$1");
      str = URLDecoder.decode(str.replaceAll("% ", "%25 "), encode);
    } catch (UnsupportedEncodingException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }
    return str;
  }

  /**
   * Get stack trace message to string
   *
   * @param exception String
   * @param ste Stack trace
   * @return String with stack trace
   */
  public static String getStackTrace(String exception, StackTraceElement[] ste) {
    StringBuilder res = new StringBuilder(exception + "\n");
    for (int i = 0; i < ste.length; i++) {
      res.append("    ").append(ste[i].toString()).append("\n");
    }
    return res.toString();
  }

  /**
   * Check if file is a zip file
   *
   * @param fileName File
   * @return True if file is a zip file, false otherwise
   * @throws IOException
   */
  public static boolean isZIPFile(String fileName) throws IOException {
    File file = new File(fileName);
    if (file.isDirectory() || file.length() < 4) {
      return false;
    }

    DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    int magic = in.readInt();
    in.close();
    return (magic == 0x504b0304);
  }

  /**
   * Check if string is an url
   *
   * @param str String
   * @return True if string is an url, false otherwise
   */
  public static boolean isUrl(String str) {
    try {
      URL urL = new URL(str);
    } catch (MalformedURLException e) {//Not pretty, No return in catch
      return false;
    }
    return true;
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
   * Get thumbnail icon from web server or cache
   *
   * @param url Thumb url
   * @param cache Movie Renamer cache
   * @param dimension Thumb dimension
   * @return Icon or null
   */
  public static Icon getSearchThumb(String url, Cache cache, Dimension dimension) {
    Icon icon = null;
    try {
      Image image;
      URL uri = new URL(url);
      image = cache.getImage(uri, Cache.THUMB);
      if (image == null) {
        cache.add(uri.openStream(), uri.toString(), Cache.THUMB);
        image = cache.getImage(uri, Cache.THUMB);
      }
      icon = new ImageIcon(image.getScaledInstance(dimension.width, dimension.height, Image.SCALE_DEFAULT));
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, "{0} {1}", new Object[]{ex.getMessage(), url});
    } catch (CMMException ex) {
      Settings.LOGGER.log(Level.SEVERE, "{0} {1}", new Object[]{ex.getMessage(), url});
    } catch (IllegalArgumentException ex) {
      Settings.LOGGER.log(Level.SEVERE, "{0} {1}", new Object[]{ex.getMessage(), url});
    } catch (NullPointerException ex) {
      Settings.LOGGER.log(Level.SEVERE, "{0} {1}", new Object[]{ex.getMessage(), url});
    }
    return icon;
  }

  /**
   * Check if string is uppercase
   *
   * @param str
   * @return True if all letter are uppercase except I,II,III,..., false otherwise
   */
  public static boolean isUpperCase(String str) {
    String[] romanNumber = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    for (String number : romanNumber) {
      if (str.equals(number)) {
        return false;
      }
    }
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (ch < 32 || ch > 96) {
        return false;
      }
    }
    return true;
  }
}