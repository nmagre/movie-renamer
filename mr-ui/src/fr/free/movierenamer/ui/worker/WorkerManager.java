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
import ca.odell.glazedlists.swing.EventListModel;
import com.alee.laf.button.WebButton;
import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.SearchResult;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.list.IIconList;
import fr.free.movierenamer.ui.list.UIFile;
import fr.free.movierenamer.ui.list.UISearchResult;
import fr.free.movierenamer.ui.panel.GalleryPanel;
import fr.free.movierenamer.ui.settings.UISettings;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import javax.swing.SwingWorker;

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

  public static void listFiles(MovieRenamer mr, List<File> files, WebList list, EventList<UIFile> eventList, EventListModel<UIFile> model) {
    ListFilesWorker listFileWorker = new ListFilesWorker(mr, files, list, eventList, model);
    start(listFileWorker, mr.getClass());
  }

  public static void search(MovieRenamer mr, UIFile media, MediaScrapper<? extends SearchResult, ? extends MediaInfo> scrapper, WebList list, WebButton button, WebTextField field, DefaultListModel model) {
    SearchMediaWorker searchWorker = new SearchMediaWorker(mr, media, scrapper, list, button, field, model);
    start(searchWorker, mr.getClass());
  }

  public static void fetchInfo(MovieRenamer mr, UIFile file, UISearchResult searchResult) {
    SearchMediaInfoWorker infoWorker = new SearchMediaInfoWorker(mr, file, searchResult);
    start(infoWorker, mr.getClass());
  }

  public static void fetchImages(MovieRenamer mr, UISearchResult searchResult) {
    SearchMediaImagesWorker imagesWorker = new SearchMediaImagesWorker(mr, searchResult);
    start(imagesWorker, mr.getClass());
  }

  public static <T extends IIconList> void fetchImages(Class<?> clazz, List<T> images, DefaultListModel model, Dimension imageSize, String defaultImage) {
    ImageWorker<T> imagesWorker = new ImageWorker<T>(images, model, imageSize, defaultImage);
    start(imagesWorker, clazz);
  }

  public static <T extends IIconList> void fetchImages(Class<?> clazz, List<T> images, GalleryPanel gallery, String defaultImage, ImageInfo.ImageSize size) {
    ImageWorker<T> imagesWorker = new ImageWorker<T>(images, gallery, defaultImage, size);
    start(imagesWorker, clazz);
  }

//  public static <T extends IIconList> void fetchImage(Class<?> clazz, T image, Dimension imageSize, String defaultImage, ImageInfo.ImageSize size, PropertyChangeListener propertyChange) {
//    ImageWorker<T> imageWorker = new ImageWorker<T>(image, imageSize, defaultImage, size);
//    if(propertyChange != null) {
//      imageWorker.addPropertyChangeListener(propertyChange);
//    }
//    start(imageWorker, clazz);
//  }
  public static void fetchCasting(Class<?> clazz, MovieRenamer mr, MediaInfo info, WebList castingList) {
    SearchMediaCastingWorker castingWorker = new SearchMediaCastingWorker(mr, info, castingList);
    start(castingWorker, clazz);
  }

  private static void start(SwingWorker<?, ?> worker, Class<?> clazz) {
    UISettings.LOGGER.log(Level.INFO, worker.getClass().getSimpleName());
    worker.execute();
    workerQueue.add(new Worker(worker, clazz));
  }

  public static void stop() {
    synchronized (workerQueue) {
      Worker worker = workerQueue.poll();
      while (worker != null) {
        SwingWorker<?, ?> sworker = worker.getWorker();
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
          SwingWorker<?, ?> sworker = worker.getWorker();
          if (!sworker.isDone()) {
            UISettings.LOGGER.log(Level.INFO, sworker.getClass().getSimpleName());
            sworker.cancel(true);
          }
          workers.add(worker);
        }
      }
      workerQueue.removeAll(workers);
    }
  }

  private static class Worker {

    private SwingWorker<?, ?> worker;
    private Class<?> clazz;

    public Worker(SwingWorker<?, ?> worker, Class<?> clazz) {
      this.worker = worker;
      this.clazz = clazz;
    }

    public SwingWorker<?, ?> getWorker() {
      return worker;
    }

    public Class<?> getClazz() {
      return clazz;
    }
  }
}
