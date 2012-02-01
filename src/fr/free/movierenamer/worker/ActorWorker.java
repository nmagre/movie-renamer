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
 *
 * @author duffy
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
        String director = "";
        String writer = "";
        setProgress(0);
        for (int i = 0; i < actors.size(); i++) {            
            if (actors.get(i).getJob().equals("Director")) {
                director += " | " + actors.get(i).getName();
            } else if (actors.get(i).getJob().equals("Writer")) {
                writer += " | " + actors.get(i).getName();
            } else if (actors.get(i).getJob().equals("Actor")) {
                boolean added = false;
                if (!actors.get(i).getThumb().equals(Utils.EMPTY)) {
                    try {
                        URL url = new URL(actors.get(i).getThumb().replace("SY214_SX314", "SY60_SX90").replace(".png", ".jpg"));
                        Image image = setting.cache.getImage(url, Cache.actor);
                        if (image == null) {
                            setting.cache.add(url.openStream(), url.toString(), Cache.actor);
                            image = setting.cache.getImage(url, Cache.actor);
                        }
                        String desc = "<html><h1>" + actors.get(i).getName() + "</h1>";
                        desc += "<img src=\"file:" + setting.cache.get(url, Cache.actor).getAbsolutePath() + "\"><br>";
                        for (int j = 0; j < actors.get(i).getRoles().size(); j++) {
                            desc += "<br>" + actors.get(i).getRoles().get(j);
                        }
                        desc += "</html>";
                        moviePnl.addActorToList(actors.get(i).getName(), image, desc);
                        added = true;
                    } catch (IOException ex) {
                    }
                }
                if (!added) {
                    Image image = Utils.getImageFromJAR("/image/dialog-cancel-2.png", getClass());
                    String desc = "<html><h1>" + actors.get(i).getName() + "</h1>";
                    for (int j = 0; j < actors.get(i).getRoles().size(); j++) {
                        desc += "<br>" + actors.get(i).getRoles().get(j);
                    }
                    desc += "</html>";
                    moviePnl.addActorToList(actors.get(i).getName(), image, desc);
                }
            }
            setProgress((i *100)/actors.size());
        }

        if (!writer.equals("")) {
            writer = writer.substring(3);
        }
        if (!director.equals("")) {
            director = director.substring(3);
        }
        setProgress(100);
        /*directorField.setText(director);
        writerField.setText(writer);*/
        return null;
    }
}
