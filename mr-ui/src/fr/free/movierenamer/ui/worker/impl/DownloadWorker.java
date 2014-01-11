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
package fr.free.movierenamer.ui.worker.impl;

import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.worker.Worker;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Class DownloadWorker
 *
 * @author Nicolas Magré
 */
public class DownloadWorker extends Worker<Void> {

  private final List<URL> urls;
  private final File destDir;

  public DownloadWorker(MovieRenamer mr, List<URL> urls, File destDir) {
    super(mr);
    this.urls = urls;
    this.destDir = destDir;
  }

  @Override
  protected Void executeInBackground() throws Exception {
    for (URL url : urls) {
      downloadFile(url, destDir);
    }
    return null;
  }

  private void downloadFile(URL url, File destFolder) throws Exception {

    InputStream input = null;
    FileOutputStream output = null;

    String surl = url.toString();

    try {
      String fileName = surl.substring(surl.lastIndexOf('/') + 1, surl.length());
      URLConnection connection = url.openConnection();
      input = connection.getInputStream();
      output = new FileOutputStream(new File(destFolder, fileName));

      byte[] buffer = new byte[1024];
      int read;
      while ((read = input.read(buffer)) > 0) {
        output.write(buffer, 0, read);
      }

      output.flush();

    } catch (IOException e) {
      throw e;
    } finally {
      try {
        if (output != null) {
          output.close();
        }

        if (input != null) {
          input.close();
        }
      } catch (IOException e) {
      }
    }
  }

  @Override
  protected void workerStarted() {
    UIEvent.fireUIEvent(UIEvent.Event.DOWNLOAD_START, MovieRenamer.class);
  }

  @Override
  protected void workerDone() throws Exception {
    UIEvent.fireUIEvent(UIEvent.Event.DOWNLOAD_DONE, MovieRenamer.class);
  }

  @Override
  public String getParam() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return "Download worker";// FIXME i18n
  }

}
