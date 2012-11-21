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
package fr.free.movierenamer.utils;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class LocaleUtilsTest
 * 
 * @author Simon QUÉMÉNEUR
 */
public class LocaleUtilsTest {

  @Test
  public void getLocaleCountry() {
    Assert.assertEquals("US", LocaleUtils.findCountry("United States").getCountry());
    Assert.assertEquals("US", LocaleUtils.findCountry("US").getCountry());
    Assert.assertEquals("US", LocaleUtils.findCountry("Etats-Unis", Locale.FRANCE, Locale.US).getCountry());
    Assert.assertEquals("US", LocaleUtils.findCountry("u.s.a.").getCountry());
    Assert.assertEquals("blabla", LocaleUtils.findCountry("blabla").getCountry());
    Assert.assertEquals("FR", LocaleUtils.findCountry("france").getCountry());
    Assert.assertEquals("AR", LocaleUtils.findCountry("Argentine", Locale.ITALY).getCountry());
    Assert.assertEquals("GB", LocaleUtils.findCountry("Royaume-Uni").getCountry());
  }

  @Test
  public void getLocaleLanguage() {
    Assert.assertEquals("sv", LocaleUtils.findLanguage("Sueco").getLanguage());
  }

}
