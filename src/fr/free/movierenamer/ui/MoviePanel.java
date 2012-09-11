/*
 * Movie Renamer
 * Copyright (C) 2012 Nicolas Magré
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
package fr.free.movierenamer.ui;

import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.WebToolBar;
import fr.free.movierenamer.media.MediaImage;
import fr.free.movierenamer.media.mediainfo.MediaAudio;
import fr.free.movierenamer.media.mediainfo.MediaSubTitle;
import fr.free.movierenamer.media.movie.Movie;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.ui.res.DropImage;
import fr.free.movierenamer.ui.res.Flag;
import fr.free.movierenamer.ui.res.IMediaPanel;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class MoviePanel
 *
 * @author Magré Nicolas
 */
public class MoviePanel extends WebPanel implements IMediaPanel {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private WebList actorList;
    private WebList countryList;
    private WebTextField directorField;
    private WebLabel directorLbl;
    private WebList fanartList;
    private WebToolBar fanartTb;
    private JScrollPane fanartsScrollPane;
    private WebTextField genreField;
    private WebLabel genreLbl;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane3;
    private WebToolBar movieTb;
    private WebTextField origTitleField;
    private WebLabel origTitleLbl;
    private WebTextField runtimeField;
    private WebLabel runtimeLbl;
    private JLabel star;
    private JLabel star1;
    private JLabel star2;
    private JLabel star3;
    private JLabel star4;
    private WebPanel starPanel;
    private JScrollPane synopsScroll;
    private JTextArea synopsisArea;
    private WebLabel synopsisLbl;
    private JLabel thumbLbl;
    private WebToolBar thumbnailTb;
    private WebList thumbnailsList;
    private JScrollPane thumbsScrollPane;
    private WebLabel titleLbl;
    private WebLabel webLabel1;
    private WebLabel webLabel2;
    private WebLabel webLabel5;
    private WebToolBar webToolBar3;
    private WebToolBar webToolBar4;
    private WebLabel yearLbl;
    // End of variables declaration//GEN-END:variables
  private static final long serialVersionUID = 1L;
  private final DefaultListModel fanartModel = new DefaultListModel();
  private final DefaultListModel thumbnailModel = new DefaultListModel();
  private final DefaultListModel actorModel = new DefaultListModel();
  private final DefaultListModel subTitleModel = new DefaultListModel();
  private final DefaultListModel audioModel = new DefaultListModel();
  private final DefaultListModel countryModel = new DefaultListModel();
  private Dimension thumbDim = new Dimension(160, 200);
  public Dimension thumbListDim = new Dimension(60, 90);
  public Dimension fanartListDim = new Dimension(200, 90);
  public Dimension actorListDim = new Dimension(30, 53);
  private final ImageIcon actorDefault = new ImageIcon(getClass().getResource("/image/unknown.png"));
  private final Icon STAR = new ImageIcon(getClass().getResource("/image/star.png"));
  private final Icon STAR_HALF = new ImageIcon(getClass().getResource("/image/star-half.png"));
  private final Icon STAR_EMPTY = new ImageIcon(getClass().getResource("/image/star-empty.png"));
  private Image fanartBack;
  private DropTarget dropThumbTarget;
  private DropTarget dropFanartTarget;
  private List<actorImage> actors;
  private List<MediaImage> thumbs;
  private List<MediaImage> fanarts;
  private Settings setting;
  private Cache cache = Cache.getInstance();
  private static final String SEP = " : ";

