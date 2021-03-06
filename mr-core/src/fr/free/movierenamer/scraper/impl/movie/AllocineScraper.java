/*
 * movie-renamer-core
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
package fr.free.movierenamer.scraper.impl.movie;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MovieInfo.MotionPictureRating;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.XPathUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Node;

/**
 * Class AllocineScraper : search movie on allocine
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class AllocineScraper extends AlloGroupScraper {

    private static final String host = "www.allocine.fr";
    private static final String imageHost = "images.allocine.fr";
    private static final String name = "Allocine";
    private static final String search = "recherche";
    private final Pattern allocineID = Pattern.compile(".*gen_cfilm=(\\d+).*");
    private final Pattern allocinePersonID = Pattern.compile(".*cpersonne=(\\d+).*");
    private final Pattern countryCode = Pattern.compile("pays-(\\d{4})");

    public AllocineScraper() {
        super(AvailableLanguages.fr);
    }

    // Only most common country for video media
    private static enum Nationality {

        N_5025("Argentine"),
        N_5029("Australie"),
        N_5032("Autriche"),
        N_5014("Belgique"),
        N_5028("Brésil"),
        N_5064("Bulgarie"),
        N_5018("Canada"),
        N_5027("Chine"),
        N_5065("Colombie"),
        N_7270("Costa Rica"),
        N_5165("République tchèque"),
        N_5061("Danemark"),
        N_5069("Finlande"),
        N_5001("France"),
        N_5129("Allemagne"),
        N_5019("Grèce"),
        N_5142("Hong Kong"),
        N_5068("Hongrie"),
        N_5093("Islande"),
        N_5042("Inde"),
        N_5086("Iran"),
        N_5030("Irlande"),
        N_5020("Italie"),
        N_5021("Japon"),
        N_5149("Malaisie"),
        N_5031("Mexique"),
        N_5177("Pays-Bas"),
        N_5005("Nouvelle-Zélande"),
        N_5155("Pakistan"),
        N_5023("Pologne"),
        N_5024("Portugal"),
        N_5088("Roumanie"),
        N_5039("Russie"),
        N_5113("Singapour"),
        N_5007("Afrique du Sud"),
        N_5017("Espagne"),
        N_5067("Suède"),
        N_5010("Suisse"),
        N_5127("Thaïlande"),
        N_5004("Royaume-Uni"),
        N_5002("USA");

        private final String country;

        private Nationality(String country) {
            this.country = country;
        }

        public String getCountry() {
            return country;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected String getHost() {
        return host;
    }

    @Override
    protected AvailableLanguages getDefaultLanguage() {
        return AvailableLanguages.fr;
    }

    @Override
    protected String getSearchString() {
        return search;
    }

    @Override
    protected String getMoviePageString(IdInfo id) {
        return "/film/fichefilm_gen_cfilm=" + id + ".html";
    }

    @Override
    protected String getCastingPageString(IdInfo id) {
        return "/film/fichefilm-" + id + "/casting/";
    }

    @Override
    protected Pattern getIdPattern() {
        return allocineID;
    }

    @Override
    protected Pattern getPersonIdPattern() {
        return allocinePersonID;
    }

    @Override
    protected String getImageHost() {
        return imageHost;
    }

    @Override
    protected MotionPictureRating getRatingScale() {
        return MotionPictureRating.FRANCE;
    }

    @Override
    protected InfoTag getInfoTag(String str) {
        try {
            return InfoTag.valueOf(str);
        } catch (Exception ex) {
        }
        return InfoTag.unknown;
    }

    @Override
    protected List<String> parseCountry(List<Node> nodes) {
        List<String> countries = new ArrayList<>();
        if (nodes != null) {
            for (Node country : nodes) {
                String href = XPathUtils.getAttribute("href", country);
                String url = decodeUrl(href.replace("acLnk ", "").replace(" underline", ""));
                Matcher m = countryCode.matcher(url);
                String cnt = null;
                if (m.find()) {
                    try {
                        cnt = Nationality.valueOf("N_" + m.group(1)).getCountry();
                    } catch (Exception ex) {
                    }
                }

                if (cnt == null) {
                    cnt = country.getTextContent();
                }

                countries.add(cnt);
            }
        }

        return countries;

    }
}
