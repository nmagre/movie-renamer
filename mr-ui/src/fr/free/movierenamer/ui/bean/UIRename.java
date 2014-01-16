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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.settings.Settings;
import java.util.Map;

/**
 * Class UIRename
 *
 * @author Nicolas Magré
 */
public class UIRename {

  private final UIFile file;
  private final String renamedTitle;
  private final Map<Settings.IProperty, String> options;

  public UIRename(UIFile file, String renamedTitle, Map<Settings.IProperty, String> options) {
    this.file = file;
    this.renamedTitle = renamedTitle;
    this.options = options;
  }

  public UIFile getFile() {
    return file;
  }

  public String getRenamedTitle() {
    return renamedTitle;
  }

  public String getOption(Settings.IProperty property) {

    if (options != null && options.containsKey(property)) {
      return options.get(property);
    }
    return property.getValue();
  }

  public Map<Settings.IProperty, String> getOptions() {
    return options;
  }
}
