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
package fr.free.movierenamer.info;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import fr.free.movierenamer.info.FileInfo.MediaType;
import fr.free.movierenamer.utils.StringUtils;

/**
 * Class FileInfoTest
 *
 * @author Simon QUÉMÉNEUR
 */
public class FileInfoTest {

  @Test
  public void test() {
    String randomName = StringUtils.generateRandomString(10);
    FileInfo fi = new FileInfo(new File(randomName + ".avi"));
    Assert.assertEquals(MediaType.MOVIE, fi.getType());
    Assert.assertEquals(randomName, fi.getSearch());
    Assert.assertEquals(null, fi.getYear());
    Assert.assertEquals(Boolean.FALSE.booleanValue(), fi.wasRenamed());
  }
}
