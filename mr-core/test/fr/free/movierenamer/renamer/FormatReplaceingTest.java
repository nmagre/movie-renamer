/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.renamer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author duffy
 */
public class FormatReplaceingTest {

  private static final Map<String, Object> replace = new HashMap<>();
  private static final FormatReplacing fr = new FormatReplacing(replace, "<", ">");

  static {
    replace.put("empty", "");
    replace.put("null", null);
    replace.put("number", 720);
    replace.put("digit", 3);
    replace.put("snumber", "1080");
    replace.put("UPPER", "UPPER");
    replace.put("lower", "lower");
    replace.put("digit", 3);
    replace.put("str", "Az_$3D.m");
    replace.put("array", Arrays.asList(new String[]{"v1", "v2", "v3", "v4"}));
  }

  @Test
  public void simpleFormat() {

    Assert.assertEquals("Az_$3d.m", fr.getReplacedString("<str>"));
    Assert.assertEquals("  ", fr.getReplacedString(" <empty> "));
    Assert.assertEquals("foo 720 bar", fr.getReplacedString("foo <number> bar"));
    Assert.assertEquals("foo Upper bar", fr.getReplacedString("foo <UPPER> bar"));
    Assert.assertEquals("Upper (720)", fr.getReplacedString("<empty><UPPER><null> (<number>)"));
    Assert.assertEquals("V1, v2, v3, v4", fr.getReplacedString("<array>"));
    Assert.assertEquals("V1", fr.getReplacedString("<array1>"));
    Assert.assertEquals("V3", fr.getReplacedString("<array3>"));
    Assert.assertEquals("v", fr.getReplacedString("<array2:lw>"));
    Assert.assertEquals("", fr.getReplacedString("<array2017>"));
    Assert.assertEquals("V1, v2, v3, v4", fr.getReplacedString("<array0>"));
  }

  @Test
  public void simpleOptionFormat() {
    Assert.assertEquals("UPPER", fr.getReplacedString("<UPPER:u>"));
    Assert.assertEquals("lower", fr.getReplacedString("<lower:l>"));
    Assert.assertEquals("UPPER", fr.getReplacedString("<UPPER:i>"));
    Assert.assertEquals("AZ_DM", fr.getReplacedString("<str:wu>"));
    Assert.assertEquals("3", fr.getReplacedString("<str:d>"));
    Assert.assertEquals("MyString", fr.getReplacedString("<MyString:i>"));
    Assert.assertEquals("mystring", fr.getReplacedString("<MyString:l>"));
    //Only last case is used (u : upper , i : ignore, l : lower)
    Assert.assertEquals("az_dm", fr.getReplacedString("<str:wuil>"));
  }

