/*
 * This file is part of WebLookAndFeel library.
 *
 * WebLookAndFeel library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WebLookAndFeel library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WebLookAndFeel library.  If not, see <http://www.gnu.org/licenses/>.*/
package fr.free.movierenamer.ui.panel;

import com.alee.extended.image.WebImageGallery;
import com.alee.laf.StyleConstants;
import com.alee.laf.scroll.WebScrollBarUI;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.utils.ImageUtils;
import com.alee.utils.LafUtils;
import com.alee.utils.SwingUtils;
import com.alee.utils.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * User: mgarin Date: 05.09.11 Time: 15:45
 */
/**
 * Class CustomWebImageGallery
 */
public class CustomWebImageGallery extends WebImageGallery {

  private int spacing = 20;
  private int imageLength = 200;
  private int borderWidth = 3;
  private float fadeHeight = 0.7f;
  private int opacity = 125;
  private Color light = new Color(128, 128, 128);
  private Color selectedLight = new Color(255, 255, 255);
  private Color transparent = new Color(128, 128, 128, 0);
  private Color selectedTransparent = new Color(255, 255, 255, 0);
  private int maxWidth = 0;
  private int maxHeight = 0;
  private List<ImageIcon> images = new ArrayList<ImageIcon>();
  private List<BufferedImage> reflections = new ArrayList<BufferedImage>();
  private List<String> descriptions = new ArrayList<String>();
  //    private List<Integer> sizes = new ArrayList<Integer> (  );
  private int preferredColumnCount = 4;
  private boolean scrollOnSelection = true;
  private int selectedIndex = -1;
  private int oldSelectedIndex = -1;
  private float progress = 0f;
  private Timer reflectionMover = null;
  private WebScrollPane view;

