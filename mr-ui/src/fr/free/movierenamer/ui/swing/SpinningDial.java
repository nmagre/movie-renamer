/* -*- java -*-
 * $Id:$
 * Copyright (c) 2006 Timothy Wall, All Rights Reserved
 */
package fr.free.movierenamer.ui.swing;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Class SpinningDial
 *
 * Modified by Nicolas Magré
 * Note : This file is not under GPL licence (CPL)
 *
 * @author Timothy Wall
 * @see http://abbot.sourceforge.net/doc/overview.shtml
 */

/**
 * Provides a spinning disk of hash marks.
 */
public class SpinningDial implements Icon {

  private static final int MIN_ALPHA = 32;
  private static final int SPOKES = 16;
  private static final int DEFAULT_INTERVAL = 1000 / 24;
  public static final int SPIN_INTERVAL = 1000 / SPOKES;
  private Timer timer;
  private int frame;
  private int w;
  private int h;
  private Image[] frames;
  private Set<RepaintArea> repaints = new HashSet<RepaintArea>();

  public SpinningDial(int w, int h) {
    this.w = w;
    this.h = h;
    frames = new Image[SPOKES];
    setFrameInterval(DEFAULT_INTERVAL);
  }

  @Override
  public int getIconHeight() {
    return h;
  }

  @Override
  public int getIconWidth() {
    return w;
  }

  @Override
  public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
    paintFrame(c, g, x, y);
    if (c != null) {
      int w = getIconWidth();
      int h = getIconHeight();
      AffineTransform tx = ((Graphics2D) g).getTransform();
      w = (int) (w * tx.getScaleX());
      h = (int) (h * tx.getScaleY());
      registerRepaintArea(c, x, y, w, h);
    }
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    timer.stop();
  }

  /**
   * Trigger a repaint on all components on which we've previously been
   * painted.
   */
  protected synchronized void repaint() {
    for (Iterator<RepaintArea> i = repaints.iterator(); i.hasNext();) {
      i.next().repaint();
    }
    repaints.clear();
  }

  /**
   * Register repaint areas, which get get cleared once the repaint request
   * has been queued.
   *
   * @param c
   * @param x
   * @param y
   * @param w
   * @param h
   */
  protected void registerRepaintArea(Component c, int x, int y, int w, int h) {
    if (timer != null && !timer.isRunning()) {
      timer.start();
    }
    repaints.add(new RepaintArea(c, x, y, w, h));
  }

  /**
   * Advance to the next animation frame.
   */
  public void nextFrame() {
    setFrame(getFrame() + 1);
  }

  /**
   * Set the current animation frame number.
   *
   * @param f
   */
  public void setFrame(int f) {
    this.frame = f;
    if (SPOKES != 0) {
      frame = frame % SPOKES;
    }
    repaint();
  }

  /**
   * Returns the current animation frame number.
   *
   * @return
   */
  public int getFrame() {
    return frame;
  }

  /**
   * Setting a frame interval of zero stops automatic animation.
   *
   * @param interval
   */
  public void setFrameInterval(int interval) {
    if (interval != 0) {
      if (timer == null) {
        timer = new Timer(interval, new AnimationUpdater(this));
        timer.setRepeats(true);
      } else {
        timer.setDelay(interval);
      }
    } else if (timer != null) {
      timer.stop();
      timer = null;
    }
  }

  /**
   * Object to encapsulate an area on a component to be repainted.
   */
  private class RepaintArea {

    public int x, y, w, h;
    public Component component;
    private int hashCode;

    public RepaintArea(Component c, int x, int y, int w, int h) {
      Component ancestor = findNonRendererAncestor(c);
      if (ancestor != c) {
        Point pt = SwingUtilities.convertPoint(c, x, y, ancestor);
        c = ancestor;
        x = pt.x;
        y = pt.y;
      }
      this.component = c;
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      String hash = String.valueOf(x) + "," + y + ":" + c.hashCode();
      this.hashCode = hash.hashCode();
    }

    /**
     * Find the first ancestor <em>not</em> descending from a
     * {@link CellRendererPane}.
     */
    private Component findNonRendererAncestor(Component c) {
      Component ancestor = SwingUtilities.getAncestorOfClass(CellRendererPane.class, c);
      if (ancestor != null && ancestor != c && ancestor.getParent() != null) {
        c = findNonRendererAncestor(ancestor.getParent());
      }
      return c;
    }

    /**
     * Queue a repaint request for this area.
     */
    public void repaint() {
      component.repaint(x, y, w, h);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof RepaintArea) {
        RepaintArea area = (RepaintArea) o;
        return area.component == component
                && area.x == x && area.y == y
                && area.w == w && area.h == h;
      }
      return false;
    }

    /**
     * Since we're using a HashSet.
     */
    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public String toString() {
      return "Repaint(" + component.getClass().getName() + "@" + x + "," + y + " " + w + "x" + h + ")";
    }
  }

  private static class AnimationUpdater implements ActionListener {

    private WeakReference<SpinningDial> ref;

    public AnimationUpdater(SpinningDial icon) {
      this.ref = new WeakReference<SpinningDial>(icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      SpinningDial icon = ref.get();
      if (icon != null) {
        icon.nextFrame();
      }
    }
  }

  /**
   * Set the stroke width according to the size.
   *
   * @param size
   * @return
   */
  protected float getStrokeWidth(int size) {
    return size / 16f;
  }

  protected void paintFrame(Component c, Graphics graphics, int x, int y) {
    int idx = getFrame();
    if (frames[idx] == null) {
      int w = getIconWidth();
      int h = getIconHeight();
      int size = Math.min(w, h);
      Image image = (c != null && c.getGraphicsConfiguration() != null ? c.getGraphicsConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT) : new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
      Graphics2D g = (Graphics2D) image.getGraphics();
      g.setComposite(AlphaComposite.Clear);
      g.fillRect(0, 0, w, h);
      g.setComposite(AlphaComposite.Src);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      final int FULL_SIZE = 256;
      float strokeWidth = getStrokeWidth(FULL_SIZE);
      float fraction = .6f;
      g.setStroke(new BasicStroke(strokeWidth,
              BasicStroke.CAP_ROUND,
              BasicStroke.JOIN_ROUND));
      g.translate((float) w / 2, (float) h / 2);
      float scale = (float) size / FULL_SIZE;
      g.scale(scale, scale);
      int alpha = 255;
      int x1, y1, x2, y2;
      int radius = FULL_SIZE / 2 - 1 - (int) (strokeWidth / 2);
      int frameCount = SPOKES;
      for (int i = 0; i < frameCount; i++) {
        double cos = Math.cos(Math.PI * 2 - Math.PI * 2 * (i - idx) / frameCount);
        double sin = Math.sin(Math.PI * 2 - Math.PI * 2 * (i - idx) / frameCount);
        x1 = (int) (radius * fraction * cos);
        x2 = (int) (radius * cos);
        y1 = (int) (radius * fraction * sin);
        y2 = (int) (radius * sin);
        g.setColor(new Color(0, 0, 0, Math.min(255, alpha)));
        g.drawLine(x1, y1, x2, y2);
        alpha = Math.max(MIN_ALPHA, alpha * 3 / 4);
      }
      g.dispose();
      frames[idx] = image;
    }
    graphics.drawImage(frames[idx], x, y, null);
  }

  @Override
  public String toString() {
    return "SpinningDial(" + getIconWidth() + "x" + getIconHeight() + ")";
  }
}
