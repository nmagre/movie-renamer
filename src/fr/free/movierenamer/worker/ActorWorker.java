/*
 * Movie Renamer
 * Copyright (C) 2012 Nicolas Magré
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
package fr.free.movierenamer.worker;

import fr.free.movierenamer.media.MediaPerson;
import fr.free.movierenamer.ui.res.IMediaPanel;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

/**
 * Class ActorWorker , Download and add actor images to moviePanel
 * 
 * @author Magré Nicolas
 */
public class ActorWorker extends Worker<Void> {

  private final List<MediaPerson> actors;
  private final IMediaPanel mediaPanel;

  /**
   * Constructor arguments
   *
   * @param actors List of actor
   * @param mediaPanel Movie Renamer media panel
   */
  public ActorWorker(List<MediaPerson> actors, IMediaPanel mediaPanel) {
    this.actors = actors;
    this.mediaPanel = mediaPanel;
  }

  @Override
  protected Void executeInBackground() {
    setProgress(0);
    for (int i = 0; i < actors.size(); i++) {
      Image image = null;
      URL url = null;
      StringBuilder desc = new StringBuilder("<html><h1>" + actors.get(i).getName() + "</h1>");

      if (!actors.get(i).getThumb().equals(Utils.EMPTY) && config.actorImage && config.movieInfoPanel) {

        try {
          url = new URL(actors.get(i).getThumb().replace(".png", ".jpg"));
          image = Cache.getInstance().getImage(url, Cache.CacheType.ACTOR);
          if (image == null) {
            Cache.getInstance().add(url, Cache.CacheType.ACTOR);
            image = Cache.getInstance().getImage(url, Cache.CacheType.ACTOR);
          }
        } catch (IOException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        }

        if (image == null) {
          image = Utils.getImageFromJAR("/image/unknown.png", getClass());
        }

        if (url != null) {
          desc.append("<img src=\"file:").append(Cache.getInstance().get(url, Cache.CacheType.ACTOR).getAbsolutePath()).append("\"><br>");
        }

        for (int j = 0; j < actors.get(i).getRoles().size(); j++) {
          desc.append("<br>").append(actors.get(i).getRoles().get(j));
        }
        desc.append("</html>");

        mediaPanel.addActorToList(actors.get(i).getName(), image, desc.toString());
      } else {

        for (int j = 0; j < actors.get(i).getRoles().size(); j++) {
          desc.append("<br>").append(actors.get(i).getRoles().get(j));
        }
        desc.append("</html>");
        mediaPanel.addActorToList(actors.get(i).getName(), null, desc.toString());
      }

      setProgress((i * 100) / actors.size());
    }
    setProgress(100);
    return null;
  }
}
