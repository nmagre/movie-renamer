/*
 * Movie Renamer
 * Copyright (C) 2012-2014 Nicolas Magré
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
import fr.free.movierenamer.ui.bean.UIRename;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.swing.dialog.GalleryDialog;
import fr.free.movierenamer.ui.swing.panel.TaskPanel;
import fr.free.movierenamer.ui.worker.impl.GalleryWorker;
import fr.free.movierenamer.ui.worker.impl.ImageWorker;
import fr.free.movierenamer.ui.worker.impl.ListFilesWorker;
import fr.free.movierenamer.ui.worker.impl.RenameThread;
import fr.free.movierenamer.ui.worker.impl.RenamerWorker;
import fr.free.movierenamer.ui.worker.impl.SearchMediaCastingWorker;
import fr.free.movierenamer.ui.worker.impl.SearchMediaImagesWorker;
import fr.free.movierenamer.ui.worker.impl.SearchMediaInfoWorker;
import fr.free.movierenamer.ui.worker.impl.SearchMediaTrailerWorker;
import fr.free.movierenamer.ui.worker.impl.SearchMediaWorker;
import java.awt.Dimension;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.Icon;

/**
 * Class WorkerManager
 *
 * @author Nicolas Magré
 */
public final class WorkerManager {

  private static final Queue<AbstractWorker<?, ?>> workerQueue = new ConcurrentLinkedQueue<>();
  private static final BlockingQueue<RenamerWorker> renameQueue = new LinkedBlockingQueue<>();
  private static final Thread renameThread = new RenameThread(renameQueue);

  public final static void listFiles(MovieRenamer mr, List<File> files, EventList<UIFile> eventList) {
    ListFilesWorker listFileWorker = new ListFilesWorker(mr, files, eventList);
    start(listFileWorker);
  }

  public final static void search(MovieRenamer mr, UIFile media) {
    SearchMediaWorker searchWorker = new SearchMediaWorker(mr, media);
    start(searchWorker);
  }

  public final static void searchInfo(MovieRenamer mr, UISearchResult searchResult) {
    SearchMediaInfoWorker infoWorker = new SearchMediaInfoWorker(mr, searchResult);
    start(infoWorker);
  }

  public final static void searchImage(MovieRenamer mr, UISearchResult searchResult) {
    SearchMediaImagesWorker imagesWorker = new SearchMediaImagesWorker(mr, searchResult);
    start(imagesWorker);
  }

  public final static void searchCasting(MovieRenamer mr, MediaInfo info, WebList castingList, ImageListModel<UIPersonImage> model) {// not used
    SearchMediaCastingWorker castingWorker = new SearchMediaCastingWorker(mr, info, castingList, model);
    start(castingWorker);
  }

  public final static void searchTrailer(MovieRenamer mr, UISearchResult searchResult) {
    SearchMediaTrailerWorker trailerWorker = new SearchMediaTrailerWorker(mr, searchResult);
    start(trailerWorker);
  }

  public final static <T extends IImage> void fetchImages(List<T> images, ImageListModel<T> model, Dimension resize, Icon defaultImage, boolean downloadImage) {
    ImageWorker<T> imagesWorker = new ImageWorker<>(images, model, resize, defaultImage, downloadImage);
    start(imagesWorker);
  }

  public final static <T extends IImage> void fetchImages(List<T> images, ImageListModel<T> model, ImageInfo.ImageSize size, Dimension resize, Icon defaultImage, boolean downloadImage) {
    ImageWorker<T> imagesWorker = new ImageWorker<>(images, model, size, resize, defaultImage, downloadImage);
    start(imagesWorker);
  }

  public final static void fetchGalleryImages(List<UIMediaImage> images, GalleryDialog gallery, Dimension resize, Icon defaultImage, ImageInfo.ImageSize size) {
    AbstractImageWorker<UIMediaImage> GalleryWorker = new GalleryWorker(images, gallery, size, resize, defaultImage);
    start(GalleryWorker);
  }

  public static void rename(MovieRenamer mr, UIFile file, TaskPanel taskPanel, UIRename uirename) throws InterruptedException {
    RenamerWorker renameworker = new RenamerWorker(mr, file, taskPanel, uirename);
    renameQueue.put(renameworker);
  }

  public final static void startRenameThread() {
    if (!renameThread.isAlive()) {
      renameThread.start();
    }
  }

  public final static void stopRenameThread() {
    renameQueue.clear();
    if (renameThread.isAlive()) {
      renameThread.interrupt();
    }
  }

  /**
   * Update worker queue
   */
  public static synchronized void updateWorkerQueue() {
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

  /**
   * Start worker
   *
   * @param worker Worker to start
   */
  private static void start(AbstractWorker<?, ?> worker) {
    start(worker, true, UIEvent.Event.WORKER_STARTED);
  }

  /**
   * Start worker
   *
   * @param worker Worker to start
   */
  private static void start(AbstractWorker<?, ?> worker, boolean addToQueue, UIEvent.Event event) {
    synchronized (WorkerManager.class) {
      if (addToQueue) {
        workerQueue.add(worker);
      }
      UIEvent.fireUIEvent(event, worker);
    }
    worker.execute();
  }

  /**
   * Stop all running worker
   */
  public static void stop() {
    synchronized (WorkerManager.class) {
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

  /**
   * Stop all running workers except worker with class "clazz" and generic super
   * class "genSuperClazz"
   *
   * @param clazz Worker class to keep running
   * @param genSuperClazz Generic super class (object class)
   */
  public static void stopExcept(Class clazz, Class genSuperClazz) {
    synchronized (WorkerManager.class) {
      Iterator<AbstractWorker<?, ?>> iterator = workerQueue.iterator();
      while (iterator.hasNext()) {
        AbstractWorker<?, ?> rworker = iterator.next();
        if (rworker.getClass().equals(clazz)) {
          if (genSuperClazz != null && clazz.getGenericSuperclass() != null) {
            if (!genSuperClazz.equals(rworker.getClass().getEnclosingClass())) {
              if (!rworker.isDone()) {
                rworker.cancel(true);
              }
            }
          } else if (rworker.isDone()) {
            rworker.cancel(true);
          }
        }
      }
    }
    updateWorkerQueue();
  }

  private WorkerManager() {
    throw new UnsupportedOperationException();
  }

}
