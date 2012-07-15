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

import fr.free.movierenamer.media.movie.MovieImage;
import org.xml.sax.SAXException;

/**
 * Class ImdbImage
 * 
 * @author QUÉMÉNEUR Simon
 */
public class ImdbImage extends MrParser<MovieImage> {
  private final MovieImage movieImage;
  
  /**
   * The exception to bypass parsing file ;)
   */
  private final NOSAXException ex = new NOSAXException();

  public ImdbImage() {
    super();
    movieImage = new MovieImage();
  }

  @Override
  public void startDocument() throws SAXException {
    
    throw ex;
  }
  /*
   * (non-Javadoc)
   * 
   * @see fr.free.movierenamer.parser.xml.MrParser#getObject()
   */
  @Override
  public MovieImage getObject() {
    return movieImage;
  }

}