  public CustomWebImageGallery() {
    super();

    SwingUtils.setOrientation(this);
    setFocusable(true);
    setFont(new JLabel().getFont().deriveFont(Font.BOLD));

    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
          CustomWebImageGallery.this.requestFocusInWindow();
          for (int i = 0; i < images.size(); i++) {
            if (getImageRect(i).contains(e.getPoint())) {
              setSelectedIndex(i);
              break;
            }
          }
        }
      }

      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        int index = getSelectedIndex();
        int maxIndex = images.size() - 1;
        int wheelRotation = e.getWheelRotation();
        int newIndex;
        if (wheelRotation > 0) {
          newIndex = index + wheelRotation;
          while (newIndex > maxIndex) {
            newIndex -= images.size();
          }
        } else {
          newIndex = index + wheelRotation;
          while (newIndex < 0) {
            newIndex += images.size();
          }
        }
        setSelectedIndex(newIndex);
      }
    };
    addMouseListener(mouseAdapter);
    addMouseWheelListener(mouseAdapter);

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (images.size() > 0) {
          int si = getSelectedIndex();
          if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            setSelectedIndex(si == -1 || si == 0 ? images.size() - 1 : si - 1);
          } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            setSelectedIndex(si == -1 || si == images.size() - 1 ? 0 : si + 1);
          } else if (e.getKeyCode() == KeyEvent.VK_HOME) {
            setSelectedIndex(0);
          } else if (e.getKeyCode() == KeyEvent.VK_END) {
            setSelectedIndex(images.size() - 1);
          }
        }
      }
    });
  }

  @Override
  public List<ImageIcon> getImages() {
    return images;
  }

  @Override
  public int getPreferredColumnCount() {
    return preferredColumnCount;
  }

  @Override
  public void setPreferredColumnCount(int preferredColumnCount) {
    this.preferredColumnCount = preferredColumnCount;
  }

  @Override
  public WebScrollPane getView() {
    return getView(true);
  }

  @Override
  public WebScrollPane getView(boolean withBorder) {
    if (view == null) {
      view = new WebScrollPane(CustomWebImageGallery.this, withBorder) {
        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredSize() {
          int columns = Math.min(images.size(), preferredColumnCount);
          return new Dimension(spacing * (columns + 1) + columns * maxWidth,
                  CustomWebImageGallery.this.getPreferredSize().height + WebScrollBarUI.LENGTH);
        }
      };
      view.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      view.setVerticalScrollBarPolicy(WebScrollPane.VERTICAL_SCROLLBAR_NEVER);

      InputMap im = view.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      im.put(KeyStroke.getKeyStroke("UP"), "none");
      im.put(KeyStroke.getKeyStroke("DOWN"), "none");
      im.put(KeyStroke.getKeyStroke("LEFT"), "none");
      im.put(KeyStroke.getKeyStroke("RIGHT"), "none");
    }
    return view;
  }

  @Override
  public int getImageLength() {
    return imageLength;
  }

  @Override
  public void setImageLength(int imageLength) {
    this.imageLength = imageLength;
    refresh();
  }

  @Override
  public boolean isScrollOnSelection() {
    return scrollOnSelection;
  }

  @Override
  public void setScrollOnSelection(boolean scrollOnSelection) {
    this.scrollOnSelection = scrollOnSelection;
  }

  @Override
  public int getSelectedIndex() {
    return selectedIndex;
  }

  @Override
  public void setSelectedIndex(int selectedIndex) {
    if (this.selectedIndex == selectedIndex) {
      return;
    }

    this.oldSelectedIndex = this.selectedIndex;
    this.selectedIndex = selectedIndex;
    repaint();
    if (scrollOnSelection) {
      Rectangle rect = getImageRect(selectedIndex);
      if(rect == null) {
        return;
      }
      SwingUtils.scrollSmoothly(getView(), rect.x + rect.width / 2 - CustomWebImageGallery.this.getVisibleRect().width / 2, rect.y);
    }
    moveReflection();
  }

  private void moveReflection() {
    if (reflectionMover != null && reflectionMover.isRunning()) {
      reflectionMover.stop();
    }

    progress = 0f;
    reflectionMover = new Timer("WebImageGallery.reflectionMoveTimer", StyleConstants.fastAnimationDelay, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (progress < 1f) {
          progress += 0.08f;
          progress = Math.min(progress, 1f);
          CustomWebImageGallery.this.repaint();
        } else {
          reflectionMover.stop();
        }
      }
    });
    reflectionMover.start();
  }

  public boolean isEmpty() {
    return images.isEmpty();
  }

  @Override
  public Rectangle getImageRect(int index) {
    if(images.isEmpty()) {
      return null;
    }
    int iconWidth = images.get(index).getIconWidth();
    int iconHeight = images.get(index).getIconHeight();
    Dimension ps = getPreferredSize();
    int x = (getWidth() > ps.width ? (getWidth() - ps.width) / 2 : 0) + spacing
            + (maxWidth + spacing) * index + maxWidth / 2;
    int y = getHeight() / 2 - spacing / 2 - iconHeight / 2;
    return new Rectangle(x - iconWidth / 2, y - iconHeight / 2, iconWidth, iconHeight);
  }

  @Override
  public void addImage(ImageIcon image) {
    addImage(0, image);
  }

  @Override
  public void addImage(int index, ImageIcon image) {
    try {
      ImageIcon previewIcon = ImageUtils.createPreviewIcon(image, imageLength);
      int rwidth = previewIcon.getIconWidth() * (imageLength /600);
      int rheight = previewIcon.getIconHeight() * (imageLength /600);

      BufferedImage reflection = ImageUtils.createCompatibleImage(rwidth, rheight, Transparency.TRANSLUCENT);
      Graphics2D g2d = reflection.createGraphics();
      LafUtils.setupAntialias(g2d);
      g2d.drawImage(previewIcon.getImage(), 0, 0, null);
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
      g2d.setPaint(new GradientPaint(0, rheight * (1f - fadeHeight), new Color(0, 0, 0, 0), 0, rheight,
              new Color(0, 0, 0, opacity)));
      g2d.fillRect(0, 0, rwidth, rheight);
      g2d.dispose();

      images.add(index, previewIcon);
      descriptions.add(index, image.getIconWidth() + " x " + image.getIconHeight() + " px \n" + image.getDescription());

      reflections.add(index, reflection);
    } catch (Throwable e) {
      // Out of memory
    }

    recalcualteMaxSizes();
    updateContainer();
  }

  public void refresh() {

    recalcualteMaxSizes();
    updateContainer();
  }

  @Override
  public void removeImage(ImageIcon image) {
    if (images.contains(image)) {
      removeImage(images.indexOf(image));
    }
  }

  @Override
  public void removeImage(int index) {
    if (index >= 0 && index < images.size()) {
      boolean wasSelected = getSelectedIndex() == index;

      images.remove(index);
      descriptions.remove(index);
      reflections.remove(index).flush();
      recalcualteMaxSizes();
      updateContainer();

      if (wasSelected && images.size() > 0) {
        setSelectedIndex(index < images.size() ? index : index - 1);
      }
    }
  }

  private void updateContainer() {
    if (getParent() instanceof JComponent) {
      ((JComponent) getParent()).revalidate();
    }
    repaint();
  }

  private void recalcualteMaxSizes() {
    for (ImageIcon icon : images) {
      maxWidth = Math.max(maxWidth, icon.getIconWidth() * (imageLength /600));
      maxHeight = Math.max(maxHeight, icon.getIconHeight() * (imageLength /600));
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    int height = getHeight();
    int width = getWidth();

    Graphics2D g2d = (Graphics2D) g;
    LafUtils.setupAntialias(g2d);

    g2d.setPaint(new GradientPaint(0, 0, Color.black, 0, height, Color.darkGray));
    g2d.fillRect(0, 0, width, height);

    Rectangle vr = getVisibleRect();
    Dimension ps = getPreferredSize();
    Composite oldComposite = g2d.getComposite();
    for (int i = 0; i < images.size(); i++) {
      if (!getImageRect(i).intersects(vr)) {
        continue;
      }

      ImageIcon icon = images.get(i);
      BufferedImage bi = ImageUtils.getBufferedImage(icon);
      int imageWidth = icon.getIconWidth() * (imageLength /600);
      int imageHeight = icon.getIconHeight() * (imageLength /600);

      int x = (getWidth() > ps.width ? (getWidth() - ps.width) / 2 : 0) + spacing
              + (maxWidth + spacing) * i + maxWidth / 2;
      int y = height / 2 - spacing / 2 - imageHeight / 2;
      int y2 = height / 2 + spacing / 2 + imageHeight / 2;

      // Initial image

      float add = selectedIndex == i ? progress * 0.4f : (oldSelectedIndex == i ? 0.4f - progress * 0.4f : 0);
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f + add));

      g2d.drawImage(bi, x - imageWidth / 2, y - imageHeight / 2, null);

      g2d.setPaint(selectedIndex == i ? Color.WHITE : Color.GRAY);
      Area gp = new Area(new RoundRectangle2D.Double(x - imageWidth / 2 - borderWidth, y - imageHeight / 2 - borderWidth,
              imageWidth + borderWidth * 2, imageHeight + borderWidth * 2, borderWidth * 2, borderWidth * 2));
      gp.subtract(new Area(new Rectangle(x - imageWidth / 2, y - imageHeight / 2, imageWidth, imageHeight)));
      g2d.fill(gp);

      g2d.setComposite(oldComposite);

      // Info text

      if (selectedIndex == i || oldSelectedIndex == i) {
        float opacity = selectedIndex == i ? progress : 1f - progress;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2d.setPaint(Color.WHITE);
        String infoText = descriptions.get(i);
        g2d.drawString(infoText, x - g2d.getFontMetrics().stringWidth(infoText) / 2,
                getHeight() / 2 + spacing / 2 + g2d.getFontMetrics().getAscent() / 2);
        g2d.setComposite(oldComposite);
      }

      // Reflection

      int rwidth = imageWidth + borderWidth * 2;
      int rheight = imageHeight + borderWidth * 2;

      int addition = selectedIndex == i ? Math.round(progress * spacing)
              : (oldSelectedIndex == i ? spacing - Math.round(progress * spacing) : 0);
      if (reflections.get(i) != null) {
        g2d.drawImage(reflections.get(i), x - imageWidth / 2, y2 + imageHeight / 2 + addition, imageWidth, -imageHeight,
                null);
      }

      gp = new Area(new RoundRectangle2D.Double(x - rwidth / 2, y2 - rheight / 2 + addition, rwidth, rheight, borderWidth * 2,
              borderWidth * 2));
      gp.subtract(new Area(
              new Rectangle(x - rwidth / 2 + borderWidth, y2 - rheight / 2 + addition + borderWidth, rwidth - borderWidth * 2,
              rheight - borderWidth * 2)));
      g2d.setPaint(new GradientPaint(0, y2 - imageHeight / 2 + addition, selectedIndex == i ? selectedLight : light, 0,
              y2 - imageHeight / 2 + addition + imageHeight * fadeHeight, selectedIndex == i ? selectedTransparent : transparent));
      g2d.fill(gp);
    }
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(spacing * (images.size() + 1) + maxWidth * images.size(), spacing * 3 + maxHeight * 2);
  }
}
