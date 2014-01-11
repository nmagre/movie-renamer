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
package fr.free.movierenamer.namematcher;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class TvShowEpisodeNumMatcherTest
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class TvShowEpisodeNumMatcherTest {

  @Test
  public void testMatchEpisode() {
    Assert.assertEquals(new SxE(3, 2), new TvShowEpisodeNumMatcher(new File("Test.test.S03E02.FRENCH.HD.avi")).matchEpisode());
    Assert.assertEquals(new SxE(2, 2), new TvShowEpisodeNumMatcher(new File("Test 2x02 - Test test test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(1, 2), new TvShowEpisodeNumMatcher(new File("Test - [1x02] - test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(1, 11), new TvShowEpisodeNumMatcher(new File("test.S01E11.FRENCH.HDRIp.avi")).matchEpisode());
    Assert.assertEquals(new SxE(1, 3), new TvShowEpisodeNumMatcher(new File("Test Saison 1 - Ep. 03 - Un test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(8, 43), new TvShowEpisodeNumMatcher(new File("test Season 8 - Ep 43 - en test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(2, 3), new TvShowEpisodeNumMatcher(new File("Test Saison 2 Vol. 1 - Ep. 3 - Une Histoire de test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(3, 1), new TvShowEpisodeNumMatcher(new File("test test test S03 E01 VF.avi")).matchEpisode());
    Assert.assertEquals(new SxE(3, 9), new TvShowEpisodeNumMatcher(new File("test test test 0309.avi")).matchEpisode());
    Assert.assertEquals(new SxE(4, 19), new TvShowEpisodeNumMatcher(new File("test test test S04E19 VOSTFR --test 4011--.avi")).matchEpisode());
    Assert.assertEquals(new SxE(0, 7), new TvShowEpisodeNumMatcher(new File("Le test -- 007 --  test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(4, 6), new TvShowEpisodeNumMatcher(new File("4x06 - Le test - test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(1, 3), new TvShowEpisodeNumMatcher(new File("test_test_s1e03.avi")).matchEpisode());
    Assert.assertEquals(new SxE(3, 12), new TvShowEpisodeNumMatcher(new File("test S3 episode (12).avi")).matchEpisode());
    Assert.assertEquals(new SxE(4, 5), new TvShowEpisodeNumMatcher(new File("test_s4e05_fr_hd_2.avi")).matchEpisode());
    Assert.assertEquals(new SxE(3, 4), new TvShowEpisodeNumMatcher(new File("test.saison.3.episode.4.VOSTFR.avi")).matchEpisode());
    Assert.assertEquals(new SxE(1, 10), new TvShowEpisodeNumMatcher(new File("01 - 10 Mon test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(7, 3), new TvShowEpisodeNumMatcher(new File("test.703.mon.test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(9, 2), new TvShowEpisodeNumMatcher(new File("TEST - 180 s09e02 - le principal test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(10, 1), new TvShowEpisodeNumMatcher(new File("S10E01 Test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(13, 2), new TvShowEpisodeNumMatcher(new File("1302 - Les test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(14, 2), new TvShowEpisodeNumMatcher(new File("Les.test.Saison.14.Episode.02.-.encore.un.test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(21, 22), new TvShowEpisodeNumMatcher(new File("TeSt.S21.E22.avi")).matchEpisode());
    Assert.assertEquals(new SxE(5, 2), new TvShowEpisodeNumMatcher(new File("test 502 - Un Monde De test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(5, 4), new TvShowEpisodeNumMatcher(new File("Test Test S05xE04_Tous les test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(3, 9), new TvShowEpisodeNumMatcher(new File("[Test]-[Saison.03.Épisode.09]-[x264HP]-[FR+ENG+Sub.FR+ENG].mkv")).matchEpisode());
    Assert.assertEquals(new SxE(4, 16), new TvShowEpisodeNumMatcher(new File("test 4x16 2010.avi")).matchEpisode());
    Assert.assertEquals(new SxE(8, 3), new TvShowEpisodeNumMatcher(new File("(803) test - Season 8 - Ep 03 - test - fr.avi")).matchEpisode());
    Assert.assertEquals(new SxE(10, 9), new TvShowEpisodeNumMatcher(new File("test- Season 10 - Episode 9 - test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(9, 9), new TvShowEpisodeNumMatcher(new File("test - 9x09 - test (HD 720x400 Fr).avi")).matchEpisode());
    Assert.assertEquals(new SxE(0, 9), new TvShowEpisodeNumMatcher(new File("test - 009 - test (HD 720x400 Fr).avi")).matchEpisode());
  }

  @Test
  public void testMatchEpisodeWithFolder() {
    Assert.assertEquals(new SxE(3, 5), new TvShowEpisodeNumMatcher(new File("S3/test - Ep. 05 -test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(1, 51), new TvShowEpisodeNumMatcher(new File("Saison 03/test - 1x51 - test.FR.avi")).matchEpisode());
    Assert.assertEquals(new SxE(3, 11), new TvShowEpisodeNumMatcher(new File("test/test s3/Episode 11.avi")).matchEpisode());
    Assert.assertEquals(new SxE(9, 2), new TvShowEpisodeNumMatcher(new File("Season 9/test - 180 s09e02 - le principal test.avi")).matchEpisode());
    Assert.assertEquals(new SxE(4, 3), new TvShowEpisodeNumMatcher(new File("saison 4/03 - Un test 720p 800x600.avi")).matchEpisode());
    Assert.assertEquals(new SxE(1, 1), new TvShowEpisodeNumMatcher(new File("Spartacus/saison 01/01 - Epilogue.avi")).matchEpisode());
  }

}
