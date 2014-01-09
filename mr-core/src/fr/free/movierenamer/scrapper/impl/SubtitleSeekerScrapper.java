/*
 * movie-renamer-core
 * Copyright (C) 2012-2013 Nicolas Magré
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
package fr.free.movierenamer.scrapper.impl;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.SubtitleInfo;
import fr.free.movierenamer.scrapper.SubtitleScrapper;
import fr.free.movierenamer.searchinfo.Subtitle;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.URIRequest;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * Class SubtitleSeekerScrapper
 *
 * @author Nicolas Magré
 */
public class SubtitleSeekerScrapper extends SubtitleScrapper {// TODO

  private final String apikey;
  private final String host = "api.subtitleseeker.com";

  public SubtitleSeekerScrapper() {
    super(AvailableLanguages.values());
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("subtitleseeker.apkapikey"));
    if (key == null || key.trim().length() == 0) {
      throw new NullPointerException("apikey must not be null");
    }
    this.apikey = key;
  }

  @Override
  protected List<Subtitle> searchSubtitles(String query, AvailableLanguages language) throws Exception {//TODO
    // http://api.subtitleseeker.com/search/
    URL searchUrl = new URL("http", host, "/search/?api_key=" + apikey.trim() + "&search_in=movie_titles&return_type=json&q=" + URIRequest.encode(query));

    return null;
  }

  @Override
  protected List<Subtitle> searchSubtitlesById(IdInfo id, AvailableLanguages language) {
    return null;
  }

  @Override
  protected List<SubtitleInfo> fetchSubtitlesInfo(Subtitle subtitle, AvailableLanguages language) throws Exception {
    return null;
  }

  @Override
  protected AvailableLanguages getDefaultLanguage() {
    return AvailableLanguages.en;
  }

  @Override
  public String getName() {
    return "SubtitleSeeker";
  }

  @Override
  protected String getHost() {
    return host;
  }
}
