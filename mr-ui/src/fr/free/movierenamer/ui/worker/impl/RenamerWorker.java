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
import fr.free.movierenamer.renamer.MoveFile;
import fr.free.movierenamer.renamer.Nfo;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.XMLSettings.ISimpleProperty;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.bean.UIMediaImage;
import fr.free.movierenamer.ui.bean.UIMediaInfo;
import fr.free.movierenamer.ui.bean.UIRename;
import fr.free.movierenamer.ui.bean.UIRenamer;
import fr.free.movierenamer.ui.exception.CancelException;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.UISettingsProperty;
import fr.free.movierenamer.ui.swing.dialog.FileConflictDialog;
import fr.free.movierenamer.ui.swing.dialog.FileConflictDialog.Action;
import fr.free.movierenamer.ui.swing.panel.TaskPanel;
import fr.free.movierenamer.ui.utils.UIUtils;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.ui.worker.ControlWorker;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.URIRequest;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
public class RenamerWorker extends ControlWorker<Void, RenamerWorker.RenameFile> {

  private final UIRename uirename;
  private final UIFile uiFile;
  private UIFile newUiFile;
  private final UIMediaInfo<?> info;
  private Action action = Action.none;
  private static final UISettings settings = UISettings.getInstance();
  private static final String fileToRename = "fanart|thumb|poster|cdart|logo|clearart|banner|front|back|discart|clearlogo|season|saison|movieset|set|saga";
  private static final int RETRY = 3;
  private final List<RenameFile> toMove;
  public static final File imageCacheDir = new File(Settings.APPFOLDER, "cache/images/thumb");

  private static enum DownloadImage {

    thumb(ImageCategoryProperty.thumb, UIRename.RenameOption.THUMB, UISettingsProperty.imageThumbName, UISettingsProperty.imageThumbSize,
            UISettingsProperty.imageThumbResize, false, UISettingsProperty.imageThumbHeight),
    fanart(ImageCategoryProperty.fanart, UIRename.RenameOption.FANART, UISettingsProperty.imageFanartName, UISettingsProperty.imageFanartSize,
            UISettingsProperty.imageFanartResize, true, UISettingsProperty.imageFanartWidth),
    logo(ImageCategoryProperty.logo, UIRename.RenameOption.LOGO, UISettingsProperty.imageLogoName, UISettingsProperty.imageLogoSize),
    cdart(ImageCategoryProperty.cdart, UIRename.RenameOption.CDART, UISettingsProperty.imageCdartName, UISettingsProperty.imageCdartSize),
    clearart(ImageCategoryProperty.clearart, UIRename.RenameOption.CLEARART, UISettingsProperty.imageClearartName, UISettingsProperty.imageClearartSize),
    banner(ImageCategoryProperty.banner, UIRename.RenameOption.BANNER, UISettingsProperty.imageBannerName, UISettingsProperty.imageBannerSize);

    private final ImageCategoryProperty cat;
    private final ISimpleProperty propertyFileName;
    private final ISimpleProperty uisize;
    private final ISimpleProperty resize;
    private final boolean horizontalResize;
    private final ISimpleProperty rsize;
    private final UIRename.RenameOption renameOption;

    private DownloadImage(ImageCategoryProperty cat, UIRename.RenameOption renameOption, ISimpleProperty propertyFileName, ISimpleProperty uisize) {
      this(cat, renameOption, propertyFileName, uisize, null, false, null);
    }

