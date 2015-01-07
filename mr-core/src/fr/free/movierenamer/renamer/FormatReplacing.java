/*
 * mr-core
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
package fr.free.movierenamer.renamer;

import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.StringUtils.CaseConversionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class FormatReplacing, simple tag replace for naming scheme
 *
 * @author Nicolas Magré
 */
public class FormatReplacing {

  private static final String tokenStart = "<";
  private static final String tokenEnd = ">";
  private static final char optionSeparator = ':';
  private static final char equalsSeparator = '=';
  private static final char notEqualsSeparator = '!';
  private static final Pattern valueIndex = Pattern.compile("[a-z]+(\\d+)");
  private final Map<String, Object> replace;

  /**
   * Tag options
   */
  private enum Option {

    i,// ignore case
    u,// upper case
    l,// lower case
    f,// first letter upper
    w,// keep only word caracter
    d// keep only digit
  }

  /**
   * Simple tag replace for naming scheme
   *
   * @param replace Map of tags and replace values
   */
  public FormatReplacing(Map<String, Object> replace) {
    this.replace = replace;
  }

  /**
   * Replace tag by value
   *
   * @param format Naming scheme
   * @return Replaced string by values
   */
  public String getReplacedString(String format) {
    return getReplacedString(format, CaseConversionType.FIRSTLO, ", ", 0);
  }

  /**
   * Replace tag by value
   *
   * @param format Naming scheme
   * @param caseType Default used case if tag do not have a case option
   * @param separator Separator for multiple value tag
   * @param limit Limit for multiple value tag (limit less than 1 -> all)
   * @return Replaced string by values
   */
  public String getReplacedString(String format, CaseConversionType caseType, String separator, int limit) {

    // Split format
    List<String> list = new ArrayList<String>();
    list.addAll(Arrays.asList(format.split("(((?=[" + tokenStart + "])|(?<=" + tokenStart + "))|((?=" + tokenEnd + ")|(?<=" + tokenEnd + ")))")));

    TreeNode charaTree = new TreeNode("");
    TreeNode currentTree = charaTree;
    boolean append = false;

    // Transform list to a tree
    for (String str : list) {

      if (str.isEmpty()) {
        continue;
      }

      if (str.equals(tokenStart)) {

        if (currentTree.getParent() != null) {
          if (!currentTree.isToken()) {
            currentTree = currentTree.getParent();
          }
        }

        currentTree = currentTree.addChild(new TreeNode(""));
        currentTree.setIsToken(true);
        append = true;
      } else if (str.equals(tokenEnd)) {

        if (currentTree.getParent() != null) {

          if (!currentTree.isToken() && currentTree.getParent().isToken()) {
            currentTree.getParent().setIsClosed(true);
            currentTree = currentTree.getParent();
          } else {
            currentTree.setIsClosed(true);
          }

          currentTree = currentTree.getParent();
        }

      } else {

        if (append) {
          currentTree.appendData(str);
          append = false;
        } else {

          currentTree = currentTree.addChild(new TreeNode(str));
        }
      }

    }

    // Flat the tree if tokenEnd is missing
    flatNoEndToken(charaTree);
    String result = getTagString(charaTree, caseType, separator, limit);
    System.out.println("Format : " + format);
    System.out.println(charaTree.printTree(0));

    return result;
  }

