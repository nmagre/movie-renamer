/*
 * Movie Renamer
 * Copyright (C) 2014 Nicolas Magré
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

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MediaInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Class UIMediaInfo
 *
 * @author Nicolas Magré
 */
abstract public class UIMediaInfo<T extends MediaInfo> {

    protected T info;

    public UIMediaInfo(T info) {
        this.info = info;
    }

    public void setInfo(T info) {
        this.info = info;
    }

    public void setIdsInfo(List<IdInfo> ids) {
        info.setIdsInfo(ids);
    }

    public List<IdInfo> getIds() {
        List<IdInfo> idInfos = info.getIdsInfo();
        if (idInfos == null) {
            idInfos = new ArrayList<>();
        }
        return idInfos;
    }

    public String getTitle() {
        String title = info.getTitle();
        if (title == null) {
            title = "";
        }
        return title;
    }

    public Integer getYear() {
        return info.getYear();
    }

    public Double getRating() {
        return info.getRating();
    }

    public MediaInfo getInfo() {
        return info;
    }

    public String get(MediaInfo.MediaInfoProperty key) {
        String value = info.get(key);
        if (value == null) {
            value = "";
        }
        return value;
    }

    public void set(MediaInfo.MediaInfoProperty key, String value) {
        info.set(key, value);
    }

}