    private DownloadImage(ImageCategoryProperty cat, UIRename.RenameOption renameOption, ISimpleProperty propertyFileName, ISimpleProperty uisize, ISimpleProperty resize,
            boolean horizontalResize, ISimpleProperty rsize) {
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

  public RenamerWorker(MovieRenamer mr, UIFile uiFile, UIRename uirename) {
    super(mr);

    toMove = new ArrayList<>();
    this.uirename = uirename;
    this.uiFile = uiFile;
    this.info = mr.getMediaPanel().getInfo();
    newUiFile = uiFile;
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

    // Create destination folder
    if (!destFolderExists) {
      if (!destFolder.mkdirs()) {
        publish(i18n.getLanguage("error.rename.unableCreateFolder", false, destFolder.getName()));
        UISettings.LOGGER.warning(i18n.getLanguage("error.rename.unableCreateFolder", false, destFolder.getName()));
        return null;
      }
    }

    // Media file
    File newMediaFile = new File(destFolder, renamedTitle + "." + mediaSourceExt);
    if (!newMediaFile.equals(mediaSourceFile)) {
      toMove.add(new RenameFile(mediaSourceFile, newMediaFile));
    }

    // Create NFO
    if (uirename.getOption(UIRename.RenameOption.NFO)) {
      RenameFile mfile = generateNFO(renamedTitle, destFolder);
      if (mfile != null) {
        toMove.add(mfile);
      }
    }

    // Find all files with same name as media
    File[] files = mediaSourceFolder.listFiles(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        Pattern pattern = Pattern.compile("^" + Pattern.quote(mediaSourceFileNameNoExt) + "([ \\._-](" + fileToRename + "))?\\..{3}$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);//FIXME
        return pattern.matcher(name).find();
      }
    });

    File dfile;
    for (File sfile : files) {
      dfile = new File(destFolder, sfile.getName().replaceAll("(?i)" + mediaSourceFileNameNoExt, renamedTitle));

      if (sfile.equals(mediaSourceFile) || sfile.equals(dfile)) {
        continue;
      }

      toMove.add(new RenameFile(sfile, dfile));
    }

    // Download images
    ExecutorService service = Executors.newFixedThreadPool(1);// FIXME multithread is worse. need to find how many request can be made for each API
    CompletionService<RenameFile> pool = new ExecutorCompletionService<>(service);

    final String rtitle = renamedTitle;
    final File dFolder = destFolder;
    int nbThread = 0;
    for (final DownloadImage dimage : DownloadImage.values()) {
      if (uirename.getOption(dimage.getRenameOption())) {
        final UIMediaImage mimage = uirename.getSelectedImage(dimage.getCategory());
        if (mimage == null) {
          continue;
        }

        pool.submit(new Callable<RenameFile>() {

          @Override
          public RenameFile call() {
            return downloadImage(rtitle, dFolder, dimage, mimage);
          }
        });
        nbThread++;
      }
    }

    for (int i = 0; i < nbThread; i++) {
      RenameFile mfile = pool.take().get();
      if (mfile != null) {
        toMove.add(mfile);
      }
    }
    service.shutdownNow();

    try {
      Iterator<RenameFile> it = toMove.iterator();
      RenameFile mfile;
      while (it.hasNext()) {
        mfile = it.next();
        if (mfile.isDestinationExist()) {

          // Ask user what to do if we do not skip
          if (!action.equals(Action.skipAll) && !action.equals(Action.replaceAll)) {
            publishPause(mfile);
          }

          switch (action) {
            case cancel:
              clean();
              throw new CancelException();
            case replace:
            case replaceAll:
              if (mfile.getDestination().delete()) {
                UISettings.LOGGER.info(String.format("Delete [%s]", mfile.getDestination()));
              }
              mfile.setReplace(true);
              break;
            case skip:
            case skipAll:
              // Delete tmp file
              if (mfile.isTmpFile()) {
                mfile.getSource().delete();
                UISettings.LOGGER.info(String.format("Delete [%s]", mfile.getSource()));
              }
              it.remove();
              break;
          }
        }
      }

      // let's move files
      for (RenameFile rfile : toMove) {
        if (moveFile(rfile, destFolder)) {
          newUiFile = new UIFile(newMediaFile, newMediaFile.getName().substring(0, 1), uiFile.getMtype());// FIXME for tvshow
          UISettings.LOGGER.info(String.format("Move [%s] to [%s]", rfile.getSource(), rfile.getDestination()));
        }
      }

      // Create thumb
      UIMediaImage mimage = uirename.getSelectedImage(ImageCategoryProperty.thumb);
      String thumb = null;
      if (mimage != null) {
        if (!imageCacheDir.exists()) {
          imageCacheDir.mkdirs();
        }
        try {
          File outputFile = new File(imageCacheDir, uiFile.getMd5Hash());
          downloadAndScaleImage(mimage.getUri(ImageSize.small), outputFile, true, true, 45);
          thumb = outputFile.toString();
        } catch (Exception e) {
        }
      }

      UIRenamer renamer = UIRenamer.getInstance();
      renamer.addRenamed(uiFile, mediaSourceFile, newMediaFile, toMove, thumb);

      clean();

    } catch (CancelException ex) {
      // User cancel
      UISettings.LOGGER.info(String.format("User cancel rename of [%s]", uiFile));
    }

    return null;
  }

  private void clean() {
    // Delete tmp file
    for (RenameFile rfile : toMove) {
      if (rfile.isTmpFile() && rfile.getSource().exists()) {
        rfile.getSource().delete();
        UISettings.LOGGER.info(String.format("Delete [%s]", rfile.getSource()));
      }
    }
  }

