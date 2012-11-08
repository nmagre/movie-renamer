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
package fr.free.movierenamer.matcher;

import java.io.File;
import java.util.List;

/**
 * Class MediaNameMatcher
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class MediaNameMatcher {

  protected final File file;
  protected final String filename;
  protected final List<String> regexs;

  public MediaNameMatcher(File file, List<String> regexs) {
    this.file = file;
    if (file != null) {
      this.filename = file.getName();
    } else {
      this.filename = null;
    }
    this.regexs = regexs;
  }

  public abstract String getName();

  public abstract int getYear();
}
