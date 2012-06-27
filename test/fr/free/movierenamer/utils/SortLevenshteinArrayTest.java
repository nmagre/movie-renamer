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
package fr.free.movierenamer.utils;

import java.util.ArrayList;
import org.junit.Test;

/**
 *
 * @author Nicolas Magré
 */
public class SortLevenshteinArrayTest {

  @Test
  public void sort() {
    ArrayList<String> array = new ArrayList<String>();
    array.add("Cars - Quatre roues (2006)");
    array.add("Cars 2 (2011)");
    array.add("Écarts de conduite (2001)");
    array.add("Les voitures qui ont mangé Paris (1974)");
    array.add("Cars (2006)");
    array.add("C.A.R.S. (2011)");
    array.add("Gamle mænd i nye biler (2002)");
    array.add("La grosse magouille (1980)");
    array.add("Devrim arabalari (2008)");
    array.add("Two Cars, One Night (2004)");
    array.add("Z Cars (1962)");
    array.add("King of Cars (2006)");
    array.add("Martin se la raconte (2008)");
    array.add("Babas bilar (2006)");
    array.add("Cars: Heartbeat City (1984)");
    array.add("Der Mann, der über Autos sprang (2010)");
    array.add("Fast Cars & Babies (2003)");
    array.add("Killing Cars (1986)");
    array.add("Cars.TV (2009)");
    array.add("Cars Will Make You Free (1999)");
    array.add("Children and Cars (1970)");
    array.add("Clarkson's Top 100 Cars (2001)");
    array.add("The Stuff That Makes You Lift Cars (2002)");
    array.add("...a tutte le auto della polizia (1975)");
    array.add("Born to Race (1988)");
    array.add("Chasing Classic Cars (2008)");
    array.add("Clarkson: Unleashed on Cars (1996)");
    array.add("Love, Death, & Cars (1999)");
    array.add("10th U.S. Infantry, 2nd Battalion Leaving Cars (1898)");
    array.add("American Nitro (1979)");
    array.add("Bonzenkarren (2008)");
    array.add("Bullrun: Cops, Cars & Superstars (2004)");
    array.add("Bullrun: Cops, Cars & Superstars II (2005)");
    array.add("Bullrun: Cops, Cars & Superstars III (2006)");
    array.add("Cars 2: The Video Game (2011)");
    array.add("Cars Mater-National (2007)");
    array.add("Cars Race-O-Rama (2009)");
    array.add("Cars Toons: Mater's Tall Tales (2010)");
    array.add("Classic British Cars (1999)");
    array.add("Demonstrating the Action of the Brown Hoisting and Conveying Machine in Unloading a Schooner of Iron Ore, and Loading the Material on the Cars (1900)");
    array.add("Extremely Used Cars: There Is No Hope (2011)");
    array.add("Fast Cars and Superstars: The Gillette Young Guns Celebrity Race (2007)");
    array.add("Girls and Cars in a Colored New World (2004)");
    array.add("Great Cars (2003)");
    array.add("Hot Cars (1956)");
    array.add("Little Women, Big Cars (2012)");
    array.add("Loading the Ice on Cars, Conveying It Across the Mountains and Loading It Into Boats (1902)");
    array.add("Mecum Auto Auctions: Muscle Cars & More (2008)");
    array.add("Meeting in Cars (2006)");
    array.add("Movies' Greatest Cars (2005)");
    array.add("Police Action: Filming Cops, Cars and Chaos (2008)");
    array.add("The Cars: Live (2000)");
    array.add("The Car's the Star (1994)");
    array.add("The Cars: Unlocked (2006)");
    array.add("The Inspiration for 'Cars' (2006)");
    array.add("The Road to Cars (2006)");
    array.add("Used Cars (1997)");
    array.add("When Cars Attack (1997)");
    array.add("101 Cars You Must Drive (2008)");
    array.add("10th U.S. Infantry Disembarking from Cars (1898)");
    array.add("152 Dead Cars (2010)");
    array.add("20 Cars That Changed the World (2002)");
    array.add("A Car's Life: Sparky's Big Adventure (2006)");
    array.add("America's Favorite Cars: The Complete Corvette 50th Anniversary (2004)");
    array.add("America's Favorite Cars: The Complete Mustang (2004)");
    array.add("Back from the Dead II: Kustom Cars Lead Sleds (2009)");
    array.add("Best of Cars (2005)");
    array.add("Bullrun: Cops, Cars and Superstars VII (2010)");
    array.add("Bullrun: Cops, Cars & Superstars IV (2007)");
    array.add("Calling All Cars (1935)");
    array.add("Calling All Cars (1954)");
    array.add("Cars III (2009)");
    array.add("Cars of the Future (1969)");
    array.add("Cars: Under the Hood (2006)");
    array.add("Chasing Parked Cars (2006)");
    array.add("Combat Cars (2002)");
    array.add("Dream Cars (2006)");
    array.add("Dream Cars (2011)");
    array.add("Electric Cars (2001)");
    array.add("Extreme Concept Cars (2004)");
    array.add("Hot Cars and Knockout Stars (1990)");
    array.add("Kidsongs: Cars, Boats, Trains and Planes (1986)");
    array.add("La madone des sleepings (1929)");
    array.add("Le contrôleur des wagons-lits (1935)");
    array.add("Lewis Mumford on the City, Part 2: The City - Cars or People? (1963)");
    array.add("Loading Cars (1901)");
    array.add("No Cars (????)");
    array.add("One of a Kind: Cars (2012)");
    array.add("Otros autos (2005)");
    array.add("Outlaw Street Cars: Death or Glory (2004)");
    array.add("Peeps in Cars (2010)");
    array.add("Racing Colors (1976)");
    array.add("Ranking the cars (2011)");
    array.add("Red Cars (2005)");
    array.add("Shooting Cars (2006)");
    array.add("Spike TV's 52 Favorite Cars (2004)");
    array.add("Stars in Fast Cars (2005)");
    array.add("Street Cars and Carbuncles (1917)");
    array.add("Street Sprinkling and Trolley Cars (1896)");
    array.add("Super Cars (1990)");
    array.add("Super Cars II (1991)");
    array.add("Super Cars International (1996)");
    array.add("Taiya ni môtâ ga hairutoki: Micchaku! Shingata denki jidousha (2011)");
    array.add("The Cars in Your Life (1960)");
    array.add("The Cars of the Bond Movies (2008)");
    array.add("The Cars That Ate China (2008)");
    array.add("The Refrigerator Car's Captive (1914)");
    array.add("The World's Fastest Cars (1995)");
    array.add("This Week in Cars (2010)");
    array.add("Trolley: The Cars That Built Our Cities (1993)");
    array.add("Turbocharge: The Unauthorized Story of The Cars (2008)");
    array.add("TV's Greatest Cars (2004)");
    array.add("Weapons and Wheels: The Guns, Cars and Stunts of Faster (2011)");
    array.add("When Cars Sing (2011)");
    array.add("Where Cars Don't Dare (2010)");
    array.add("Where Cars Don't Dare (2010)");
    array.add("World of Collector Cars (1991)");
    array.add("X Prize Cars: Accelerating the Future (2010)");
    array.add("Z Cars: A Lot of Fuss About Light (2010)");
    
    String testStr = "Cars";
    Levenshtein.sortByLevenshteinDistanceYear(testStr, 2006, array);
    for(String str: array){
      System.out.println(str);
    }
  }
}
