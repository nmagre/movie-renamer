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
package fr.free.movierenamer.parser;

import fr.free.movierenamer.utils.Settings;
import java.util.logging.Level;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class MrSettings
 * 
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class MrSettings extends MrParser<Settings> {

  private final Settings settings = Settings.getInstance();
  private StringBuffer buffer;
  private boolean settingXML;
  private boolean setting;

  public MrSettings() {
    super();
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    settingXML = false;
    setting = false;
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase(Settings.movieRenamerTag)) {
      settingXML = true;
      Settings.xmlVersion = attributes.getValue(Settings.versionAtt);
    }
    if (name.equalsIgnoreCase(Settings.settingTag)) {
      setting = true;
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (settingXML && !name.equalsIgnoreCase(Settings.movieRenamerTag)) {
      if (setting && !name.equalsIgnoreCase(Settings.settingTag)) {
        try {
          settings.setValue(name, buffer.toString());
        } catch (NullPointerException ex) {
          Settings.LOGGER.log(Level.SEVERE, ex.getMessage());
          Settings.xmlError = true;
        }
      }
    }
    buffer = null;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String lecture = new String(ch, start, length);
    if (buffer != null) {
      buffer.append(lecture);
    }
  }

  @Override
  public Settings getObject() {
    return this.settings;
  }
}
