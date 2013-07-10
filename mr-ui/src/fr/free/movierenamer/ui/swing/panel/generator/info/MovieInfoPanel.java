/*
 * Movie Renamer
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
package fr.free.movierenamer.ui.swing.panel.generator.info;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.ui.bean.UIEditor;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.utils.ClassUtils;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.swing.Icon;

/**
 * Class MovieInfoPanel
 *
 * @author Nicolas Magré
 */
public class MovieInfoPanel extends InfoEditorPanel<MovieInfo> {
  
  private final int smallFieldSize = 4;
  private final int fieldSize = 10;
  private final String genre = "genre";
  private final String country = "country";
  private final String studio = "studio";
  private final String tag = "tag";
  private final List<MovieProperty> excludeProperty = Arrays.asList(new MovieProperty[]{
            MovieProperty.overview,
            MovieProperty.posterPath,
            MovieProperty.rating,
            MovieProperty.budget,
            MovieProperty.votes,
            MovieProperty.releasedDate,
            MovieProperty.runtime,
            MovieProperty.certification,
            MovieProperty.certificationCode
          });// Will be added manually
  private MovieInfo info;
  
  private enum InlineProperty {
    
    DATE(MovieProperty.releasedDate, MovieProperty.runtime),
    CERT(MovieProperty.certification, MovieProperty.certificationCode),
    RATE(MovieProperty.rating, MovieProperty.votes, MovieProperty.budget);
    private MovieProperty[] properties;
    
    private InlineProperty(MovieProperty... properties) {
      this.properties = properties;
    }
    
    public List<MovieProperty> getProperties() {
      return Arrays.asList(properties);
    }
    
    public static int getMaxSize() {
      int max = 0;
      for (InlineProperty property : InlineProperty.values()) {
        max = Math.max(max, property.getProperties().size());
      }
      return max;
    }
  }

  /**
   * Creates new form MovieInfoPanel
   */
  public MovieInfoPanel() {
    initComponents();
    
    int maxGridWith = InlineProperty.getMaxSize() * 3;// 3 -> Label + field + edit/cancel button

    for (MovieProperty property : MovieProperty.values()) {
      if (excludeProperty.contains(property)) {
        continue;
      }
      
      UIEditor editor = new UIEditor(new WebTextField(fieldSize), false);
      createEditableField(property.name(), editor, maxGridWith);
      map.put(property, editor);
    }
    
    UIEditor genres = new UIEditor(new WebTextField(fieldSize), true);
    createEditableField(genre, genres, maxGridWith);
    map.put(genre, genres);
    
    UIEditor studios = new UIEditor(new WebTextField(fieldSize), true);
    createEditableField(studio, studios, maxGridWith);
    map.put(studio, studios);
    
    UIEditor tags = new UIEditor(new WebTextField(fieldSize), true);
    createEditableField(tag, tags, maxGridWith);
    map.put(tag, tags);
    
    for (InlineProperty property : InlineProperty.values()) {
      
      List<MovieProperty> properties = property.getProperties();
      int size = properties.size();
      for (int i = 0; i < size; i++) {
        UIEditor editor = new UIEditor(new WebTextField(smallFieldSize), false);
        createEditableField(properties.get(i).name(), editor, maxGridWith, size, true, (i + 1 >= size));
        map.put(properties.get(i), editor);
      }
    }
    
    WebTextArea textArea = new WebTextArea();
    textArea.setColumns(5);
    textArea.setRows(6);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    
    UIEditor overview = new UIEditor(textArea, false);
    createEditableField(MovieProperty.overview.name(), overview, maxGridWith);
    map.put(MovieProperty.overview, overview);

    // Add dummy Panel to avoid centering
    add(new WebPanel(), getDummyPanelConstraint());
  }
  
  @Override
  public void clear() {
    super.clear();
    info = null;
  }
  
  @Override
  public MovieInfo getInfo() {
    return info;
  }
  
  @Override
  public void setInfo(MovieInfo info) {
    try {
      this.info = info;
      for (MovieProperty property : MovieProperty.values()) {
        if (map.containsKey(property)) {
          map.get(property).setValue(info.get(property));
        }
      }
      map.get(genre).setValue(info.getGenres());
      map.get(studio).setValue(info.getStudios());
      map.get(tag).setValue(info.getTags());
    } catch (Exception ex) {
      UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace(ex));
    }
  }

  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    setLayout(new java.awt.GridBagLayout());
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables

  @Override
  public Icon getIcon() {
    return ImageUtils.MOVIE_16;
  }
  
  @Override
  public String getPanelName() {// TODO
    return "movie info";
  }
}
