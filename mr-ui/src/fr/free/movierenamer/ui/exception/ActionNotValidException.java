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
package fr.free.movierenamer.ui.exception;

/**
 * Class ActionNotValidException, Exception on non valid action
 * @author Nicolas Magré
 */
public class ActionNotValidException extends Exception {
  private static final long serialVersionUID = 1L;

	public ActionNotValidException() {}

	public ActionNotValidException(String message) {
		super(message);
	}

	public ActionNotValidException(Throwable cause) {
		super(cause);
	}

	public ActionNotValidException(String message, Throwable cause) {
		super(message, cause);
	}
}
