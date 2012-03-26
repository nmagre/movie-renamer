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
package fr.free.movierenamer.ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.movie.MovieImage;
import fr.free.movierenamer.movie.MovieInfo;
import fr.free.movierenamer.ui.res.DropImage;
import fr.free.movierenamer.utils.Settings;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import plugins.IPluginInfo;

/**
 * Class MoviePanel
 * @author Magré Nicolas
 */
public class MoviePanel extends javax.swing.JPanel {
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JPanel InfoPnl;
  private JList actorList;
  private JPanel actorPnl;
  private JScrollPane actorScroll;
  private JTextField countryField;
  private JPanel detailsPnl;
  private JTextField directorField;
  private final JList fanartList = new JList();
  private JScrollPane fanartsScrollPane;
  private JTextField genreField;
  private JPanel imagePnl;
  private JTabbedPane movieTabbedPane;
  private JTextField origTitleField;
  private JTextField runtimeField;
  private JLabel star;
  private JLabel star1;
  private JLabel star2;
  private JLabel star3;
  private JLabel star4;
  private JScrollPane synopsScroll;
  private JTextArea synopsisArea;
  private JLabel thumbLbl;
  private final JList thumbnailsList = new JList(){
    // This method is called as the cursor moves within the list.
    public String getToolTipText(MouseEvent evt) {
      int index = locationToIndex(evt.getPoint());
      if(index == -1) return null;
      ImageIcon item = (ImageIcon) getModel().getElementAt(index);
      return item.getDescription();
    }
  };
  private JScrollPane thumbsScrollPane;
  private JLabel titleLbl;
  private JTextField yearField;
  // End of variables declaration//GEN-END:variables
  private final DefaultListModel fanartModel = new DefaultListModel();
  private final DefaultListModel thumbnailModel = new DefaultListModel();
  private final DefaultListModel actorModel = new DefaultListModel();
  private final ImageIcon actorDefault = new ImageIcon(getClass().getResource("/image/unknown.png"));
  private Dimension thumbDim = new Dimension(160, 200);
  public Dimension thumbListDim = new Dimension(60, 90);
  public Dimension fanartListDim = new Dimension(200, 90);
  public Dimension actorListDim = new Dimension(60, 90);
  private final Icon STAR = new ImageIcon(getClass().getResource("/image/star.png"));
  private final Icon STAR_HALF = new ImageIcon(getClass().getResource("/image/star-half.png"));
  private final Icon STAR_EMPTY = new ImageIcon(getClass().getResource("/image/star-empty.png"));
  private Image fanartBack;
  private DropTarget dropThumbTarget;
  private DropTarget dropFanartTarget;
  private ArrayList<actorImage> actors;
  private ArrayList<MovieImage> thumbs;
  private ArrayList<MovieImage> fanarts;
  private Settings setting;