  private RenameFile generateNFO(String renamedTitle, File destFolder) throws Exception {
    String filename;
    File tmpFile;
    File destFile;

    try {
      Nfo nfo = new Nfo(info.getInfo(), uirename.getImages());
      Document nfoDom = nfo.getNFO();

      filename = settings.coreInstance.getMediaNFOFileName(uirename.getMediaType()).replace("<fileName>", renamedTitle);
      tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separator + filename);
      FileUtils.writeXmlFile(nfoDom, tmpFile);
      destFile = new File(destFolder, filename);

      return new RenameFile(tmpFile, destFile, true);
    } catch (ParserConfigurationException ex) {
      publish(i18n.getLanguage("error.rename.createNfoFailed", false));
      UISettings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return null;
  }

  private RenameFile downloadImage(String renamedTitle, File destFolder, DownloadImage image, UIMediaImage mimage) {

    final String filename = image.getFilename().replace("<fileName>", renamedTitle);
    final File tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separator + filename);
    final File destFile = new File(destFolder, filename);

    for (int i = 0; i < RETRY; i++) {
      try {
        downloadImage(image, mimage, tmpFile);
        return new RenameFile(tmpFile, destFile, true);
      } catch (Exception ex) {
        UISettings.LOGGER.log(Level.SEVERE, null, ex);
      }
    }

    publish(i18n.getLanguage("error.rename.downloadImageFailed", false, filename));

    return null;
  }

  private void downloadImage(DownloadImage image, UIMediaImage mediaImage, File tmpFile) throws Exception {

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
      downloadAndScaleImage(uri, tmpFile, image.isResize(), image.isResizeHorizontal(), image.getResizeSize());
    }
  }

  private void downloadAndScaleImage(URI uri, File outputFile, boolean resize, boolean horizontal, Integer size) throws Exception {
    // TODO add cache
    InputStream input = URIRequest.getInputStream(uri);
    Image bimg = ImageIO.read(input);
    
    if (resize) {
      int width = size;
      int height = size;
      if (horizontal) {
        height = width * bimg.getHeight(null) / bimg.getWidth(null);
      } else {
        width = height * bimg.getWidth(null) / bimg.getHeight(null);
      }

      bimg = bimg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    int imageType = settings.getImageFormat().name().equals("JPG") ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
    BufferedImage buffered = new BufferedImage(bimg.getWidth(null), bimg.getHeight(null), imageType);
    buffered.getGraphics().drawImage(bimg, 0, 0, null);
    ImageIO.write(buffered, settings.getImageFormat().name(), outputFile);
  }

  private Boolean moveFile(RenameFile rfile, File destFolder) throws Exception {

    File sourceFile = rfile.getSource();
    File destFile = rfile.getDestination();

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
            UISettings.LOGGER.log(Level.WARNING, null, "Md5 failed for file : " + sourceFile);
            publish(i18n.getLanguage("error.rename.copyCheckFailed", false, sourceFile.getName()));
            return false;
          case REMOVE_FAILED:
            UISettings.LOGGER.log(Level.WARNING, null, "Remove failed for file : " + sourceFile);
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
  protected void processPause(RenameFile conflictFile) {
    FileConflictDialog conflictDialog = new FileConflictDialog(mr, conflictFile.getSource(), conflictFile.getDestination());
    conflictDialog.setVisible(true);
    action = conflictDialog.getAction();
  }

  @Override
  public String getDisplayName() {
    return UIUtils.i18n.getLanguage("main.statusTb.renaming", false);
  }

  @Override
  public WorkerId getWorkerId() {
    return WorkerId.RENAME;
  }

  public class RenameFile {

    private final File source;
    private final File destination;
    private final boolean isTmpFile;
    private boolean isReplace;

    public RenameFile(File source, File destination) {
      this(source, destination, false);
    }

    public RenameFile(File source, File destination, boolean isTmpFile) {
      this.source = source;
      this.destination = destination;
      this.isTmpFile = isTmpFile;
      isReplace = false;
    }

    public File getSource() {
      return source;
    }

    public File getDestination() {
      return destination;
    }

    public boolean isTmpFile() {
      return isTmpFile;
    }

    public boolean isDestinationExist() {
      return destination.exists();
    }

    public boolean isReplace() {
      return isReplace;
    }

    public void setReplace(boolean isReplace) {
      this.isReplace = isReplace;
    }

    @Override
    public String toString() {
      String str = String.format("Source [%s] Destination [%s] ", source.toString(), destination.toString());
      if (isTmpFile) {
        str += " TEMPFILE";
      }

      if (isReplace) {
        str += " REPLACE";
      }

      return str;
    }

  }
}
