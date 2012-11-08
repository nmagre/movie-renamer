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
package fr.free.movierenamer.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Class FileUtilsTest
 * 
 * @author Simon QUÉMÉNEUR
 */
public class FileUtilsTest {
  @Test
  public void hasExtension() {
    assertTrue(FileUtils.hasExtension("abc.txt", null, "txt"));
    assertTrue(FileUtils.hasExtension(".hidden", null, "txt"));
    assertFalse(FileUtils.hasExtension(".hidden", "txt"));
  }

  @Test
  public void getExtension() {
    assertEquals("txt", FileUtils.getExtension("abc.txt"));
    assertEquals("out", FileUtils.getExtension("a.out"));
    assertEquals(null, FileUtils.getExtension(".hidden"));
    assertEquals(null, FileUtils.getExtension("a."));

    assertEquals("r00", FileUtils.getExtension("archive.r00"));
    assertEquals(null, FileUtils.getExtension("archive.r??"));
    assertEquals(null, FileUtils.getExtension("archive.invalid extension"));
  }

  @Test
  public void getNameWithoutExtension() {
    assertEquals("abc", FileUtils.getNameWithoutExtension("abc.txt"));
    assertEquals("a", FileUtils.getNameWithoutExtension("a.out"));
    assertEquals(".hidden", FileUtils.getNameWithoutExtension(".hidden"));
    assertEquals("a.", FileUtils.getNameWithoutExtension("a."));

    assertEquals("archive", FileUtils.getNameWithoutExtension("archive.r00"));
    assertEquals("archive.r??", FileUtils.getNameWithoutExtension("archive.r??"));
    assertEquals("archive.invalid extension", FileUtils.getNameWithoutExtension("archive.invalid extension"));
  }
}
