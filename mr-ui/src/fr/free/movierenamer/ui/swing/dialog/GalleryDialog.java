/*
 * Movie Renamer
 * Copyright (C) 2012-2014 Nicolas Magré
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
package fr.free.movierenamer.ui.swing.dialog;

import com.alee.laf.button.WebButton;
import fr.free.movierenamer.info.ImageInfo;
import static fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UILang;
import fr.free.movierenamer.ui.bean.UIMediaImage;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.custom.DragAndDropGallery;
import fr.free.movierenamer.ui.swing.model.ImageListModel;
import fr.free.movierenamer.ui.swing.panel.ImagePanel;
import fr.free.movierenamer.ui.swing.panel.ImagePanel.SupportedImages;
import fr.free.movierenamer.ui.swing.renderer.IconComboRenderer;
import fr.free.movierenamer.ui.swing.renderer.ZoomListRenderer;
import fr.free.movierenamer.ui.utils.FlagUtils;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.IWorker.WorkerId;
import fr.free.movierenamer.ui.worker.WorkerManager;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class GalleryPanel
 *
 * @author Nicolas Magré
 */
public class GalleryDialog extends AbstractDialog {

  //private static final WebButton generateBtn = UIUtils.createButton("Generate", ImageUtils.CD_16);
  private final WebButton closeBtn = UIUtils.createButton("Close", ImageUtils.CLOSE_16, false, true);// FIXME i18n
  private final ImageInfo.ImageCategoryProperty property;
  private final ImageListModel<UIMediaImage> imageListModel;
  private final ZoomListRenderer zoomListRenderer;
  private final DefaultComboBoxModel<UILang> languagesModel;
  private final ItemListener languageChangeListener;
  private final WorkerId wid;
  private final Map<UILang, Integer> selectedIndex;

  private boolean useLanguage;
  private List<UIMediaImage> images;
  private List<UIMediaImage> imagesByLang;
  private List<UILang> languages;
  private SupportedImages supportedImage;
  private final DropTarget dt;
  private int userAdId = -1;
  private UILang currentLanguage;

  @SuppressWarnings("unchecked")
  public GalleryDialog(MovieRenamer mr, SupportedImages supportedImage) {
    super(mr, UIUtils.i18n.getLanguageKey("image.gallery"));

    this.supportedImage = supportedImage;
    this.property = supportedImage.getCategoryProperty();
    float defaultRatio = supportedImage.getRatio();
    useLanguage = true;
    imageListModel = new ImageListModel<>();
    languagesModel = new DefaultComboBoxModel<>();
    images = new ArrayList<>();
    languages = new ArrayList<>();
    selectedIndex = new HashMap<>();
    currentLanguage = null;

    initComponents();
    
    topTb.addToEnd(closeBtn);

    int imgSize;
    switch (property) {
      case banner:
        imgSize = 300;
        wid = WorkerId.IMAGE_GALLERY_BANNER;
        break;
      case cdart:
        imgSize = 80;
        wid = WorkerId.IMAGE_GALLERY_CDART;
        //topTb.addToEnd(generateBtn);
        //generateBtn.addActionListener(createGenCDArtListener());
        break;
      case clearart:
        imgSize = 200;
        useLanguage = false;
        wid = WorkerId.IMAGE_GALLERY_CLEARART;
        break;
      case logo:
        imgSize = 250;
        wid = WorkerId.IMAGE_GALLERY_LOGO;
        break;
      case thumb:
        imgSize = 70;
        wid = WorkerId.IMAGE_GALLERY_THUMB;
        break;
      case fanart:
        imgSize = 200;
        useLanguage = false;
        wid = WorkerId.IMAGE_GALLERY_FANART;
        break;
      default:
        imgSize = 70;
        defaultRatio = 0.75F;
        wid = WorkerId.IMAGE_GALLERY_THUMB;
    }

    zoomListRenderer = new ZoomListRenderer(imgSize, defaultRatio);

    languageCbb.setVisible(useLanguage);
    languageCbb.setModel(languagesModel);
    languageCbb.setRenderer(new IconComboRenderer<>(languageCbb));
    languageChangeListener = createLanguageListener();

    imageList.setCellRenderer(zoomListRenderer);
    imageList.setModel(imageListModel);
    imageList.setVisibleRowCount(0);
    imageList.addListSelectionListener(createImageListListener());
    imageList.addListDataListener(createDataListener());

    zoomSlider.setMinimum(0);
    zoomSlider.setMaximum(100);
    zoomSlider.setMinorTickSpacing(5);
    zoomSlider.setMajorTickSpacing(10);
    zoomSlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        int zoomPercent = zoomSlider.getValue();
        float r = zoomPercent / 50.00F;
        zoomListRenderer.setScale(1 + r);
        imageListModel.update();
      }
    });

    bottomTb.addToEnd(nbImagesLbl);
    closeBtn.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent ae) {
        setVisible(false);
      }
    });
    

    setPreferredSize(new Dimension(600, 550));
    pack();
    setName(property.name());

    // Drag and drop
    DragAndDropGallery dropFile = new DragAndDropGallery(mr, this);
    dt = new DropTarget(imageList, dropFile);
    dt.setActive(false);
  }

  private ListSelectionListener createImageListListener() {
    return new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting()) {
          setThumb();
        }
      }
    };
  }

  private ListDataListener createDataListener() {
    return new ListDataListener() {

      @Override
      public void intervalAdded(ListDataEvent lde) {

      }

      @Override
      public void intervalRemoved(ListDataEvent lde) {

      }

      @Override
      public void contentsChanged(ListDataEvent lde) {
        nbImagesLbl.setText(imageListModel.getSize() + " Images");// FIXME i18n
      }
    };
  }

  private ItemListener createLanguageListener() {
    return new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED && languagesModel.getSize() > 0) {
          imageListModel.clear();
          selectedIndex.put(currentLanguage, getSelectedIndex());
          currentLanguage = (UILang) languagesModel.getSelectedItem();
          fetchImages(getImageByLanguage(currentLanguage));

          // Select image
          Integer prevIndex = selectedIndex.get(currentLanguage);
          imageList.setSelectedIndex(prevIndex != null ? prevIndex : 0);
        }
      }
    };
  }

