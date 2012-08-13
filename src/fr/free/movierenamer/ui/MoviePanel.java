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
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import fr.free.movierenamer.media.MediaImage;
import fr.free.movierenamer.media.mediainfo.MediaAudio;
import fr.free.movierenamer.media.mediainfo.MediaSubTitle;
import fr.free.movierenamer.media.movie.Movie;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.ui.res.DropImage;
import fr.free.movierenamer.ui.res.Flag;
import fr.free.movierenamer.ui.res.IMediaPanel;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseAdapter;
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
    private JPanel InfoPnl;
    private JList actorList;
    private JScrollPane actorScroll;
    private WebList audioList;
    private JScrollPane audioSp;
    private WebList countryList;
    private WebToolBar countryTb;
    private JTextField directorField;
    private final JList fanartList = new JList();
    private WebToolBar fanartTb;
    private JScrollPane fanartsScrollPane;
    private JTextField genreField;
    private JPanel imagePnl;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private WebToolBar movieTb;
    private WebTextField origTitleField;
    private JTextField runtimeField;
    private JLabel star;
    private JLabel star1;
    private JLabel star2;
    private JLabel star3;
    private JLabel star4;
    private WebList subTitleList;
    private JScrollPane subTitleSp;
    private JScrollPane synopsScroll;
    private JTextArea synopsisArea;
    private JLabel thumbLbl;
    private WebToolBar thumbTb;
    private WebLabel thumbnailLbl;
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
    private WebLabel titleLbl;
    private WebLabel webLabel1;
    private WebLabel webLabel2;
    private WebLabel webLabel3;
    private WebLabel webLabel4;
    private WebLabel webLabel5;
    private WebPanel webPanel1;
    private WebToolBar webToolBar1;
    private WebToolBar webToolBar2;
    private WebToolBar webToolBar3;
    private WebLabel yearLbl;
    // End of variables declaration//GEN-END:variables
  private static final long serialVersionUID = 1L;
  private final DefaultListModel fanartModel = new DefaultListModel();
  private final DefaultListModel thumbnailModel = new DefaultListModel();
  private final DefaultListModel actorModel = new DefaultListModel();
  private final DefaultListModel subTitleModel = new DefaultListModel();
  private final DefaultListModel audioModel = new DefaultListModel();
  private final DefaultListModel countryModel = new DefaultListModel();
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
  private List<actorImage> actors;
  private List<MediaImage> thumbs;
  private List<MediaImage> fanarts;
  private Settings setting;
  private Cache cache = Cache.getInstance();

  /**
   * Creates new form MoviePanel
   *
   * @param setting
   */
  public MoviePanel(Settings setting) {
    this.setting = setting;

    // Init
    initComponents();

    origTitleField.setLeadingComponent(new JLabel(Utils.i18n("origTitle") + " : "));

    // Add component to toolbar
    movieTb.addToEnd(star);
    movieTb.addToEnd(star1);
    movieTb.addToEnd(star2);
    movieTb.addToEnd(star3);
    movieTb.addToEnd(star4);

    actors = new ArrayList<actorImage>();
    thumbs = new ArrayList<MediaImage>();
    fanarts = new ArrayList<MediaImage>();

    thumbnailsList.setModel(thumbnailModel);
    fanartList.setModel(fanartModel);
    actorList.setModel(actorModel);
    subTitleList.setModel(subTitleModel);
    audioList.setModel(audioModel);
    countryList.setModel(countryModel);

    subTitleList.setCellRenderer(new IconListRenderer<MediaSubTitle>());
    audioList.setCellRenderer(new IconListRenderer<MediaAudio>());

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
          label.setIcon(actorDefault);
        }
        return label;
      }
    });

    // Disable drag and drop on list until a movie is added
    dropFanartTarget.setActive(false);
    dropThumbTarget.setActive(false);

    thumbsScrollPane.setVisible(setting.thumb);
    fanartsScrollPane.setVisible(setting.fanart);
    imagePnl.setVisible(setting.thumb || setting.fanart);
    fanartBack = null;
  }

  @Override
  public void setDisplay(Settings setting) {
    thumbsScrollPane.setVisible(setting.thumb);
    fanartsScrollPane.setVisible(setting.fanart);
    imagePnl.setVisible(setting.thumb || setting.fanart);
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

        subTitleList.setModel(subTitleModel);
        audioList.setModel(audioModel);
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
        this.img = actorDefault;
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

        star4 = new JLabel();
        star3 = new JLabel();
        star2 = new JLabel();
        star1 = new JLabel();
        star = new JLabel();
        InfoPnl = new JPanel();
        thumbLbl = new JLabel();
        movieTb = new WebToolBar();
        titleLbl = new WebLabel();
        yearLbl = new WebLabel();
        imagePnl = new JPanel();
        fanartsScrollPane = new JScrollPane();
        thumbsScrollPane = new JScrollPane();
        thumbTb = new WebToolBar();
        thumbnailLbl = new WebLabel();
        fanartTb = new WebToolBar();
        webLabel2 = new WebLabel();
        countryTb = new WebToolBar();
        webLabel4 = new WebLabel();
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
        origTitleField = new WebTextField();
        jScrollPane2 = new JScrollPane();
        webPanel1 = new WebPanel();
        subTitleSp = new JScrollPane();
        subTitleList = new WebList();
        audioSp = new JScrollPane();
        audioList = new WebList();
        webToolBar1 = new WebToolBar();
        webLabel1 = new WebLabel();
        webToolBar2 = new WebToolBar();
        webLabel3 = new WebLabel();
        synopsScroll = new JScrollPane();
        synopsisArea = new JTextArea();
        runtimeField = new JTextField();
        directorField = new JTextField();
        genreField = new JTextField();
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
        webToolBar3 = new WebToolBar();
        webLabel5 = new WebLabel();

        star4.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); 
        star3.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); 
        star2.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); 
        star1.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); 
        star.setIcon(new ImageIcon(getClass().getResource("/image/star-empty.png"))); 
        setMargin(new Insets(10, 10, 10, 10));
        setMinimumSize(new Dimension(10, 380));
        setPreferredSize(new Dimension(562, 400));

        InfoPnl.setPreferredSize(new Dimension(562, 300));

        thumbLbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        movieTb.setFloatable(false);
        movieTb.setRollover(true);

        titleLbl.setFont(new Font("Ubuntu", 1, 13));         movieTb.add(titleLbl);

        yearLbl.setFont(new Font("Ubuntu", 1, 13));         movieTb.add(yearLbl);

        imagePnl.setPreferredSize(new Dimension(562, 125));

        fanartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fanartList.setAutoscrolls(false);
        fanartList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        fanartList.setMinimumSize(new Dimension(0, 110));
        fanartList.setVisibleRowCount(1);
        fanartsScrollPane.setViewportView(fanartList);

        thumbsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        thumbnailsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        thumbnailsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        thumbnailsList.setVisibleRowCount(1);
        thumbsScrollPane.setViewportView(thumbnailsList);

        thumbTb.setFloatable(false);
        thumbTb.setRollover(true);
        thumbTb.setFont(new Font("Ubuntu", 1, 13)); 
        thumbnailLbl.setText(Utils.i18n("thumbnails"));         thumbnailLbl.setFont(new Font("Ubuntu", 1, 13));         thumbTb.add(thumbnailLbl);

        fanartTb.setFloatable(false);
        fanartTb.setRollover(true);

        webLabel2.setText("Fanarts");
        webLabel2.setFont(new Font("Ubuntu", 1, 13));         fanartTb.add(webLabel2);

        GroupLayout imagePnlLayout = new GroupLayout(imagePnl);
        imagePnl.setLayout(imagePnlLayout);
        imagePnlLayout.setHorizontalGroup(
            imagePnlLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(imagePnlLayout.createSequentialGroup()
                .addGroup(imagePnlLayout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(thumbTb, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                    .addComponent(thumbsScrollPane))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(imagePnlLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(fanartsScrollPane)
                    .addComponent(fanartTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        imagePnlLayout.setVerticalGroup(
            imagePnlLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, imagePnlLayout.createSequentialGroup()
                .addGroup(imagePnlLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(thumbTb, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(fanartTb, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(imagePnlLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(thumbsScrollPane, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                    .addComponent(fanartsScrollPane)))
        );

        countryTb.setFloatable(false);
        countryTb.setRollover(true);

        webLabel4.setText(Utils.i18n("country"));         webLabel4.setFont(new Font("Ubuntu", 1, 13));         countryTb.add(webLabel4);

        countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        countryList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        jScrollPane1.setViewportView(countryList);

        origTitleField.setEditable(false);

        jScrollPane2.setAutoscrolls(true);
        jScrollPane2.setPreferredSize(new Dimension(300, 300));

        subTitleSp.setViewportView(subTitleList);

        audioSp.setViewportView(audioList);

        webToolBar1.setFloatable(false);
        webToolBar1.setRollover(true);

        webLabel1.setText("Audio");
        webLabel1.setFont(new Font("Ubuntu", 1, 13));         webToolBar1.add(webLabel1);

        webToolBar2.setFloatable(false);
        webToolBar2.setRollover(true);

        webLabel3.setText("SubTitle");
        webLabel3.setFont(new Font("Ubuntu", 1, 13));         webToolBar2.add(webLabel3);

        synopsisArea.setColumns(20);
        synopsisArea.setEditable(false);
        synopsisArea.setLineWrap(true);
        synopsisArea.setRows(5);
        synopsisArea.setWrapStyleWord(true);
        synopsisArea.setBorder(null);
        synopsisArea.setOpaque(false);
        synopsScroll.setViewportView(synopsisArea);

        runtimeField.setEditable(false);
        runtimeField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

        directorField.setEditable(false);
        directorField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

        genreField.setEditable(false);
        genreField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

        GroupLayout webPanel1Layout = new GroupLayout(webPanel1);
        webPanel1.setLayout(webPanel1Layout);
        webPanel1Layout.setHorizontalGroup(
            webPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(webPanel1Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(runtimeField, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                .addGap(37, 37, 37)
                .addGroup(webPanel1Layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(directorField, GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                    .addComponent(genreField, GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE))
                .addGap(70, 70, 70))
            .addGroup(Alignment.TRAILING, webPanel1Layout.createSequentialGroup()
                .addGroup(webPanel1Layout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(audioSp, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                    .addComponent(webToolBar1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(webPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(webToolBar2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(subTitleSp)))
            .addGroup(Alignment.TRAILING, webPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(synopsScroll)
                .addGap(14, 14, 14))
        );
        webPanel1Layout.setVerticalGroup(
            webPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, webPanel1Layout.createSequentialGroup()
                .addComponent(genreField, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addGroup(webPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(directorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(runtimeField, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(synopsScroll, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(webPanel1Layout.createParallelGroup(Alignment.LEADING, false)
                    .addGroup(webPanel1Layout.createSequentialGroup()
                        .addComponent(webToolBar2, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(subTitleSp, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(webPanel1Layout.createSequentialGroup()
                        .addComponent(webToolBar1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(audioSp, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(150, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(webPanel1);

        actorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        actorScroll.setViewportView(actorList);

        webToolBar3.setFloatable(false);
        webToolBar3.setRollover(true);

        webLabel5.setText(Utils.i18n("actor"));         webLabel5.setFont(new Font("Ubuntu", 1, 13));         webToolBar3.add(webLabel5);

        GroupLayout InfoPnlLayout = new GroupLayout(InfoPnl);
        InfoPnl.setLayout(InfoPnlLayout);
        InfoPnlLayout.setHorizontalGroup(
            InfoPnlLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(movieTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(actorScroll, Alignment.TRAILING)
            .addGroup(InfoPnlLayout.createSequentialGroup()
                .addGroup(InfoPnlLayout.createParallelGroup(Alignment.TRAILING, false)
                    .addComponent(webToolBar3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(thumbLbl, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(countryTb, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, Alignment.LEADING))
                .addGroup(InfoPnlLayout.createParallelGroup(Alignment.LEADING)
                    .addGroup(InfoPnlLayout.createSequentialGroup()
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE))
                    .addGroup(InfoPnlLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(origTitleField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addComponent(imagePnl, GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
        );
        InfoPnlLayout.setVerticalGroup(
            InfoPnlLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(InfoPnlLayout.createSequentialGroup()
                .addComponent(movieTb, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(InfoPnlLayout.createParallelGroup(Alignment.LEADING)
                    .addGroup(InfoPnlLayout.createSequentialGroup()
                        .addComponent(thumbLbl, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(countryTb, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(webToolBar3, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                    .addGroup(InfoPnlLayout.createSequentialGroup()
                        .addComponent(origTitleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(actorScroll, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(imagePnl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        add(InfoPnl, BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
}
