/*
 * Movie Renamer
 * Copyright (C) 2015 Nicolas Magré
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

import fr.free.movierenamer.settings.XMLSettings;
import java.io.IOException;

/**
 * Class UIPasswordSettings
 * @author Nicolas Magré
 */
public class UIPasswordSettings implements XMLSettings.IProperty {

  private final XMLSettings.IProperty property;

  public UIPasswordSettings(XMLSettings.IProperty property) {
    this.property = property;
  }

  @Override
  public Class<?> getVclass() {// A little bit tricky :-(
    return UIPasswordSettings.class;
  }

  @Override
  public Object getDefaultValue() {
    return property.getDefaultValue();
  }

  @Override
  public String getValue() {
    return property.getValue();
  }

  @Override
  public String name() {
    return property.name();
  }

  @Override
  public XMLSettings.SettingsType getType() {
    return property.getType();
  }

  @Override
  public XMLSettings.SettingsSubType getSubType() {
    return property.getSubType();
  }

  @Override
  public boolean hasChild() {
    return property.hasChild();
  }

  @Override
  public void setValue(Object value) throws IOException {
    property.setValue(value);
  }

}
