/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.scrapper.impl;

import fr.free.movierenamer.info.SubtitleInfo;
import fr.free.movierenamer.scrapper.SubtitleScrapper;
import fr.free.movierenamer.searchinfo.Subtitle;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Nicolas Magré
 */
public class SubtitleSeekerScrapper extends SubtitleScrapper {// TODO

  @Override
  protected List<SubtitleInfo> fetchSubtitlesInfo(Subtitle subtitle, Locale language) throws Exception {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected List<Subtitle> searchSubtitles(String query, Locale language) throws Exception {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected Locale getDefaultLanguage() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getName() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected String getHost() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
}
