/*
 * Movie Renamer
 * Copyright (C) 2012-2013 Nicolas Magré
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
package fr.free.movierenamer.ui.utils;

import fr.free.movierenamer.ui.bean.UILang;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.utils.LocaleUtils;
import java.util.Locale;
import java.util.logging.Level;
import javax.swing.Icon;

/**
 * Class Flag
 *
 * @author Nicolas Magré
 */
public abstract class FlagUtils {

  //Unknown flag
  private static final Icon Unknown = ImageUtils.getIconFromJar("country/unknown.png");

  // Only most common flag for video media
  private enum FlagsIcon {

    ara(ImageUtils.getIconFromJar("country/ara.png")),
    ar(ImageUtils.getIconFromJar("country/ar.png")),
    at(ImageUtils.getIconFromJar("country/at.png")),
    au(ImageUtils.getIconFromJar("country/au.png")),
    az(ImageUtils.getIconFromJar("country/tr.png")),
    be(ImageUtils.getIconFromJar("country/be.png")),
    bg(ImageUtils.getIconFromJar("country/bg.png")),
    br(ImageUtils.getIconFromJar("country/br.png")),
    ca(ImageUtils.getIconFromJar("country/ca.png")),
    ch(ImageUtils.getIconFromJar("country/ch.png")),
    cn(ImageUtils.getIconFromJar("country/cn.png")),
    co(ImageUtils.getIconFromJar("country/co.png")),
    cr(ImageUtils.getIconFromJar("country/cr.png")),
    cs(ImageUtils.getIconFromJar("country/cs.png")),
    cz(ImageUtils.getIconFromJar("country/cz.png")),
    da(ImageUtils.getIconFromJar("country/da.png")),
    de(ImageUtils.getIconFromJar("country/de.png")),
    dk(ImageUtils.getIconFromJar("country/dk.png")),
    el(ImageUtils.getIconFromJar("country/el.png")),
    en(ImageUtils.getIconFromJar("country/gb.png")),
    es(ImageUtils.getIconFromJar("country/es.png")),
    et(ImageUtils.getIconFromJar("country/et.png")),
    fa(ImageUtils.getIconFromJar("country/fa.png")),
    fi(ImageUtils.getIconFromJar("country/fi.png")),
    fr(ImageUtils.getIconFromJar("country/fr.png")),
    gb(ImageUtils.getIconFromJar("country/gb.png")),
    gr(ImageUtils.getIconFromJar("country/gr.png")),
    he(ImageUtils.getIconFromJar("country/he.png")),
    hi(ImageUtils.getIconFromJar("country/hi.png")),
    hk(ImageUtils.getIconFromJar("country/hk.png")),
    hr(ImageUtils.getIconFromJar("country/hr.png")),
    hu(ImageUtils.getIconFromJar("country/hu.png")),
    hy(ImageUtils.getIconFromJar("country/hy.png")),
    id(ImageUtils.getIconFromJar("country/id.png")),
    ie(ImageUtils.getIconFromJar("country/ie.png")),
    in(ImageUtils.getIconFromJar("country/in.png")),
    ir(ImageUtils.getIconFromJar("country/ir.png")),
    is(ImageUtils.getIconFromJar("country/is.png")),
    it(ImageUtils.getIconFromJar("country/it.png")),
    iw(ImageUtils.getIconFromJar("country/iw.png")),
    ja(ImageUtils.getIconFromJar("country/ja.png")),
    jp(ImageUtils.getIconFromJar("country/jp.png")),
    ko(ImageUtils.getIconFromJar("country/ko.png")),
    la(ImageUtils.getIconFromJar("country/la.png")),
    lt(ImageUtils.getIconFromJar("country/lt.png")),
    lv(ImageUtils.getIconFromJar("country/lv.png")),
    mc(ImageUtils.getIconFromJar("country/mc.png")),
    mk(ImageUtils.getIconFromJar("country/mk.png")),
    ms(ImageUtils.getIconFromJar("country/ms.png")),
    mx(ImageUtils.getIconFromJar("country/mx.png")),
    my(ImageUtils.getIconFromJar("country/my.png")),
    nl(ImageUtils.getIconFromJar("country/nl.png")),
    no(ImageUtils.getIconFromJar("country/no.png")),
    nz(ImageUtils.getIconFromJar("country/nz.png")),
    pb(ImageUtils.getIconFromJar("country/pb.png")),
    pk(ImageUtils.getIconFromJar("country/pk.png")),
    pl(ImageUtils.getIconFromJar("country/pl.png")),
    pr(ImageUtils.getIconFromJar("country/pr.png")),
    pt(ImageUtils.getIconFromJar("country/pt.png")),
    ro(ImageUtils.getIconFromJar("country/ro.png")),
    ru(ImageUtils.getIconFromJar("country/ru.png")),
    se(ImageUtils.getIconFromJar("country/se.png")),
    sg(ImageUtils.getIconFromJar("country/sg.png")),
    sk(ImageUtils.getIconFromJar("country/sk.png")),
    sl(ImageUtils.getIconFromJar("country/sl.png")),
    sq(ImageUtils.getIconFromJar("country/sq.png")),
    sr(ImageUtils.getIconFromJar("country/sr.png")),
    sv(ImageUtils.getIconFromJar("country/sv.png")),
    th(ImageUtils.getIconFromJar("country/th.png")),
    tr(ImageUtils.getIconFromJar("country/tr.png")),
    uk(ImageUtils.getIconFromJar("country/uk.png")),
    unknown(ImageUtils.getIconFromJar("country/unknown.png")),
    us(ImageUtils.getIconFromJar("country/us.png")),
    vi(ImageUtils.getIconFromJar("country/vi.png")),
    za(ImageUtils.getIconFromJar("country/za.png")),
    zh(ImageUtils.getIconFromJar("country/zh.png"));
    private final Icon flag;

    private FlagsIcon(Icon flag) {
      this.flag = flag;
    }

    public Icon getFlagIcon() {
      return flag;
    }
  }

  public static UILang getFlag(String code) {
    if (code == null || code.length() == 0 || code.equals("xx")) {// tmdb return xx if there is no language
      return new UILang(null, Unknown);
    }

    code = code.toLowerCase();

    Locale langLocal = LocaleUtils.findLanguage(code);
    Locale countryLocale = LocaleUtils.findCountry(code);

    try {
      if (langLocal != null || countryLocale != null) {
        for (FlagsIcon lFlag : FlagsIcon.values()) {

          if (lFlag.name().equals(code)) {
            return new UILang(LocaleUtils.AvailableLanguages.valueOf(code), lFlag.getFlagIcon());
          }

          if (langLocal != null) {
            String language = langLocal.getLanguage();
            if (language.equals("ar")) {
              language = "ara";
            }

            if (language.equalsIgnoreCase(lFlag.name())) {
              return new UILang(LocaleUtils.AvailableLanguages.valueOf(code), lFlag.getFlagIcon());
            }
          }

          if (countryLocale != null && langLocal.getCountry().equalsIgnoreCase(lFlag.name())) {
            return new UILang(LocaleUtils.AvailableLanguages.valueOf(code), lFlag.getFlagIcon());
          }
        }
      }
    } catch (IllegalArgumentException ex) {
      // No flag image :(
    }

    UISettings.LOGGER.log(Level.WARNING, String.format("Flag not found : %s", code));
    return new UILang(null, Unknown);
  }
}
