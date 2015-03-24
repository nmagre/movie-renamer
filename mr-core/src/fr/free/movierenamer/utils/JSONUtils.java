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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Class JSONUtils
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class JSONUtils {

  public static JSONObject selectObject(String path, final JSONObject rootObject) {
    try {
      JSONObject toSearch = rootObject;
      String[] nodes = path.split("/");
      for (String node : nodes) {
        toSearch = toSearch.getJSONObject(node);
      }
      return toSearch;
    } catch (Exception e) {
      
    }
    
    return null;
  }

  public static JSONObject selectFirstObject(final JSONObject rootObject) {
    try {
      JSONObject toSearch = rootObject;
      @SuppressWarnings("unchecked")
      Iterator<String> it = (Iterator<String>) toSearch.keys();
      while (it.hasNext()) {
        String key = it.next();
        if (key != null) {
          // If key is empty we try to get next one (empty key is a bug in API)
          if (key.equals("")) {
            continue;
          }
          return (JSONObject) toSearch.get(key);
        }

        break;
      }
    } catch (Exception e) {
      //
    }
    return null;
  }

  public static JSONObject selectNextObject(final JSONObject rootObject) {
    try {
      JSONObject toSearch = rootObject;
      return (JSONObject) toSearch.get((String) toSearch.keys().next());
    } catch (Exception e) {
      //
    }
    return null;
  }

  public static List<JSONObject> selectList(String path, final JSONObject rootObject) {
    try {
      JSONObject toSearch = rootObject;
      int last = path.lastIndexOf('/');
      if (last >= 0) {
        toSearch = selectObject(path.substring(0, last), toSearch);
      }
      JSONArray array = (JSONArray) toSearch.get(path.substring(last + 1, path.length()));
      return jsonList(array);
    } catch (Exception e) {
    }
    
    return new ArrayList<>();
  }

  public static String selectString(String attribute, final JSONObject node) {
    try {
      if (node == null) {
        return null;
      } else {
        Object val = node.get(attribute);
        if (val == null) {
          return null;
        } else {
          return String.valueOf(val);
        }
      }
    } catch (Exception e) {
    }
    
    return null;
  }

  public static Integer selectInteger(String attribute, final JSONObject node) {
    return Integer.valueOf(selectString(attribute, node));
  }

  public static Double selectDouble(String attribute, final JSONObject node) {
    return Double.valueOf(selectString(attribute, node));
  }

  private static List<JSONObject> jsonList(final Object array) {
    return new AbstractList<JSONObject>() {

      @Override
      public JSONObject get(int index) {
        if (array == null) {
          return null;
        }
        
        try {
          return (JSONObject) ((JSONArray) array).get(index);
        } catch (JSONException ex) {

        }
        
        return null;
      }

      @Override
      public int size() {
        if (array == null) {
          return 0;
        }
        return ((JSONArray) array).length();
      }
    };
  }

  private JSONUtils() {
    throw new UnsupportedOperationException();
  }
}
