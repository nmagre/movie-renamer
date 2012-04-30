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
package fr.free.movierenamer.utils;

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
    epMatchesTest.put(new SxE(3, 2), "American.Dad.S03E02.FRENCH.HD.avi");
    epMatchesTest.put(new SxE(2, 2), "Alf 2x02 - La grande ballade.avi");
    epMatchesTest.put(new SxE(1, 2), "Code Lisa - [1x02] - Télécommande de choc.avi");
    epMatchesTest.put(new SxE(1, 11), "Dexter.S01E11.FRENCH.HDRIp.avi");
    epMatchesTest.put(new SxE(1, 3), "H Saison 1 - Ep. 03 - Le Manuscrit.avi");
    epMatchesTest.put(new SxE(8, 43), "H Season 8 - Ep. 43 - en test.avi");
    epMatchesTest.put(new SxE(2, 3), "H Saison 2 Vol. 1 - Ep. 3 - Une Histoire de Démission.avi");
    epMatchesTest.put(new SxE(3, 1), "How i met your mother S03 E01 VF.avi");
    epMatchesTest.put(new SxE(3, 9), "How i met your mother 0309.avi");
    epMatchesTest.put(new SxE(4, 19), "How I Met Your Mother S04E19 VOSTFR --Antoine 4011--.avi");
    epMatchesTest.put(new SxE(0, 7), "La bande à Picsou -- 007 --  L'argent ça va ça vient.avi");
    epMatchesTest.put(new SxE(4, 6), "4x06 - Le Prince De Bel Air - Cas de conscience.avi");
    epMatchesTest.put(new SxE(1, 3), "darkwing_duck_s1e03.avi");
    epMatchesTest.put(new SxE(3, 12), "Nerdz S3 episode (12).avi");
    epMatchesTest.put(new SxE(4, 5), "nerdz_s4e05_fr_hd_2.avi");
    epMatchesTest.put(new SxE(3, 4), "Prison.break.saison.3.episode.4.VOSTFR.avi");
    epMatchesTest.put(new SxE(1, 10), "01 - 10 Mon surnom.avi");
    epMatchesTest.put(new SxE(7, 3), "scrubs.703.ma.verite.qui.derange.avi");
    epMatchesTest.put(new SxE(9, 2), "SIMPSONS - 180 s09e02 - le principal principal.avi");
    epMatchesTest.put(new SxE(10, 1), "S10E01 La graisse antique.avi");
    epMatchesTest.put(new SxE(13, 2), "1302 - Les Maux de Moe.avi");
    epMatchesTest.put(new SxE(14, 2), "Les.Simpson.Saison.14.Episode.02.-.Homer.like.a.rolling.stone.avi");
    epMatchesTest.put(new SxE(21, 22), "TS.S21.E22.avi");
    epMatchesTest.put(new SxE(5, 2), "Sliders 502 - Un Monde De Fluctuations Quantiques.avi");
    epMatchesTest.put(new SxE(5, 4), "Stargate Atlantis S05xE04_Tous les possibles.avi");
    epMatchesTest.put(new SxE(3, 9), "[Stargate.SG1]-[Saison.03.Épisode.09]-[x264HP]-[FR+ENG+Sub.FR+ENG].mkv");
    epMatchesTest.put(new SxE(4, 16), "Stargate SG-1 4x16 2010.avi");
    epMatchesTest.put(new SxE(8, 3), "(803) Stargate Sg1 - Season 8 - Ep 03 - Quarantaine - fr.avi");
    epMatchesTest.put(new SxE(10, 9), "Stargate Sg1 - Season 10 - Episode 9 - unknown.avi");
    epMatchesTest.put(new SxE(9, 9), "Stargate SG-1 - 9x09 - Prototype (HD 720x400 Fr).avi");
    return epMatchesTest;
  }

  /**
   * Test of matchEpisode method, of class TvShowEpisodeMatcher.
   */
  @Test
  public void testMatchEpisode() {
    Iterator i = epMatches.keySet().iterator();

    int p = 0, v;
    v = epMatches.size();
    while (i.hasNext()) {
      SxE key = (SxE) i.next();
      SxE found = new TvShowEpisodeMatcher((String) epMatches.get(key)).matchEpisode();
      try {
        assertEquals(key.toString(), found.toString());
        p++;
      } catch (AssertionError e) {
        System.err.println("Match failed : " + (String) epMatches.get(key));
        System.err.println("Expect : " + key.toString() + " , found : " + found.toString() + "\n");
      }
    }
    if (p != v) {
      fail("Only " + p + "/" + v + " test Success");
    }
  }
}
