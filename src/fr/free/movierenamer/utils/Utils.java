/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.utils;

import fr.free.movierenamer.movie.MoviePerson;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Class Utils
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
  public static final Icon MOVIERENAMEDICON = new ImageIcon(Utils.getImageFromJAR("/image/icon-32.gif", Utils.class));
  public static final Icon MOVIEWASRENAMEDICON = new ImageIcon(Utils.getImageFromJAR("/image/icon-22.gif", Utils.class));
  public static final Icon MOVIEICON = new ImageIcon(Utils.getImageFromJAR("/image/film.png", Utils.class));
  public static final Icon WARNINGICON = new ImageIcon(Utils.getImageFromJAR("/image/film-error.png", Utils.class));
  public static final ResourceBundle rb = ResourceBundle.getBundle("fr/free/movierenamer/version");

  public static String getRbTok(String propToken) {
    String msg = "";
    try {
      msg = rb.getString(propToken);
    } catch (MissingResourceException e) {
      System.err.println("Token ".concat(propToken).concat(" not in Propertyfile!"));
    }
    return msg;
  }

  public static String getOsName() {
    if (OS == null)
      OS = System.getProperty("os.name");
    return OS;
  }

  public static boolean isWindows() {
    return getOsName().startsWith("Windows");
  }

  public static String getFilteredName(String movieName, String[] replaceBy) {
    String res = movieName.replaceAll("\\.", " ");
    for (int i = 0; i < replaceBy.length; i++) {
      res = res.replaceAll("(?i)" + replaceBy[i], "");
    }
    res = res.replaceAll(" [0-9][0-9][0-9][0-9]", "");
    res = res.replaceAll("\\([0-9][0-9][0-9][0-9]\\)", "");
    res = res.replaceAll("\\(.*\\)", "");
    res = res.replaceAll(" {2,}", " ");
    res = res.replaceAll("^ ", "");
    res = res.replaceAll(" $", "");
    return res;
  }

  public static boolean checkFile(String fileName, Settings setting) {
    if (!fileName.contains(DOT))
      return false;
    if (!setting.useExtensionFilter) return true;
    String ext = fileName.substring(fileName.lastIndexOf(DOT) + 1);
    for (int i = 0; i < setting.extensions.length; i++) {
      if (ext.equalsIgnoreCase(setting.extensions[i]))
        return true;
    }
    return false;
  }

  public static String[] removeFromArray(String[] array, int index) {
    if (index >= array.length)
      return null;
    String[] newArray = new String[array.length - 1];
    int pos = 0;
    for (int i = 0; i < array.length; i++) {
      if (i != index)
        newArray[pos++] = array[i];
    }
    return newArray;
  }

  public static String arrayToString(Object[] array, String separator, int limit) {
    StringBuilder res = new StringBuilder();
    if (array.length == 0)
      return res.toString();
    for (int i = 0; i < array.length; i++) {
      if(limit != 0 && i == limit) break;
      res.append(array[i].toString().trim());
      if((i+1) != limit) res.append((i < (array.length - 1)) ? separator : "");
    }
    return res.toString();
  }

  public static String arrayToString(ArrayList<String> array, String separator, int limit) {
    return arrayToString(array.toArray(new Object[array.size()]), separator, limit);
  }

  public static String arrayPersonnToString(ArrayList<MoviePerson> array, String separator, int limit) {
    String[] arr = new String[array.size()];
    for (int i = 0; i < array.size(); i++) {
      arr[i] = array.get(i).toString();
    }
    return arrayToString(arr, separator, limit);
  }

  public static ArrayList<String> stringToArray(String str, String seprarator) {
    ArrayList<String> array = new ArrayList<String>();
    String[] res = str.split(seprarator);
    array.addAll(Arrays.asList(res));
    return array;
  }

  public static String md5(String s) {
    try {
      MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
      digest.update(s.getBytes());
      byte messageDigest[] = digest.digest();

      StringBuilder hexString = new StringBuilder();
      for (int i = 0; i < messageDigest.length; i++) {
        hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
      }
      return hexString.toString();

    } catch (NoSuchAlgorithmException e) {
    }
    return s;
  }

  public static long getDirSize(File dir) {
    long size = 0;
    if (dir.isFile())
      size = dir.length();
    else {
      File[] subFiles = dir.listFiles();
      if (subFiles == null)
        return 0;
      for (File file : subFiles) {
        if (file.isFile())
          size += file.length();
        else
          size += getDirSize(file);
      }
    }
    return size;
  }

  public static long getDirSizeInMegabytes(File dir) {
    return getDirSize(dir) / 1024 / 1024;
  }

  public static boolean isDigit(String text) {
    if (text == null)
      return false;
    if (text.length() == 0)
      return false;

    for (int i = 0; i < text.length(); i++) {
      if (!Character.isDigit(text.charAt(i)) && text.charAt(i) != '.')
        return false;
    }
    return true;
  }

  public static boolean createFilePath(String fileName, boolean dir) {
    boolean ret = true;
    File f = new File(fileName);
    if (!f.exists())
      if (dir)
        ret = f.mkdirs();
      else {
        File d = new File(f.getParent());
        ret = d.mkdirs();
      }
    return ret;
  }

  public static boolean downloadFile(URL uri, String fileName) throws IOException {
    InputStream is = null;
    OutputStream out = null;
    boolean downloaded = false;
    try {
      is = uri.openStream();
      File f = new File(fileName);
      if (!f.exists()) {
        File d = new File(f.getParent());
        if (!d.exists())
          if (!d.mkdirs()) throw new IOException(bundle.getString("unabletoCreate") + " : " + fileName);
        if (!f.createNewFile()) throw new IOException(bundle.getString("unabletoCreate") + " : " + fileName);
      }
      out = new FileOutputStream(f);
      byte buf[] = new byte[1024];
      int len;
      if (is.available() != 0) downloaded = true;
      while ((len = is.read(buf)) > 0)
        out.write(buf, 0, len);
      is.close();
      out.close();
    } catch (IOException ex) {
      if (out != null) try {
          out.close();
        } catch (IOException ex1) {
        }
      throw ex;
    }
    return downloaded;
  }

  public static Image getImageFromJAR(String fileName, Class cls) {
    if (fileName == null)
      return null;

    Image image = null;
    byte[] thanksToNetscape = null;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    InputStream in = cls.getResourceAsStream(fileName);

    try {
      int length = in.available();
      thanksToNetscape = new byte[length];
      in.read(thanksToNetscape);
      image = toolkit.createImage(thanksToNetscape);

    } catch (Exception ex) {
      Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        in.close();
      } catch (IOException ex) {
        Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return image;
  }

  public static boolean deleteFileInDirectory(File dir) {
    boolean del = true;
    for (File fils : dir.listFiles()) {
      if (fils.isDirectory()) {
        if (!deleteFileInDirectory(fils)) del = false;
        else if (!fils.delete()) del = false;
      } else if (!fils.delete()) del = false;
    }
    return del;
  }

  public static boolean copyFile(File sourceFile, File destFile) throws IOException {
    boolean cpFile = false;
    if (!destFile.exists())
      cpFile = destFile.createNewFile();

    FileChannel source = null;
    FileChannel destination = null;
    try {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0, source.size());
    } finally {
      if (source != null)
        source.close();
      if (destination != null)
        destination.close();
    }
    return cpFile;
  }

  public static void copyStream(InputStream is, OutputStream os) {
    final int buffer_size = 1024;
    try {
      byte[] bytes = new byte[buffer_size];
      for (;;) {
        int count = is.read(bytes, 0, buffer_size);
        if (count == -1)
          break;
        os.write(bytes, 0, count);
      }
    } catch (Exception ex) {
    }
  }

  public static boolean restartApplication(File jarFile) throws Exception {
    String javaBin = System.getProperty("java.home") + "/bin/java";

    if (!jarFile.getName().endsWith(".jar"))
      return false;
    String toExec[] = new String[]{javaBin, "-jar", jarFile.getPath()};
    try {
      Process p = Runtime.getRuntime().exec(toExec);
    } catch (Exception e) {
      throw e;
    }
    return true;
  }

  public static String rot13(String text) {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c >= 'a' && c <= 'm')
        c += 13;
      else if (c >= 'n' && c <= 'z')
        c -= 13;
      else if (c >= 'A' && c <= 'M')
        c += 13;
      else if (c >= 'A' && c <= 'Z')
        c -= 13;
      res.append(c);
    }
    return res.toString();
  }

  public static String capitalizedLetter(String str, boolean onlyFirst) {
    StringBuilder res = new StringBuilder();
    char ch, prevCh;
    boolean toUpper = true;
    prevCh = '.';
    str = str.toLowerCase();
    for (int i = 0; i < str.length(); i++) {
      ch = str.charAt(i);
      if (toUpper && Character.isLetter(ch))
        if (!Character.isLetter(prevCh) || (prevCh == 'i' && ch == 'i')) {
          res.append(Character.toUpperCase(ch));
          if (onlyFirst) toUpper = false;
        } else
          res.append(ch);
      else
        res.append(ch);

      prevCh = ch;
    }
    return res.toString();
  }

  public static String escapeXML(String text) {
    if (text == null)
      return text;

    StringBuilder stringBuffer = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      boolean needEscape = (ch == '<' || ch == '&' || ch == '>');

      if (needEscape || (ch < 32) || (ch > 136))
        stringBuffer.append("&#").append((int) ch).append(";");
      else
        stringBuffer.append(ch);
    }
    return stringBuffer.toString();
  }

  public static String unEscapeXML(String text, String encode) {
    if (text == null)
      return text;

    StringBuilder stringBuffer = new StringBuilder();
    try {
      text = text.replaceAll("&#x(\\w\\w);", "%$1");
      text = URLDecoder.decode(text.replaceAll("% ", "%25 "), encode);
      return text.trim();
    } catch (UnsupportedEncodingException e) {
      Logger.getLogger("Movie Renamer").log(Level.SEVERE, e.getMessage());
    }
    return stringBuffer.toString();
  }

  public static String getStackTrace(String exception, StackTraceElement[] ste) {
    StringBuilder res = new StringBuilder(exception + "\n");
    for (int i = 0; i < ste.length; i++) {
      res.append("    ").append(ste[i].toString()).append("\n");
    }
    return res.toString();
  }
}
