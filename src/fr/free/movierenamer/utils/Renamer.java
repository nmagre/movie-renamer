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

import fr.free.movierenamer.media.MediaRenamed;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 * Class Renamer , Rename movie files, download thumb/fanart, create XBMC NFO
 *
 * @author Nicolas Magré
 */
public class Renamer {

  private String title;
  private Settings setting;
  private File oldFile;
  private File newFile;
  private String oldPath;
  private String oldFileNameNoExt;
  private String ext;
  private String newFileName;
  private String newPath = "";
  private String newFileNameNoExt;
  private MediaRenamed renamed;
  private ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");
  public boolean cancel = false;

  /**
   * Constructor arguments
   *
   * @param title Movie title
   * @param oldFile File to rename
   * @param newFileName Renamed file
   * @param setting Movie Renamer settings
   */
  public Renamer(String title, File oldFile, String newFileName, Settings setting) {
    this.title = title;
    this.setting = setting;
    this.oldFile = oldFile;
    oldPath = oldFile.getParent();
    oldFileNameNoExt = oldFile.getName().substring(0, oldFile.getName().lastIndexOf("."));
    ext = oldFile.getName().substring(oldFile.getName().lastIndexOf(".") + 1);
    this.newFileName = newFileName;
    if (newFileName.contains(File.separator)) {
      newPath = newFileName.substring(0, newFileName.lastIndexOf(File.separator));
      this.newFileName = newFileName.substring(newFileName.lastIndexOf(File.separator) + 1);
    }
    newFileNameNoExt = this.newFileName.substring(0, this.newFileName.lastIndexOf("."));

    renamed = new MediaRenamed(title);
  }

  /**
   * Rename a movie file and add it to renamed XML file
   *
   * @return True if renamed success, False otherwise
   */
  public boolean rename() {
    String lastDir = oldPath.substring(oldPath.lastIndexOf(File.separator) + 1);

    if (oldFileNameNoExt.matches(lastDir + ".*")) {
      File files[] = new File(oldPath).listFiles(new FileFilter() {

        @Override
        public boolean accept(File file) {
          String fname = file.toString();
          fname = fname.toLowerCase();
          for (int i = 0; i < setting.extensions.length; i++) {
            if (fname.endsWith("." + setting.extensions[i])) {
              return true;
            }
          }
          return false;
        }
      });

      if (files.length == 1) {
        if (newPath.equals("")) {
          newPath = title;
        }

        File dir = new File(oldPath);
        File oldFileNewDir = new File(oldPath.substring(0, oldPath.lastIndexOf(File.separator) + 1) + newPath);
        Settings.LOGGER.log(Level.INFO, "Rename folder : {0} \nTo : {1}", new Object[]{dir, oldFileNewDir});
        boolean success = dir.renameTo(oldFileNewDir);
        if (!success) {
          Settings.LOGGER.log(Level.SEVERE, "Failed to rename : {0} \nTo : {1}", new Object[]{dir, oldFileNewDir});
          return false;
        }

        oldFile = new File(oldFileNewDir.getAbsolutePath() + File.separator + oldFileNameNoExt + "." + ext);
      }
      newPath = "";
    }

    if (!newPath.equals("")) {
      Settings.LOGGER.log(Level.INFO, "Create path : {0}{1}{2}", new Object[]{oldFile.getParent(), File.separator, newPath});
      Utils.createFilePath(oldFile.getParent() + File.separator + newPath, true);
      if (!newPath.endsWith(File.separator)) {
        newPath += File.separator;
      }
    }

    if (!newFileNameNoExt.equals(oldFileNameNoExt) || !newPath.equals("")) {
      newFile = new File(oldFile.getParent() + File.separator + newPath + newFileName);
      if (newFile.exists()) {
        int n = JOptionPane.showConfirmDialog(null, Settings.APPNAME + Utils.SPACE + bundle.getString("alreadyExist"), "Question", JOptionPane.YES_NO_OPTION);
        if (n != JOptionPane.YES_OPTION) {
          cancel = true;
          return true;
        }
      }

      boolean success = oldFile.renameTo(newFile);
      Settings.LOGGER.log(Level.INFO, "Rename file : {0} \nTo : {1}", new Object[]{oldFile, oldFile.getParent() + File.separator + newPath + newFileName});
      if (!success) {
        Settings.LOGGER.log(Level.SEVERE, "Failed to rename : {0} \nTo : {1}", new Object[]{oldFile, oldFile.getParent() + File.separator + newPath + newFileName});
        return false;
      }

    } else {
      newFile = oldFile;
    }

    File[] files = oldFile.getParentFile().listFiles(new FileFilter() {

      @Override
      public boolean accept(File file) {
        if (file.getName().equals(oldFileNameNoExt + ".srt")) {
          return true;
        }
        if (file.getName().equals(oldFileNameNoExt + ".sub")) {
          return true;
        }
        return false;
      }
    });

    for (int i = 0; i < files.length; i++) {
      String fext = files[i].getName().substring(files[i].getName().lastIndexOf(Utils.DOT));

      boolean success = files[i].renameTo(new File(oldFile.getParent() + File.separator + newPath + newFileNameNoExt + fext));
      Settings.LOGGER.log(Level.INFO, "Rename file : {0} \nTo : {1}", new Object[]{files[i], oldFile.getParent() + File.separator + newPath + newFileNameNoExt + fext});
      if (!success) {
        Settings.LOGGER.log(Level.SEVERE, "Filed to rename : {0}\nTo : {1}", new Object[]{files[i], oldFile.getParent() + File.separator + newPath + newFileNameNoExt + fext});
      }
    }


    renamed.setDate(new Date().toString());
    renamed.setMovieFileSrc(oldFile.getAbsolutePath());
    renamed.setMovieFileDest(newFile.getAbsolutePath());
    renamed.setRenameFailed(false);
    return true;
  }

