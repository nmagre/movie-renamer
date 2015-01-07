/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.renamer;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author duffy
 */
public class FormatReplaceingTest {

  private static final Map<String, Object> replace = new HashMap<String, Object>();
  private static final FormatReplacing fr = new FormatReplacing(replace);

  static {
    replace.put("empty", "");
    replace.put("null", null);
    replace.put("number", 720);
    replace.put("digit", 3);
    replace.put("snumber", "1080");
    replace.put("UPPER", "UPPER");
    replace.put("lower", "lower");
    replace.put("str", "Az_$3D.m");
  }

  @Test
  public void simpleFormat() {

    Assert.assertEquals("Az_$3d.m", fr.getReplacedString("<str>"));
    Assert.assertEquals("  ", fr.getReplacedString(" <empty> "));
    Assert.assertEquals("foo 720 bar", fr.getReplacedString("foo <number> bar"));
    Assert.assertEquals("foo Upper bar", fr.getReplacedString("foo <UPPER> bar"));
    Assert.assertEquals("Upper (720)", fr.getReplacedString("<empty><UPPER><null> (<number>)"));
  }

  @Test
  public void simpleOptionFormat() {
    Assert.assertEquals("UPPER", fr.getReplacedString("<UPPER:u>"));
    Assert.assertEquals("lower", fr.getReplacedString("<lower:l>"));
    Assert.assertEquals("UPPER", fr.getReplacedString("<UPPER:i>"));
    Assert.assertEquals("AZ_DM", fr.getReplacedString("<str:wu>"));
    Assert.assertEquals("3", fr.getReplacedString("<str:d>"));
    //Only last case is used (u : upper , i : ignore, l : lower)
    Assert.assertEquals("az_dm", fr.getReplacedString("<str:wuil>"));
  }

  @Test
  public void operatorFormat() {
    Assert.assertEquals("", fr.getReplacedString("<str=>"));
    Assert.assertEquals("Az_$3d.m", fr.getReplacedString("<str!=>"));
    Assert.assertEquals("", fr.getReplacedString("<digit=4>"));
    Assert.assertEquals("3", fr.getReplacedString("<digit!=4>"));
    Assert.assertEquals("3", fr.getReplacedString("<digit=<digit>>"));
    Assert.assertEquals("", fr.getReplacedString("<digit!=<digit>>"));
    Assert.assertEquals("3", fr.getReplacedString("<<digit=<digit>>>"));
    Assert.assertEquals("", fr.getReplacedString("<(=)>"));
    Assert.assertEquals("(", fr.getReplacedString("<(!=)>"));
  }

  @Test
  public void isEmptyFormat() {
    // Expression which are not a tag between "<" and ">" are not displayed if the first tag (in it)
    // is empty or null
    Assert.assertEquals("", fr.getReplacedString("<Foo <empty> Bar>"));
    Assert.assertEquals("", fr.getReplacedString("<Foo <digit!=3> Bar>"));
    Assert.assertEquals("", fr.getReplacedString("<Foo <str=$_*> Bar>"));
    Assert.assertEquals("", fr.getReplacedString("<Foo <str=> Bar>"));
    Assert.assertEquals("", fr.getReplacedString("<Foo <null><digit> Bar>"));
    Assert.assertEquals("Foo 3 Bar", fr.getReplacedString("<Foo <digit><null> Bar>"));
    Assert.assertEquals("Foo 3 Bar", fr.getReplacedString("<Foo <<digit><null>> Bar>"));
    Assert.assertEquals("", fr.getReplacedString("<Foo <<null><digit> >Bar>"));
    // <str:u>=<str> is true so str UPPER is return, but "3" != "UPPER"
    Assert.assertEquals("", fr.getReplacedString("<Foo <digit=<str:u>=<str>> Bar>"));
    Assert.assertEquals("Foo 3 Bar", fr.getReplacedString("<Foo <digit!=<str:u>=<str>> Bar>"));
  }
}
