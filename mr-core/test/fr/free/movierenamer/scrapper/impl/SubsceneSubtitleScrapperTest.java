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

import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;

import fr.free.movierenamer.info.SubtitleInfo;
import fr.free.movierenamer.scrapper.SubtitleScrapperTest;
import fr.free.movierenamer.searchinfo.Subtitle;

/**
 * Class SubsceneSubtitleScrapperTest
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SubsceneSubtitleScrapperTest extends SubtitleScrapperTest {
  private SubsceneSubtitleScrapper subscene = null;

  @Override
  public void init() {
    subscene = new SubsceneSubtitleScrapper();
  }
  
  @Override
  public void search() throws Exception {
    List<Subtitle> subtitles = subscene.search("Avatar");
    Assert.assertTrue(subtitles.size() > 15);

    Subtitle subtitle = subtitles.get(0);
    Assert.assertEquals("Avatar (2009)", subtitle.getName());
  }
  
  @Override
  public void getSubtitleInfo() throws Exception {
    subscene.setLanguage(Locale.FRENCH);
    List<SubtitleInfo> subtitles = subscene.getSubtitles(new Subtitle(null, null, new URL("http://subscene.com/subtitles/avatar")));
    Assert.assertTrue(subtitles.size() > 15);

    SubtitleInfo subtitle = subtitles.get(0);
    Assert.assertTrue(subtitle.getName().contains("Avatar"));
    Assert.assertEquals(Locale.FRENCH.getDisplayLanguage(Locale.ENGLISH), subtitle.getLanguage());
  }
  
}
