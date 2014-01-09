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
package fr.free.movierenamer.ui.worker;

import fr.free.movierenamer.ui.MovieRenamer;
import java.util.List;

/**
 * Class ControlWorker, worker with pause/resume control
 *
 * @author Nicolas Magré
 */
public abstract class ControlWorker<T> extends Worker<T> {

  protected boolean paused = false;
  protected boolean usePause = false;

  public ControlWorker(MovieRenamer mr) {
    super(mr);
  }

  protected final void publishPause(String... chunks) {
    usePause = true;
    publish(chunks);
    pause();
  }

  @Override
  public final void process(List<String> v) {
    if (usePause) {
      processPause(v);
      resume();
    } else {
      super.process(v);
    }
    usePause = false;
  }

  protected abstract void processPause(List<String> v);

  protected void pause() {
    paused = true;
    while (paused && !isCancelled()) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException ex) {
      }
    }
  }

  protected void resume() {
    paused = false;
  }

}
