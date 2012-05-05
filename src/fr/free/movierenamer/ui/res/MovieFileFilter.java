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
package fr.free.movierenamer.ui.res;

import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.filechooser.FileFilter;

/**
 * Class MovieFileFilter
 *
 * @author Nicolas Magré
 */
public class MovieFileFilter extends FileFilter {//A refaire en media

  private ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");
  private Settings setting;

  public MovieFileFilter(Settings setting) {
    this.setting = setting;
  }

  @Override
  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;
    }
    
    if (!setting.useExtensionFilter) {
      return true;
    }
    
    return Utils.checkFileExt(file.getName(), setting.extensions);
  }

  @Override
  public String getDescription() {
    return bundle.getString("movie");
  }
}
