/******************************************************************************
*                                                                             *
*    Movie Renamer                                                            *
*    Copyright (C) 2011 Magré Nicolas                                         *
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

package plugins;

import java.beans.PropertyChangeListener;

/**
 * Interface IPlugin
 * @author Nicolas Magré
 */
public interface IPlugin {
  public String getName();
  public void exec(PropertyChangeListener proper);
  public boolean checkForUpdate();
  public boolean update();
  public String getVersion();
}
