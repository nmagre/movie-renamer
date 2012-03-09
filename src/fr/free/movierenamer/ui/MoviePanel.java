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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.movie.Movie;
import fr.free.movierenamer.movie.MovieImage;
import fr.free.movierenamer.utils.Settings;
import javax.swing.Icon;

/**
 *
 * @author duffy
 */
public class MoviePanel extends javax.swing.JPanel {

  private final DefaultListModel fanartModel = new DefaultListModel();
  private final DefaultListModel thumbnailModel = new DefaultListModel();
  private final DefaultListModel actorModel = new DefaultListModel();
  private Dimension thumbDim = new Dimension(160, 200);
  public Dimension thumbListDim = new Dimension(60, 90);
  public Dimension fanartListDim = new Dimension(200, 90);
  public Dimension actorListDim = new Dimension(60, 90);
  private final Icon STAR = new ImageIcon(getClass().getResource("/image/star.png"));
  private final Icon STAR_HALF = new ImageIcon(getClass().getResource("/image/star-half.png"));
  private final Icon STAR_EMPTY = new ImageIcon(getClass().getResource("/image/star-empty.png"));
  private Image img;
  private ArrayList<MovieImage> thumbs;
  private ArrayList<MovieImage> fanarts;
  private Settings setting;

  /** Creates new form MovieImagePanel
   * @param setting 
   */
  public MoviePanel(Settings setting) {
    this.setting = setting;
    initComponents();
    thumbs = new ArrayList<MovieImage>();
    fanarts = new ArrayList<MovieImage>();

    thumbnailsList.setModel(thumbnailModel);
    fanartList.setModel(fanartModel);
    actorList.setModel(actorModel);

    thumbnailsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    thumbnailsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    thumbnailsList.setVisibleRowCount(-1);

    MouseListener mouseListener = new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {//A refaire (http request in EDT)
        int index = thumbnailsList.locationToIndex(e.getPoint());
        if (index != -1)
          img = getImage(thumbs.get(index).getOrigUrl().replace(".png", ".jpg"), Cache.thumb);
        if (img != null)
          thumbLbl.setIcon(new ImageIcon(img.getScaledInstance(thumbDim.width, thumbDim.height, Image.SCALE_DEFAULT)));
      }
    };
    thumbnailsList.addMouseListener(mouseListener);


