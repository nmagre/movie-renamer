/*
 * Movie Renamer
 * Copyright (C) 2012-2013 Nicolas Magré
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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.ui.settings.UISettings;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.SwingUtilities;

/**
 * Class UIEvent
 *
 * @author Nicolas Magré
 */
public final class UIEvent {

  private static final long serialVersionUID = 1L;
  private static volatile Map<Class<? extends IEventListener>, IEventListener> listenerList = new HashMap<Class<? extends IEventListener>, IEventListener>();

  private enum FireType {

    ALL,
    CLASS
  }

  public enum Event {

    WORKER_DONE,
    WORKER_ALL_DONE,// All workers done
    WORKER_STARTED,
    WORKER_RUNNING,
    SETTINGS,
    EDIT,
    RENAME_FILE,
    RENAME_FILE_DONE
  }

  public static void addEventListener(Class<? extends IEventListener> clazz, IEventListener listener) {
    listenerList.put(clazz, listener);
  }

  public static void removeEventListener(Class<? extends IEventListener> clazz) {
    listenerList.remove(clazz);
  }

  public static void fireUIEvent(Event event) {
    fireUIEvent(event, FireType.ALL, null, null, null, null);
  }

  public static void fireUIEvent(Event event, Class<? extends IEventListener> clazz) {
    fireUIEvent(event, FireType.CLASS, clazz, null, null, null);
  }

  public static void fireUIEvent(Event event, IEventInfo info) {
    fireUIEvent(event, FireType.ALL, null, info, null, null);
  }

  public static void fireUIEvent(Event event, Class<? extends IEventListener> clazz, IEventInfo info) {
    fireUIEvent(event, FireType.CLASS, clazz, info, null, null);
  }

  public static void fireUIEvent(Event event, Object oldObj, Object newObj) {
    fireUIEvent(event, FireType.ALL, null, null, oldObj, newObj);
  }

  public static void fireUIEvent(Event event, IEventInfo info, Object oldObj, Object newObj) {
    fireUIEvent(event, FireType.ALL, null, info, oldObj, newObj);
  }

  public static void fireUIEvent(Event event, Class<? extends IEventListener> clazz, IEventInfo info, Object oldObj, Object newObj) {
    fireUIEvent(event, FireType.CLASS, clazz, info, oldObj, newObj);
  }

  private static void fireUIEvent(Event event, FireType type, Class<? extends IEventListener> clazz, IEventInfo info, Object oldObj, Object newObj) {
    if (oldObj != null && newObj != null && oldObj.equals(newObj)) {
      return;
    }

    if (!SwingUtilities.isEventDispatchThread()) {
      UISettings.LOGGER.severe(String.format("UIEvent must run in EDT, event %s : Target %s", event,
              (type.equals(FireType.CLASS) ? clazz : FireType.ALL)));
    }

    UISettings.LOGGER.fine(String.format("Send event %s%s to %s", event, (info != null ? " " + info.getClass().getSimpleName() + " " + info.getParam() : ""),
            (type.equals(FireType.ALL) ? FireType.ALL : clazz.getSimpleName())));

    for (Entry<Class<? extends IEventListener>, IEventListener> key : listenerList.entrySet()) {
      switch (type) {
        case ALL:
          key.getValue().UIEventHandler(event, info, newObj);
          break;
        case CLASS:
          if (key.getKey().equals(clazz) || clazz.isAssignableFrom(key.getKey())) {
            key.getValue().UIEventHandler(event, info, newObj);
          }
          break;
      }
    }
  }
}
