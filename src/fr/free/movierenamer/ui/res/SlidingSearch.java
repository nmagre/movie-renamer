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
 * along with WebLookAndFeel library.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.ui.res;

import com.alee.extended.background.NinePatchBackgroundPainter;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebEditorPane;
import com.alee.laf.text.WebTextField;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyManager;
import com.alee.managers.hotkey.HotkeyRunnable;
import com.alee.managers.tooltip.WebCustomTooltipStyle;
import com.alee.utils.ImageUtils;
import com.alee.utils.LafUtils;
import fr.free.movierenamer.Main;
import info.clearthought.layout.TableLayout;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;

/**
 * User: mgarin Date: 14.02.12 Time: 16:01
 */
public class SlidingSearch {

  private boolean searchShown = false;
  private int contentSpacing = 4;
  private int shadeWidth = 5;
  private int topRound = 16;
  private int bottomRound = 6;
  private boolean connected = true;
  private WebPanel searchPanel;
  private SlidingLayout slideLayout;
  private WebPanel searchPlate;
  private UpperLine leftLine;
  private UpperLine rightLine;
  private WebTextField searchField;

  public SlidingSearch(final JLayeredPane layeredPane) {
    super();


    final WebPanel topPanel = new WebPanel();
    topPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL, TableLayout.PREFERRED, TableLayout.FILL}, {TableLayout.PREFERRED}}));
    topPanel.setOpaque(false);

    searchPanel = new WebPanel() {

      @Override
      public boolean contains(int x, int y) {
        return topPanel.getBounds().contains(x, y) && super.contains(x, y);
      }
    };
    searchPanel.setOpaque(false);
    slideLayout = new SlidingLayout(searchPanel);
    searchPanel.setLayout(slideLayout);
    searchPanel.add(topPanel);


    searchPlate = new WebPanel() {

      private BufferedImage bi = ImageUtils.getBufferedImage(Main.class.getResource("/image/searchBg.png"));
      private Paint bg = new TexturePaint(bi, new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));

      {
        setMargin(contentSpacing - 1,
                (connected ? 0 : shadeWidth) + topRound + contentSpacing + 1,
                shadeWidth + contentSpacing,
                (connected ? 0 : shadeWidth) + topRound + contentSpacing);
      }

      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        LafUtils.setupAntialias(g2d);

        Shape border = createBorderShape();

        LafUtils.drawShade(g2d, border, WebCustomTooltipStyle.shadeColor, shadeWidth);

        g2d.setPaint(bg);
        g2d.fill(border);
        g2d.setPaint(Color.WHITE);
        g2d.draw(border);
      }

      private Shape createBorderShape() {
        int leftSpace = connected ? 0 : shadeWidth;
        int rightSpace = connected ? 0 : shadeWidth;
        int shear = connected ? 0 : -1;
        GeneralPath gp = new GeneralPath();
        gp.moveTo(leftSpace, shear);
        gp.quadTo(leftSpace + topRound, shear, leftSpace + topRound, topRound);
        gp.lineTo(leftSpace + topRound, getHeight() - shadeWidth - bottomRound);
        gp.quadTo(leftSpace + topRound, getHeight() - shadeWidth, leftSpace + topRound + bottomRound, getHeight() - shadeWidth);
        gp.lineTo(getWidth() - rightSpace - topRound - bottomRound, getHeight() - shadeWidth);
        gp.quadTo(getWidth() - rightSpace - topRound, getHeight() - shadeWidth, getWidth() - rightSpace - topRound, getHeight() - shadeWidth - bottomRound);
        gp.lineTo(getWidth() - rightSpace - topRound, topRound);
        gp.quadTo(getWidth() - rightSpace - topRound, shear, getWidth() - rightSpace, shear);
        return gp;
      }
    };
    searchPlate.setOpaque(false);
    searchPlate.setLayout(new BorderLayout());
    searchPlate.setVisible(false);

    leftLine = new UpperLine();
    leftLine.setVisible(false);

    rightLine = new UpperLine();
    rightLine.setVisible(false);

    topPanel.add(leftLine, "0,0");
    topPanel.add(searchPlate, "1,0");
    topPanel.add(rightLine, "2,0");


    searchField = new WebTextField(15);

    searchField.setBackgroundPainter(new NinePatchBackgroundPainter(SlidingSearch.class.getResource("/image/searchField.png")));
    searchField.setForeground(Color.WHITE);
    searchField.setCaretColor(Color.LIGHT_GRAY);
    searchField.setHorizontalAlignment(WebTextField.CENTER);
    searchField.addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(KeyEvent e) {
        if (Hotkey.ENTER.isHotkeyTriggered(e)) {
          toggleSearch();
        }
      }
    });
    searchPlate.add(searchField);


    HotkeyManager.registerHotkey(layeredPane, Hotkey.CTRL_F, new HotkeyRunnable() {

      @Override
      public void run(KeyEvent e) {
        toggleSearch();
      }
    });
    HotkeyManager.registerHotkey(layeredPane, Hotkey.ESCAPE, new HotkeyRunnable() {

      @Override
      public void run(KeyEvent e) {
        if (searchField.getText().length() > 0) {
          searchField.setText("");
        } else if (searchField.isFocusOwner()) {
          searchField.transferFocus();
        }
      }
    });
    searchField.addFocusListener(new FocusAdapter() {

      @Override
      public void focusLost(FocusEvent e) {
        hideSearch();
      }
    });

    layeredPane.add(searchPanel, JLayeredPane.DRAG_LAYER);
    layeredPane.addComponentListener(new ComponentAdapter() {

      @Override
      public void componentResized(ComponentEvent e) {
        searchPanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
      }
    });
  }

  public WebTextField getSearchField() {
    return searchField;
  }

  public void toggleSearch() {
    if (searchShown) {
      hideSearch();
    } else {
      showSearch();
    }
  }

  public void showSearch() {
    if (searchShown || !isSearchEnabled()) {
      return;
    }

    searchShown = true;

    leftLine.setVisible(true);
    searchPlate.setVisible(true);
    rightLine.setVisible(true);

    slideLayout.slideIn();

    searchField.requestFocusInWindow();
  }

  protected boolean isSearchEnabled() {
    return true;
  }

  public void hideSearch() {
    if (searchShown) {
      searchShown = false;
      searchField.setText("");
      slideLayout.slideOut();
    }
  }

  private class UpperLine extends JComponent {

    public UpperLine() {
      super();
    }

    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D g2d = (Graphics2D) g;
      LafUtils.setupAntialias(g2d);

      Shape border = new Line2D.Double(0, 0, getWidth(), 0);

      LafUtils.drawShade(g2d, border, WebCustomTooltipStyle.shadeColor, shadeWidth);

      g2d.setPaint(Color.WHITE);
      g2d.draw(border);
    }
  }
}