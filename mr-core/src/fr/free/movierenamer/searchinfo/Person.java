/*
 * movie-renamer-core
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
package fr.free.movierenamer.searchinfo;

/**
 * Class Person
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class Person extends SearchResult {

  private static final long serialVersionUID = 1L;
  protected int personId;

  protected Person() {
    // used by serializer
  }

  public Person(String name, int personId) {
    super(name, null, 0);
    this.personId = personId;
  }

  public int getPersonId() {
    return personId;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Person) {
      Person other = (Person) object;
      if (personId > 0 && other.personId > 0) {
        return personId == other.personId;
      }

      return name.equalsIgnoreCase(other.name);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return personId;
  }

  @Override
  public String toString() {
    if (personId > 0) {
      return super.toString() + String.format(" (id:%d))", personId);
    }
    return super.toString();
  }
}
