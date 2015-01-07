/*
 * movie-renamer-core
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
package fr.free.movierenamer.scrapper;

import fr.free.movierenamer.settings.Settings.SettingsProperty;

/**
 * Class ScrapperOptions
 *
 * @author Nicolas Magré
 */
public class ScrapperOptions {

  private final SettingsProperty property;
  private final boolean isLangdep;

  public ScrapperOptions(SettingsProperty property) {
    this(property, false);
  }

  public ScrapperOptions(SettingsProperty property, boolean isLangdep) {
    this.property = property;
    this.isLangdep = isLangdep;
  }

  public SettingsProperty getProperty() {
    return property;
  }

  public boolean isIsLangdep() {
    return isLangdep;
  }

}
