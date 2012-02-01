/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.movie;

/**
 *
 * @author duffy
 */
public class MovieImage {

    private String id;
    private String type;
    private String orig;
    private String medium;
    private String thumb;

    public MovieImage(String id, String type) {
        this.id = id;
        this.type = type;
        orig = "";
        medium = "";
        thumb = "";
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getOrigUrl() {
        return orig;
    }

    public String getMidUrl() {
        return medium;
    }

    public String getThumbUrl() {
        return thumb;
    }

    public void setOrigUrl(String url) {
        orig = url;
    }

    public void setMidUrl(String url) {
        medium = url;
    }

    public void setThumbUrl(String url) {
        thumb = url;
    }

    @Override
    public String toString() {
        return id + " : " + type;
    }
}
