/*
 * mr-core
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
package fr.free.movierenamer.renamer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Class NameCleanerTest
 *
 * @author Simon QUÉMÉNEUR
 */
public class NameCleanerTest {

  @Test
  public void extractYear() {
    Assert.assertEquals(null, NameCleaner.extractYear("2012.avi"));// return "null", because there is no year
    Assert.assertEquals(Integer.valueOf(2009), NameCleaner.extractYear("2012 (2009).avi"));
    Assert.assertEquals(Integer.valueOf(2009), NameCleaner.extractYear("12 Rounds 2009.avi"));
    Assert.assertEquals(Integer.valueOf(2009), NameCleaner.extractYear("12 Rounds (2009).avi"));
    Assert.assertEquals(Integer.valueOf(1980), NameCleaner.extractYear("1980 12 Rounds x264 (2009x720).avi"));
  }

  @Test
  public void extractNameNotStrict() {
    Assert.assertEquals("2012", NameCleaner.extractName("2012.avi", false));
    Assert.assertEquals("12 Rounds", NameCleaner.extractName("12 Rounds (2009).avi", false));
    Assert.assertEquals("13 jeux de mort", NameCleaner.extractName("13 jeux de mort (2006).avi", false));
    Assert.assertEquals("16 Wishes", NameCleaner.extractName("16 Wishes (2010).avi", false));
    Assert.assertEquals("17 Again", NameCleaner.extractName("17.Again.FRENCH.DVDRiP.XViD-SURViVAL.avi", false));
    Assert.assertEquals("17 again", NameCleaner.extractName("17.again.FRENCH.DVDScr.XviD-VODKA.avi", false));
    Assert.assertEquals("2012", NameCleaner.extractName("2012 (2009).avi", false));
    Assert.assertEquals("2 22", NameCleaner.extractName("2:22 (2008).avi", false));
    Assert.assertEquals("30 Days of Night Dark Days", NameCleaner.extractName("30 Days of Night: Dark Days (2010).avi", false));
    Assert.assertEquals("35 rhums", NameCleaner.extractName("35 rhums (2008).avi", false));
    Assert.assertEquals("600 kilos d'or pur", NameCleaner.extractName("600 kilos d'or pur (2010).avi", false));
    Assert.assertEquals("7 Plans Avant Mes 30 Ans", NameCleaner.extractName("7.Plans.Avant.Mes.30.Ans.FRENCH.DVDRiP.XviD-HARIJO.avi", false));
    Assert.assertEquals("8 fois debout", NameCleaner.extractName("8 fois debout (2009).avi", false));
    Assert.assertEquals("A Christmas Carol", NameCleaner.extractName("A Christmas Carol (2004).avi", false));
    Assert.assertEquals("Acolytes", NameCleaner.extractName("Acolytes.2009.FRENCH.DVDRiP.XViD-ARTEFAC.avi", false));
    Assert.assertEquals("Adam", NameCleaner.extractName("Adam (2009).avi", false));
    Assert.assertEquals("Adventureland", NameCleaner.extractName("Adventureland.TRUEFRENCH.DVDRiP.XviD-HARIJO.avi", false));
    Assert.assertEquals("After Life", NameCleaner.extractName("After.Life (2009).avi", false));
    Assert.assertEquals("Age Of The Dragons", NameCleaner.extractName("Age.Of.The.Dragons.2011.TRUEFRENCH.DVDRiP.XViD-Julien333.avi", false));
    Assert.assertEquals("Agora", NameCleaner.extractName("Agora (2009).avi", false));
    Assert.assertEquals("Tout Est Illuminé 2005", NameCleaner.extractName("Tout Est Illuminé 2005 (2005).avi", false));
    Assert.assertEquals("King Kong", NameCleaner.extractName("King-Kong-FRENCH.DVDRiP.XviD-HARIJO.avi", false));
    Assert.assertEquals("Animal 2", NameCleaner.extractName("Animal.2.FRENCH.DVDRiP.XViD-THEWARRIOR777.avi", false));
    Assert.assertEquals("Sammy s Adventures 2", NameCleaner.extractName("Sammy.s.Adventures.2.3D.2012.FRENCH.1080p.Bluray.DTS.X264-JASS", false));
  }

  @Test
  public void extractNameStrict() {
    Assert.assertEquals("12 Rounds", NameCleaner.extractName("12 Rounds (2009).avi", true));
    Assert.assertEquals("17 Again", NameCleaner.extractName("17.Again.FRENCH.DVDRiP.XViD.avi", true));
    Assert.assertEquals("17 again", NameCleaner.extractName("17.again.FRENCH.DVDScr.XviD.avi", true));
  }

}