//  private ActionListener createGenCDArtListener() {
//    return new ActionListener() {
//
//      @Override
//      public void actionPerformed(ActionEvent ae) {
//        SwingUtilities.invokeLater(new Runnable() {
//          @Override
//          public void run() {
//            fakeCdDial.setVisible(true);
//          }
//        });
//      }
//    };
//  }

  public int getUserAdId() {
    return userAdId--;
  }

  /**
   * Add a list of images. If language is enable, all languages will be
   * extracted and added to the languages combobox
   *
   * @param images List of UIMediaImage
   */
  public void addImages(List<UIMediaImage> images) {
    UILang imglang = null;
    this.images = images;

    dt.setActive(true);

    if (useLanguage) {
      ItemListener[] itemListeners = languageCbb.getItemListeners();
      if (itemListeners.length > 0) {
        for (ItemListener itemListener : itemListeners) {
          languageCbb.removeItemListener(itemListener);
        }
      }

      for (UIMediaImage image : this.images) {
        UILang lng = image.getImagelang();
        if (!languages.contains(lng)) {
          languages.add(lng);
          languagesModel.addElement(lng);
          if (lng.getLang().getLanguage().equals(UISettings.getInstance().coreInstance.getSearchScraperLang().getLocale().getLanguage())) {
            languagesModel.setSelectedItem(lng);
            imglang = lng;
          }

          // Select English images, if search scraper language is not found
          if (imglang == null && lng.getLang().getLanguage().equals("en")) {
            languagesModel.setSelectedItem(lng);
            imglang = lng;
          }
        }
      }

      if (languagesModel.getSize() == 0) {
        languagesModel.addElement(FlagUtils.getFlagByLang(null));
      }

      if (imglang == null && languages.size() > 0) {
        imglang = (UILang) languagesModel.getSelectedItem();
      }

      languageCbb.addItemListener(languageChangeListener);
    }

    currentLanguage = imglang;
    fetchImages(getImageByLanguage(imglang));

    // Select image
    Integer prevIndex = selectedIndex.get(imglang);
    imageList.setSelectedIndex(prevIndex != null ? prevIndex : 0);
  }

  public void addLocaleImage(UIMediaImage image) {
    imageListModel.add(image);
    imageList.setSelectedValue(image);
  }

  public void addRemoteImage(UIMediaImage image) {
    imageListModel.add(image);
    imageList.setSelectedValue(image);
    supportedImage.getLabel().setIcon(ImageUtils.LOAD_24);
    List<UIMediaImage> imgs = new ArrayList<>();
    imgs.add(image);
    WorkerManager.fetchGalleryImages(WorkerId.IMAGE_GALLERY_REMOTE, imgs, this, null, ImageUtils.NO_IMAGE,
            property.equals(ImageInfo.ImageCategoryProperty.thumb) ? ImageInfo.ImageSize.medium : ImageInfo.ImageSize.small);
  }

  private void fetchImages(List<UIMediaImage> imgs) {
    imagesByLang = imgs;
    imageListModel.addAll(imgs);

    supportedImage.getLabel().setIcon(!imgs.isEmpty() ? ImageUtils.LOAD_24 : supportedImage.getRatio() > 1
            ? ImageUtils.NO_IMAGE_H : ImageUtils.NO_IMAGE);

    if (!imgs.isEmpty()) {
      WorkerManager.fetchGalleryImages(wid, imgs, this, null, ImageUtils.NO_IMAGE,
              property.equals(ImageInfo.ImageCategoryProperty.thumb) ? ImageInfo.ImageSize.medium : ImageInfo.ImageSize.small);
    }
  }

  public List<UIMediaImage> getImageByLanguage() {
    return Collections.unmodifiableList(imagesByLang);
  }

  private List<UIMediaImage> getImageByLanguage(UILang lang) {
    List<UIMediaImage> limages = new ArrayList<>();

    int nbImage = UISettings.getInstance().getNumberImageGallery();
    if (nbImage > images.size()) {
      nbImage = images.size();
    }

    if (lang == null) {
      return new ArrayList<>(images.subList(0, nbImage));// Avoid reference issue
    }

    for (UIMediaImage image : images) {
      Locale uiimagelang = image.getImagelang().getLang();

      if (lang.isKnown()) {
        if (lang.getLang().getCountry().replace("_", "").equalsIgnoreCase(uiimagelang.getCountry())) {
          limages.add(image);
        }
      } else if (uiimagelang.equals(Locale.ROOT)) {
        limages.add(image);
      }
    }

    nbImage = UISettings.getInstance().getNumberImageGallery();
    if (nbImage > limages.size()) {
      nbImage = limages.size();
    }

    return limages.subList(0, nbImage);
  }

  public void setImage(Icon icon, int id) {

    UIMediaImage mimage = imageListModel.getElementById(id);
    if (mimage != null) {
      mimage.setIcon(icon);
      imageListModel.setElement(mimage);
    }

    if (!imageListModel.isEmpty()) {
      if (!(supportedImage.getLabel().getIcon() instanceof ImageIcon)) {
        setThumb();
      }
    }
  }

  private void setThumb() {
    UIMediaImage mediaImage = getSelectedValue();
    if (mediaImage == null) {
      return;
    }

    Icon icon = mediaImage.getIcon();
    if (icon instanceof ImageIcon) {
      Image img = ((ImageIcon) icon).getImage();
      Image newimg = img.getScaledInstance(ImagePanel.pwidth, (int) (ImagePanel.pwidth / supportedImage.getRatio()),
              java.awt.Image.SCALE_SMOOTH);
      supportedImage.getLabel().setIcon(new ImageIcon(newimg));
    }
  }

  public UIMediaImage getSelectedValue() {
    return (UIMediaImage) imageList.getSelectedValue();
  }

  private int getSelectedIndex() {
    int index = imageList.getSelectedIndex();
    return index > 0 ? index : 0;
  }

  public ImageCategoryProperty getImageProperty() {
    return property;
  }

  public void clear() {
    selectedIndex.clear();
    languageCbb.removeItemListener(languageChangeListener);
    imageListModel.clear();
    languagesModel.removeAllElements();
    languages.clear();
    nbImagesLbl.setText(null);
    images.clear();
    userAdId = -1;
    dt.setActive(false);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    nbImagesLbl = new com.alee.laf.label.WebLabel();
    topTb = new com.alee.laf.toolbar.WebToolBar();
    languageCbb = new com.alee.laf.combobox.WebComboBox();
    bottomTb = new com.alee.laf.toolbar.WebToolBar();
    zoomoutLbl = new com.alee.laf.label.WebLabel();
    zoomSlider = new com.alee.laf.slider.WebSlider();
    zoominLbl = new com.alee.laf.label.WebLabel();
    imagePnl = new com.alee.laf.panel.WebPanel();
    imageListSp = new javax.swing.JScrollPane();
    imageList = new com.alee.laf.list.WebList();

    nbImagesLbl.setText("webLabel1");

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    topTb.setFloatable(false);
    topTb.setRollover(true);
    topTb.add(languageCbb);

    getContentPane().add(topTb, java.awt.BorderLayout.PAGE_START);

    bottomTb.setFloatable(false);
    bottomTb.setRollover(true);

    zoomoutLbl.setIcon(ImageUtils.ZOOMOUT_16);
    bottomTb.add(zoomoutLbl);
    bottomTb.add(zoomSlider);

    zoominLbl.setIcon(ImageUtils.ZOOMIN_16);
    bottomTb.add(zoominLbl);

    getContentPane().add(bottomTb, java.awt.BorderLayout.PAGE_END);

    imageListSp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    imageList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    imageList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
    imageList.setVisibleRowCount(2);
    imageListSp.setViewportView(imageList);

    imagePnl.add(imageListSp, java.awt.BorderLayout.CENTER);

    getContentPane().add(imagePnl, java.awt.BorderLayout.CENTER);

    pack();
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.toolbar.WebToolBar bottomTb;
  private com.alee.laf.list.WebList imageList;
  private javax.swing.JScrollPane imageListSp;
  private com.alee.laf.panel.WebPanel imagePnl;
  private com.alee.laf.combobox.WebComboBox languageCbb;
  private com.alee.laf.label.WebLabel nbImagesLbl;
  private com.alee.laf.toolbar.WebToolBar topTb;
  private com.alee.laf.slider.WebSlider zoomSlider;
  private com.alee.laf.label.WebLabel zoominLbl;
  private com.alee.laf.label.WebLabel zoomoutLbl;
  // End of variables declaration//GEN-END:variables
}
