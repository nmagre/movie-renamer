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

import fr.free.movierenamer.ui.settings.UISettings;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

/**
 * Class RenameThread
 *
 * @author Nicolas Magré
 */
public final class RenameThread extends Thread {

  private final BlockingQueue<RenamerWorker> sharedQueue;
  private boolean stop = false;

  public RenameThread(BlockingQueue<RenamerWorker> sharedQueue) {
    this.sharedQueue = sharedQueue;
  }

  @Override
  public void run() {

    RenamerWorker worker;
    while (!stop) {
      try {
        worker = sharedQueue.take();
        worker.execute();

        // Wait for renamer worker
        if (UISettings.getInstance().isMoveFileOneByOne()) {
          try {
            worker.get();
          } catch (ExecutionException ex) {
            UISettings.LOGGER.log(Level.SEVERE, null, ex);
          }
        }

      } catch (InterruptedException ex) {
        stop = true;
      }
    }
  }

}
