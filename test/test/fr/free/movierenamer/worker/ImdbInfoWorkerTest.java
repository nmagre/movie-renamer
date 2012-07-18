/*
 * movie-renamer
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
package test.fr.free.movierenamer.worker;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.worker.provider.ImdbInfoWorker;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Class ImdbInfoWorkerTest
 * @author Simon QUÉMÉNEUR
 */
public class ImdbInfoWorkerTest {

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  @Test(expected=NullPointerException.class)
  public void testNullId() throws ActionNotValidException {
    new ImdbInfoWorker(null, null);
  }

  @Test(expected=ActionNotValidException.class)
  public void testIdNotValid() throws ActionNotValidException {
    new ImdbInfoWorker(null, new MediaID(null, MediaID.MediaIdType.TMDBID));
  }

  @Test
  public void test() {
    fail("Not yet implemented");
  }

}