  /**
   * Creates new form MoviePanel
   *
   * @param setting
   */
  public MoviePanel(Settings setting) {
    this.setting = setting;

    // Init
    initComponents();

    origTitleLbl.setText(origTitleLbl.getText() + SEP);
    directorLbl.setText(directorLbl.getText() + SEP);
    runtimeLbl.setText(runtimeLbl.getText() + SEP);
    genreLbl.setText(genreLbl.getText() + SEP);

    origTitleField.setLeadingComponent(origTitleLbl);
    directorField.setLeadingComponent(directorLbl);
    runtimeField.setLeadingComponent(runtimeLbl);
    genreField.setLeadingComponent(genreLbl);

    // Add component to toolbar
    movieTb.addToEnd(starPanel);

    actors = new ArrayList<actorImage>();
    thumbs = new ArrayList<MediaImage>();
    fanarts = new ArrayList<MediaImage>();

    thumbnailsList.setModel(thumbnailModel);
    fanartList.setModel(fanartModel);
    actorList.setModel(actorModel);
    countryList.setModel(countryModel);

    countryList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    countryList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    countryList.setVisibleRowCount(-1);

    thumbnailsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    thumbnailsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    thumbnailsList.setVisibleRowCount(-1);
    thumbnailsList.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent e) {
        try {
          if (thumbnailsList.getSelectedIndex() == -1) {
            return;
          }
          thumbnailsList.ensureIndexIsVisible(thumbnailsList.getSelectedIndex());
          Image img = cache.getImage(new URL(thumbs.get(thumbnailsList.getSelectedIndex()).getUrl(MediaImage.MediaImageSize.THUMB)), Cache.CacheType.THUMB);
          if (img != null) {
            thumbLbl.setIcon(new ImageIcon(img.getScaledInstance(thumbDim.width, thumbDim.height, Image.SCALE_DEFAULT)));
          }
        } catch (IOException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        }
      }
    });

    fanartList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    fanartList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    fanartList.setVisibleRowCount(-1);
    fanartList.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent lse) {
        if (fanartList.getSelectedIndex() == -1) {
          return;
        }
        fanartList.ensureIndexIsVisible(fanartList.getSelectedIndex());
        fanartBack = getImage(fanarts.get(fanartList.getSelectedIndex()).getUrl(MediaImage.MediaImageSize.THUMB), Cache.CacheType.FANART);
      }
    });

    // Add drag and drop image on thumbnail list
    DropImage dropThumb = new DropImage(this, MoviePanel.this, MediaImage.MediaImageType.THUMB, Cache.CacheType.THUMB);
    dropThumbTarget = new DropTarget(thumbnailsList, dropThumb);

    // Add drag and drop image on fanart list
    DropImage dropFanart = new DropImage(this, MoviePanel.this, MediaImage.MediaImageType.FANART, Cache.CacheType.FANART);
    dropFanartTarget = new DropTarget(fanartList, dropFanart);

    actorList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    actorList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    actorList.setVisibleRowCount(-1);
    actorList.setCellRenderer(new DefaultListCellRenderer() {

      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (index >= actors.size()) {
          return label;
        }
        Icon icon = actors.get(index).getImage();

        if (icon != null) {
          label.setIcon(icon);
        } else {
          label.setIcon(new ImageIcon(actorDefault.getImage().getScaledInstance(actorListDim.width, actorListDim.height, Image.SCALE_DEFAULT)));
        }
        return label;
      }
    });

    // Disable drag and drop on list until a movie is added
    dropFanartTarget.setActive(false);
    dropThumbTarget.setActive(false);

    thumbnailTb.setVisible(setting.thumb);
    fanartTb.setVisible(setting.fanart);
    thumbsScrollPane.setVisible(setting.thumb);
    fanartsScrollPane.setVisible(setting.fanart);
    fanartBack = null;
  }

  @Override
  public void setDisplay(Settings setting) {
    thumbnailTb.setVisible(setting.thumb);
    fanartTb.setVisible(setting.fanart);
    thumbsScrollPane.setVisible(setting.thumb);
    fanartsScrollPane.setVisible(setting.fanart);
  }

  private Image getImage(String strUrl, Cache.CacheType cache) {// FIXME rien a faire là, on ne fait pas de requete dans l'edt (même si ce n'est pas dans l'edt)
    Image image = null;
    try {
      URL url = new URL(strUrl);
      image = Cache.getInstance().getImage(url, cache);
      if (image == null) {
        Cache.getInstance().add(url, cache);
        image = Cache.getInstance().getImage(url, cache);
      }
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, ex.toString());
    }
    return image;
  }

  @Override
  public void addImageToList(Image img, MediaImage mediaImage, boolean selectLast) {
    switch (mediaImage.getType()) {
      case THUMB:
        addThumbToList(img, mediaImage, selectLast);
        break;
      case FANART:
        addFanartToList(img, mediaImage, selectLast);
        break;
      default:
        break;
    }
  }

  /**
   * Add thumb to thumb list
   *
   * @param thumb
   * @param mvImg
   * @param selectLast
   */
  private synchronized void addThumbToList(final Image thumb, final MediaImage mvImg, final boolean selectLast) {
    thumbs.add(mvImg);

    final SwingWorker<Image, Void> worker = new SwingWorker<Image, Void>() {

      @Override
      protected Image doInBackground() throws Exception {
        Image image = null;
        if (thumbnailModel.isEmpty()) {
          image = cache.getImage(new URL(thumbs.get(0).getUrl(MediaImage.MediaImageSize.THUMB)), Cache.CacheType.THUMB);
        }
        return image;
      }
    };

    worker.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getNewValue().equals(SwingWorker.StateValue.DONE)) {
          try {
            Image img = worker.get();
            if (img != null) {
              thumbLbl.setIcon(new ImageIcon(img.getScaledInstance(thumbDim.width, thumbDim.height, Image.SCALE_DEFAULT)));
            }
            if (thumb != null) {
              thumbnailModel.addElement(new ImageIcon(thumb.getScaledInstance(thumbListDim.width, thumbListDim.height, Image.SCALE_DEFAULT)));
            }
            if (!thumbnailModel.isEmpty()) {
              thumbnailsList.setSelectedIndex((selectLast ? (thumbnailModel.size() - 1) : 0));
            }
          } catch (InterruptedException ex) {
            Settings.LOGGER.log(Level.SEVERE, ex.toString());
          } catch (ExecutionException ex) {
            Settings.LOGGER.log(Level.SEVERE, ex.toString());
          }
        }
      }
    });

    worker.execute();
  }

  private synchronized void addFanartToList(final Image fanart, final MediaImage mvImg, final boolean selectLast) {
    fanarts.add(mvImg);
    final SwingWorker<Image, Void> worker = new SwingWorker<Image, Void>() {

      @Override
      protected Image doInBackground() throws Exception {
        Image img = null;
        if (fanartModel.isEmpty()) {
          img = getImage(fanarts.get(0).getUrl(MediaImage.MediaImageSize.THUMB), Cache.CacheType.FANART);
        }

        return img;
      }
    };

    worker.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getNewValue().equals(SwingWorker.StateValue.DONE)) {
          try {
            fanartBack = worker.get();
            if (fanartBack != null) {
            }

          } catch (InterruptedException ex) {
            Settings.LOGGER.log(Level.SEVERE, null, ex);
          } catch (ExecutionException ex) {
            Settings.LOGGER.log(Level.SEVERE, null, ex);
          }

          if (fanart != null) {
            fanartModel.addElement(new ImageIcon(fanart.getScaledInstance(fanartListDim.width, fanartListDim.height, Image.SCALE_DEFAULT)));
          }
          if (!fanartModel.isEmpty()) {
            fanartList.setSelectedIndex((selectLast ? (fanartModel.size() - 1) : 0));
          }
        }
      }
    });

    worker.execute();
  }

  @Override
  public void addActorToList(final String actor, final Image actorImg, final String desc) {
    ImageIcon icon = null;
    if (actorImg != null) {
      icon = new ImageIcon(actorImg.getScaledInstance(actorListDim.width, actorListDim.height, Image.SCALE_DEFAULT), desc);
    }
    actors.add(new actorImage(actor, desc, icon));
    SwingUtilities.invokeLater(new Thread() {

      @Override
      public void run() {
        actorModel.addElement(actor);
      }
    });
  }

  @Override
  public void clear() {
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
        subTitleModel.clear();
        audioModel.clear();
        countryModel.clear();
        origTitleField.setText("");
        yearLbl.setText("");
        runtimeField.setText("");
        synopsisArea.setText("");
        genreField.setText("");
        directorField.setText("");
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

  public void clearActorList() {// TODO A refaire, ??? ça sert a quelque chose ?
    actorModel.clear();
    actors.clear();
  }

  public void clearThumbList() {// TODO A refaire, ??? ça sert a quelque chose ?
    thumbnailModel.clear();
    thumbs.clear();
  }

  public void clearFanartList() {// TODO A refaire, ??? ça sert a quelque chose ?
    fanartModel.clear();
    fanarts.clear();
  }

  public void addMovieInfo(final Movie movie) {// TODO A refaire, le thread est-il vraiment nécéssaire ?

    SwingUtilities.invokeLater(new Thread() {

      @Override
      public void run() {
        MovieInfo movieInfo = movie.getMovieInfo();
        List<MediaSubTitle> subtitles = movie.getMediaTag().getMediaSubTitles();
        List<MediaAudio> audios = movie.getMediaTag().getMediaAudios();
        List<String> countries = movieInfo.getCountries();
        for (MediaSubTitle sub : subtitles) {
          System.out.println(sub.getTitle() + " : " + sub.getLanguage());
          subTitleModel.addElement(sub);
        }
        for (MediaAudio audio : audios) {
          audioModel.addElement(audio);
        }
        for (String country : countries) {
          ImageIcon icon = Flag.getFlagByCountry(country);
          icon.setDescription(country);
          countryModel.addElement(icon);
        }

        countryList.setModel(countryModel);

        titleLbl.setText(movieInfo.getTitle());
        origTitleField.setText(movieInfo.getOrigTitle());
        yearLbl.setText("(" + movieInfo.getYear() + ")");
        runtimeField.setText(movieInfo.getRuntime() + " min");
        synopsisArea.setText(movieInfo.getSynopsis());
        genreField.setText(movieInfo.getGenresString(" | ", 0));
        directorField.setText(movieInfo.getDirectorsString(" | ", 0));
        setRate(Float.parseFloat(movieInfo.getRating().replace(",", ".")));
        dropFanartTarget.setActive(true);
        dropThumbTarget.setActive(true);

        origTitleField.setCaretPosition(0);
        synopsisArea.setCaretPosition(0);
        genreField.setCaretPosition(0);
        directorField.setCaretPosition(0);

        if (!setting.thumb) {
          if (!movieInfo.getThumb().equals("")) {
            Image imThumb = getImage(movieInfo.getThumb(), Cache.CacheType.THUMB);
            if (imThumb != null) {
              thumbLbl.setIcon(new ImageIcon(imThumb.getScaledInstance(thumbDim.width, thumbDim.height, Image.SCALE_DEFAULT)));
            }
          }
        }
      }
    });
  }

  /**
   * Set star compared with rate
   *
   * @param rate
   */
  private void setRate(Float rate) {
    if (rate < 0.00) {
      return;
    }
    rate /= 2;
    int n = rate.intValue();
    switch (n) {
      case 0:
        break;
      case 1:
        star.setIcon(STAR);
        if ((rate - rate.intValue()) >= 0.50) {
          star1.setIcon(STAR_HALF);
        }
        break;
      case 2:
        star.setIcon(STAR);
        star1.setIcon(STAR);
        if ((rate - rate.intValue()) >= 0.50) {
          star2.setIcon(STAR_HALF);
        }
        break;
      case 3:
        star.setIcon(STAR);
        star1.setIcon(STAR);
        star2.setIcon(STAR);
        if ((rate - rate.intValue()) >= 0.50) {
          star3.setIcon(STAR_HALF);
        }
        break;
      case 4:
        star.setIcon(STAR);
        star1.setIcon(STAR);
        star2.setIcon(STAR);
        star3.setIcon(STAR);
        if ((rate - rate.intValue()) >= 0.50) {
          star4.setIcon(STAR_HALF);
        }
        break;
      case 5:
        star.setIcon(STAR);
        star1.setIcon(STAR);
        star2.setIcon(STAR);
        star3.setIcon(STAR);
        star4.setIcon(STAR);
        break;
      default:
        break;
    }
  }

  public List<MediaImage> getAddedThumb() {
    List<MediaImage> res = new ArrayList<MediaImage>();
    for (int i = 0; i < thumbs.size(); i++) {
      if (thumbs.get(i).getId() == -1) {
        if (!thumbs.get(i).getUrl(MediaImage.MediaImageSize.THUMB).startsWith("file://")) {// Don't add images from hdd
          res.add(thumbs.get(i));
        }
      }
    }
    return res;
  }

  public List<MediaImage> getAddedFanart() {
    List<MediaImage> res = new ArrayList<MediaImage>();
    for (int i = 0; i < fanarts.size(); i++) {
      if (fanarts.get(i).getId() == -1) {
        if (!fanarts.get(i).getUrl(MediaImage.MediaImageSize.THUMB).startsWith("file://")) {// Don't add images from hdd
          res.add(fanarts.get(i));
        }
      }
    }
    return res;
  }

  private class actorImage {

    private String name;
    private String desc;
    private ImageIcon img;

    public actorImage(String name, String desc, ImageIcon img) {
      this.name = name;
      this.desc = desc;
      if (img == null) {
        this.img = new ImageIcon(actorDefault.getImage().getScaledInstance(actorListDim.width, actorListDim.height, Image.SCALE_DEFAULT));
      } else {
        this.img = img;
      }
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

  /**
   * This method is called from within the constructor to initialize the form.
   */
  //WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        origTitleLbl = new WebLabel();
        directorLbl = new WebLabel();
        runtimeLbl = new WebLabel();
        genreLbl = new WebLabel();
        starPanel = new WebPanel();
        star4 = new JLabel();
        star3 = new JLabel();
        star2 = new JLabel();
        star1 = new JLabel();
        star = new JLabel();
        movieTb = new WebToolBar();
        titleLbl = new WebLabel();
        yearLbl = new WebLabel();
        thumbLbl = new JLabel();
        origTitleField = new WebTextField();
        directorField = new WebTextField();
        genreField = new WebTextField();
        webToolBar3 = new WebToolBar();
        webLabel5 = new WebLabel();
        jScrollPane3 = new JScrollPane();
        actorList = new WebList();
        webToolBar4 = new WebToolBar();
        synopsisLbl = new WebLabel();
        synopsScroll = new JScrollPane();
        synopsisArea = new JTextArea();
        runtimeField = new WebTextField();
        jScrollPane1 = new JScrollPane();
        countryList = new WebList(){
            public String getToolTipText(MouseEvent evt) {
                // Get item index
                int index = locationToIndex(evt.getPoint());

                // Get item
                ImageIcon item = (ImageIcon) getModel().getElementAt(index);

                // Return the tool tip text
                return item.getDescription();
            }
        };
        thumbnailTb = new WebToolBar();
        webLabel1 = new WebLabel();
        thumbsScrollPane = new JScrollPane();
        thumbnailsList = new WebList();
        fanartTb = new WebToolBar();
        webLabel2 = new WebLabel();
        fanartsScrollPane = new JScrollPane();
        fanartList = new WebList();

        origTitleLbl.setText(Utils.i18n("origTitle"));         origTitleLbl.setFont(new Font("Ubuntu", 1, 13)); 
        directorLbl.setText(Utils.i18n("director"));         directorLbl.setFont(new Font("Ubuntu", 1, 13)); 
        runtimeLbl.setText(Utils.i18n("runtime"));         runtimeLbl.setFont(new Font("Ubuntu", 1, 13)); 
        genreLbl.setText("Genre");
        genreLbl.setFont(new Font("Ubuntu", 1, 13)); 
        starPanel.setAlignmentY(0.0F);

        star4.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); 
        star3.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); 
        star2.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); 
        star1.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); 
        star.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); 
        GroupLayout starPanelLayout = new GroupLayout(starPanel);
        starPanel.setLayout(starPanelLayout);
        starPanelLayout.setHorizontalGroup(
            starPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(starPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(star)
                .addGap(8, 8, 8)
                .addComponent(star1)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(star2)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(star3)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(star4)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        starPanelLayout.setVerticalGroup(
            starPanelLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(star1)
            .addComponent(star2)
            .addComponent(star3)
            .addComponent(star4)
            .addComponent(star)
        );

        setMargin(new Insets(10, 10, 10, 10));
        setMinimumSize(new Dimension(10, 380));
        setPreferredSize(new Dimension(562, 400));

        movieTb.setFloatable(false);
        movieTb.setRollover(true);

        titleLbl.setFont(new Font("Ubuntu", 1, 14));         movieTb.add(titleLbl);

        yearLbl.setFont(new Font("Ubuntu", 1, 14));         movieTb.add(yearLbl);

        thumbLbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        origTitleField.setEditable(false);

        directorField.setEditable(false);

        webToolBar3.setFloatable(false);
        webToolBar3.setRollover(true);

        webLabel5.setText(Utils.i18n("actor"));         webLabel5.setFont(new Font("Ubuntu", 1, 13));         webToolBar3.add(webLabel5);

        actorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(actorList);

        webToolBar4.setFloatable(false);
        webToolBar4.setRollover(true);

        synopsisLbl.setText("Synopsis");
        synopsisLbl.setFont(new Font("Ubuntu", 1, 13));         webToolBar4.add(synopsisLbl);

        synopsScroll.setPreferredSize(new Dimension(264, 62));

        synopsisArea.setColumns(20);
        synopsisArea.setEditable(false);
        synopsisArea.setLineWrap(true);
        synopsisArea.setRows(4);
        synopsisArea.setWrapStyleWord(true);
        synopsisArea.setBorder(null);
        synopsisArea.setOpaque(false);
        synopsScroll.setViewportView(synopsisArea);

        countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        countryList.setAutoscrolls(false);
        countryList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        jScrollPane1.setViewportView(countryList);

        thumbnailTb.setFloatable(false);
        thumbnailTb.setRollover(true);

        webLabel1.setText(Utils.i18n("thumbnails"));         webLabel1.setFont(new Font("Ubuntu", 1, 13));         thumbnailTb.add(webLabel1);

        thumbnailsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        thumbsScrollPane.setViewportView(thumbnailsList);

        fanartTb.setFloatable(false);
        fanartTb.setRollover(true);
        fanartTb.setFont(new Font("Ubuntu", 1, 13)); 
        webLabel2.setText("Fanart");
        webLabel2.setFont(new Font("Ubuntu", 1, 13));         fanartTb.add(webLabel2);

        fanartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fanartsScrollPane.setViewportView(fanartList);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(movieTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(thumbLbl, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(directorField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(genreField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addComponent(origTitleField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(webToolBar3, GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                        .addGap(9, 9, 9)
                        .addComponent(runtimeField, GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE))))
            .addComponent(synopsScroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(webToolBar4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(thumbsScrollPane, GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                    .addComponent(thumbnailTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(fanartsScrollPane, GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                    .addComponent(fanartTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(movieTb, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(thumbLbl, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(origTitleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(directorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(genreField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                            .addComponent(webToolBar3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(runtimeField, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(webToolBar4, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(synopsScroll, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(thumbnailTb, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(fanartTb, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(thumbsScrollPane, GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                    .addComponent(fanartsScrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents
}
