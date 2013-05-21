/*
 * Movie Renamer
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
package fr.free.movierenamer.ui.worker;

import ca.odell.glazedlists.EventList;
import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.IImage;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.bean.UIMediaImage;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.panel.GalleryPanel;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.worker.impl.GalleryWorker;
import fr.free.movierenamer.ui.worker.impl.GetFileInfoWorker;
import fr.free.movierenamer.ui.worker.impl.ImageWorker;
import fr.free.movierenamer.ui.worker.impl.ListFilesWorker;
import fr.free.movierenamer.ui.worker.impl.SearchMediaCastingWorker;
import fr.free.movierenamer.ui.worker.impl.SearchMediaImagesWorker;
import fr.free.movierenamer.ui.worker.impl.SearchMediaInfoWorker;
import fr.free.movierenamer.ui.worker.impl.SearchMediaWorker;
import java.awt.Dimension;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

/**
 * Class WorkerManager
 *
 * @author Nicolas Magré
 */
public final class WorkerManager {

  private static final Queue<AbstractWorker<?, ?>> workerQueue = new LinkedList<AbstractWorker<?, ?>>();

  private WorkerManager() {
    throw new UnsupportedOperationException();
  }

  public static void getFileInfo(MovieRenamer mr, UIFile file) {
    GetFileInfoWorker getFileInfoWorker = new GetFileInfoWorker(mr, file);
    start(getFileInfoWorker, file);
  }

  public static void listFiles(MovieRenamer mr, List<File> files, EventList<UIFile> eventList) {
    ListFilesWorker listFileWorker = new ListFilesWorker(mr, files, eventList);
    start(listFileWorker, files);
  }

  public static void search(MovieRenamer mr, UIFile media) {
    SearchMediaWorker searchWorker = new SearchMediaWorker(mr, media);
    start(searchWorker, media);
  }

  public static void searchInfo(MovieRenamer mr, UISearchResult searchResult) {
    SearchMediaInfoWorker infoWorker = new SearchMediaInfoWorker(mr, searchResult);
    start(infoWorker, searchResult);
  }

  public static void searchImage(MovieRenamer mr, UISearchResult searchResult) {
    SearchMediaImagesWorker imagesWorker = new SearchMediaImagesWorker(mr, searchResult);
    start(imagesWorker, searchResult);
  }

  public static void searchCasting(MovieRenamer mr, MediaInfo info, WebList castingList, DefaultListModel model) {
    SearchMediaCastingWorker castingWorker = new SearchMediaCastingWorker(mr, info, castingList, model);
    start(castingWorker, "");
  }

  public static <T extends IImage> void fetchImages(List<T> images, DefaultListModel model, Dimension imageSize, String defaultImage) {
    ImageWorker<T> imagesWorker = new ImageWorker<T>(images, model, imageSize, defaultImage);
    start(imagesWorker, "[" + images.size() + "]");
  }

  public static <T extends IImage> void fetchImages(List<T> images, DefaultListModel model, ImageInfo.ImageSize size, Dimension imageSize, String defaultImage) {
    ImageWorker<T> imagesWorker = new ImageWorker<T>(images, model, size, imageSize, defaultImage);
    start(imagesWorker, "[" + images.size() + "]");
  }

  public static AbstractImageWorker<UIMediaImage> fetchImages(List<UIMediaImage> images, GalleryPanel gallery, String defaultImage, Dimension imageSize, ImageInfo.ImageSize size) {
    AbstractImageWorker<UIMediaImage> GalleryWorker = new GalleryWorker(images, gallery, size, imageSize, defaultImage);
    start(GalleryWorker, gallery + " [" + images.size() + "]");
    return GalleryWorker;
  }

  public static void updateWorkerQueue() {
    synchronized (workerQueue) {
      Iterator<AbstractWorker<?, ?>> iterator = workerQueue.iterator();
      while (iterator.hasNext()) {
        AbstractWorker<?, ?> worker = iterator.next();
        if (worker.isDone()) {
          iterator.remove();
        }
      }

      if (workerQueue.isEmpty()) {
        UIEvent.fireUIEvent(UIEvent.Event.WORKER_DONE, MovieRenamer.class);
      } else {
        UIEvent.fireUIEvent(UIEvent.Event.WORKER_RUNNING, MovieRenamer.class, workerQueue.peek().getName());
      }
    }
  }

  private static void start(AbstractWorker<?, ?> worker, Object obj) {
    UISettings.LOGGER.log(Level.INFO, String.format("%s %s", worker.getName(), obj.toString()));
    synchronized (workerQueue) {
      workerQueue.add(worker);
      UIEvent.fireUIEvent(UIEvent.Event.WORKER_STARTED, MovieRenamer.class, workerQueue.peek().getName());
    }
    worker.execute();
  }

  public static void stop() {
    synchronized (workerQueue) {
      AbstractWorker<?, ?> worker = workerQueue.poll();
      while (worker != null) {
        if (!worker.isDone()) {
          UISettings.LOGGER.log(Level.INFO, worker.getName());
          worker.cancel(true);
        }
        worker = workerQueue.poll();
      }
    }
  }

  public static void stop(AbstractImageWorker<?> worker) {
    synchronized (workerQueue) {
      Iterator<AbstractWorker<?, ?>> iterator = workerQueue.iterator();
      while (iterator.hasNext()) {
        AbstractWorker<?, ?> rworker = iterator.next();
        if (rworker == worker) {
          if (!worker.isDone()) {
            UISettings.LOGGER.log(Level.INFO, worker.getName());
            worker.cancel(true);
          }
        }
        workerQueue.remove(rworker);
      }
    }
  }
}
