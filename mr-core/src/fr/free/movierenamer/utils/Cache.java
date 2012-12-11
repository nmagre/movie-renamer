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

import fr.free.movierenamer.settings.Settings;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Class Cache
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class Cache {
  static {
    Cache.initializeCache();
    if (Settings.getInstance().isCacheClear()) {
      Cache.clearAllCache();
    }
  }

  private static void initializeCache() {
    // prepare cache folder for this application instance
    File cacheRoot = new File(Settings.appFolder, "cache");

    try {
      for (int i = 0; true; i++) {
        File cache = new File(cacheRoot, String.format("%d", i));
        if (!cache.isDirectory() && !cache.mkdirs()) {
          throw new IOException("Failed to create cache dir: " + cache);
        }

        File lockFile = new File(cache, ".lock");
        final RandomAccessFile handle = new RandomAccessFile(lockFile, "rw");
        final FileLock lock = handle.getChannel().tryLock();
        if (lock != null) {
          // setup cache dir for ehcache
          System.setProperty("ehcache.disk.store.dir", cache.getAbsolutePath());

          // make sure to orderly shutdown cache
          Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
              CacheManager.getInstance().shutdown();
              try {
                lock.release();
              } catch (Exception e) {
                Logger.getLogger(Settings.class.getName()).log(Level.WARNING, e.toString());
              }
              try {
                handle.close();
              } catch (Exception e) {
                Logger.getLogger(Settings.class.getName()).log(Level.WARNING, e.toString());
              }
            }
          });

          // cache for this application instance is successfully set up and locked
          // handle is close in previous hook !
          return;
        }

        // try next lock file
        handle.close();
      }
    } catch (Exception e) {
      Logger.getLogger(Settings.class.getName()).log(Level.WARNING, e.toString(), e);
    }

    // use cache root itself as fail-safe fallback
    System.setProperty("ehcache.disk.store.dir", new File(cacheRoot, "default").getAbsolutePath());
  }

  public synchronized static Cache getCache(String name) {
    return new Cache(CacheManager.getInstance().getCache(name));
  }

  public synchronized static void clearCache(String name) {
    net.sf.ehcache.Cache cache = CacheManager.getInstance().getCache(name);
    if (cache != null) {
      Logger.getLogger(Cache.class.getName()).log(Level.FINER, String.format("Clear cache %s", cache.getName()));
      cache.removeAll();
    }
  }

  public static void clearAllCache() {
    for (String cacheName : CacheManager.getInstance().getCacheNames()) {
      clearCache(cacheName);
    }
  }

  private final net.sf.ehcache.Cache cache;

  protected Cache(net.sf.ehcache.Cache cache) {
    this.cache = cache;
  }

  public synchronized void put(Object key, Object value) {
    try {
      Logger.getLogger(Cache.class.getName()).log(Level.FINER, String.format("Add object to cache %s", cache.getName()));
      cache.put(new Element(key, value));
      Logger.getLogger(Cache.class.getName()).log(Level.FINEST, String.format("Cache %s is now %s octets", cache.getName(), getSize()));
    } catch (Throwable e) {
      Logger.getLogger(Cache.class.getName()).log(Level.WARNING, e.getMessage());
      remove(key); // fail-safe
    }
  }

  public Object get(Object key) {
    return get(key, Object.class);
  }

  public <T> T get(Object key, Class<T> type) {
    try {
      Element element = cache.get(key);
      if (element != null && key.equals(element.getKey())) {
        return type.cast(element.getValue());
      }
    } catch (Exception e) {
      Logger.getLogger(Cache.class.getName()).log(Level.WARNING, e.getMessage(), e);
      remove(key); // fail-safe
    }

    return null;
  }

  public synchronized void remove(Object key) {
    try {
      cache.remove(key);
    } catch (Exception e) {
      Logger.getLogger(Cache.class.getName()).log(Level.WARNING, e.getMessage(), e);
    }
  }

  public long getSize() {
    long size = 0;
    size = cache.getMemoryStoreSize();
    size = cache.getDiskStoreSize();
    size = cache.getSize();
    size = cache.calculateInMemorySize();
    return size;
  }

  public static class CacheKey implements Serializable {

    private static final long serialVersionUID = 1L;
    protected Object[] fields;

    public CacheKey(Object... fields) {
      this.fields = fields;
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(fields);
    }

    @Override
    public boolean equals(Object other) {
      if (other instanceof CacheKey) {
        return Arrays.equals(this.fields, ((CacheKey) other).fields);
      }

      return false;
    }

    @Override
    public String toString() {
      return Arrays.toString(fields);
    }
  }
}
