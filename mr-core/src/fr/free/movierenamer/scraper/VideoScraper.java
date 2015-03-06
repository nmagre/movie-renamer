/*
 * movie-renamer-core
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.scraper;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.VideoInfo;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.ClassUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import java.util.List;

/**
 * Class VideoScraper
 *
 * @author Nicolas Magré
 */
public abstract class VideoScraper<M extends Media, MI extends VideoInfo> extends MediaScraper<M, MI> {

  protected VideoScraper(AvailableLanguages... supportedLanguages) {
    super(supportedLanguages);
  }

  @Override
  protected void addCutomInfo(MI info, M search, IdInfo id, LocaleUtils.AvailableLanguages language) {
    //let's fetch casting
    List<CastingInfo> casting;
    try {
      casting = getCasting(search, id, language);
    } catch (Exception ex) {
      Settings.LOGGER.severe(ClassUtils.getStackTrace(ex));
      casting = null;
    }

    if (casting != null) {
      info.setCasting(casting);
    }
  }

}
