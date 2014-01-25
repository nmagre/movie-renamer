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
import fr.free.movierenamer.ui.exception.CancelException;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.UISettingsProperty;
import fr.free.movierenamer.ui.swing.dialog.FileConflictDialog;
import fr.free.movierenamer.ui.swing.dialog.FileConflictDialog.Action;
import fr.free.movierenamer.ui.swing.panel.TaskPanel;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
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

  private final UIRename uirename;
  private final UIFile uiFile;
  private UIFile newUiFile;
  private final MediaInfo info;
  private final TaskPanel taskPanel;
  private Action action = Action.none;
  private static final UISettings settings = UISettings.getInstance();
  private static final String fileToRename = "fanart|thumb|poster|cdart|logo|clearart|banner|front|back|discart|clearlogo|season|saison|movieset|set|saga";
  private static final int RETRY = 3;

  private static enum GenerateImage {

    thumb(ImageCategoryProperty.thumb, UIRename.RenameOption.THUMB, UISettingsProperty.imageThumbName, UISettingsProperty.imageThumbSize,
            UISettingsProperty.imageThumbResize, false, UISettingsProperty.imageThumbHeight),
    fanart(ImageCategoryProperty.fanart, UIRename.RenameOption.FANART, UISettingsProperty.imageFanartName, UISettingsProperty.imageFanartSize,
            UISettingsProperty.imageFanartResize, true, UISettingsProperty.imageFanartWidth),
    logo(ImageCategoryProperty.logo, UIRename.RenameOption.LOGO, UISettingsProperty.imageLogoName, UISettingsProperty.imageLogoSize),
    cdart(ImageCategoryProperty.cdart, UIRename.RenameOption.CDART, UISettingsProperty.imageCdartName, UISettingsProperty.imageCdartSize),
    clearart(ImageCategoryProperty.clearart, UIRename.RenameOption.CLEARART, UISettingsProperty.imageClearartName, UISettingsProperty.imageClearartSize),
    banner(ImageCategoryProperty.banner, UIRename.RenameOption.BANNER, UISettingsProperty.imageBannerName, UISettingsProperty.imageBannerSize);

    private final ImageCategoryProperty cat;
    private final IProperty propertyFileName;
    private final IProperty uisize;
    private final IProperty resize;
    private final boolean horizontalResize;
    private final IProperty rsize;
    private final UIRename.RenameOption renameOption;

    private GenerateImage(ImageCategoryProperty cat, UIRename.RenameOption renameOption, IProperty propertyFileName, IProperty uisize) {
      this(cat, renameOption, propertyFileName, uisize, null, false, null);
    }

    private GenerateImage(ImageCategoryProperty cat, UIRename.RenameOption renameOption, IProperty propertyFileName, IProperty uisize, IProperty resize,
            boolean horizontalResize, IProperty rsize) {
      this.cat = cat;
      this.renameOption = renameOption;
      this.propertyFileName = propertyFileName;
      this.uisize = uisize;
      this.resize = resize;
      this.horizontalResize = horizontalResize;
      this.rsize = rsize;
    }

    public ImageCategoryProperty getCategory() {
      return cat;
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

    public UIRename.RenameOption getRenameOption() {
      return renameOption;
    }

  }

  public RenamerWorker(MovieRenamer mr, UIFile uiFile, TaskPanel taskPanel, UIRename uirename) {
    super(mr);

    this.uirename = uirename;
    this.uiFile = uiFile;
    this.info = mr.getMediaPanel().getInfo();
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

    if (!mediaSourceFile.exists()) {
      publish(i18n.getLanguage("error.rename.fileNotExists", false, mediaSourceFile.getName()));
      UISettings.LOGGER.warning(i18n.getLanguage("error.rename.fileNotExists", false, mediaSourceFile.getName()));
      return null;
    }

    final boolean destFolderExists = destFolder.exists();
    if (destFolderExists && !destFolder.canWrite()) {
      publish(i18n.getLanguage("error.rename.unableWriteFolder", false, mediaSourceFile.getName()));
      UISettings.LOGGER.warning(i18n.getLanguage("error.rename.unableWriteFolder", false, mediaSourceFile.getName()));
      return null;
    }

    File newMediaFile = new File(destFolder, renamedTitle + "." + mediaSourceExt);
    if (!mediaSourceFile.equals(newMediaFile) && newMediaFile.exists()) {
      publishPause(new ConflitFile(mediaSourceFile, newMediaFile));
    }

    try {
      newUiFile = uiFile;
      // Rename/move media file
      switch (action) {
        case cancel:
          throw new CancelException();
        case replace:
          action = Action.none;
        case replaceAll:
          if (newMediaFile.delete()) {
            UISettings.LOGGER.info(String.format("Delete [%s]", newMediaFile));
          }
        case none:
          if (!destFolderExists) {
            if (!destFolder.mkdirs()) {
              publish(i18n.getLanguage("error.rename.unableCreateFolder", false, destFolder.getName()));
              UISettings.LOGGER.warning(i18n.getLanguage("error.rename.unableCreateFolder", false, destFolder.getName()));
              return null;
            }
          }

          if (moveFile(mediaSourceFile, newMediaFile, destFolder)) {
            newUiFile = new UIFile(newMediaFile, newMediaFile.getName().substring(0, 1), uiFile.getMtype());// FIXME for tvshow
            UISettings.LOGGER.info(String.format("Move [%s] to [%s]", mediaSourceFile, newMediaFile));
          }
          break;
      }

      File[] files = mediaSourceFolder.listFiles(new FilenameFilter() {

        @Override
        public boolean accept(File dir, String name) {
          Pattern pattern = Pattern.compile("^" + Pattern.quote(mediaSourceFileNameNoExt) + "([ \\._-](" + fileToRename + "))?\\..{3}$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);//FIXME
          return pattern.matcher(name).find();
        }
      });

      // Move/rename other files next to the media
      File ofile;
      for (File file : files) {
        ofile = new File(destFolder, file.getName().replace(mediaSourceFileNameNoExt, renamedTitle));

        if (ofile.equals(file)) {
          continue;
        }

        if (!updateAction(file, ofile)) {
          continue;
        }

        if (moveFile(file, ofile, destFolder)) {
          UISettings.LOGGER.info(String.format("Move [%s] to [%s]", file, ofile));
        }
      }

      // Generate NFO file
      if (uirename.getOption(UIRename.RenameOption.NFO)) {
        if (generateNFO(renamedTitle, destFolder) == null) {
          return null;
        }
      }

      // Generate images
      for (GenerateImage gimage : GenerateImage.values()) {
        if (uirename.getOption(gimage.getRenameOption())) {
          UIMediaImage mimage = uirename.getSelectedImage(gimage.getCategory());
          if (mimage == null) {
            continue;
          }

          if (generateImage(renamedTitle, destFolder, gimage, mimage) == null) {
            return null;
          }
        }
      }
    } catch (CancelException ex) {
      // User cancel
      UISettings.LOGGER.info(String.format("User cancel rename of [%s]", uiFile));
    }

    return null;
  }

  private boolean updateAction(File srcFile, File destFile) throws CancelException {
    if (!(action.equals(Action.skipAll) || action.equals(Action.replaceAll))) {
      action = Action.none;

      if (destFile.exists()) {
        publishPause(new ConflitFile(destFile, srcFile));
      }
    }

    if (action.equals(Action.cancel)) {
      throw new CancelException();
    }

    return (action.equals(Action.none) || action.equals(Action.replace) || action.equals(Action.replaceAll));
  }

  private Boolean generate(File tmpFile, File destFile, File destFolder) throws Exception {

    switch (action) {
      case replace:
      case replaceAll:
        if (destFile.delete()) {
          UISettings.LOGGER.info(String.format("Delete [%s]", destFile));
        }
      case none:
        if (moveFile(tmpFile, destFile, destFolder) == null) {
          return null;
        }
        UISettings.LOGGER.info(String.format("Move [%s] to [%s]", tmpFile, destFile));
        break;
      default:
        if (tmpFile.delete()) {
          UISettings.LOGGER.info(String.format("Delete [%s]", tmpFile));
        }
    }

    return false;
  }

  private Boolean generateImage(String renamedTitle, File destFolder, GenerateImage image, UIMediaImage mimage) throws Exception {

    final String filename = image.getFilename().replace("<fileName>", renamedTitle);
    final File tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separator + filename);
    final File destFile = new File(destFolder, filename);

    for (int i = 0; i < RETRY; i++) {
      try {
        downloadImage(image, mimage, tmpFile);
      } catch (Exception ex) {
        UISettings.LOGGER.log(Level.SEVERE, null, ex);
        if (i + 1 >= RETRY) {
          publish(i18n.getLanguage("error.rename.downloadImageFailed", false, filename));
          return false;
        }
      }
    }

    if (!updateAction(tmpFile, destFile)) {
      return false;
    }

    return generate(tmpFile, destFile, destFolder);
  }

  private Boolean generateNFO(String renamedTitle, File destFolder) throws Exception {
    String filename;
    File tmpFile;
    File destFile;

    try {
      Nfo nfo = new Nfo(info, uirename.getImages());
      Document nfoDom = nfo.getNFO();

      filename = settings.coreInstance.getNFOFileName().replace("<fileName>", renamedTitle);
      tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separator + filename);
      FileUtils.writeXmlFile(nfoDom, tmpFile);
    } catch (ParserConfigurationException ex) {
      publish(i18n.getLanguage("error.rename.createNfoFailed", false));
      UISettings.LOGGER.log(Level.SEVERE, null, ex);
      return false;
    }

    destFile = new File(destFolder, filename);

    if (!updateAction(tmpFile, destFile)) {
      return false;
    }

    return generate(tmpFile, destFile, destFolder);
  }

  private Boolean moveFile(File sourceFile, File destFile, File destFolder) throws Exception {

    if (destFile.equals(sourceFile)) {
      return false;
    }

    // If FileSystem is not the same we use "MoveFile Thread" to get progress
    if (!Files.getFileStore(sourceFile.toPath()).equals(Files.getFileStore(destFolder.toPath()))) {
      final long size = sourceFile.length();
      final long availableSpace = destFolder.getFreeSpace();
      if (size >= availableSpace) {// Not enough space
        publish(i18n.getLanguage("error.rename.noSpaceLeft", false, Files.getFileStore(destFolder.toPath()).name()));
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
            publish(i18n.getLanguage("error.rename.copyCheckFailed", false, sourceFile.getName()));
            return false;
          case REMOVE_FAILED:
            publish(i18n.getLanguage("error.rename.removeFailed", false, sourceFile.getName()));
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

    boolean res = sourceFile.renameTo(destFile);
    if (!res) {
      publish(i18n.getLanguage("error.rename.renameFailed", false, sourceFile.getName()));
    }

    return res;
  }

  private void downloadImage(GenerateImage image, UIMediaImage mediaImage, File tmpFile) throws Exception {

    if (tmpFile.exists()) {
      if (tmpFile.delete()) {
        UISettings.LOGGER.info(String.format("Delete [%s]", tmpFile));
      }
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
      // TODO add cache
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

      BufferedImage buffered = new BufferedImage(bimg.getWidth(null), bimg.getHeight(null), BufferedImage.TYPE_INT_RGB);
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
    // FIXME if new file is equals to the old file, the event is not pushed,
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
