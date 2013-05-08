/*
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
import com.alee.laf.button.WebButton;
import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.SearchResult;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.IImage;
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
import java.util.ArrayList;
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

  private static final Queue<Worker> workerQueue = new LinkedList<Worker>();

  private WorkerManager() {
    throw new UnsupportedOperationException();
  }

  public static void getFileInfo(MovieRenamer mr, UIFile file) {
    GetFileInfoWorker getFileInfoWorker = new GetFileInfoWorker(mr, file);
    start(getFileInfoWorker, mr.getClass(), file);
  }

  public static void listFiles(MovieRenamer mr, List<File> files, EventList<UIFile> eventList) {
    ListFilesWorker listFileWorker = new ListFilesWorker(mr, files, eventList);
    start(listFileWorker, mr.getClass(), files);
  }

  public static void search(MovieRenamer mr, UIFile media, MediaScrapper<? extends SearchResult, ? extends MediaInfo> scrapper, WebList list, WebButton button, WebTextField field, DefaultListModel model) {
    SearchMediaWorker searchWorker = new SearchMediaWorker(mr, media, scrapper, list, button, field, model);
    start(searchWorker, mr.getClass(), media);
  }

  public static void fetchInfo(MovieRenamer mr, UIFile file, UISearchResult searchResult) {
    SearchMediaInfoWorker infoWorker = new SearchMediaInfoWorker(mr, file, searchResult);
    start(infoWorker, mr.getClass(), searchResult);
  }

  public static void fetchImages(MovieRenamer mr, UISearchResult searchResult) {
    SearchMediaImagesWorker imagesWorker = new SearchMediaImagesWorker(mr, searchResult);
    start(imagesWorker, mr.getClass(), searchResult);
  }

  public static <T extends IImage> void fetchImages(Class<?> clazz, List<T> images, DefaultListModel model, Dimension imageSize, String defaultImage) {
    ImageWorker<T> imagesWorker = new ImageWorker<T>(images, model, imageSize, defaultImage);
    start(imagesWorker, clazz, "");
  }

  public static <T extends IImage> void fetchImages(Class<?> clazz, List<T> images, DefaultListModel model, ImageInfo.ImageSize size, Dimension imageSize, String defaultImage) {
    ImageWorker<T> imagesWorker = new ImageWorker<T>(images, model, size, imageSize, defaultImage);
    start(imagesWorker, clazz, "");
  }

  public static AbstractImageWorker<UIMediaImage> fetchImages(Class<?> clazz, List<UIMediaImage> images, GalleryPanel gallery, String defaultImage, Dimension imageSize, ImageInfo.ImageSize size) {
    AbstractImageWorker<UIMediaImage> GalleryWorker = new GalleryWorker(images, gallery, size, imageSize, defaultImage); // FIXME dimension
    start(GalleryWorker, clazz, "");
    return GalleryWorker;
  }

  public static void fetchCasting(Class<?> clazz, MovieRenamer mr, MediaInfo info, WebList castingList, DefaultListModel model) {
    SearchMediaCastingWorker castingWorker = new SearchMediaCastingWorker(mr, info, castingList, model);
    start(castingWorker, clazz, "");
  }

  private static void start(AbstractWorker<?, ?> worker, Class<?> clazz, Object obj) {
    UISettings.LOGGER.log(Level.INFO, String.format("%s %s", worker.getName(), obj.toString()));
    worker.execute();
    workerQueue.add(new Worker(worker, clazz));
  }

  public static void stop() {
    synchronized (workerQueue) {
      Worker worker = workerQueue.poll();
      while (worker != null) {
        AbstractWorker<?, ?> sworker = worker.getWorker();
        if (!sworker.isDone()) {
          UISettings.LOGGER.log(Level.INFO, sworker.getClass().getSimpleName());
          sworker.cancel(true);
        }
        worker = workerQueue.poll();
      }
    }
  }

  public static void stop(Class<?> clazz) {
    synchronized (workerQueue) {
      List<Worker> workers = new ArrayList<Worker>();
      Iterator<Worker> iterator = workerQueue.iterator();
      while (iterator.hasNext()) {
        Worker worker = iterator.next();
        if (worker.getClazz().equals(clazz)) {
          AbstractWorker<?, ?> sworker = worker.getWorker();
          if (!sworker.isDone()) {
            UISettings.LOGGER.log(Level.INFO, sworker.getName());
            sworker.cancel(true);
          }
          workers.add(worker);
        }
      }
      workerQueue.removeAll(workers);
    }
  }

  public static void stop(AbstractImageWorker<?> worker) {
    synchronized (workerQueue) {
      Iterator<Worker> iterator = workerQueue.iterator();
      while (iterator.hasNext()) {
        Worker qworker = iterator.next();
        AbstractWorker<?, ?> sworker = qworker.getWorker();
        if (sworker == worker) {
          if (!worker.isDone()) {
            UISettings.LOGGER.log(Level.INFO, worker.getName());
            worker.cancel(true);
          }
        }
        workerQueue.remove(qworker);
      }
    }
  }

  private static class Worker {

    private AbstractWorker<?, ?> worker;
    private Class<?> clazz;

    public Worker(AbstractWorker<?, ?> worker, Class<?> clazz) {
      this.worker = worker;
      this.clazz = clazz;
    }

    public AbstractWorker<?, ?> getWorker() {
      return worker;
    }

    public Class<?> getClazz() {
      return clazz;
    }
  }
}
