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
package fr.free.movierenamer.matcher;

import fr.free.movierenamer.matcher.TvShowEpisodeMatcher;
import fr.free.movierenamer.media.tvshow.SxE;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class TvShowEpisodeMatcherTest {

  private static Map<SxE, String> epMatches = createEPMatch();

  public TvShowEpisodeMatcherTest() {
  }

  private static Map<SxE, String> createEPMatch() {
    Map<SxE, String> epMatchesTest = new LinkedHashMap<SxE, String>();
    epMatchesTest.put(new SxE(3, 2), "Test.test.S03E02.FRENCH.HD.avi");
    epMatchesTest.put(new SxE(2, 2), "Test 2x02 - Test test test.avi");
    epMatchesTest.put(new SxE(7, 18), "Test 07x18 - Test test test.avi");
    epMatchesTest.put(new SxE(1, 2), "Test - [1x02] - test.avi");
    epMatchesTest.put(new SxE(1, 11), "test.S01E11.FRENCH.HDRIp.avi");
    epMatchesTest.put(new SxE(1, 3), "Test Saison 1 - Ep. 03 - Un test.avi");
    epMatchesTest.put(new SxE(8, 43), "test Season 8 - Ep 43 - en test.avi");
    epMatchesTest.put(new SxE(2, 3), "Test Saison 2 Vol. 1 - Ep. 3 - Une Histoire de test.avi");
    epMatchesTest.put(new SxE(3, 1), "test test test S03 E01 VF.avi");
    epMatchesTest.put(new SxE(3, 9), "test test test 0309.avi");
    epMatchesTest.put(new SxE(4, 19), "test test test S04E19 VOSTFR --test 4011--.avi");
    epMatchesTest.put(new SxE(0, 7), "Le test -- 007 --  test.avi");
    epMatchesTest.put(new SxE(4, 6), "4x06 - Le test - test.avi");
    epMatchesTest.put(new SxE(1, 3), "test_test_s1e03.avi");
    epMatchesTest.put(new SxE(3, 12), "test S3 episode (12).avi");
    epMatchesTest.put(new SxE(4, 5), "test_s4e05_fr_hd_2.avi");
    epMatchesTest.put(new SxE(3, 4), "test.saison.3.episode.4.VOSTFR.avi");
    epMatchesTest.put(new SxE(1, 10), "01 - 10 Mon test.avi");
    epMatchesTest.put(new SxE(7, 3), "test.703.mon.test.avi");
    epMatchesTest.put(new SxE(9, 2), "TEST - 180 s09e02 - le principal test.avi");
    epMatchesTest.put(new SxE(10, 1), "S10E01 Test.avi");
    epMatchesTest.put(new SxE(13, 2), "1302 - Les test.avi");
    epMatchesTest.put(new SxE(14, 2), "Les.test.Saison.14.Episode.02.-.encore.un.test.avi");
    epMatchesTest.put(new SxE(21, 22), "TeSt.S21.E22.avi");
    epMatchesTest.put(new SxE(5, 2), "test 502 - Un Monde De test.avi");
    epMatchesTest.put(new SxE(5, 4), "Test Test S05xE04_Tous les test.avi");
    epMatchesTest.put(new SxE(3, 9), "[Test]-[Saison.03.Épisode.09]-[x264HP]-[FR+ENG+Sub.FR+ENG].mkv");
    epMatchesTest.put(new SxE(4, 16), "test 4x16 2010.avi");
    epMatchesTest.put(new SxE(8, 3), "(803) test - Season 8 - Ep 03 - test - fr.avi");
    epMatchesTest.put(new SxE(10, 9), "test- Season 10 - Episode 9 - test.avi");
    epMatchesTest.put(new SxE(9, 9), "test - 9x09 - test (HD 720x400 Fr).avi");
    epMatchesTest.put(new SxE(0, 9), "test - 009 - test (HD 720x400 Fr).avi");
    epMatchesTest.put(new SxE(1, 1), "test -test- test (HD 720x400 Fr).avi");
    //With folder
    epMatchesTest.put(new SxE(3, 5), "S3/test - Ep. 05 -test.avi");
    epMatchesTest.put(new SxE(1, 51), "Saison 03/test - 1x51 - test.FR.avi");
    epMatchesTest.put(new SxE(3, 11), "test/test s3/Episode 11.avi");
    epMatchesTest.put(new SxE(9, 2), "Season 9/test - 180 s09e02 - le principal test.avi");
    epMatchesTest.put(new SxE(4, 3), "saison 4/03 - Un test 720p 800x600.avi");
    return epMatchesTest;
  }

  /**
   * Test of matchEpisode method, of class TvShowEpisodeMatcher.
   */
  @Test
  public void testMatchEpisode() {
    Iterator<SxE> i = epMatches.keySet().iterator();

    int p = 0, v;
    v = epMatches.size();
    while (i.hasNext()) {
      SxE key = i.next();
      SxE found = new TvShowEpisodeMatcher(epMatches.get(key)).matchEpisode();
      assertEquals(key.toString(), found.toString());
      p++;
    }
    
    if (p != v) {
      fail("Only " + p + "/" + v + " test Success");
    }
  }
}
