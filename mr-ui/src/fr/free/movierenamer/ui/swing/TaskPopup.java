/*
 * Copyright (C) 2013 Nicolas Magré
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

import com.alee.managers.popup.WebPopup;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.worker.impl.RenamerWorker;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Queue;
import javax.swing.BoxLayout;

/**
 * Class TaskPopUp
 *
 * @author Nicolas Magré
 */
public class TaskPopup extends WebPopup {

  private final MovieRenamer mr;

  public TaskPopup(MovieRenamer mr) {
    super();
    this.mr = mr;

    setFocusable(true);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setMargin(10);

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          hidePopup();
        }
      }
    });
    setCloseOnFocusLoss(true);
  }

  public void update() {
    removeAll();
    Queue<RenamerWorker> rqueue = mr.getTaskWorker();
    for (RenamerWorker taskWorker : rqueue) {
      add(taskWorker.getTaskPanel());
    }

    if (rqueue.isEmpty()) {
      hidePopup();
      return;
    }

    revalidate();
  }

  /**
   * Add listener to close popup when click outside
   */
  public void armPopup() {
    Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
      @Override
      public void eventDispatched(AWTEvent event) {

        if (event instanceof MouseEvent) {
          MouseEvent m = (MouseEvent) event;
          if (m.getID() == MouseEvent.MOUSE_RELEASED) {
            hidePopup();
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
          }
        }

        if (event instanceof WindowEvent) {
          WindowEvent we = (WindowEvent) event;
          if (we.getID() == WindowEvent.WINDOW_DEACTIVATED || we.getID() == WindowEvent.WINDOW_STATE_CHANGED) {
            hidePopup();
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
          }
        }
      }
    }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK);
  }
}
