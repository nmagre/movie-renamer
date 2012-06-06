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
package fr.free.movierenamer.cli;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Original code come from Jargs (author Steve Purcell) (http://jargs.sourceforge.net/) under BSD license which is compatible with GPL license
 *
 * @author Nicolas Magré
 */
public class CmdLineParser {

  private ArrayList<String> remainingArgs = null;
  private HashMap<String, Option> options = new HashMap<String, Option>();
  private HashMap<String, Object> values;

  public static class CmdParserException extends Exception {

    public CmdParserException(String str) {
      super(str);
    }
  }

  public static abstract class Option {

    private String shortForm = null;
    private String longForm = null;
    private boolean wantsValue = false;

    protected Option(String longForm, boolean wantsValue) {
      this(null, longForm, wantsValue);
    }

    protected Option(char shortForm, String longForm, boolean wantsValue) {
      this(new String(new char[]{shortForm}), longForm, wantsValue);
    }

    private Option(String shortForm, String longForm, boolean wantsValue) {
      if (longForm == null) {
        throw new IllegalArgumentException("Null longForm not allowed");
      }
      this.shortForm = shortForm;
      this.longForm = longForm;
      this.wantsValue = wantsValue;
    }

    public String shortForm() {
      return this.shortForm;
    }

    public String longForm() {
      return this.longForm;
    }

    public boolean wantsValue() {
      return this.wantsValue;
    }

    public final Object getValue(String arg) throws CmdParserException {
      if (this.wantsValue) {
        if (arg == null) {
          throw new CmdParserException("Arg cannot be null for option -" + this.shortForm);
        }
        return this.parseValue(arg);
      } else {
        return Boolean.TRUE;
      }
    }

    protected Object parseValue(String arg) throws CmdParserException {
      return null;
    }

    public static class BooleanOption extends Option {

      public BooleanOption(char shortForm, String longForm) {
        super(shortForm, longForm, false);
      }

      public BooleanOption(String longForm) {
        super(longForm, false);
      }
    }

    public static class StringOption extends Option {

      public StringOption(char shortForm, String longForm) {
        super(shortForm, longForm, true);
      }

      public StringOption(String longForm) {
        super(longForm, true);
      }

      @Override
      protected Object parseValue(String arg) {
        return arg;
      }
    }
  }

  public final Option addOption(Option opt) {
    if (opt.shortForm() != null) {
      this.options.put("-" + opt.shortForm(), opt);
    }
    this.options.put("--" + opt.longForm(), opt);
    return opt;
  }

  public final Option addStringOption(char shortForm, String longForm) {
    return addOption(new Option.StringOption(shortForm, longForm));
  }

  public final Option addBooleanOption(char shortForm, String longForm) {
    return addOption(new Option.BooleanOption(shortForm, longForm));
  }

  public final Option addBooleanOption(String longForm) {
    return addOption(new Option.BooleanOption(longForm));
  }

  public final Object getOptionValue(Option o) {
    return getOptionValue(o, null);
  }

  public final Object getOptionValue(Option o, Object def) {
    if (!values.containsKey(o.longForm())) {
      if (!o.wantsValue) {
        return false;
      }
      return def;
    }

    return values.get(o.longForm());
  }

  public final ArrayList<String> getRemainingArgs() {
    return this.remainingArgs;
  }

  public final void parse(String[] argv) throws CmdParserException {

    int position = 0;
    this.values = new HashMap<String, Object>();
    this.remainingArgs = new ArrayList<String>();

    while (position < argv.length) {
      String curArg = argv[position];
      if (curArg.startsWith("-")) {
        if (curArg.equals("--")) { // end of options
          position += 1;
          break;
        }

        String valueArg = null;
        if (curArg.startsWith("--")) { // handle --arg=value
          int equalsPos = curArg.indexOf("=");
          if (equalsPos != -1) {
            valueArg = curArg.substring(equalsPos + 1);
            curArg = curArg.substring(0, equalsPos);
          }
        } else if (curArg.length() > 2) {  // handle -abcd
          for (int i = 1; i < curArg.length(); i++) {
            Option opt = this.options.get("-" + curArg.charAt(i));
            if (opt == null) {
              throw new CmdParserException("Unknown option " + curArg.charAt(i));
            }

            if (opt.wantsValue()) {
              throw new CmdParserException("Option -" + curArg.charAt(i) + " need a value");
            }
            addValue(opt, opt.getValue(null));

          }
          position++;
          continue;
        }

        Option opt = this.options.get(curArg);
        if (opt == null) {
          throw new CmdParserException("Unknown option " + curArg);
        }

        Object value;
        if (opt.wantsValue()) {
          if (valueArg == null) {
            position += 1;
            if (position < argv.length) {
              valueArg = argv[position];
            }
          }
          value = opt.getValue(valueArg);
        } else {
          value = opt.getValue(null);
        }

        addValue(opt, value);

        position += 1;
      } else {
        remainingArgs.add(curArg);
        position += 1;
      }
    }

    for (; position < argv.length; ++position) {
      remainingArgs.add(argv[position]);
    }
  }

  private void addValue(Option opt, Object value) throws CmdParserException {
    String key = opt.longForm();

    if (values.containsKey(key)) {
      throw new CmdParserException("Option " + key + " is already defined");
    }

    values.put(key, value);
  }
}
