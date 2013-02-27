/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.scrapper.impl;

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.searchinfo.TvShow;
import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.URIRequest;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 * Class FanartTVshowImagesScrapper
 *
 * @author Nicolas Magré
 */
public class FanartTVshowImagesScrapper extends FanartTvScrapper<TvShow> {

  @Override
  protected List<ImageInfo> fetchImagesInfo(TvShow media, Locale language) throws Exception {// TODO
    URL searchUrl = new URL("http", host, "/movie/" + apikey + "/" + media.getMediaId() + "/");// Last slash is required
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
    JSONObject movie = JSONUtils.selectFirstObject(json);

    List<ImageInfo> imagesInfos = new ArrayList<ImageInfo>();

//    for (ImageType type : ImageType.values()) {
//      List<JSONObject> images = JSONUtils.selectList(type.name(), movie);
//      if (images == null) {
//        continue;
//      }
//
//      for (JSONObject image : images) {
//        Map<ImageInfo.ImageProperty, String> imageFields = new EnumMap<ImageInfo.ImageProperty, String>(ImageInfo.ImageProperty.class);
//        imageFields.put(ImageInfo.ImageProperty.url, JSONUtils.selectString("url", image));
//        imageFields.put(ImageInfo.ImageProperty.language, JSONUtils.selectString("lang", image));
//        ImageInfo.ImageCategoryProperty category;
//        switch (type) {
//          case hdmovielogo:
//          case movielogo:
//            category = ImageInfo.ImageCategoryProperty.logo;
//            break;
//          case hdmovieart:
//          case movieart:
//            category = ImageInfo.ImageCategoryProperty.clearart;
//            break;
//          case moviedisc:
//            category = ImageInfo.ImageCategoryProperty.cdart;
//            break;
//          case moviebackground:
//            category = ImageInfo.ImageCategoryProperty.fanart;
//            break;
//          case moviebanner:
//            category = ImageInfo.ImageCategoryProperty.banner;
//            break;
//          default:
//            category = ImageInfo.ImageCategoryProperty.unknown;
//        }
//        imagesInfos.add(new ImageInfo(imageFields, category));
//      }
//    }

    return imagesInfos;
  }
}
