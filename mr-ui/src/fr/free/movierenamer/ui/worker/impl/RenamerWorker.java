/*
 * Copyright (C) 2013-2014 Nicolas Magré
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
package fr.free.movierenamer.ui.worker.impl;

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.ImageInfo.ImageSize;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.renamer.MoveFile;
import fr.free.movierenamer.renamer.Nfo;
import fr.free.movierenamer.settings.Settings.IProperty;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.bean.UIMediaImage;
import fr.free.movierenamer.ui.bean.UIRename;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.UISettingsProperty;
import fr.free.movierenamer.ui.swing.UIManager;
import fr.free.movierenamer.ui.swing.dialog.FileConflictDialog;
import fr.free.movierenamer.ui.swing.dialog.FileConflictDialog.Action;
import fr.free.movierenamer.ui.swing.panel.ImagePanel;
import fr.free.movierenamer.ui.swing.panel.TaskPanel;
import fr.free.movierenamer.ui.worker.ControlWorker;
import fr.free.movierenamer.ui.worker.impl.RenamerWorker.ConflitFile;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.URIRequest;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;

/**
 * Class RenamerWorker
 *
 * @author Nicolas Magré
 */
public class RenamerWorker extends ControlWorker<Void, RenamerWorker.ConflitFile> {

//  private final TaskPanel tpanel;
  private final UIRename uirename;
  private final UIFile uiFile;
  private UIFile newUiFile;
  private final MediaInfo info;
  private final List<ImageInfo> images;
  private final ImagePanel imagePanel;
  private final TaskPanel taskPanel;
  private Action action = Action.none;
  private static final UISettings settings = UISettings.getInstance();
  private static final String fileToRename = "fanart|thumb|poster|cdart|logo|clearart|banner|front|back";

  private static enum GenerateImage {

    thumb(ImageCategoryProperty.thumb, UISettingsProperty.generateThumb, UISettingsProperty.imageThumbName, UISettingsProperty.imageThumbSize,
            UISettingsProperty.imageThumbResize, false, UISettingsProperty.imageThumbHeight),
    fanart(ImageCategoryProperty.fanart, UISettingsProperty.generateFanart, UISettingsProperty.imageFanartName, UISettingsProperty.imageFanartSize,
            UISettingsProperty.imageFanartResize, true, UISettingsProperty.imageFanartWidth),
    logo(ImageCategoryProperty.logo, UISettingsProperty.generateLogo, UISettingsProperty.imageLogoName, UISettingsProperty.imageLogoSize),
    cdart(ImageCategoryProperty.cdart, UISettingsProperty.generateCdart, UISettingsProperty.imageCdartName, UISettingsProperty.imageCdartSize),
    clearart(ImageCategoryProperty.clearart, UISettingsProperty.generateClearart, UISettingsProperty.imageClearartName, UISettingsProperty.imageClearartSize),
    banner(ImageCategoryProperty.banner, UISettingsProperty.generateBanner, UISettingsProperty.imageBannerName, UISettingsProperty.imageBannerSize);

    private final ImageCategoryProperty cat;
    private final IProperty propertyBoolean;
    private final IProperty propertyFileName;
    private final IProperty uisize;
    private final IProperty resize;
    private final boolean horizontalResize;
    private final IProperty rsize;

    private GenerateImage(ImageCategoryProperty cat, IProperty propertyBoolean, IProperty propertyFileName, IProperty uisize) {
      this(cat, propertyBoolean, propertyFileName, uisize, null, false, null);
    }

    private GenerateImage(ImageCategoryProperty cat, IProperty propertyBoolean, IProperty propertyFileName, IProperty uisize, IProperty resize,
            boolean horizontalResize, IProperty rsize) {
      this.cat = cat;
      this.propertyBoolean = propertyBoolean;
      this.propertyFileName = propertyFileName;
      this.uisize = uisize;
      this.resize = resize;
      this.horizontalResize = horizontalResize;
      this.rsize = rsize;
    }

    public ImageCategoryProperty getCategory() {
      return cat;
    }

    public boolean isGenerate() {
      return Boolean.parseBoolean(propertyBoolean.getValue());
    }

    public String getFilename() {
      return propertyFileName.getValue();
    }

    public UISettings.ImageSize getSize() {
      return UISettings.ImageSize.valueOf(uisize.getValue());
    }

    public boolean isResize() {
      return resize != null ? Boolean.parseBoolean(resize.getValue()) : false;
    }

    public boolean isResizeHorizontal() {
      return horizontalResize;
    }

    public Integer getResizeSize() {
      return (Integer) (rsize != null ? Integer.valueOf(rsize.getValue()) : rsize);
    }

  }

