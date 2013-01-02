/*
 * movie-renamer-core
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
package fr.free.movierenamer.scrapper.impl;

import java.util.List;
import java.util.Locale;

import fr.free.movierenamer.info.SubtitleInfo;
import fr.free.movierenamer.scrapper.SubtitleScrapper;
import fr.free.movierenamer.searchinfo.Subtitle;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;

/**
 * Class OpenSubtitlesScrapper : search subtitles on OpenSubtitles
 *
 * @see http://api.opensubtitles.org/xml-rpc
 * @see http://trac.opensubtitles.org/projects/opensubtitles/wiki/XMLRPC
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class OpenSubtitlesScrapper extends SubtitleScrapper {

  private static final String host = "???";
  private static final String name = "OpenSubtitles";
  
//  private final OpenSubtitlesXmlRpc xmlrpc;

  public OpenSubtitlesScrapper() {
    super(AvailableLanguages.ENGLISH);
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
  protected List<SubtitleInfo> fetchSubtitlesInfo(Subtitle subtitle, Locale language) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

  @Override
  protected List<Subtitle> searchSubtitles(String query, Locale language) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

}
