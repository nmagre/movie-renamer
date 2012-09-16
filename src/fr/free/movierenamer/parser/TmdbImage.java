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

import fr.free.movierenamer.media.MediaImage;
import fr.free.movierenamer.media.MediaImages;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.SAXException;

/**
 * Class TheMovieDbImage
 *
 * @author Nicolas Magré
 */
public class TmdbImage extends MrParser<MediaImages> {

  /**
   * The exception to bypass parsing file ;)
   */
  private final NOSAXException ex = new NOSAXException();
  private String url = "http://cf2.imgobject.com/t/p/w";
  private List<MediaImage> thumbs;
  private List<MediaImage> fanarts;
  private MediaImages movieImages;

  public TmdbImage() {
    super();
  }

  @Override
  public void startDocument() throws SAXException {
    thumbs = new ArrayList<MediaImage>();
    fanarts = new ArrayList<MediaImage>();
    movieImages = new MediaImages();

    ObjectMapper mapper = new ObjectMapper();
    try {
      ImageJson json = mapper.readValue(getContent("UTF-8"), ImageJson.class);
      addToList(thumbs, json.getPosters(), MediaImage.MediaImageType.THUMB, 92, 500);
      addToList(fanarts, json.getBackdrops(), MediaImage.MediaImageType.FANART, 300, 780);

      MediaImage.sortByLanguage(config.movieScrapperLang.getShort(), thumbs);
      MediaImage.sortByLanguage(config.movieScrapperLang.getShort(), fanarts);

      movieImages.setFanarts(fanarts);
      movieImages.setThumbs(thumbs);
    } catch (IOException exc) {
      Logger.getLogger(TmdbImage.class.getName()).log(Level.SEVERE, null, exc);
    }

    throw ex;
  }

  private void addToList(List<MediaImage> list, List<Map<String, Object>> values, MediaImage.MediaImageType type, int thumb, int medium) {
    for (Map<String, Object> value : values) {
      MediaImage mImage = new MediaImage(0, type, (String) value.get("iso_639_1") == null ? "" : (String) value.get("iso_639_1"));
      mImage.setUrl(url + thumb + (String) value.get("file_path"), MediaImage.MediaImageSize.THUMB);
      mImage.setUrl(url + medium + (String) value.get("file_path"), MediaImage.MediaImageSize.MEDIUM);
      mImage.setUrl(url + (Integer) value.get("width") + (String) value.get("file_path"), MediaImage.MediaImageSize.ORIGINAL);
      list.add(mImage);
    }
  }

  @Override
  public MediaImages getObject() {
    return movieImages;
  }
}
