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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Class JSONUtilsTest
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class JSONUtilsTest {

  private static JSONObject json;

  @BeforeClass
  public static void init() throws FileNotFoundException, UnsupportedEncodingException, URISyntaxException, JSONException {
    URL url = JSONUtilsTest.class.getResource("json.txt");
    File file = new File(url.toURI());
    Reader reader = new FileReader(file);
    json = new JSONObject(new JSONTokener(reader));
  }

  @Test
  public void selectObject() {
    List<JSONObject> objects = JSONUtils.selectList("feed/movie", json);
    for (JSONObject object : objects) {
      Assert.assertEquals("Avatar", JSONUtils.selectString("title", object));
      break;
    }
  }

}
