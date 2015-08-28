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
package fr.free.movierenamer.scraper.impl;

import fr.free.movierenamer.info.IdInfo;
import java.util.List;

import fr.free.movierenamer.info.SubtitleInfo;
import fr.free.movierenamer.scraper.SubtitleScraper;
import fr.free.movierenamer.searchinfo.Subtitle;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;

/**
 * Class OpenSubtitlesScraper : search subtitles on OpenSubtitles
 *
 * @see http://api.opensubtitles.org/xml-rpc
 * @see http://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class OpenSubtitlesScraper extends SubtitleScraper {

  private static final String host = "???";
  private static final String name = "OpenSubtitles";

//  private final OpenSubtitlesXmlRpc xmlrpc;
  public OpenSubtitlesScraper() {
    super(AvailableLanguages.en);
//    String useragent = Settings.getApplicationProperty("opensubtitles.useragent");
//    if (useragent == null || useragent.trim().length() == 0) {
//      throw new NullPointerException("OpenSubtitles useragent must not be null");
//    }
//    this.xmlrpc = new OpenSubtitlesXmlRpc(useragent);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getHost() {
    return host;
  }

  @Override
  protected List<SubtitleInfo> fetchSubtitlesInfo(Subtitle subtitle, AvailableLanguages language) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

  @Override
  protected List<Subtitle> searchSubtitles(String query, AvailableLanguages language) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

  @Override
  protected AvailableLanguages getDefaultLanguage() {// TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected List<Subtitle> searchSubtitlesById(IdInfo id, AvailableLanguages language) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public InfoQuality getQuality() {
    return InfoQuality.AVERAGE;// TODO
  }

}
