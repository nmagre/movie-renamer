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
package fr.free.movierenamer.ui.utils;

import fr.free.movierenamer.ui.settings.Settings;

import com.alee.extended.filefilter.DefaultFileFilter;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import java.io.File;

/**
 * Class MovieFileFilter
 * 
 * @author Nicolas Magré
 */
public class FileFilter extends DefaultFileFilter {

  public FileFilter() {
    super();
  }

  @Override
  public boolean accept(File file) {
    if (!Settings.getInstance().useExtensionFilter) {
      return true;
    }

    if (file.isDirectory()) {
      return true;
    }

    return FileUtils.hasExtension(file, Settings.getInstance().extensions);
  }

  @Override
  public String getDescription() {
    return LocaleUtils.i18n("media");
  }
}
