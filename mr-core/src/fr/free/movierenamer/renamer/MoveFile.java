/*
 * mr-core
 * Copyright (C) 2012-2013 Nicolas Magré
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 * Class MoveFile
 *
 * @author Nicolas Magré
 */
public class MoveFile extends Thread {

  private boolean done;
  private int progress;
  private File source, dest;
  private boolean cancel;

  public MoveFile(File source, File dest) {
    this.source = source;
    this.dest = dest;
    this.done = false;
    this.progress = -1;
    this.cancel = false;
  }

  @Override
  public void run() {
    if (dest.exists()) {
      // error
      return;
    }

    if (source.renameTo(dest)) {
      return;
    }

    long size = source.length();
    if (size == 0L) {
      size = -1;
    }

    InputStream input = null;
    OutputStream output = null;
    try {
      input = new FileInputStream(source);
      output = new FileOutputStream(dest);
      byte[] buf = new byte[1024];
      int bytesRead;
      long count = 0;

      while (!cancel && (bytesRead = input.read(buf)) > 0) {
        output.write(buf, 0, bytesRead);
        count += bytesRead;
        progress = (int) ((count * 100) / size);
      }
    } catch (IOException ex) {
    } finally {
      try {
        if (input != null) {
          input.close();
        }

        if (output != null) {
          output.close();
        }

        if (cancel) {
          if (dest.exists()) {
            dest.delete();
          }
        }

      } catch (IOException ex) {
        Settings.LOGGER.log(Level.SEVERE, null, ex);
        if (dest.exists()) {
          dest.delete();
        }
      }
    }

    done = true;
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
