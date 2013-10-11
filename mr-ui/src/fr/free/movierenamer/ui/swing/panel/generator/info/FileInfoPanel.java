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

import com.alee.laf.label.WebLabel;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.mediainfo.MediaAudio;
import fr.free.movierenamer.mediainfo.MediaTag;
import fr.free.movierenamer.mediainfo.MediaVideo;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * Class FileInfoPanel
 *
 * @author Nicolas Magré
 */
public final class FileInfoPanel extends InfoPanel<FileInfo> {

  private final int level = 1;
  private FileInfo info;

  @Override
  public void setInfo(FileInfo info) {
    this.info = info;

    MediaTag mtag = info.getMediaTag();

    MediaVideo mvideo = mtag.getMediaVideo();
    add(createTitle("fileinfo.video", true), getTitleConstraint());
    try {
      createFields(mvideo, level);
    } catch (IntrospectionException ex) {
      UISettings.LOGGER.log(Level.SEVERE, null, ex);
    }

    List<MediaAudio> audios = mtag.getMediaAudios();
    if (audios.size() > 0) {
      add(createTitle("fileinfo.audio", true), getTitleConstraint());
    }

    int count = 1;
    for (MediaAudio audio : audios) {
      add(createTitle(("fileinfo.audio") + " #" + count++, false), getGroupConstraint(2));// FIXME i18n
      try {
        createFields(audio, 2);
      } catch (IntrospectionException ex) {
        UISettings.LOGGER.log(Level.SEVERE, null, ex);
      }
    }

    // Add dummy Panel to avoid centering
    add(new JPanel(), getDummyPanelConstraint());
  }

  private void createFields(Object object, int level) throws IntrospectionException {
    for (PropertyDescriptor pd : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
      if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
        try {
          WebLabel label = (WebLabel) createComponent(Component.LABEL, "fileinfo." + pd.getName());
          add(label, getGroupConstraint(0, false, false, level));
          WebTextField field = (WebTextField) createComponent(Component.FIELD, null);
          field.setEditable(false);
          field.setText(pd.getReadMethod().invoke(object).toString());
          add(field, getGroupConstraint(1, true, true, level));
        } catch (IllegalAccessException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
        }
      }
    }
  }

  @Override
  public void clear() {
    info = null;
    removeAll();
  }

  @Override
  public Icon getIcon() {
    return ImageUtils.MEDIA_16;
  }

  @Override
  public String getPanelName() {
    return "fileinfo.file";
  }

  @Override
  public FileInfo getInfo() {
    return info;
  }
}
