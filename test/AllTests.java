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
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import test.fr.free.movierenamer.matcher.AllMatcherTests;
import test.fr.free.movierenamer.parser.AllParserTests;
import test.fr.free.movierenamer.worker.AllWorkerTests;


@RunWith(Suite.class)
@Suite.SuiteClasses({
  AllWorkerTests.class,
  AllParserTests.class,
  AllMatcherTests.class
})
/**
 *
 * @author Nicolas Magré
 */
public class AllTests {

  public AllTests() {
  }
}
