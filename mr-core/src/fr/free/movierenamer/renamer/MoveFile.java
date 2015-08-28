/*
 * mr-core
 * Copyright (C) 2013-2014 Nicolas Magré
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
package fr.free.movierenamer.renamer;

import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.FileUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Class MoveFile
 *
 * @author Nicolas Magré
 */
public class MoveFile extends Thread {

  private boolean done;
  private int progress;
  private final File source, dest;
  private boolean cancel;
  private Status status;
  private String errorStr;

  public static enum Status {

    OK,
    CHECK_FAILED,
    REMOVE_FAILED,
    ERROR
  }

  public MoveFile(File source, File dest) {
    this.source = source;
    this.dest = dest;
    this.done = false;
    this.progress = 0;
    this.cancel = false;
    errorStr = "";
  }

  @Override
  public void run() {

    status = null;
    long size = source.length();
    if (size == 0L) {
      size = 1;
    }

    final String sourceChk = FileUtils.getFileChecksum(source);

    BufferedInputStream input = null;
    BufferedOutputStream output = null;
    try {
      input = new BufferedInputStream(new FileInputStream(source));
      output = new BufferedOutputStream(new FileOutputStream(dest));
      final byte[] buf = new byte[1024];
      int bytesRead;
      long count = 0;

      while (!cancel && (bytesRead = input.read(buf)) > 0) {
        output.write(buf, 0, bytesRead);
        count += bytesRead;
        progress = (int) ((count * 100) / size);
      }

    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
      status = Status.ERROR;
      errorStr = ex.getMessage();
    } finally {
      try {
        if (input != null) {
          input.close();
        }

        if (output != null) {
          output.close();
        }

        if (cancel) {
          Settings.LOGGER.log(Level.INFO, null, String.format("Move file canceled for [%s] to [%s]", source, dest));
          if (dest.exists() && !dest.delete()) {
            Settings.LOGGER.log(Level.WARNING, null, String.format("Remove destination file failed on cancel : [%s]", dest));
            status = Status.REMOVE_FAILED;
          }
        }

      } catch (IOException ex) {
        Settings.LOGGER.log(Level.SEVERE, null, ex);
        status = Status.ERROR;
        errorStr = ex.getLocalizedMessage();
        dest.delete();
      }
    }

    // Check if file is complete
    if (status == null) {
      final String destChk = FileUtils.getFileChecksum(dest);
      if (!sourceChk.equals(destChk)) {
        status = Status.CHECK_FAILED;
        dest.delete();
      } else {
        if (Settings.WINDOWS && !source.renameTo(source)) {// Windows permissions sucks
          status = Status.OK;
          source.deleteOnExit();
        } else {
          status = source.delete() ? Status.OK : Status.REMOVE_FAILED;
        }
      }
    }

    done = true;
  }

  public Status getStatus() {
    return status;
  }

  public String getErrorString() {
    return errorStr;
  }

  public boolean isDone() {
    return done;
  }

  public void cancel() {
    cancel = true;
  }

  public int getProgress() {
    return progress;
  }
}
