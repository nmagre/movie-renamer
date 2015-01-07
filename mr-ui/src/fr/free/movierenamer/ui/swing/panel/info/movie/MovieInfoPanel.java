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
package fr.free.movierenamer.ui.swing.panel.info.movie;

import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.MediaInfo.InfoProperty;
import fr.free.movierenamer.info.MediaInfo.MediaProperty;
import fr.free.movierenamer.info.MediaInfo.MultipleInfoProperty;
import fr.free.movierenamer.ui.bean.UIEditor;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.info.MovieInfo.MovieMultipleProperty;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.IEventInfo;
import fr.free.movierenamer.ui.bean.UICountry;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.bean.UIMovieInfo;
import fr.free.movierenamer.ui.swing.panel.info.InfoEditorPanel;
import fr.free.movierenamer.ui.utils.FlagUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.ClassUtils;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.swing.DefaultListModel;
import javax.swing.Icon;

/**
 * Class MovieInfoPanel
 *
 * @author Nicolas Magré
 */
public class MovieInfoPanel extends InfoEditorPanel<UIMovieInfo> {

  private UIMovieInfo info;
  private final int smallFieldSize = 4;
  private final int fieldSize = 10;
  private final String i18nKey = "main.moviepnl.info.";
  private final DefaultListModel<UICountry> countryListModel;
  private final List<InfoProperty> excludeProperty = Arrays.asList(new InfoProperty[]{// Will be added manually
    MovieProperty.overview,
    MovieProperty.posterPath,
    MediaProperty.rating,
    MovieProperty.budget,
    MovieProperty.votes,
    MovieProperty.releasedDate,
    MovieProperty.runtime,
    MovieProperty.certification,
    MovieProperty.certificationCode,
    MovieMultipleProperty.countries
  });

  private enum InlineProperty {

    DATE(MovieProperty.releasedDate, MovieProperty.runtime),
    CERT(MovieProperty.certification, MovieProperty.certificationCode),
    RATE(MediaProperty.rating, MovieProperty.votes);
    private final InfoProperty[] properties;

    private InlineProperty(InfoProperty... properties) {
      this.properties = properties;
    }

    public List<InfoProperty> getProperties() {
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
   *
   * @param mr
   */
  @SuppressWarnings("unchecked")
  public MovieInfoPanel(MovieRenamer mr) {
    super(mr);
    initComponents();

    countryListModel = new DefaultListModel<>();

    int maxGridWith = InlineProperty.getMaxSize() * EDIT_WIDTH;

    for (MediaProperty property : MediaProperty.values()) {
      if (excludeProperty.contains(property)) {
        continue;
      }

      createField(property, maxGridWith);
    }

    for (MovieProperty property : MovieProperty.values()) {
      if (excludeProperty.contains(property)) {
        continue;
      }

      createField(property, maxGridWith);
    }

    for (MovieMultipleProperty property : MovieMultipleProperty.values()) {
      if (excludeProperty.contains(property)) {
        continue;
      }
      createField(property, maxGridWith);
    }

    WebList list = new WebList();
    list.setVisibleRowCount(1);
    list.setCellRenderer(UIUtils.iconListRenderer);
    list.setModel(countryListModel);
    list.setLayoutOrientation(WebList.HORIZONTAL_WRAP);
    list.setPreferredSize(new Dimension(0, 25));
    UIEditor editor = new UIEditor(mr, MovieMultipleProperty.countries, list);
    createEditableField(i18nKey + MovieMultipleProperty.countries.name(), editor, maxGridWith);
    map.put(MovieMultipleProperty.countries, editor);

    for (InlineProperty property : InlineProperty.values()) {

      List<InfoProperty> properties = property.getProperties();
      int nbElement = properties.size();
      for (int i = 0; i < nbElement; i++) {
        editor = new UIEditor(mr, properties.get(i), new WebTextField(smallFieldSize));
        createEditableField(i18nKey + properties.get(i).name(), editor, maxGridWith, nbElement, (i + 1 >= nbElement));
        map.put(properties.get(i), editor);
      }
    }

    WebTextArea textArea = new WebTextArea();
    textArea.setColumns(5);
    textArea.setRows(6);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    UIEditor overview = new UIEditor(mr, MovieProperty.overview, textArea);
    createEditableField(i18nKey + MovieProperty.overview.name(), overview, maxGridWith);
    map.put(MovieProperty.overview, overview);

    // Add dummy Panel to avoid centering
    add(new WebPanel(), getDummyPanelConstraint());
  }

  private void createField(InfoProperty property, int maxGridWith) {
    UIEditor editor = new UIEditor(mr, property, new WebTextField(fieldSize));// FIXME
    createEditableField(i18nKey + property.name(), editor, maxGridWith);
    map.put(property, editor);
  }

  @Override
  public void UIEventHandler(UIEvent.Event event, IEventInfo ieinfo, Object object, Object param) {
    super.UIEventHandler(event, ieinfo, object, param);

    switch (event) {
      case EDITED:
        // Save change in UIMovieInfo
        if (object != null && object instanceof InfoProperty) {
          InfoProperty property = (InfoProperty) object;
          UIEditor editor = map.get(property);
          if (editor != null) {
            if(object instanceof MultipleInfoProperty) {
              editor.setValue(param.toString());
            }
            info.set(property, editor.getValue());
          }
        }

        break;
    }
  }

  @Override
  public void clear() {
    super.clear();
    info = null;
    countryListModel.clear();
  }

  @Override
  public void setInfo(UIMovieInfo info) {
    try {
      this.info = info;
      
      for (MediaProperty property : MediaProperty.values()) {
        if (map.containsKey(property)) {
          map.get(property).setValue(info.get(property));
        }
      }

      for (MovieProperty property : MovieProperty.values()) {
        if (map.containsKey(property)) {
          map.get(property).setValue(info.get(property));
        }
      }

      for (MovieMultipleProperty property : MovieMultipleProperty.values()) {
        if (property != MovieMultipleProperty.countries && map.containsKey(property)) {
          map.get(property).setValue(info.get(property));
        }
      }

      List<String> countries = info.getCountries();
      for (String country : countries) {
        countryListModel.addElement(new UICountry(country, FlagUtils.getFlagByCountry(country)));
      }
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
  public String getPanelName() {
    return "movie info";// FIXME i18n
  }

  @Override
  public PanelType getType() {
    return PanelType.INFO;
  }
}