  public RenamerWorker(MovieRenamer mr, UIFile uiFile, TaskPanel taskPanel, UIRename uirename) {
    super(mr);

    this.uirename = uirename;
    this.uiFile = uiFile;
    this.info = mr.getMediaPanel().getInfo();
    this.imagePanel = UIManager.getImagePanel();
    this.images = imagePanel.getImages();
    this.taskPanel = taskPanel;
    newUiFile = null;
  }

  @Override
  protected Void executeInBackground() throws Exception {
    // Media File source (movie, tvshow,...)
    final File mediaSourceFile = uiFile.getFile();
    final File mediaSourceFolder = mediaSourceFile.getParentFile();
    final String mediaSourceExt = FileUtils.getExtension(mediaSourceFile).toLowerCase();// FIXME let user choose case
    final String mediaSourceFileNameNoExt = FileUtils.getName(mediaSourceFile);
    // Media File destination
    String renamedTitle = uirename.getRenamedTitle();
    String folder = "";
    if (renamedTitle.contains(File.separator)) {
      folder = renamedTitle.substring(0, renamedTitle.lastIndexOf(File.separator) + 1);
      renamedTitle = renamedTitle.substring(renamedTitle.lastIndexOf(File.separator) + 1);
    }

    File destFolder = new File(folder);
    if (!destFolder.isAbsolute()) {
      destFolder = new File(mediaSourceFolder, destFolder.getPath());
    }

    final boolean destFolderExists = destFolder.exists();
    if (destFolderExists && !destFolder.canWrite()) {
      // TODO error message
      return null;
    }

    File newMediaFile = new File(destFolder, renamedTitle + "." + mediaSourceExt);
    if (!mediaSourceFile.equals(newMediaFile) && newMediaFile.exists()) {
      publishPause(new ConflitFile(mediaSourceFile, newMediaFile));
    }

    // Rename/move media file
    switch (action) {
      case cancel:
        return null;
      case replace:
        action = Action.none;
      case replaceAll:
        newMediaFile.delete();
      case none:
        if (!destFolderExists) {
          if (!destFolder.mkdirs()) {
            // TODO error message
            return null;
          }
        }

        Boolean res = moveFile(mediaSourceFile, newMediaFile, destFolder);
        if (res == null || !res) {
          // No space left or cancel or rename failed
          return null;
        }

        newUiFile = new UIFile(newMediaFile, newMediaFile.getName().substring(0, 1), uiFile.getMtype());// FIXME for tvshow
        break;
    }

    // Generate NFO file
    if (settings.coreInstance.isMovieNfogenerate()) {
      generateFile(renamedTitle, destFolder, null);
    }

    // Generate images
    for (GenerateImage image : GenerateImage.values()) {
      if (image.isGenerate()) {
        UIMediaImage mimage = imagePanel.getSelectedImage(image.getCategory());
        if (mimage == null) {
          continue;
        }

        if (!generateFile(renamedTitle, destFolder, image)) {
          // No space left or cancel
          return null;
        }
      }
    }

    File[] files = mediaSourceFolder.listFiles(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        Pattern pattern = Pattern.compile("^" + mediaSourceFileNameNoExt + "[ \\._-](" + fileToRename + ")\\..*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        return pattern.matcher(name).find();
      }
    });

    // Move/rename other files next to the media
    for (File file : files) {
      moveFile(file, new File(destFolder, file.getName().replace(mediaSourceFileNameNoExt, renamedTitle)), destFolder);
    }

