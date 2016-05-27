/*
 * Movie Renamer
 * Copyright (C) 2015 Nicolas Magré
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
package fr.free.movierenamer.ui.event;

/**
 * Class UIEventInfo
 *
 * @author Nicolas Magré
 */
public class UIEventInfo implements IEventInfo {

    private final String name;
    private final Object obj;

    public UIEventInfo(String name, Object obj) {
        this.name = name;
        this.obj = obj;
    }

    @Override
    public Object getEventObject() {
        return obj;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

}
