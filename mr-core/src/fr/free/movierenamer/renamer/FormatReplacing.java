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

import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.StringUtils.CaseConversionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class FormatReplacing, simple tag tokens for naming scheme
 *
 * @author Nicolas Magré
 */
public class FormatReplacing {//FIXME A tree is overkill, a simple stack should do the trick

  private static final Settings settings = Settings.getInstance();
  private static final char optionSeparator = ':';
  private static final char equalsSeparator = '=';
  private static final char notEqualsSeparator = '!';
  //private static final Pattern containsPattern = Pattern.compile(".*%.*%.*");
  private static final Pattern arrayPattern = Pattern.compile(".*\\|.*\\|.*");
  private static final Pattern valueIndex = Pattern.compile("[a-z]+(\\d+)");
  private final String tokenStart;
  private final String tokenEnd;
  private final Map<String, Object> tokens;

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
   * Simple tag tokens for naming scheme
   *
   * @param tokens Map of tags and tokens values
   */
  public FormatReplacing(Map<String, Object> tokens) {
    this(tokens, settings.getFormatTokenStart(), settings.getFormatTokenEnd());
  }

  public FormatReplacing(Map<String, Object> tokens, String tokenStart, String tokenEnd) {
    this.tokens = tokens;
    this.tokenStart = tokenStart;
    this.tokenEnd = tokenEnd;
  }

  /**
   * Replace tag by value
   *
   * @param format Naming scheme
   * @return Replaced string by values
   */
  public String getReplacedString(String format) {
    return getReplacedString(format, CaseConversionType.FIRSTLO, ", ", 0, settings.isFilenameRomanUpper());
  }

  /**
   * Replace tag by value
   *
   * @param format Naming scheme
   * @param caseType Default used case if tag do not have a case option
   * @param separator Separator for multiple value tag
   * @param limit Limit for multiple value tag (limit less than 1 -> all)
   * @param isRomanUpper Keep roman number uppercase
   * @return Replaced string by values
   */
  public String getReplacedString(String format, CaseConversionType caseType, String separator, int limit, boolean isRomanUpper) {
    
    // Split format
    List<String> list = new ArrayList<>();
    list.addAll(Arrays.asList(format.split("(((?=[" + tokenStart + "])|(?<=" + tokenStart + "))|((?=" + tokenEnd + ")|(?<=" + tokenEnd + ")))")));

    System.out.println(list);

    TreeNode charaTree = new TreeNode("root");
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

    // Flat the tree where "tokenEnd" is missing
    flatNoEndToken(charaTree);
    System.out.println("\n\nDEBUG FORMAT : " + format);
    System.out.println("DEBUG TREE --------------------------");
    System.out.println(charaTree.printTree(0));
    System.out.println("-------------------------------------");

    return getTagString(charaTree, caseType, separator, limit, isRomanUpper);
  }

  private String getTagString(TreeNode tree, CaseConversionType caseType, String separator, int limit, boolean isRomanUpper) {

    StringBuilder data = new StringBuilder(tree.isRoot() ? "" : tree.getData());
    String childtokenData;

    // If first token (or expression) is empty or null we return nothing
    TreeNode firstTokenChild = tree.getTokenChild();
    if (!tree.isRoot() && firstTokenChild != null) {
      childtokenData = getTagString(firstTokenChild, caseType, separator, limit, isRomanUpper);
      if (childtokenData.isEmpty()) {
        System.out.println("CHILD IS EMPTY return void");
        return "";
      }
    }

    // Append all childs values
    for (TreeNode child : tree.getChilds()) {
      data.append(getTagString(child, caseType, separator, limit, isRomanUpper));
    }

    // If it's not a token return value unchanged
    if (!tree.isToken()) {
      System.out.println("NOT TOKEN return : " + data);
      return data.toString();
    }

    // Let's parse options/equals/...
    List<Option> options = new ArrayList<>();
    String equalsValue = null;
    boolean equals = true;
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
            if (equalsValue == null) {

              if (lastC == notEqualsSeparator) {
                equals = false;
                tag.deleteCharAt(index - 1);
                index--;
              }

              equalsValue = "";
              if (index + 1 < tag.length()) {
                equalsValue = tag.substring(index + 1, tag.length());
                tag = new StringBuilder(tag.subSequence(0, index + 1));
              }

              tag.deleteCharAt(index);
              index--;
            }
            break;

          case optionSeparator:

            if (equalsValue == null) {
              isOption = true;

              tag.deleteCharAt(index);
              index--;
            }
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
    } catch (Exception ex) {// should never happen
      Settings.LOGGER.severe(ex.getMessage());
    }
    System.out.println("TAG : " + tag.toString() + " |" + equalsValue + "| : " + options);
    String res = getValue(tag.toString(), caseType, separator, limit, isRomanUpper, equalsValue, equals, options.toArray(new Option[options.size()]));
    System.out.println("VALUE : " + res);
    return res;
  }

  /**
   * Remove all child of nodes which are not a TOKEN This mean that tokenEnd is
   * missing
   *
   * @param tree
   */
  private void flatNoEndToken(TreeNode tree) {
    List<TreeNode> childs = new ArrayList<>(tree.getChilds());
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
   * @param equalsValue Value to compare
   * @param equals Equals if true, otherwise not equals
   * @param options Tag options
   * @return Value or token if tag is not found
   */
  private String getValue(String token, CaseConversionType caseType, String separator, int limit, boolean isRomanUpper, String equalsValue, boolean equals, Option... options) {

    int index = -1;
    final Matcher matcher = valueIndex.matcher(token);
    String value = token;
    CaseConversionType scase = null;
    Object obj;

    // Get index for multiple value Tag (ex: <a1>)
    if (matcher.find()) {
      index = Integer.parseInt(matcher.group(1)) - 1;
      value = token.replaceAll("\\d+", "");
    }

    // Get token value
    if (tokens.containsKey(value)) {
      obj = tokens.get(value);
      if (obj == null) {
        return "";
      }

      value = obj.toString();
      if (obj instanceof List) {
        final List<?> list = (List<?>) obj;
        if (index >= 0) {
          value = index >= list.size() ? "" : list.get(index).toString();
        } else {
          value = StringUtils.arrayToString(list, separator, limit);
        }
      }
    } else {
      scase = CaseConversionType.NONE;
      value = token;// Reset value (Fix token end with a number)
    }

    if (equalsValue != null) {

      if (arrayPattern.matcher(equalsValue).find()) {

        String arrValues = equalsValue.substring(equalsValue.indexOf(StringUtils.PIPE) + 1, equalsValue.lastIndexOf(StringUtils.PIPE));
        final List<String> values = Arrays.asList(arrValues.toLowerCase().split(StringUtils.COMMA));

        if (equals ^ values.contains(value.toLowerCase())) {
          value = "";
        }

      } else if (equals ^ value.equalsIgnoreCase(equalsValue)) {
        value = "";
      }
    }

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

    return StringUtils.applyCase(value, scase != null ? scase : caseType, isRomanUpper);
  }

  /**
   * Simple tree class
   */
  private class TreeNode {

    private final List<TreeNode> children = new LinkedList<>();
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

    public boolean isRoot() {
      return parent == null;
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
     * Get first token child
     *
     * @return First token child or null
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

}
