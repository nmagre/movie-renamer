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

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import fr.free.movierenamer.info.FileInfo;

/**
 * Class RenamerTest
 * 
 * @author Simon QUÉMÉNEUR
 */
public class RenamerTest {

  @Test
  public void test() {
    FileInfo fi = new FileInfo(new File("coucou2.avi"));
//    Assert.assertEquals(true,  fi.renamed("coucou3.avi"));
//    Assert.assertEquals(true,  fi.renamed("coucou4.avi"));
//    Assert.assertEquals(true, fi.wasRenamed());
  }
}
