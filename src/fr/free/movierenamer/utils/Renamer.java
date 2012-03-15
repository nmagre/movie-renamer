/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2011 Magré Nicolas                                         *
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

import fr.free.movierenamer.ui.MovieRenamer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author duffy
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
  }

  public boolean rename() {
    String lastDir = oldPath.substring(oldPath.lastIndexOf(File.separator) + 1);
    setting.getLogger().log(Level.INFO, "Movie folder : {0}\nMovie old filename : {1}", new Object[]{lastDir, oldFileNameNoExt});
    if (oldFileNameNoExt.matches(lastDir + ".*")) {
      File files[] = new File(oldPath).listFiles(new FileFilter() {

        @Override
        public boolean accept(File file) {
          String fname = file.toString();
          fname = fname.toLowerCase();
          for (int i = 0; i <setting.extensions.length; i++) {
            if (fname.endsWith("." + setting.extensions[i])) return true;
          }
          return false;
        }
      });
      
      if (files.length == 1) {
        if (newPath.equals(""))
          newPath = title;

        File dir = new File(oldPath);
        File oldFileNewDir = new File(oldPath.substring(0, oldPath.lastIndexOf(File.separator) + 1) + newPath);
        boolean success = dir.renameTo(oldFileNewDir);
        if (!success) {
          setting.getLogger().log(Level.SEVERE, "Failed to rename : {0} to : {1}", new Object[]{dir, oldFileNewDir});
          return false;
        }

        oldFile = new File(oldFileNewDir.getAbsolutePath() + File.separator + oldFileNameNoExt + "." + ext);
      }
      newPath = "";
    }

    if (!newPath.equals("")) {
      Utils.createFilePath(oldFile.getParent() + File.separator + newPath, true);
      if (!newPath.endsWith(File.separator)) newPath += File.separator;
    }

    if (!newFileNameNoExt.equals(oldFileNameNoExt) || !newPath.equals("")) {
      boolean success = oldFile.renameTo(new File(oldFile.getParent() + File.separator + newPath + newFileName));
      if (!success) {
        setting.getLogger().log(Level.SEVERE, "Failed to rename : {0} to : {1}", new Object[]{oldFile, oldFile.getParent() + File.separator + newPath + newFileName});
        return false;
      }
      newFile = new File(oldFile.getParent() + File.separator + newPath + newFileName);
    } else newFile = oldFile;

    File[] files = oldFile.getParentFile().listFiles(new FileFilter() {

      @Override
      public boolean accept(File file) {
        if (file.getName().equals(oldFileNameNoExt + ".srt")) return true;
        if (file.getName().equals(oldFileNameNoExt + ".sub")) return true;
        return false;
      }
    });

    for (int i = 0; i < files.length; i++) {
      String fext = files[i].getName().substring(files[i].getName().lastIndexOf(Utils.DOT));

      boolean success = files[i].renameTo(new File(oldFile.getParent() + File.separator + newPath + newFileNameNoExt + fext));
      if (!success)
        setting.getLogger().log(Level.SEVERE, "Filed to rename : {0}\nTo : {1}", new Object[]{files[i], oldFile.getParent() + File.separator + newPath + newFileNameNoExt + fext});
    }

    setting.getLogger().log(Level.INFO, "Rename : {0}\nTo : {1}", new Object[]{oldFile, newFile});
    return true;
  }

  public File getNewFile() {
    return newFile;
  }

  public void createNFO(boolean rename, String snfo) {
    File nfo = new File(oldFile.getParent() + File.separator + oldFileNameNoExt + ".nfo");
    File newNfo = new File(oldFile.getParent() + File.separator + newPath + newFileNameNoExt + ".nfo");
    if (!rename) {
      try {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newNfo), "UTF-8"));
        out.write(snfo, 0, snfo.length());
        out.close();
      } catch (IOException ex) {
        Logger.getLogger(MovieRenamer.class.getName()).log(Level.SEVERE, null, ex);
      }
      if (nfo.exists() && !nfo.equals(newNfo)) nfo.delete();
    } else if (nfo.exists())// Move nfo
      nfo.renameTo(newNfo);
  }

  public void createThumb(boolean rename, URL url) {
    File thumb = new File(oldFile.getParent() + File.separator + oldFileNameNoExt + setting.thumbExtList[setting.thumbExt]);
    File newThumb = new File(oldFile.getParent() + File.separator + newPath + newFileNameNoExt + setting.thumbExtList[setting.thumbExt]);
    if (!rename) {
      try {
        Utils.copyFile(setting.cache.get(url, Cache.thumb), newThumb);
      } catch (IOException ex) {
        Logger.getLogger(MovieRenamer.class.getName()).log(Level.SEVERE, null, ex);
      }
      if (thumb.exists() && !thumb.equals(newThumb)) thumb.delete();
    } else if (thumb.exists()) thumb.renameTo(newThumb);
  }

  public void createFanart(boolean rename, URL url) {
    File fanart = new File(oldFile.getParent() + File.separator + oldFileNameNoExt + "-fanart.jpg");
    File newFanart = new File(oldFile.getParent() + File.separator + newPath + newFileNameNoExt + "-fanart.jpg");
    if (!rename) {
      try {
        Utils.copyFile(setting.cache.get(url, Cache.fanart), newFanart);
      } catch (IOException ex) {
        Logger.getLogger(MovieRenamer.class.getName()).log(Level.SEVERE, null, ex);
      }
      if (fanart.exists() && !fanart.equals(newFanart)) fanart.delete();
    } else if (fanart.exists()) fanart.renameTo(newFanart);
  }
}