    return null;
  }

  /**
   *
   * @param image
   * @return null (means stop), true (ok), false continue
   */
  private Boolean generateFile(String renamedTitle, File destFolder, GenerateImage image) throws ParserConfigurationException, Exception {
    String filename;
    File tmpFile;

    if (image == null) {// NFO
      Nfo nfo = new Nfo(info, images);
      Document nfoDom = nfo.getNFO();

      filename = settings.coreInstance.getNFOFileName().replace("<fileName>", renamedTitle);
      tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separator + filename);
      FileUtils.writeXmlFile(nfoDom, tmpFile);
    } else {
      UIMediaImage mimage = imagePanel.getSelectedImage(image.getCategory());
      filename = image.getFilename().replace("<fileName>", renamedTitle);
      tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separator + filename);

      downloadImage(image, mimage, tmpFile);
    }

    File destFile = new File(destFolder, filename);
    if (destFile.exists() && !(action.equals(Action.replaceAll) || action.equals(Action.skipAll))) {
      publishPause(new ConflitFile(destFile, tmpFile));
    }

    switch (action) {
      case cancel:
        tmpFile.delete();
        return null;
      case replace:
        action = Action.none;
      case replaceAll:
        destFile.delete();
      case none:
        if (moveFile(tmpFile, destFile, destFolder) == null) {
          return null;
        }
        break;
      default:
        tmpFile.delete();
    }

    return true;
  }

  private Boolean moveFile(File sourceFile, File destFile, File destFolder) throws Exception {

    if (destFile.equals(sourceFile)) {
      return true;
    }

    // If FileSystem is not the same we use use "MoveFile Thread" to get progress
    if (!Files.getFileStore(sourceFile.toPath()).equals(Files.getFileStore(destFolder.toPath()))) {
      System.out.println("Ok then let's move");
      final long size = sourceFile.length();
      final long availableSpace = destFolder.getFreeSpace();
      if (size >= availableSpace) {// Not enough space
        // TODO error no space available
        return null;
      }

      try {
        MoveFile moveThread = new MoveFile(sourceFile, destFile);
        moveThread.start();
        int progress;
        while (moveThread.isAlive()) {
          progress = moveThread.getProgress();
          if (progress < 0 || progress > 100) {
            progress = 0;
          }
          setProgress(progress);
          Thread.sleep(500);
        }
        moveThread.join();

        MoveFile.Status status = moveThread.getStatus();
        switch (status) {
          case CHECK_FAILED:
            // TODO error message
            return false;
          case REMOVE_FAILED:
            // TODO error message
            return false;
          case ERROR:
            throw new Exception(moveThread.getErrorString());
        }

        return true;
      } catch (InterruptedException ex) {
        UISettings.LOGGER.log(Level.SEVERE, null, ex);
      }
      return false;
    }

    return sourceFile.renameTo(destFile);
  }

  private void downloadImage(GenerateImage image, UIMediaImage mediaImage, File tmpFile) throws Exception {

    if (tmpFile.exists()) {
      tmpFile.delete();
    }

    ImageSize size;
    switch (image.getSize()) {
      case ORIGINAL:
        size = ImageSize.big;
        break;
      case MEDIUM:
        size = ImageSize.medium;
        break;
      case SMALL:
        size = ImageSize.small;
        break;
      default:
        size = ImageSize.big;
    }

    URI uri = mediaImage.getUri(size);

    if (uri.getScheme().toLowerCase().equals("file")) {// Local image
      MoveFile moveThread = new MoveFile(new File(uri), tmpFile);
      moveThread.start();
      moveThread.join();
    } else {

      InputStream input = URIRequest.getInputStream(uri);
      Image bimg = ImageIO.read(input);
      if (image.isResize()) {
        int width = image.getResizeSize();
        int height = width;
        if (image.horizontalResize) {
          height = (int) (width * bimg.getHeight(null)) / bimg.getWidth(null);
        } else {
          width = (int) (height * bimg.getWidth(null)) / bimg.getHeight(null);
        }

        bimg = bimg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
      }

      BufferedImage buffered = new BufferedImage(bimg.getWidth(null), bimg.getHeight(null), BufferedImage.TYPE_INT_ARGB);
      buffered.getGraphics().drawImage(bimg, 0, 0, null);
      ImageIO.write(buffered, settings.getImageFormat().name(), tmpFile);
    }
  }

  @Override
  protected void workerStarted() {
    UIEvent.fireUIEvent(UIEvent.Event.RENAME_FILE, uiFile, null);
  }

  @Override
  protected void workerDone() throws Exception {
    // FIXME if new file is the as old file, the event is not pushed,
    // so we replace old file by null.
    UIEvent.fireUIEvent(UIEvent.Event.RENAME_FILE_DONE, uiFile.equals(newUiFile) ? null : uiFile, newUiFile);
  }

  @Override
  public String getParam() {
    return null;
  }

  @Override
  protected void processPause(ConflitFile conflictFile) {
    FileConflictDialog conflictDialog = new FileConflictDialog(mr, conflictFile.getSourceFile(), conflictFile.getDestFile());
    conflictDialog.setVisible(true);
    action = conflictDialog.getAction();
  }

  @Override
  protected void workerProgress(int progress) {
    taskPanel.setProgress(progress);
  }

  @Override
  public String getDisplayName() {
    return "Renamer";// FIXME i18n
  }

  public class ConflitFile {

    private final File sourceFile, destFile;

    public ConflitFile(File sourceFile, File destFile) {
      this.sourceFile = sourceFile;
      this.destFile = destFile;
    }

    public File getSourceFile() {
      return sourceFile;
    }

    public File getDestFile() {
      return destFile;
    }

  }
}
