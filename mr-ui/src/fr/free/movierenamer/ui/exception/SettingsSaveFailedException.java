/*
 * Movie Renamer
 * Copyright (C) 2012-2015 Nicolas Magré
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
package fr.free.movierenamer.ui.exception;

import fr.free.movierenamer.ui.settings.UISettings;

/**
 * Class SettingsSaveFailedException
 *
 * @author Nicolas Magré
 */
public class SettingsSaveFailedException extends Exception {
  private static final long serialVersionUID = 1L;

  private final UISettings defaultSettings;

  public SettingsSaveFailedException(UISettings config, String message) {
    super(message);
    this.defaultSettings = config;
  }

  public UISettings getDefaultSettings() {
    return defaultSettings;
  }
}