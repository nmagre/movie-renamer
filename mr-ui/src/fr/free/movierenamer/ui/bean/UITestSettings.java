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
import fr.free.movierenamer.settings.XMLSettings;
import fr.free.movierenamer.ui.swing.ITestActionListener;
import java.io.IOException;

/**
 * Class UITestSettings
 *
 * @author Nicolas Magré
 */
public abstract class UITestSettings implements XMLSettings.IProperty {

  private final XMLSettings.SettingsType type;
  private final XMLSettings.SettingsSubType subType;

  public UITestSettings(XMLSettings.SettingsType type, XMLSettings.SettingsSubType subType) {
    this.type = type;
    this.subType = subType;
  }

  public abstract ITestActionListener getActionListener();

  @Override
  public Class<?> getVclass() {
    return null;
  }

  @Override
  public Object getDefaultValue() {
    return null;
  }

  @Override
  public String getValue() {
    return null;
  }

  @Override
  public String name() {
    return "test";
  }

  @Override
  public Settings.SettingsType getType() {
    return type;
  }

  @Override
  public Settings.SettingsSubType getSubType() {
    return subType;
  }

  @Override
  public boolean hasChild() {
    return false;
  }

  @Override
  public void setValue(Object value) throws IOException {
  }

}
