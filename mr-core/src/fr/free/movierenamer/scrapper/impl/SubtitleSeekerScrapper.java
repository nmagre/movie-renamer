/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.scrapper.impl;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.SubtitleInfo;
import fr.free.movierenamer.scrapper.SubtitleScrapper;
import fr.free.movierenamer.searchinfo.Subtitle;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Nicolas Magré
 */
public class SubtitleSeekerScrapper extends SubtitleScrapper {// TODO

  private final String apikey;
  private final String host = "api.subtitleseeker.com";

  public SubtitleSeekerScrapper() {
    super(LocaleUtils.AvailableLanguages.values());
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("subtitleseeker.apkapikey"));
    if (key == null || key.trim().length() == 0) {
      throw new NullPointerException("apikey must not be null");
    }
    this.apikey = key;
  }

  @Override
  protected List<Subtitle> searchSubtitles(String query, Locale language) throws Exception {//TODO
    //URL searchUrl = new URL("http", host, "/" + getTypeName() + "/" + apikey + "/" + media.getMediaId() + "/");// Last slash is required
    return null;
  }

  @Override
  protected List<Subtitle> searchSubtitlesById(IdInfo id, Locale language) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected List<SubtitleInfo> fetchSubtitlesInfo(Subtitle subtitle, Locale language) throws Exception {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected Locale getDefaultLanguage() {
    throw new UnsupportedOperationException("Not supported yet.");
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