    fanartList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    fanartList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    fanartList.setVisibleRowCount(-1);
    mouseListener = new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {//A refaire (http request in EDT)
        int index = fanartList.locationToIndex(e.getPoint());
        if (index != -1) {
          img = getImage(fanarts.get(index).getOrigUrl().replace(".png", ".jpg"), Cache.fanart);
          detailsPnl.validate();
          detailsPnl.repaint();
        }
      }
    };
    fanartList.addMouseListener(mouseListener);

    actorList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    actorList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    actorList.setVisibleRowCount(-1);

    thumbsScrollPane.setVisible(setting.thumb);
    fanartsScrollPane.setVisible(setting.fanart);
    imagePnl.setVisible(setting.thumb || setting.fanart);
    img = null;
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
      Logger.getLogger(MoviePanel.class.getName()).log(Level.SEVERE, null, ex);
    }
    return image;
  }

  public synchronized void addThumbToList(final Image thumb, final MovieImage mvImg) {//A refaire (http request in EDT)

    SwingUtilities.invokeLater(new Thread() {

      @Override
      public void run() {
        thumbs.add(mvImg);
        if (thumbnailModel.isEmpty())
          img = getImage(thumbs.get(0).getOrigUrl().replace(".png", ".jpg"), Cache.thumb);
          if (img != null)
            thumbLbl.setIcon(new ImageIcon(img.getScaledInstance(thumbDim.width, thumbDim.height, Image.SCALE_DEFAULT)));
        if (thumb != null)
          thumbnailModel.addElement(new ImageIcon(thumb.getScaledInstance(thumbListDim.width, thumbListDim.height, Image.SCALE_DEFAULT)));
        if (!thumbnailModel.isEmpty())
          thumbnailsList.setSelectedIndex(0);
      }
    });
  }

  public synchronized void addFanartToList(final Image fanart, final MovieImage mvImg) {//A refaire (http request in EDT)

    SwingUtilities.invokeLater(new Thread() {

      @Override
      public void run() {
        fanarts.add(mvImg);
        if (fanartModel.isEmpty()) {
            img = getImage(fanarts.get(0).getOrigUrl().replace(".png", ".jpg"), Cache.fanart);
          detailsPnl.validate();
          detailsPnl.repaint();
        }

        if (fanart != null)
          fanartModel.addElement(new ImageIcon(fanart.getScaledInstance(fanartListDim.width, fanartListDim.height, Image.SCALE_DEFAULT)));
        if (!fanartModel.isEmpty())
          fanartList.setSelectedIndex(0);
      }
    });
  }

  public void addActorToList(final String actor, final Image actorImg, final String desc) {
    SwingUtilities.invokeLater(new Thread() {

      @Override
      public void run() {
        if (actorImg != null)
          actorModel.addElement(new ImageIcon(actorImg.getScaledInstance(actorListDim.width, actorListDim.height, Image.SCALE_DEFAULT), desc));
        else actorModel.addElement(actor);
      }
    });
  }

  public void clearList() {
    fanarts.clear();
    thumbs.clear();
    img = null;
    SwingUtilities.invokeLater(new Thread() {

      @Override
      public void run() {
        img = null;
        fanartModel.clear();
        thumbnailModel.clear();
        actorModel.clear();
        genreField.setText("");
        yearField.setText("");
        runtimeField.setText("");
        synopsisArea.setText("");
        origTitleField.setText("");
        countryField.setText("");
        titleLbl.setText("");
        thumbLbl.setIcon(null);
        star.setIcon(STAR_EMPTY);
        star1.setIcon(STAR_EMPTY);
        star2.setIcon(STAR_EMPTY);
        star3.setIcon(STAR_EMPTY);
        star4.setIcon(STAR_EMPTY);

        validate();
        repaint();
      }
    });
  }

  public void addMovie(final Movie movie) {
    SwingUtilities.invokeLater(new Thread() {

      @Override
      public void run() {
        titleLbl.setText(movie.getTitle());
        genreField.setText(movie.getGenresString());
        yearField.setText(movie.getYear());
        runtimeField.setText(movie.getRuntime() + " min");
        synopsisArea.setText(movie.getSynopsis());
        origTitleField.setText(movie.getOrigTitle());
        countryField.setText(movie.getCountriesString());
        setRate(Float.parseFloat(movie.getRating().replace(",", ".")));
        if (!setting.thumb)
          if (!movie.getImdbThumb().equals("")) {
            Image imThumb = getImage(movie.getImdbThumb(), Cache.thumb);
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

  public URL getSelectedThumb() {
    return getSelectedItem(thumbs, thumbnailsList);
  }

  public URL getSelectedFanart() {
    return getSelectedItem(fanarts, fanartList);
  }

  private URL getSelectedItem(ArrayList<MovieImage> array, JList list) {
    try {
      return new URL(array.get(list.getSelectedIndex()).getOrigUrl().replace(".png", ".jpg"));
    } catch (MalformedURLException ex) {
      Logger.getLogger(MoviePanel.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
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
        if(img != null){
          g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
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
    genreField = new JTextField();
    yearField = new JTextField();
    runtimeField = new JTextField();
    jTextField4 = new JTextField();
    origTitleField = new JTextField();
    countryField = new JTextField();
    actorPnl = new JPanel();
    actorScroll = new JScrollPane();
    actorList = new JList();
    thumbLbl = new JLabel();
    star4 = new JLabel();
    star3 = new JLabel();
    star2 = new JLabel();
    star1 = new JLabel();
    star = new JLabel();
    titleLbl = new JLabel();
    jButton1 = new JButton();
    jButton2 = new JButton();

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

    genreField.setEditable(false);
    genreField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    yearField.setEditable(false);
    yearField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    runtimeField.setEditable(false);
    runtimeField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    jTextField4.setEditable(false);
    jTextField4.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    origTitleField.setEditable(false);
    origTitleField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

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
              .addComponent(genreField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
              .addComponent(yearField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
              .addComponent(runtimeField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
            .addGap(37, 37, 37)
            .addGroup(detailsPnlLayout.createParallelGroup(Alignment.TRAILING)
              .addComponent(jTextField4, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
              .addComponent(origTitleField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
              .addComponent(countryField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))))
        .addContainerGap())
    );
    detailsPnlLayout.setVerticalGroup(
      detailsPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(detailsPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(detailsPnlLayout.createParallelGroup(Alignment.LEADING, false)
          .addGroup(detailsPnlLayout.createSequentialGroup()
            .addComponent(genreField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(yearField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addGroup(detailsPnlLayout.createSequentialGroup()
            .addComponent(countryField, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(origTitleField, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)))
        .addGap(18, 18, 18)
        .addGroup(detailsPnlLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(jTextField4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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

    jButton1.setIcon(new ImageIcon(getClass().getResource("/image/film.png"))); // NOI18N
    jButton1.setToolTipText("Play movie");

    jButton2.setIcon(new ImageIcon(getClass().getResource("/image/film-error.png"))); // NOI18N
    jButton2.setToolTipText("Imdb Page");

    GroupLayout InfoPnlLayout = new GroupLayout(InfoPnl);
    InfoPnl.setLayout(InfoPnlLayout);
    InfoPnlLayout.setHorizontalGroup(
      InfoPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(InfoPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(InfoPnlLayout.createParallelGroup(Alignment.LEADING)
          .addGroup(Alignment.TRAILING, InfoPnlLayout.createSequentialGroup()
            .addComponent(titleLbl, GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
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
        .addGroup(InfoPnlLayout.createParallelGroup(Alignment.LEADING)
          .addGroup(InfoPnlLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(titleLbl, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
            .addComponent(jButton1))
          .addComponent(jButton2))
        .addPreferredGap(ComponentPlacement.RELATED)
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
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JPanel InfoPnl;
  private JList actorList;
  private JPanel actorPnl;
  private JScrollPane actorScroll;
  private JTextField countryField;
  private JPanel detailsPnl;
  private final JList fanartList = new JList();
  private JScrollPane fanartsScrollPane;
  private JTextField genreField;
  private JPanel imagePnl;
  private JButton jButton1;
  private JButton jButton2;
  private JTextField jTextField4;
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
}
