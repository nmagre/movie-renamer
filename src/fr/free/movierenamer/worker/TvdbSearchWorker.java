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

import fr.free.movierenamer.ui.res.SearchResult;
import fr.free.movierenamer.utils.Settings;
import java.util.ArrayList;
import javax.swing.SwingWorker;

/**
 *
 * @author duffy
 */
public class TvdbSearchWorker extends SwingWorker<ArrayList<SearchResult>, Void> {
  //A faire
  
  public TvdbSearchWorker(String searchTitle, Settings setting){
    
  }

  @Override
  protected ArrayList<SearchResult> doInBackground() throws Exception {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
