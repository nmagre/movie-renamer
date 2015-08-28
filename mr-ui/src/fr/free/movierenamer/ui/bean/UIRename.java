/*
 * Movie Renamer
 * Copyright (C) 2014 Nicolas Magré
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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.ui.swing.UIManager;
import fr.free.movierenamer.ui.swing.panel.ImagePanel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class UIRename
 *
 * @author Nicolas Magré
 */
public class UIRename {

    private final UIFile file;
    private final String renamedTitle;
    private final Map<RenameOption, Boolean> options;
    private final List<ImageInfo> images;
    private final Map<ImageInfo.ImageCategoryProperty, UIMediaImage> selectedImages;
    private final MediaType mediaType;

    public static enum RenameOption {

        NFO(false),
        THUMB(false),
        FANART(false),
        CDART(false),
        LOGO(false),
        CLEARART(false),
        BANNER(false);

        private final boolean defaultValue;

        private RenameOption(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }

        public boolean getDefaultValue() {
            return defaultValue;
        }
    }

    public UIRename(UIFile file, String renamedTitle, Map<RenameOption, Boolean> options, MediaType mediaType) {
        this.file = file;
        this.renamedTitle = renamedTitle;
        this.options = options;
        this.mediaType = mediaType;
        ImagePanel imagePanel = UIManager.getImagePanel();
        this.images = imagePanel.getImages();
        selectedImages = new HashMap<>();

        UIMediaImage selectedImage;
        for (ImageInfo.ImageCategoryProperty property : ImageInfo.ImageCategoryProperty.values()) {
            selectedImage = imagePanel.getSelectedImage(property);
            if (selectedImage != null) {
                selectedImages.put(property, selectedImage);
            }
        }
    }

    public UIFile getFile() {
        return file;
    }

    public String getRenamedTitle() {
        return renamedTitle;
    }

    public List<ImageInfo> getImages() {
        return images;
    }

    public UIMediaImage getSelectedImage(ImageInfo.ImageCategoryProperty property) {
        return selectedImages.get(property);
    }

    public boolean getOption(RenameOption option) {

        if (options != null && options.containsKey(option)) {
            return options.get(option);
        }

        return option.getDefaultValue();
    }

    public Map<RenameOption, Boolean> getOptions() {
        return options;
    }

    public MediaType getMediaType() {
        return mediaType;
    }
}
