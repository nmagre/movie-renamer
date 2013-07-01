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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;
import javax.swing.table.AbstractTableModel;

/**
 * Class LogsTableModel
 * @author Nicolas Magré
 */
public class LogsTableModel extends AbstractTableModel {
  private static final long serialVersionUID = 1L;

  private final List<LogRecord> logs;
  private enum Tablefield {
    ID,
    Level,
    Thread_ID,
    Logger,
    Class,
    Method,
    Message;
  }

  public LogsTableModel() {
    logs = new ArrayList<LogRecord>();
  }

  @Override
  public int getRowCount() {
    return logs.size();
  }

  @Override
  public int getColumnCount() {
    return Tablefield.values().length;
  }

  @Override
  public String getColumnName(int columnIndex) {
    return Tablefield.values()[columnIndex].name();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    switch (Tablefield.values()[columnIndex]) {
      case ID:
        return logs.get(rowIndex).getSequenceNumber();
      case Level:
        return logs.get(rowIndex).getLevel();
      case Thread_ID:
        return logs.get(rowIndex).getThreadID();
      case Class:
        return logs.get(rowIndex).getSourceClassName();
      case Method:
        return logs.get(rowIndex).getSourceMethodName();
      case Message:
        return logs.get(rowIndex).getMessage();
      case Logger:
        return logs.get(rowIndex).getLoggerName();
      default:
        return null;
    }
  }

  public synchronized void addRecord(LogRecord record) {
    logs.add(record);
    fireTableRowsInserted(logs.size() - 1, logs.size() - 1);
  }
}
