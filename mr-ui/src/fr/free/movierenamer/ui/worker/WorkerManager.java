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
import com.alee.laf.list.WebList;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.IImage;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.bean.UIMediaImage;
import fr.free.movierenamer.ui.bean.UIPersonImage;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.swing.panel.GalleryPanel;
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
    start(getFileInfoWorker);
  }

  public static void listFiles(MovieRenamer mr, List<File> files, EventList<UIFile> eventList) {
    ListFilesWorker listFileWorker = new ListFilesWorker(mr, files, eventList);
    start(listFileWorker);
  }

  public static void search(MovieRenamer mr, UIFile media) {
    SearchMediaWorker searchWorker = new SearchMediaWorker(mr, media);
    start(searchWorker);
  }

  public static void searchInfo(MovieRenamer mr, UISearchResult searchResult) {
    SearchMediaInfoWorker infoWorker = new SearchMediaInfoWorker(mr, searchResult);
    start(infoWorker);
  }

  public static void searchImage(MovieRenamer mr, UISearchResult searchResult) {
    SearchMediaImagesWorker imagesWorker = new SearchMediaImagesWorker(mr, searchResult);
    start(imagesWorker);
  }

  public static void searchCasting(MovieRenamer mr, MediaInfo info, WebList castingList, ImageListModel<UIPersonImage> model) {
    SearchMediaCastingWorker castingWorker = new SearchMediaCastingWorker(mr, info, castingList, model);
    start(castingWorker);
  }

  public static <T extends IImage> void fetchImages(List<T> images, ImageListModel<T> model, Dimension imageSize, String defaultImage) {
    ImageWorker<T> imagesWorker = new ImageWorker<T>(images, model, imageSize, defaultImage);
    start(imagesWorker);
  }

  public static <T extends IImage> void fetchImages(List<T> images, ImageListModel<T> model, ImageInfo.ImageSize size, Dimension imageSize, String defaultImage) {
    ImageWorker<T> imagesWorker = new ImageWorker<T>(images, model, size, imageSize, defaultImage);
    start(imagesWorker);
  }

  public static AbstractImageWorker<UIMediaImage> fetchImages(List<UIMediaImage> images, GalleryPanel gallery, String defaultImage, Dimension imageSize, ImageInfo.ImageSize size) {
    AbstractImageWorker<UIMediaImage> GalleryWorker = new GalleryWorker(images, gallery, size, imageSize, defaultImage);
    start(GalleryWorker);
    return GalleryWorker;
  }

  public static void updateWorkerQueue() {
    synchronized (workerQueue) {
      boolean isEmpty = workerQueue.isEmpty();
      Iterator<AbstractWorker<?, ?>> iterator = workerQueue.iterator();
      while (iterator.hasNext()) {
        AbstractWorker<?, ?> worker = iterator.next();
        if (worker.isDone()) {
          iterator.remove();
          UIEvent.fireUIEvent(UIEvent.Event.WORKER_DONE, worker);
        }
      }

      if (workerQueue.isEmpty()) {
        if (!isEmpty) {
          UIEvent.fireUIEvent(UIEvent.Event.WORKER_ALL_DONE, MovieRenamer.class);
        }
      } else {
        UIEvent.fireUIEvent(UIEvent.Event.WORKER_RUNNING, MovieRenamer.class, workerQueue.peek());
      }
    }
  }

  private static void start(AbstractWorker<?, ?> worker) {
    synchronized (workerQueue) {
      workerQueue.add(worker);
      UIEvent.fireUIEvent(UIEvent.Event.WORKER_STARTED, worker);
    }
    worker.execute();
  }

  public static void stop() {
    synchronized (workerQueue) {
      boolean isEmpty = workerQueue.isEmpty();
      AbstractWorker<?, ?> worker = workerQueue.poll();
      while (worker != null) {
        if (!worker.isDone()) {
          worker.cancel(true);
        }
        worker = workerQueue.poll();
      }
      
      if (!isEmpty) {
        UIEvent.fireUIEvent(UIEvent.Event.WORKER_ALL_DONE, MovieRenamer.class);
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
            worker.cancel(true);
            UIEvent.fireUIEvent(UIEvent.Event.WORKER_DONE, rworker);
          }
          workerQueue.remove(rworker);
        }
      }
    }
  }
}