  @Test
  public void operatorFormat() {
    Assert.assertEquals("", fr.getReplacedString("<str=>"));
    Assert.assertEquals("Az_$3d.m", fr.getReplacedString("<str!=>"));
    Assert.assertEquals("", fr.getReplacedString("<digit=4>"));
    Assert.assertEquals("3", fr.getReplacedString("<digit!=4>"));
    Assert.assertEquals("3", fr.getReplacedString("<digit!=<number>>"));
    Assert.assertEquals("720", fr.getReplacedString("<<number>!=4874126841>"));
    Assert.assertEquals("", fr.getReplacedString("<<number>!=720>"));
    Assert.assertEquals("", fr.getReplacedString("<number!=720>"));
    Assert.assertEquals("3", fr.getReplacedString("<digit=<digit>>"));
    Assert.assertEquals("", fr.getReplacedString("<digit!=<digit>>"));
    Assert.assertEquals("3", fr.getReplacedString("<<digit=<digit>>>"));
    Assert.assertEquals("(", fr.getReplacedString("<(=(>"));
    Assert.assertEquals("", fr.getReplacedString("<(=)>"));
    Assert.assertEquals("(", fr.getReplacedString("<(!=)>"));
    Assert.assertEquals("1080:u=1080:u", fr.getReplacedString("<snumber>:u=1080:u"));
    Assert.assertEquals("1080!=1080:i", fr.getReplacedString("<snumber:u>!=1080:i"));
    Assert.assertEquals("1080", fr.getReplacedString("<<snumber>=1080>"));
    Assert.assertEquals("", fr.getReplacedString("<<snumber>!=1080>"));
    Assert.assertEquals("1080p", fr.getReplacedString("<<snumber=|720,1080|>p>"));
    Assert.assertEquals("1080p", fr.getReplacedString("<<snumber=<|720,1080|>>p>"));
    Assert.assertEquals("1080", fr.getReplacedString("<<snumber>=<|720,1080|>p>"));// FIXME ? "p" is removed which is "normal" but not user friendly 
    Assert.assertEquals("1080", fr.getReplacedString("<<snumber>=<|720,1080|><p>>"));// FIXME ? "p" is removed which is "normal" but not user friendly
    Assert.assertEquals("", fr.getReplacedString("<<|720,1080|>=<snumber>>"));
    Assert.assertEquals("720", fr.getReplacedString("<720!=<snumber>>"));
    Assert.assertEquals("V1", fr.getReplacedString("<<array1>!=<|v10,v2,v17|>>"));
    Assert.assertEquals("V1", fr.getReplacedString("<<array1>=<|v5,V2,V1,v17|>>"));
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
    Assert.assertEquals("", fr.getReplacedString("<Foo <<null><digit>> Bar>"));
    Assert.assertEquals("Foo  Bar", fr.getReplacedString("<<Foo> <<null><digit>> Bar>"));
    Assert.assertEquals("Foo 3 Bar", fr.getReplacedString("<Foo <digit><null> Bar>"));
    Assert.assertEquals("Foo 3 Bar", fr.getReplacedString("<Foo <<digit><null>> Bar>"));
    Assert.assertEquals("1080 (UPPER)", fr.getReplacedString("<<snumber> <(<empty:u>) ><(<UPPER:u>)>"));
    Assert.assertEquals("", fr.getReplacedString("<Foo <<null><digit> >Bar>"));
    // <str:u>=<str> is true so str UPPER is return, but "3" != "UPPER"
    Assert.assertEquals("", fr.getReplacedString("<Foo <digit=<str:u>=<str>> Bar>"));
    Assert.assertEquals("FoO 3 BAr", fr.getReplacedString("<FoO <digit!=<str:u>=<str>> BAr>"));
    Assert.assertEquals("", fr.getReplacedString("<FoO <digit!=|1,3,5|> BAr>"));
    Assert.assertEquals("Foo 3 Bar", fr.getReplacedString("<Foo <digit=|1,3,5|> Bar>"));
    Assert.assertEquals("", fr.getReplacedString("<Foo <digit>=|1,3,5| Bar>"));
    Assert.assertEquals("Az_$3d.m", fr.getReplacedString("<<str>=|Az_$3d.m|:d>"));
    Assert.assertEquals("Az_$3d.m=|Az_$3d|.m", fr.getReplacedString("<str>=|Az_$3d|.m"));
    Assert.assertEquals("Az_$3d.m=|B,Az,M_|$3d.m", fr.getReplacedString("<str>=|B,Az,M_|$3d.m"));
    Assert.assertEquals("", fr.getReplacedString("<<str>=|B,Az_,M_|$3d.x>"));
    Assert.assertEquals("", fr.getReplacedString("<<str>=|B,Az_,M_|$3d.m>"));
    Assert.assertEquals("Az_$3d.m 3 HD", fr.getReplacedString("<<str=Az_$3d.m> <digit> HD>"));
    Assert.assertEquals("Az_$3d.m3 HD", fr.getReplacedString("<<str=Az_$3d.m><digit> HD>"));
    Assert.assertEquals("V23 HD", fr.getReplacedString("<<array2=v2><digit> HD>"));
    Assert.assertEquals("", fr.getReplacedString("<<empty!=720>p>"));
  }

}