  /**
   * Get renamed
   *
   * @return Renamed
   */
  public MediaRenamed getRenamed() {
    return renamed;
  }

  /**
   * Get new file
   *
   * @return Renamed file
   */
  public File getNewFile() {
    return newFile;
  }

  /**
   * Create XBMC NFO
   *
   * @param create Create NFO, Rename NFO (if exist) otherwise
   * @param snfo NFO string
   * @return True if success, False otherwise
   */
  public boolean createNFO(boolean create, String snfo) {
    boolean createNfo = true;
    File nfo = new File(oldFile.getParent() + File.separator + oldFileNameNoExt + ".nfo");
    File newNfo = new File(oldFile.getParent() + File.separator + newPath + newFileNameNoExt + ".nfo");
    if (create) {
      Settings.LOGGER.log(Level.INFO, "Create nfo file{0}", newNfo);
      try {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newNfo), "UTF-8"));
        out.write(snfo, 0, snfo.length());
        out.close();
      } catch (IOException ex) {
        Settings.LOGGER.log(Level.SEVERE, ex.toString());
      }
      if (nfo.exists() && !nfo.equals(newNfo)) {
        Settings.LOGGER.log(Level.INFO, "Delete nfo file{0}", nfo);
        createNfo = nfo.delete();
      }
    } else if (nfo.exists()) {
      Settings.LOGGER.log(Level.INFO, "Rename nfo : {0} \nTo : {1}", new Object[]{nfo, newNfo});
      createNfo = nfo.renameTo(newNfo);
    }
    return createNfo;
  }

  /**
   * Download and rename thumb
   *
   * @param create Download thumb, Rename thumb (if exist) otherwise
   * @param url Thumb url
   * @return True if success, False otherwise
   */
  public boolean createThumb(boolean create, URL url) {
    if (url == null) {
      return true;
    }
    boolean createThum = true;
    File thumb = new File(oldFile.getParent() + File.separator + oldFileNameNoExt + setting.thumbExtList[setting.thumbExt]);
    File newThumb = new File(oldFile.getParent() + File.separator + newPath + newFileNameNoExt + setting.thumbExtList[setting.thumbExt]);
    if (create) {
      Settings.LOGGER.log(Level.INFO, "Create thumb : {0}", newThumb);
      try {
        File file = Cache.getInstance().get(url, Cache.CacheType.THUMB);
        if (file == null) {
          Cache.getInstance().add(url.openStream(), url.toString(), Cache.CacheType.THUMB);
        }
        Utils.copyFile(Cache.getInstance().get(url, Cache.CacheType.THUMB), newThumb);
      } catch (IOException ex) {
        Settings.LOGGER.log(Level.SEVERE, ex.toString());
      }
      if (thumb.exists() && !thumb.equals(newThumb)) {
        Settings.LOGGER.log(Level.INFO, "Delete thumb : {0}", thumb);
        createThum = thumb.delete();
      }
    } else if (thumb.exists()) {
      Settings.LOGGER.log(Level.INFO, "Rename thumb : {0} \nTo : {1}", new Object[]{thumb, newThumb});
      createThum = thumb.renameTo(newThumb);
    }
    return createThum;
  }

  /**
   * Download and rename fanart
   *
   * @param create Download fanart Rename fanart (if exist) otherwise
   * @param url Fanart url
   * @return True if success , False otherwise
   */
  public boolean createFanart(boolean create, URL url) {
    if (url == null) {
      return true;
    }
    boolean createfan = true;
    File fanart = new File(oldFile.getParent() + File.separator + oldFileNameNoExt + "-fanart.jpg");
    File newFanart = new File(oldFile.getParent() + File.separator + newPath + newFileNameNoExt + "-fanart.jpg");
    if (create) {
      Settings.LOGGER.log(Level.INFO, "Create fanart : {0}", newFanart);
      try {
        File file = Cache.getInstance().get(url, Cache.CacheType.FANART);
        if (file == null) {
          Cache.getInstance().add(url.openStream(), url.toString(), Cache.CacheType.FANART);
        }
        Utils.copyFile(Cache.getInstance().get(url, Cache.CacheType.FANART), newFanart);
      } catch (IOException ex) {
        Settings.LOGGER.log(Level.SEVERE, ex.toString());
      }
      if (fanart.exists() && !fanart.equals(newFanart)) {
        Settings.LOGGER.log(Level.INFO, "Delete fanart : {0}", fanart);
        createfan = fanart.delete();
      }
    } else if (fanart.exists()) {
      Settings.LOGGER.log(Level.INFO, "Rename fanart : {0} \nTo : {1}", new Object[]{fanart, newFanart});
      createfan = fanart.renameTo(newFanart);
    }
    return createfan;
  }

  /**
   * Get new filename and path without extension
   *
   * @return Filename and path
   */
  public String getNewFileNoExt() {
    return oldFile.getParent() + File.separator + newPath + newFileNameNoExt;
  }

  /**
   * Set thumb url
   *
   * @param thumb
   */
  public void setThumb(String thumb) {
    renamed.setThumb(thumb);
  }

  public void setTmDbId(String id) {
    renamed.setTmDbId(id);
  }
}
