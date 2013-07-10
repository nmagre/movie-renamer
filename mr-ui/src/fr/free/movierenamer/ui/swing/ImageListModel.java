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
package fr.free.movierenamer.ui.swing;

import fr.free.movierenamer.ui.bean.IImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.AbstractListModel;

/**
 * Class ImageListModel
 * @author Nicolas Magré
 */
public class ImageListModel<T extends IImage> extends AbstractListModel {

  private static final long serialVersionUID = 1L;
  private Map<Integer, T> model;

  public ImageListModel() {
    model = new LinkedHashMap<Integer, T>();
  }

  public ImageListModel(Collection<T> data) {
    addAll(data);
  }
  
  public ImageListModel(T elements[]) {
    addAll(elements);
  }

  public void add(T element) {
    model.put(element.getId(), element);
  }

  public void addAll(T elements[]) {
    Collection<T> c = Arrays.asList(elements);
    addAll(c);
    fireContentsChanged(this, 0, getSize());
  }

  public void addAll(Collection<T> data) {
    for (T value : data) {
      model.put(value.getId(), value);
    }
  }
  
  public boolean isEmpty() {
    return model.isEmpty();
  }

  public void clear() {
    model.clear();
    fireContentsChanged(this, 0, getSize());
  }

  public boolean contains(T element) {
    return model.containsKey(element.getId());
  }
  
  public boolean contains(int id) {
    return model.containsKey(id);
  }

  @SuppressWarnings("unchecked")
  public T firstElement() {
    return (T) getElementAt(0);
  }

  @SuppressWarnings("unchecked")
  public T lastElement() {
    return (T) getElementAt(model.size() - 1);
  }

  public boolean removeElement(T element) {
    boolean removed = contains(element);
    if (removed) {
      model.remove(element.getId());
      fireContentsChanged(this, 0, getSize());
    }
    return removed;
  }

  public T getElementById(int id) {
    return model.get(id);
  }

  @Override
  public int getSize() {
    return model.size();
  }

  @Override
  public Object getElementAt(int index) {
    if (index >= model.size()) {
      return null;
    }

    int pos = 0;
    for (T value : model.values()) {
      if (pos == index) {
        return value;
      }
      pos++;
    }

    return null;
  }
  
  public void setElement(T element) {
    int id = element.getId();
    if(contains(id)) {
      model.put(id, element);
      fireContentsChanged(this, 0, getSize());
    }
  }
}
