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
package fr.free.movierenamer.worker.provider;

import fr.free.movierenamer.media.tvshow.SxE;

import fr.free.movierenamer.utils.Cache.CacheType;

import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.parser.MrParser;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.tvshow.TvShowInfo;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.worker.TvShowInfoWorker;
import java.beans.PropertyChangeSupport;
import javax.xml.bind.DatatypeConverter;

/**
 * Class TvdbInfoWorker
 * 
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class TvdbInfoWorker extends TvShowInfoWorker {// TODO A faire

  private final SxE sxe;
  
  public TvdbInfoWorker(PropertyChangeSupport errorSupport, MediaID id, SxE sxe) throws ActionNotValidException {
    super(errorSupport, id);
    if (id.getType() != MediaID.MediaIdType.TVDBID) {
      throw new ActionNotValidException("TvdbInfoWorker can only use tvdb id");
    }
    this.sxe = sxe;
  }

  @Override
  protected String getUri() throws Exception {
    String uri = Settings.tvdbAPIUrlTvShow + new String(DatatypeConverter.parseBase64Binary(Settings.xurlTdb));
    uri += "/" + "series/" + id.getID() + "/all/";

    switch (config.tvshowScrapperLang) {
    case ENGLISH:
      uri += "en";
      break;
    case FRENCH:
      uri += "fr";
      break;
    case ITALIAN:
      uri += "en"; // API do not have italian language
      break;
    case SPANISH:
      uri += "es";
      break;
    default:
      uri += "en";
      break;
    }

    uri += ".zip";
    return uri;
  }

  @Override
  protected MrParser<TvShowInfo> getParser() throws Exception {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected CacheType getCacheType() {
    return CacheType.TVSHOWZIP;
  }
}
