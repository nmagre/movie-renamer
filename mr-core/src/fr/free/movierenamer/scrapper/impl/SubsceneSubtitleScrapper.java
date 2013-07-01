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

import fr.free.movierenamer.info.IdInfo;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import fr.free.movierenamer.info.SubtitleInfo;
import fr.free.movierenamer.info.SubtitleInfo.SubtitleProperty;
import fr.free.movierenamer.scrapper.SubtitleScrapper;
import fr.free.movierenamer.searchinfo.Subtitle;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;

/**
 * Class SubsceneSubtitleScrapper : search subtitle on Subscene
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SubsceneSubtitleScrapper extends SubtitleScrapper {
  private static final String host = "subscene.com";
  private static final String name = "Subscene";

  public SubsceneSubtitleScrapper() {
    super(AvailableLanguages.en);
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
  protected List<Subtitle> searchSubtitles(String query, Locale language) throws Exception {
    URL searchUrl = new URL("http", host, "/subtitles/title.aspx?q=" + URIRequest.encode(query));
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());

    List<Node> nodes = XPathUtils.selectNodes("//H2//following::DIV[@class='title']//A", dom);
    List<Subtitle> subtitles = new ArrayList<Subtitle>(nodes.size());

    Pattern titleSuffixPattern = Pattern.compile("\\s-\\s([^-]+)[(](\\d{4})[)]$");

    for (Node node : nodes) {
      String title = XPathUtils.getTextContent(node);
      String href = XPathUtils.getAttribute("href", node);

      // simplified name for easy matching
      String shortName = titleSuffixPattern.matcher(title).replaceFirst("");

      try {
        subtitles.add(new Subtitle(shortName, title, new URL("http", host, href)));
      } catch (MalformedURLException e) {
        Settings.LOGGER.log(Level.WARNING, "Invalid href: " + href, e);
      }
    }

    return subtitles;
  }

  @Override
  protected List<SubtitleInfo> fetchSubtitlesInfo(Subtitle subtitle, Locale language) throws Exception {
    Document dom = URIRequest.getHtmlDocument(subtitle.getURL().toURI(), new URIRequest.RequestProperty("Cookie", "Filter=" + language.getDisplayLanguage()));

    List<Node> rows = XPathUtils.selectNodes("//TD[@class='a1']", dom);
    List<SubtitleInfo> subtitles = new ArrayList<SubtitleInfo>();
    for (Node row : rows) {
      try {
        List<Node> fields = XPathUtils.selectNodes(".//SPAN", row);
        String lang = XPathUtils.getTextContent(fields.get(0));
        if (lang == null || lang.equalsIgnoreCase(language.getDisplayLanguage(Locale.ENGLISH))) {
          String href = XPathUtils.selectString(".//A/@href", row);
          Map<SubtitleProperty, String> subtitleFields = new EnumMap<SubtitleProperty, String>(SubtitleProperty.class);
          subtitleFields.put(SubtitleProperty.name, XPathUtils.getTextContent(fields.get(1)));
          subtitleFields.put(SubtitleProperty.href, new URL(subtitle.getURL().getProtocol(), subtitle.getURL().getHost(), href).toExternalForm());
          subtitleFields.put(SubtitleProperty.language, lang);
          subtitles.add(new SubtitleInfo(subtitleFields));
        }
      } catch (Exception e) {
        Settings.LOGGER.log(Level.WARNING, "Cannot parse subtitle node", e);
      }
    }

    return subtitles;
  }

  @Override
  protected Locale getDefaultLanguage() {
    return Locale.ENGLISH;
  }

  @Override
  protected List<Subtitle> searchSubtitlesById(IdInfo id, Locale language) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
