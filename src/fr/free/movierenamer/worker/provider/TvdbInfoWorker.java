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

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.tvshow.SxE;
import fr.free.movierenamer.media.tvshow.TvShowInfo;
import fr.free.movierenamer.parser.TvdbInfo;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import fr.free.movierenamer.worker.TvShowInfoWorker;
import java.beans.PropertyChangeSupport;
import java.io.File;
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

    uri += getLang();

    uri += ".zip";
    return uri;
  }
  
  @Override
  protected TvShowInfo fileAnalysis(File zipFile) throws Exception {
    File tvshowFile = Utils.getInnerZipFile(zipFile, getLang() + ".xml");
    File actorsFile = Utils.getInnerZipFile(zipFile, "actors.xml");
    File bannersFile = Utils.getInnerZipFile(zipFile, "banners.xml");
    TvShowInfo info = Utils.parseFile(tvshowFile, new TvdbInfo());
    tvshowFile.delete();
    actorsFile.delete();
    bannersFile.delete();
    return info;
  }

  private String getLang() {
    String lang;
    switch (config.tvshowScrapperLang) {
    case ENGLISH:
      lang = "en";
      break;
    case FRENCH:
      lang = "fr";
      break;
    case ITALIAN:
      lang = "en"; // API do not have italian language
      break;
    case SPANISH:
      lang = "es";
      break;
    default:
      lang = "en";
      break;
    }
    return lang;
  }
}
