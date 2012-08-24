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
package data;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import fr.free.movierenamer.worker.provider.ImdbInfoWorker;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class UpdateData
 * @author Nicolas Magré
 */
public class UpdateData {

  /**
   * @param args the command line arguments
   * @throws ActionNotValidException
   * @throws Exception
   */
  public static void main(String[] args) throws ActionNotValidException, Exception {
    Settings.newInstance();

    File dir = getPath(".");
    if (dir != null) {
      updateImdbData(dir);
    } else {
      System.out.println("Update data failed");
    }
  }

  /**
   * Get dta folder path
   * @param spath
   * @return
   * @throws IOException 
   */
  private static File getPath(String spath) throws IOException {
    File path = new File(spath);
    File cpath = path.getCanonicalFile();

    if (Utils.isRootDir(cpath)) {
      return null;
    }

    File[] dirs = cpath.listFiles();

    for (File dir : dirs) {
      if (dir.getName().equals("data")) {
        return dir;
      }
      if (dir.getName().equals("test")) {
        return getPath(dir.getAbsolutePath());
      }
    }
    return getPath(cpath.getParent());
  }

  /**
   * Write object to file
   * @param obj
   * @param file
   * @throws IOException 
   */
  private static void writeToFile(Object obj, String file) throws IOException {
    File data = new File(file);

    if (obj == null) {
      return;
    }

    if (!data.exists()) {
      data.createNewFile();
    }
    Utils.serializeObjectToFile(obj, new FileOutputStream(data));
  }

  private static void updateImdbData(File path) throws ActionNotValidException, Exception {
    String imdbId = "tt0133093";
    MediaID id = new MediaID(imdbId, MediaID.MediaIdType.IMDBID);

    imdbWorker(path, id, Utils.Language.ENGLISH);
    imdbWorker(path, id, Utils.Language.FRENCH);
    imdbWorker(path, id, Utils.Language.ITALIAN);
    imdbWorker(path, id, Utils.Language.SPANISH);
  }
  
  private static void imdbWorker(File path, MediaID id, Utils.Language lang) throws ActionNotValidException, Exception{
    String file =  path.getAbsolutePath() + File.separator + id.getID() + "_" + lang.getShort() + ".ser";
    ImdbInfoWorker worker = new ImdbInfoWorker(null, id, lang);
    worker.execute();
    writeToFile(worker.get(), file);
  }
}
