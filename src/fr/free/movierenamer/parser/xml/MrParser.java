/*
 * movie-renamer
 * Copyright (C) 2012 QUÉMÉNEUR Simon
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
package fr.free.movierenamer.parser.xml;

import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class MrParser
 * 
 * @param <T>
 *          Object returned by parser
 * @author QUÉMÉNEUR Simon
 */
public abstract class MrParser<T> extends DefaultHandler implements ContentHandler {
  protected final Settings config = Settings.getInstance();
  private File originalFile;

  public abstract T getObject();

  public final void setOriginalFile(File originalFile) {
    this.originalFile = originalFile;
  }

  protected final String getContent() {
    try {
      return Utils.getInputStreamContent(new FileInputStream(originalFile), "UTF-8");
    } catch (FileNotFoundException e) {
      return "";
    } catch (IOException e) {
      return "";
    }
  }
}