  /** Creates new form MoviePanel
   * @param setting
   * @param pluginsInfo
   */
  public MoviePanel(Settings setting, IPluginInfo[] pluginsInfo) {
    this.setting = setting;

    initComponents();
    actors = new ArrayList<actorImage>();
    thumbs = new ArrayList<MovieImage>();
    fanarts = new ArrayList<MovieImage>();

    thumbnailsList.setModel(thumbnailModel);
    fanartList.setModel(fanartModel);
    actorList.setModel(actorModel);

    thumbnailsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    thumbnailsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    thumbnailsList.setVisibleRowCount(-1);
    thumbnailsList.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (thumbnailsList.getSelectedIndex() == -1) return;
        thumbnailsList.ensureIndexIsVisible(thumbnailsList.getSelectedIndex());
        Image img = getImage(thumbs.get(thumbnailsList.getSelectedIndex()).getThumbUrl().replace(".png", ".jpg"), Cache.thumb);
        if (img != null)
          thumbLbl.setIcon(new ImageIcon(img.getScaledInstance(thumbDim.width, thumbDim.height, Image.SCALE_DEFAULT)));
      }
    });

    DropImage dropThumb = new DropImage(this, Cache.thumb, setting);
    dropThumbTarget = new DropTarget(thumbnailsList, dropThumb);

    fanartList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    fanartList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    fanartList.setVisibleRowCount(-1);
    fanartList.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent lse) {
        if (fanartList.getSelectedIndex() == -1) return;
        fanartList.ensureIndexIsVisible(fanartList.getSelectedIndex());
        fanartBack = getImage(fanarts.get(fanartList.getSelectedIndex()).getThumbUrl(), Cache.fanart);
        detailsPnl.validate();
        detailsPnl.repaint();
      }
    });

    DropImage dropFanart = new DropImage(this, Cache.fanart, setting);
    dropFanartTarget = new DropTarget(fanartList, dropFanart);

    actorList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    actorList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    actorList.setVisibleRowCount(-1);
    actorList.setCellRenderer(new DefaultListCellRenderer() {

      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (index >= actors.size()) return label;
        Icon icon = null;
        icon = actors.get(index).getImage();

        if (icon != null) label.setIcon(icon);
        else label.setIcon(actorDefault);
        return label;
      }
    });


    for (int i = 0; i < pluginsInfo.length; i++) {
      if (pluginsInfo[i].getInfoPanel() != null)
        movieTabbedPane.addTab(pluginsInfo[i].getName(), pluginsInfo[i].getInfoPanel());
    }

    dropFanartTarget.setActive(false);
    dropThumbTarget.setActive(false);
    thumbsScrollPane.setVisible(setting.thumb);
    fanartsScrollPane.setVisible(setting.fanart);
    imagePnl.setVisible(setting.thumb || setting.fanart);
    fanartBack = null;
  }

  public void setDisplay(Settings setting) {
    thumbsScrollPane.setVisible(setting.thumb);
    fanartsScrollPane.setVisible(setting.fanart);
    imagePnl.setVisible(setting.thumb || setting.fanart);
  }

  private Image getImage(String strUrl, int cache) {
    Image image = null;
    try {
      URL url = new URL(strUrl);
      image = MoviePanel.this.setting.cache.getImage(url, cache);
      if (image == null) {
        MoviePanel.this.setting.cache.add(url.openStream(), url.toString(), cache);
        image = MoviePanel.this.setting.cache.getImage(url, cache);
      }
    } catch (IOException ex) {
      setting.getLogger().log(Level.SEVERE, ex.toString());
    }
    return image;
  }

  public synchronized void addThumbToList(final Image thumb, final MovieImage mvImg, final boolean selectLast) {

    thumbs.add(mvImg);

    final SwingWorker<Image, Void> worker = new SwingWorker<Image, Void>() {

      @Override
      protected Image doInBackground() throws Exception {
        Image image = null;
        if (thumbnailModel.isEmpty())
          image = getImage(thumbs.get(0).getThumbUrl(), Cache.thumb);
        return image;
      }
    };

    worker.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getNewValue().equals(SwingWorker.StateValue.DONE))
          try {
            Image img = (Image) worker.get();
            if (img != null)
              thumbLbl.setIcon(new ImageIcon(img.getScaledInstance(thumbDim.width, thumbDim.height, Image.SCALE_DEFAULT)));
            if (thumb != null)
              thumbnailModel.addElement(new ImageIcon(thumb.getScaledInstance(thumbListDim.width, thumbListDim.height, Image.SCALE_DEFAULT)));
            if (!thumbnailModel.isEmpty())
              thumbnailsList.setSelectedIndex((selectLast ? (thumbnailModel.size() - 1) : 0));
          } catch (InterruptedException ex) {
            setting.getLogger().log(Level.SEVERE, ex.toString());
          } catch (ExecutionException ex) {
            setting.getLogger().log(Level.SEVERE, ex.toString());
          }
      }
    });

    worker.execute();
  }

  public synchronized void addFanartToList(final Image fanart, final MovieImage mvImg, final boolean selectLast) {
    fanarts.add(mvImg);
    final SwingWorker<Image, Void> worker = new SwingWorker<Image, Void>() {

      @Override
      protected Image doInBackground() throws Exception {
        Image img = null;
        if (fanartModel.isEmpty())
          img = getImage(fanarts.get(0).getThumbUrl(), Cache.fanart);

        return img;
      }
    };

    worker.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getNewValue().equals(SwingWorker.StateValue.DONE)) {
          try {
            fanartBack = (Image) worker.get();
            if (fanartBack != null) {
              detailsPnl.validate();
              detailsPnl.repaint();
            }

          } catch (InterruptedException ex) {
            Logger.getLogger(MoviePanel.class.getName()).log(Level.SEVERE, null, ex);
          } catch (ExecutionException ex) {
            Logger.getLogger(MoviePanel.class.getName()).log(Level.SEVERE, null, ex);
          }

          if (fanart != null)
            fanartModel.addElement(new ImageIcon(fanart.getScaledInstance(fanartListDim.width, fanartListDim.height, Image.SCALE_DEFAULT)));
          if (!fanartModel.isEmpty())
            fanartList.setSelectedIndex((selectLast ? (fanartModel.size() - 1) : 0));
        }
      }
    });

    worker.execute();
  }

  public void addActorToList(final String actor, final Image actorImg, final String desc) {
    ImageIcon icon = null;
    if (actorImg != null) icon = new ImageIcon(actorImg.getScaledInstance(actorListDim.width, actorListDim.height, Image.SCALE_DEFAULT), desc);
    actors.add(new actorImage(actor, desc, icon));
    SwingUtilities.invokeLater(new Thread() {

      @Override
      public void run() {
        actorModel.addElement(actor);
      }
    });
  }

  public void clearList() {
    fanarts.clear();
    thumbs.clear();
    SwingUtilities.invokeLater(new Thread() {

      @Override
      public void run() {
        dropFanartTarget.setActive(false);
        dropThumbTarget.setActive(false);
        fanartBack = null;
        fanartModel.clear();
        thumbnailModel.clear();
        actorModel.clear();
        origTitleField.setText("");
        yearField.setText("");
        runtimeField.setText("");
        synopsisArea.setText("");
        genreField.setText("");
        directorField.setText("");
        countryField.setText("");
        titleLbl.setText("");
        thumbLbl.setIcon(null);
        star.setIcon(STAR_EMPTY);
        star1.setIcon(STAR_EMPTY);
        star2.setIcon(STAR_EMPTY);
        star3.setIcon(STAR_EMPTY);
        star4.setIcon(STAR_EMPTY);
        actors.clear();
        validate();
        repaint();
      }
    });
  }

  public void clearActorList() {
    actorModel.clear();
    actors.clear();
  }

  public void clearThumbList() {
    thumbnailModel.clear();
    thumbs.clear();
  }

  public void clearFanartList() {
    fanartModel.clear();
    fanarts.clear();
  }

  public void addMovieInfo(final MovieInfo movieInfo) {
    SwingUtilities.invokeLater(new Thread() {

      @Override
      public void run() {
        titleLbl.setText(movieInfo.getTitle());
        origTitleField.setText(movieInfo.getOrigTitle());
        yearField.setText(movieInfo.getYear());
        runtimeField.setText(movieInfo.getRuntime() + " min");
        synopsisArea.setText(movieInfo.getSynopsis());
        genreField.setText(movieInfo.getGenresString());
        directorField.setText(movieInfo.getDirectorsString());
        countryField.setText(movieInfo.getCountriesString());
        setRate(Float.parseFloat(movieInfo.getRating().replace(",", ".")));
        dropFanartTarget.setActive(true);
        dropThumbTarget.setActive(true);

        origTitleField.setCaretPosition(0);
        synopsisArea.setCaretPosition(0);
        genreField.setCaretPosition(0);
        directorField.setCaretPosition(0);
        countryField.setCaretPosition(0);

        if (!setting.thumb)
          if (!movieInfo.getImdbThumb().equals("")) {
            Image imThumb = getImage(movieInfo.getImdbThumb(), Cache.thumb);
            if (imThumb != null) thumbLbl.setIcon(new ImageIcon(imThumb.getScaledInstance(thumbDim.width, thumbDim.height, Image.SCALE_DEFAULT)));
          }
      }
    });
  }

  private void setRate(Float rate) {
    if (rate < 0.00) return;
    rate /= 2;
    int n = rate.intValue();
    switch (n) {
      case 0:
        break;
      case 1:
        star.setIcon(STAR);
        if ((rate - rate.intValue()) >= 0.50) star1.setIcon(STAR_HALF);
        break;
      case 2:
        star.setIcon(STAR);
        star1.setIcon(STAR);
        if ((rate - rate.intValue()) >= 0.50) star2.setIcon(STAR_HALF);
        break;
      case 3:
        star.setIcon(STAR);
        star1.setIcon(STAR);
        star2.setIcon(STAR);
        if ((rate - rate.intValue()) >= 0.50) star3.setIcon(STAR_HALF);
        break;
      case 4:
        star.setIcon(STAR);
        star1.setIcon(STAR);
        star2.setIcon(STAR);
        star3.setIcon(STAR);
        if ((rate - rate.intValue()) >= 0.50) star4.setIcon(STAR_HALF);
        break;
      case 5:
        star.setIcon(STAR);
        star1.setIcon(STAR);
        star2.setIcon(STAR);
        star3.setIcon(STAR);
        star4.setIcon(STAR);
        break;
      default:
        return;
    }
  }

  public ArrayList<MovieImage> getAddedThumb() {
    ArrayList<MovieImage> res = new ArrayList<MovieImage>();
    for (int i = 0; i < thumbs.size(); i++) {
      if (thumbs.get(i).getId().equals("-1"))
        if (!thumbs.get(i).getThumbUrl().startsWith("file://"))
          res.add(thumbs.get(i));
    }
    return res;
  }

  public ArrayList<MovieImage> getAddedFanart() {
    ArrayList<MovieImage> res = new ArrayList<MovieImage>();
    for (int i = 0; i < fanarts.size(); i++) {
      if (fanarts.get(i).getId().equals("-1"))
        if (!fanarts.get(i).getThumbUrl().startsWith("file://"))
          res.add(fanarts.get(i));
    }
    return res;
  }

  public URL getSelectedThumb(int size) {
    if (!thumbsScrollPane.isVisible()) return null;
    return getSelectedItem(thumbs, thumbnailsList, size);
  }

  public URL getSelectedFanart(int size) {
    if (!fanartsScrollPane.isVisible()) return null;
    return getSelectedItem(fanarts, fanartList, size);
  }

  private URL getSelectedItem(ArrayList<MovieImage> array, JList list, int size) {
    if (array.isEmpty()) return null;
    try {
      switch (size) {
        case 0:
          return new URL(array.get(list.getSelectedIndex()).getOrigUrl());
        case 1:
          return new URL(array.get(list.getSelectedIndex()).getMidUrl());
        case 2:
          return new URL(array.get(list.getSelectedIndex()).getThumbUrl());
        default:
          break;
      }

    } catch (MalformedURLException ex) {
      setting.getLogger().log(Level.SEVERE, ex.toString());
    }
    return null;
  }

  private class actorImage {

    private String name;
    private String desc;
    private ImageIcon img;

    public actorImage(String name, String desc, ImageIcon img) {
      this.name = name;
      this.desc = desc;
      if (img == null) this.img = actorDefault;
      else this.img = img;
    }

    public ImageIcon getImage() {
      return img;
    }

    public String getDesc() {
      return desc;
    }

    public String getName() {
      return name;
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    imagePnl = new JPanel();
    fanartsScrollPane = new JScrollPane();
    thumbsScrollPane = new JScrollPane();
    InfoPnl = new JPanel();
    movieTabbedPane = new JTabbedPane();
    detailsPnl = new JPanel(){
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(fanartBack != null){
          g.drawImage(fanartBack, 0, 0, getWidth(), getHeight(), this);
        }

        // Create an AlphaComposite with 50% translucency.
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
        Composite oldComp = g2d.getComposite();
        Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);

        // Set the composite on the Graphics2D object.
        g2d.setComposite(alphaComp);

        // Invoke arbitrary paint methods, which will paint
        // with 50% translucency.
        g2d.setPaint(Color.black);
        g2d.fillRoundRect(15, 15, getWidth()-30, getHeight()-30, 30, 30);

        // Restore the old composite.
        g2d.setComposite(oldComp);
      }
    }
    ;
    synopsScroll = new JScrollPane();
    synopsisArea = new JTextArea();
    origTitleField = new JTextField();
    yearField = new JTextField();
    genreField = new JTextField();
    runtimeField = new JTextField();
    directorField = new JTextField();
    countryField = new JTextField();
    actorPnl = new JPanel();
    actorScroll = new JScrollPane();
    actorList = new JList() {
      // This method is called as the cursor moves within the list.
      public String getToolTipText(MouseEvent evt) {
        // Get item index
        int index = locationToIndex(evt.getPoint());
        if(index == -1) return "";
        if(index >= actors.size()) return "";
        return actors.get(index).getDesc();
      }
    }
    ;
    thumbLbl = new JLabel();
    star4 = new JLabel();
    star3 = new JLabel();
    star2 = new JLabel();
    star1 = new JLabel();
    star = new JLabel();
    titleLbl = new JLabel();

    setMinimumSize(new Dimension(10, 380));
    setPreferredSize(new Dimension(562, 400));
    setLayout(new BorderLayout());

    imagePnl.setPreferredSize(new Dimension(562, 125));

    fanartsScrollPane.setBorder(BorderFactory.createTitledBorder("Fanarts"));

    fanartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    fanartList.setAutoscrolls(false);
    fanartList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    fanartList.setMinimumSize(new Dimension(0, 110));
    fanartList.setVisibleRowCount(1);
    fanartsScrollPane.setViewportView(fanartList);
    ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");
    thumbsScrollPane.setBorder(BorderFactory.createTitledBorder(bundle.getString("thumbnails"))); // NOI18N
    thumbsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    thumbnailsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    thumbnailsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    thumbnailsList.setVisibleRowCount(1);
    thumbsScrollPane.setViewportView(thumbnailsList);

    GroupLayout imagePnlLayout = new GroupLayout(imagePnl);
    imagePnl.setLayout(imagePnlLayout);
    imagePnlLayout.setHorizontalGroup(
      imagePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(imagePnlLayout.createSequentialGroup()
        .addComponent(thumbsScrollPane, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(fanartsScrollPane, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
    );
    imagePnlLayout.setVerticalGroup(
      imagePnlLayout.createParallelGroup(Alignment.LEADING)
      .addComponent(thumbsScrollPane, GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
      .addComponent(fanartsScrollPane, GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
    );

    add(imagePnl, BorderLayout.PAGE_END);

    InfoPnl.setPreferredSize(new Dimension(562, 300));

    detailsPnl.setMinimumSize(new Dimension(0, 189));

    synopsisArea.setColumns(20);
    synopsisArea.setEditable(false);
    synopsisArea.setLineWrap(true);
    synopsisArea.setRows(5);
    synopsisArea.setWrapStyleWord(true);
    synopsisArea.setBorder(null);
    synopsisArea.setOpaque(false);
    synopsScroll.setViewportView(synopsisArea);

    origTitleField.setEditable(false);
    origTitleField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    yearField.setEditable(false);
    yearField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    genreField.setEditable(false);
    genreField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    runtimeField.setEditable(false);
    runtimeField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    directorField.setEditable(false);
    directorField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    countryField.setEditable(false);
    countryField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    GroupLayout detailsPnlLayout = new GroupLayout(detailsPnl);
    detailsPnl.setLayout(detailsPnlLayout);
    detailsPnlLayout.setHorizontalGroup(
      detailsPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, detailsPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(detailsPnlLayout.createParallelGroup(Alignment.TRAILING)
          .addComponent(synopsScroll, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
          .addGroup(detailsPnlLayout.createSequentialGroup()
            .addGroup(detailsPnlLayout.createParallelGroup(Alignment.LEADING)
              .addComponent(origTitleField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
              .addComponent(yearField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
              .addComponent(runtimeField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
            .addGap(37, 37, 37)
            .addGroup(detailsPnlLayout.createParallelGroup(Alignment.TRAILING)
              .addComponent(directorField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
              .addComponent(genreField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
              .addComponent(countryField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))))
        .addContainerGap())
    );
    detailsPnlLayout.setVerticalGroup(
      detailsPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(detailsPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(detailsPnlLayout.createParallelGroup(Alignment.LEADING, false)
          .addGroup(detailsPnlLayout.createSequentialGroup()
            .addComponent(origTitleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(yearField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addGroup(detailsPnlLayout.createSequentialGroup()
            .addComponent(countryField, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(genreField, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)))
        .addGap(18, 18, 18)
        .addGroup(detailsPnlLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(directorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(runtimeField, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(synopsScroll)
        .addContainerGap())
    );

    detailsPnl.setBorder(new EmptyBorder(20, 20, 20, 20));

    movieTabbedPane.addTab(bundle.getString("details"), detailsPnl); // NOI18N

    actorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    actorScroll.setViewportView(actorList);

    GroupLayout actorPnlLayout = new GroupLayout(actorPnl);
    actorPnl.setLayout(actorPnlLayout);
    actorPnlLayout.setHorizontalGroup(
      actorPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(actorPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(actorScroll, GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
        .addContainerGap())
    );
    actorPnlLayout.setVerticalGroup(
      actorPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(actorPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(actorScroll, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
        .addContainerGap())
    );

    movieTabbedPane.addTab(bundle.getString("actor"), actorPnl); // NOI18N






    thumbLbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    star4.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); // NOI18N
    star3.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); // NOI18N
    star2.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); // NOI18N
    star1.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); // NOI18N
    star.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); // NOI18N
    titleLbl.setFont(new Font("Ubuntu", 1, 14));
    titleLbl.setText(" ");

    GroupLayout InfoPnlLayout = new GroupLayout(InfoPnl);
    InfoPnl.setLayout(InfoPnlLayout);
    InfoPnlLayout.setHorizontalGroup(
      InfoPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(InfoPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(InfoPnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(titleLbl, GroupLayout.PREFERRED_SIZE, 412, GroupLayout.PREFERRED_SIZE)
          .addGroup(InfoPnlLayout.createSequentialGroup()
            .addGroup(InfoPnlLayout.createParallelGroup(Alignment.LEADING, false)
              .addGroup(InfoPnlLayout.createSequentialGroup()
                .addComponent(star)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(star1)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(star2)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(star3)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(star4))
              .addComponent(thumbLbl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(20, 20, 20)
            .addComponent(movieTabbedPane, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))))
    );
    InfoPnlLayout.setVerticalGroup(
      InfoPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(InfoPnlLayout.createSequentialGroup()
        .addComponent(titleLbl, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
        .addGroup(InfoPnlLayout.createParallelGroup(Alignment.LEADING)
          .addGroup(InfoPnlLayout.createSequentialGroup()
            .addGroup(InfoPnlLayout.createParallelGroup(Alignment.LEADING)
              .addComponent(star)
              .addComponent(star1)
              .addComponent(star2)
              .addComponent(star3)
              .addComponent(star4))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(thumbLbl, GroupLayout.PREFERRED_SIZE, 197, GroupLayout.PREFERRED_SIZE))
          .addComponent(movieTabbedPane, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE))
        .addContainerGap())
    );

    add(InfoPnl, BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
}
