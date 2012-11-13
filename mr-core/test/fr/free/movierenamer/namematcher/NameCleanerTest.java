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
package fr.free.movierenamer.namematcher;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Class NameCleanerTest
 * @author Simon QUÉMÉNEUR
 */
public class NameCleanerTest {
  
  private NameCleaner cleaner;

  @Before
  public void init() {
    cleaner = new NameCleaner();
  }
  

  @Test
  public void extractYear() {
    Assert.assertEquals(2009, cleaner.extractYear("2012 (2009).avi"));
    Assert.assertEquals(2009, cleaner.extractYear("12 Rounds 2009.avi"));
    Assert.assertEquals(2009, cleaner.extractYear("12 Rounds (2009).avi"));
  }
  
  @Test
  public void extractNameNotStrict() {
    Assert.assertEquals("12 Rounds", cleaner.extractName("12 Rounds (2009).avi", false));
    Assert.assertEquals("13 jeux de mort", cleaner.extractName("13 jeux de mort (2006).avi", false));
    Assert.assertEquals("16 Wishes", cleaner.extractName("16 Wishes (2010).avi", false));
    Assert.assertEquals("17 Again", cleaner.extractName("17.Again.FRENCH.DVDRiP.XViD-SURViVAL.avi", false));
    Assert.assertEquals("17 again", cleaner.extractName("17.again.FRENCH.DVDScr.XviD-VODKA.avi", false));
    Assert.assertEquals("2012", cleaner.extractName("2012 (2009).avi", false));
    Assert.assertEquals("2:22", cleaner.extractName("2:22 (2008).avi", false));
    Assert.assertEquals("30 Days of Night: Dark Days", cleaner.extractName("30 Days of Night: Dark Days (2010).avi", false));
    Assert.assertEquals("35 rhums", cleaner.extractName("35 rhums (2008).avi", false));
    Assert.assertEquals("600 kilos d or pur", cleaner.extractName("600 kilos d'or pur (2010).avi", false));
    Assert.assertEquals("7 Plans Avant Mes 30 Ans", cleaner.extractName("7.Plans.Avant.Mes.30.Ans.FRENCH.DVDRiP.XviD-HARIJO.avi", false));
    Assert.assertEquals("8 fois debout", cleaner.extractName("8 fois debout (2009).avi", false));
    Assert.assertEquals("A Christmas Carol", cleaner.extractName("A Christmas Carol (2004).avi", false));
    Assert.assertEquals("Acolytes", cleaner.extractName("Acolytes.2009.FRENCH.DVDRiP.XViD-ARTEFAC.avi", false));
    Assert.assertEquals("Adam", cleaner.extractName("Adam (2009).avi", false));
    Assert.assertEquals("Adventureland", cleaner.extractName("Adventureland.TRUEFRENCH.DVDRiP.XviD-HARIJO.avi", false));
    Assert.assertEquals("After Life", cleaner.extractName("After.Life (2009).avi", false));
    Assert.assertEquals("Age Of The Dragons", cleaner.extractName("Age.Of.The.Dragons.2011.TRUEFRENCH.DVDRiP.XViD-Julien333.avi", false));
    Assert.assertEquals("Agora", cleaner.extractName("Agora (2009).avi", false));
  }
}
