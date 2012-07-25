/*
 * movie-renamer
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

import fr.free.movierenamer.parser.MrParser;
import fr.free.movierenamer.parser.XMLParser;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Utils;
import java.awt.Dimension;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Class MediaSearchWorker
 * 
 * @author QUÉMÉNEUR Simon
 * @author Nicolas Magré
 */
public abstract class MediaSearchWorker extends HttpWorker<ArrayList<SearchResult>> {
  protected final String searchTitle;

  public MediaSearchWorker(PropertyChangeSupport errorSupport, String searchTitle) {
    super(errorSupport);
    
    this.searchTitle = searchTitle;
  }

  @Override
  protected final ArrayList<SearchResult> proccessFile(File xmlFile) throws Exception {
    ArrayList<SearchResult> results;
    XMLParser<ArrayList<SearchResult>> xmp = new XMLParser<ArrayList<SearchResult>>(xmlFile.getAbsolutePath());
    MrParser<ArrayList<SearchResult>> parser = getParser();
    parser.setOriginalFile(xmlFile);
    xmp.setParser(parser);
    results = xmp.parseXml();

    // search images
    int i = 0;
    for (SearchResult imsres : results) {
      String thumb = imsres.getThumb();
      if (thumb != null) {
        Icon icon = Utils.getSearchThumb(thumb, Cache.getInstance(), new Dimension(45, 70));
        if (icon != null) {
          imsres.setIcon(icon);
        }
      }
      if (imsres.getIcon() == null) {
        // FIXME A mettre ailleur pour le cli, même si utilisé un swingworker n'est l'idéal
        imsres.setIcon(new ImageIcon(Utils.getImageFromJAR("/image/nothumb.png", getClass())));
      }
      setProgress((30 + (++i * 50)) / results.size());
    }

    return results;
  }

  @Override
  protected abstract MrParser<ArrayList<SearchResult>> getParser() throws Exception;
  
  @Override
  protected abstract String getUri() throws Exception;
}
