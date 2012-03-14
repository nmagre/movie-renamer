/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.worker;

import fr.free.movierenamer.utils.Utils;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.movie.MoviePerson;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.ui.MoviePanel;
import java.util.List;
import javax.swing.SwingWorker;

/**
 * Class ActorWorker
 * @author Magré Nicolas
 */
public class ActorWorker extends SwingWorker<Void, Void> {

  private List<MoviePerson> actors;
  private Settings setting;
  private MoviePanel moviePnl;

  public ActorWorker(List<MoviePerson> actors, MoviePanel moviePnl, Settings setting) {
    this.actors = actors;
    this.moviePnl = moviePnl;
    this.setting = setting;
  }

  @Override
  protected Void doInBackground() {
    setProgress(0);
    for (int i = 0; i < actors.size(); i++) {
      Image image = null;
      URL url = null;
      StringBuilder desc = new StringBuilder("<html><h1>" + actors.get(i).getName() + "</h1>");
      if (!actors.get(i).getThumb().equals(Utils.EMPTY) && setting.actorImage && setting.movieInfoPanel) {
        try {
          url = new URL(actors.get(i).getThumb().replace(".png", ".jpg"));
          image = setting.cache.getImage(url, Cache.actor);
          if (image == null) {
            setting.cache.add(url.openStream(), url.toString(), Cache.actor);
            image = setting.cache.getImage(url, Cache.actor);
          }
        } catch (IOException ex) {
          ex.printStackTrace();
        }

        if (image == null) image = Utils.getImageFromJAR("/image/unknown.png", getClass());
        
        if (url != null) desc.append("<img src=\"file:").append(setting.cache.get(url, Cache.actor).getAbsolutePath()).append("\"><br>");
        for (int j = 0; j < actors.get(i).getRoles().size(); j++) {
          desc.append("<br>").append(actors.get(i).getRoles().get(j));
        }
        desc.append("</html>");
        moviePnl.addActorToList(actors.get(i).getName(), image, desc.toString());
      }
      else {
        for (int j = 0; j < actors.get(i).getRoles().size(); j++) {
          desc.append("<br>").append(actors.get(i).getRoles().get(j));
        }
        desc.append("</html>");
        moviePnl.addActorToList(actors.get(i).getName(), null, desc.toString());
      }
      setProgress((i * 100) / actors.size());
    }
    setProgress(100);
    return null;
  }
}
