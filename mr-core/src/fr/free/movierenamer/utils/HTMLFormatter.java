/*
 * movie-renamer-core
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
package fr.free.movierenamer.utils;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Class HTMLFormatter
 *
 * @author Nicolas Magré
 */
public class HTMLFormatter extends Formatter {

  @Override
  public String format(LogRecord record) {

    String clazz = "panel-success";
    if (record.getLevel() == Level.SEVERE) {
      clazz = "panel-danger";
    } else if (record.getLevel() == Level.WARNING) {
      clazz = "panel-warning";
    } else if (record.getLevel() == Level.INFO) {
      clazz = "panel-primary";
    }

    String recordStr = "<div class=\"panel " + clazz + "\">"
      + "<div class=\"panel-heading\"><p style=\"float: right\">" + (new java.util.Date(record.getMillis())).toString() + "</p>"
      + "<h3 class=\"panel-title\">" + record.getLoggerName() + " : " + record.getSourceClassName() + " -> " + record.getSourceMethodName() + "</h3>"
      + "</div><div class=\"panel-body\">" + record.getMessage() + "</div></div>";

    return recordStr;
  }

  @Override
  public String getHead(Handler h) {
    return "<!DOCTYPE html><html lang=\"en\"><head>"
      + "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\">"
      + "</head><body><div class=\"container\"><div class=\"row\"><div class=\"col-ld-10\" role=\"main\">"
      + "<h1 id=\"overview\" class=\"page-header\">Movie Renamer log</h1><div class=\"panel panel-primary\" style=\"padding:20px\">";
  }

  @Override
  public String getTail(Handler h) {
    return "</div></div></div></div></body></html>";
  }

}
