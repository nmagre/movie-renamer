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

import fr.free.movierenamer.Main;
import fr.free.movierenamer.utils.Settings;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author duffy
 */
public class Utils {

  private static String OS = null;
  public static final String SPACE = " ";
  public static final String ENDLINE = "\n";
  public static final String EMPTY = "";
  public static final String DOT = ".";
  public static final Icon MOVIEICON = new ImageIcon(Utils.getImageFromJAR("/image/film.png", Main.class));
  public static final Icon WARNINGICON = new ImageIcon(Utils.getImageFromJAR("/image/film-error.png", Main.class));
  
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
    String ext = fileName.substring(fileName.lastIndexOf(DOT) + 1);
    for (int i = 0; i < setting.extensions.length; i++) {
      if (ext.equals(setting.extensions[i]))
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

  public static String arrayToString(String[] array, String separator) {
    String res = "";
    if (array.length == 0)
      return res;
    for (int i = 0; i < array.length; i++) {
      res += array[i] + ((i < (array.length - 1)) ? separator : "");
    }
    return res;
  }

  public static String arrayToString(ArrayList<String> array, String separator) {
    return arrayToString(array.toArray(new String[array.size()]), separator);
  }

  public static String md5(String s) {
    try {
      // Create MD5 Hash
      MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
      digest.update(s.getBytes());
      byte messageDigest[] = digest.digest();

      // Create Hex String
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

  public static boolean createFilePath(String fileName) {
    boolean ret = true;
    File f = new File(fileName);
    if (!f.exists())
      if (!f.getName().contains("."))
        ret = f.mkdirs();
      else {
        File d = new File(f.getParent());
        ret = d.mkdirs();
      }
    return ret;
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
      //setting.getLogger().log(Level.SEVERE, null, ex + " " + fileName);
      return null;
    }
    return image;
  }

  public static void deleteDirectory(File racine) {
    for (File fils : racine.listFiles()) {
      if (fils.isDirectory())
        deleteDirectory(fils);
      else
        fils.delete();
    }
  }

  public static void copyFile(File sourceFile, File destFile) throws IOException {
    if (!destFile.exists())
      destFile.createNewFile();

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

  private static void downloadFile(String url, String fileName) {
    InputStream is = null;
    try {
      URL uri = new URL(url);
      is = uri.openStream();
      File f = new File(fileName);
      if (!f.exists()) {
        File d = new File(f.getParent());
        if (!d.exists())
          d.mkdirs();
        f.createNewFile();
      }
      OutputStream out = new FileOutputStream(f);
      byte buf[] = new byte[1024];
      int len;
      while ((len = is.read(buf)) > 0)
        out.write(buf, 0, len);
      out.close();
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(null, "Download Failed\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
      try {
        is.close();
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(null, "Close Downloaded File Failed\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public static boolean restartApplication(Object classInJarFile) {
    String javaBin = System.getProperty("java.home") + "/bin/java";
    File jarFile;
    try {
      jarFile = new File(classInJarFile.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    /* is it a jar file? */
    if (!jarFile.getName().endsWith(".jar"))
      return false;
    String toExec[] = new String[]{javaBin, "-jar", jarFile.getPath()};
    try {
      Process p = Runtime.getRuntime().exec(toExec);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    System.exit(0);
    return true;
  }

  public static String rot13(String text) {
    String res = "";
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
      res += c;
    }
    return res;
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

  public static String getStackTrace(String exception, StackTraceElement[] ste){
      String res = exception + "\n";
      for(int i=0;i<ste.length;i++) res += "    " + ste[i].toString() + "\n";
      return res;
  }
}