  private String getTagString(TreeNode tree, CaseConversionType caseType, String separator, int limit) {

    StringBuilder data = new StringBuilder(tree.getData());
    String childtokenData;
    for (TreeNode child : tree.getChilds()) {
      if (child.isToken() && child.containsToken()) {
        childtokenData = getTagString(child.getTokenChild(), caseType, separator, limit);
        System.out.println("child data for [" + child.getData() + "] = |" + childtokenData + "|");
        if (childtokenData.isEmpty()) {
          return "";
        }
      }
      data.append(getTagString(child, caseType, separator, limit));
    }

    if (!tree.isToken() || !tree.isClosed()) {
      return data.toString();
    }

    System.out.println("Tag : " + data);

    List<Option> options = new ArrayList<Option>();
    String equalsOption = null;
    boolean equals = true;
    boolean isIfCondition = false;
    boolean isOption = false;

    char lastC = '\0';
    int index = 0;
    StringBuilder tag = new StringBuilder(data);
    char c;
    try {

      for (int i = 0; i < data.length(); i++) {
        c = data.charAt(i);

        switch (c) {
          case notEqualsSeparator:
            if (isOption) {
              isOption = false;
            }
            break;
          case equalsSeparator:
            isOption = false;
            if (equalsOption == null) {

              if (lastC == notEqualsSeparator) {
                equals = false;
                tag.deleteCharAt(index - 1);
                index--;
              }

              equalsOption = "";
              if (index + 1 < tag.length()) {
                equalsOption = tag.substring(index + 1, tag.length());
                tag = new StringBuilder(tag.subSequence(0, index + 1));
              }

              tag.deleteCharAt(index);
              index--;
            }
            break;
          case '?':
            isOption = false;
            isIfCondition = true;
            tag.deleteCharAt(index);
            index--;
            break;
          case optionSeparator:

            if (isIfCondition) {
              isIfCondition = false;
            } else {
              isOption = true;
            }
            tag.deleteCharAt(index);
            index--;
            break;
          default:
        }

        if (isOption && c != optionSeparator) {
          try {
            options.add(Option.valueOf("" + c));
          } catch (Exception e) {
          }
          tag.deleteCharAt(index);
          index--;
        }

        lastC = c;
        index++;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      System.err.println(tag);
      System.err.println(equalsOption);
    }

    return getValue(tag.toString(), caseType, separator, limit, equalsOption, equals, options.toArray(new Option[options.size()]));
  }

  /**
   * Remove all child of nodes which are not a TOKEN This mean that tokenEnd is
   * missing
   *
   * @param tree
   */
  private void flatNoEndToken(TreeNode tree) {
    List<TreeNode> childs = new ArrayList<TreeNode>(tree.getChilds());
    Iterator<TreeNode> it = childs.iterator();
    TreeNode child;
    while (it.hasNext()) {
      child = it.next();
      flatNoEndToken(child);

      if (!child.isToken() && child.hasChild() || child.isToken() && !child.isClosed()) {
        child.getParent().addChilds(child.getChilds());
        child.removeChilds();
      }
    }
  }

  /**
   * Get tag value
   *
   * @param token Tag
   * @param equalsValue
   * @param options Tag options
   * @return Value or token if tag is not found
   */
  private String getValue(String token, CaseConversionType caseType, String separator, int limit, String equalsValue, boolean equals, Option... options) {

    int index = -1;
    final Matcher matcher = valueIndex.matcher(token);

    // Get index for multiple value Tag (ex: <a1>)
    if (matcher.find()) {
      index = Integer.parseInt(matcher.group(1)) - 1;
      token = token.replaceAll("\\d+", "");
    }

    // Get token value
    final Object obj = replace.get(token);
    if (obj == null) {
      if (replace.containsKey(token)) {
        return "";
      }

      return token;
    }

    String value = obj.toString();
    if (obj instanceof List) {
      final List<?> list = (List<?>) obj;
      value = "";
      if (index >= 0) {
        if (index < list.size()) {
          value = list.get(index).toString();
        }
      } else {
        value = StringUtils.arrayToString(list, separator, limit);
      }
    }

    if (equalsValue != null) {
      if (value.equalsIgnoreCase(equalsValue)) {
        if (!equals) {
          value = "";
        }
      } else if (equals) {
        value = "";
      }
    }

    CaseConversionType scase = null;
    for (Option option : options) {
      switch (option) {
        case d:
          value = value.replaceAll("\\D", "");
          break;
        case f:
          scase = CaseConversionType.FIRSTLA;
          break;
        case i:
          scase = CaseConversionType.NONE;
          break;
        case l:
          scase = CaseConversionType.LOWER;
          break;
        case u:
          scase = CaseConversionType.UPPER;
          break;
        case w:
          value = value.replaceAll("(?:\\W|\\d)", "");
          break;
      }
    }

    return StringUtils.applyCase(value, scase != null ? scase : caseType);
  }

  /**
   * Simple tree class
   */
  private class TreeNode {

    private final List<TreeNode> children = new LinkedList<TreeNode>();
    private TreeNode parent = null;
    private String data;
    private boolean isToken;
    private boolean isClosed;

    public TreeNode(String data) {
      this(data, null);
    }

    public TreeNode(String data, TreeNode parent) {
      this.data = data;
      this.parent = parent;
      isToken = false;
      isClosed = false;
    }

    public TreeNode getParent() {
      return parent;
    }

    public void setParent(TreeNode parent) {
      this.parent = parent;
    }

    public String getData() {
      return data;
    }

    public void appendData(String data) {
      this.data += data;
    }

    public void setIsToken(boolean isToken) {
      this.isToken = isToken;
    }

    public void setIsClosed(boolean isClosed) {
      this.isClosed = isClosed;
    }

    public boolean isToken() {
      return isToken;
    }

    public boolean isClosed() {
      return isClosed;
    }

    public boolean hasChild() {
      return !children.isEmpty();
    }

    public List<TreeNode> getChilds() {
      return Collections.unmodifiableList(children);
    }

    /**
     * Add a child and set parent
     *
     * @param child
     * @return TreeNode child
     */
    public TreeNode addChild(TreeNode child) {
      child.setParent(this);
      children.add(child);

      return child;
    }

    /**
     * Add a list of child and set parent
     *
     * @param child list of child
     */
    public void addChilds(List<TreeNode> childs) {
      for (TreeNode child : childs) {
        child.setParent(this);
        children.add(child);
      }
    }

    /**
     * Remove all child
     */
    public void removeChilds() {
      children.clear();
    }

    /**
     * Search if a child (only child of current TreeNode) is a token
     *
     * @return true if a child is a token, otherwise false
     */
    public boolean containsToken() {
      for (TreeNode child : children) {
        if (child.isToken()) {
          return true;
        }
      }

      return false;
    }

    /**
     *
     * @return
     */
    public TreeNode getTokenChild() {
      for (TreeNode child : children) {
        if (child.isToken()) {
          return child;
        }
      }

      return null;
    }

    /**
     * Get the tree as a string
     *
     * @param increment Space increment
     * @return Tree as a string
     */
    public String printTree(int increment) {
      String s;
      String inc = "";
      for (int i = 0; i < increment; ++i) {
        inc += " ";
      }

      s = inc + "|" + data + "| " + (isToken ? " [Token]" + (isClosed ? " [Closed]" : " [Opened]") : "");

      for (TreeNode child : children) {
        s += "\n" + child.printTree(increment + 2);
      }
      return s;
    }
  }

  public static void main(String[] args) {
    final Map<String, Object> replace = new HashMap<String, Object>();
    replace.put("fn", "Matrix.mkv");
    replace.put("t", "Matrix");
    replace.put("tp", "Matrix");
    replace.put("st", "The");
    replace.put("ot", "The matrix");
    replace.put("y", "1999");
    replace.put("test", "");
    replace.put("vr", "720P");

    FormatReplacing pm = new FormatReplacing(replace);
    System.out.println("Result : " + pm.getReplacedString("<tp> ou pas <fn> <st>"));
    System.out.println();
    System.out.println();
    System.out.println("Result : " + pm.getReplacedString("<tp!=<fn>> <st>"));
    System.out.println();
    System.out.println();
    System.out.println("Result : " + pm.getReplacedString("<ot!=<fn> <st>"));
    System.out.println();
    System.out.println();
    System.out.println("Result : " + pm.getReplacedString("<t!=<fn:i=<ot>>>"));
    System.out.println();
    System.out.println();
    System.out.println("Result : " + pm.getReplacedString("<t:u!=<ot>>"));
    System.out.println();
    System.out.println();
    System.out.println("Result : " + pm.getReplacedString("<t> <(<ot=!=3=2>)> (<y>)"));
    System.out.println();
    System.out.println();
    System.out.println("Result : " + pm.getReplacedString("<t> <(<test:u>) ><(<y>)>"));
    System.out.println();
    System.out.println();
    System.out.println("Result : " + pm.getReplacedString("<t> <(<vr:l=720p>) ><(<y>)>"));
    System.out.println();
    System.out.println();
    System.out.println("Result : " + pm.getReplacedString("<t> <(<test? - <tt> -:|<ot>|) ><(<y>)>"));
    System.out.println();
    System.out.println();
    System.out.println("Result : " + pm.getReplacedString("<Foo <<test><y>> Bar>", CaseConversionType.FIRSTLO, ", ", 0));
    System.out.println();
    System.out.println();
    System.out.println("Result : " + pm.getReplacedString("<Foo <humm!!!> Bar>"));

  }

}
