/*
 * Movie Renamer
 * Copyright (C) 2015 Nicolas Magré
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

import fr.free.movierenamer.info.ImageInfo.ImageSize;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.ui.bean.IImage;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.AbstractWorker;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.swing.Icon;

/**
 * Class ImageThreadedWorker
 *
 * @author Nicolas Magré
 */
public class ImageThreadedWorker<T extends IImage> extends AbstractWorker<Void, Void> {

  private final int MAX_THREAD = 5;
  private final ImageListModel<T> model;
  private final WorkerId wid;
  private final ImageSize size;
  private final Dimension resize;
  private final Icon defaultImage;
  private final boolean downloadImage;
  private final ExecutorService executor;

  public ImageThreadedWorker(WorkerId wid, ImageListModel<T> model, Dimension resize, Icon defaultImage, boolean downloadImage) {
    this(wid, model, ImageSize.small, resize, defaultImage, downloadImage);
  }

  public ImageThreadedWorker(WorkerId wid, ImageListModel<T> model, ImageSize size, Dimension resize, Icon defaultImage, boolean downloadImage) {
    super();
    this.wid = wid;
    this.model = model;
    this.size = size;
    this.resize = resize;
    this.defaultImage = defaultImage;
    this.downloadImage = downloadImage;
    executor = Executors.newFixedThreadPool(MAX_THREAD);
  }

  @Override
  protected Void executeInBackground() throws Exception {// TODO send event worker done + progress

    List<Future<?>> results = new ArrayList<>();
    CompletionService<Void> pool = new ExecutorCompletionService<>(executor);
    int nbthread = model.getSize();
    for (T image : model.getAll()) {
      List<T> test = new ArrayList<>();
      test.add(image);
      results.add(executor.submit(new ImageWorker<>(wid, test, model, size, resize, defaultImage, downloadImage)));
    }

    // Wait for all thread
    for (int i = 0; i < nbthread; i++) {
      try {
        pool.take();
      } catch (Exception ex) {

      }

      setProgress(((i + 1) * 100) / nbthread);
    }

    return null;
  }

  @Override
  protected void workerDone() throws Exception {

  }

  @Override
  protected void workerCanceled() {
    executor.shutdownNow();
  }

  @Override
  public String getDisplayName() {

    String type = "???";
    switch (wid) {
      case IMAGE_INFO_ACTOR:
        type = UIUtils.i18n.getLanguage("main.statusTb.actor", false);
        break;
      case IMAGE_INFO_DIRECTOR:
        type = UIUtils.i18n.getLanguage("main.statusTb.directorwriter", false);
        break;
      case IMAGE_INFO_TRAILER:
        type = UIUtils.i18n.getLanguage("main.statusTb.trailer", false);
        break;
      case IMAGE_SEARCH_RESULT:
        type = UIUtils.i18n.getLanguage("main.statusTb.search", false);
        break;
    }

    return UIUtils.i18n.getLanguage("main.image", false) + " " + type;
  }

  @Override
  public WorkerId getWorkerId() {
    return wid;
  }

}
