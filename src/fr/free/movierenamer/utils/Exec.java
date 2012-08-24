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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class Exec
 * @author Nicolas Magré
 */
public class Exec {

  private StringBuilder stdErr;
  private StringBuilder stdIn;
  
  public Exec(){
    stdErr = new StringBuilder();
    stdIn = new StringBuilder();
  }
  
  public int run(String script, String args) throws IOException, InterruptedException {// TODO
      Process p = Runtime.getRuntime().exec("");
      BufferedReader debugInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
      BufferedReader errorInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      
      //Flush buffer
      stdErr.delete(0, stdErr.length());
      stdIn.delete(0, stdIn.length());
      
      String line;
      while ((line = debugInput.readLine()) != null) {
        stdIn.append(line).append(Utils.ENDLINE);
      }
      debugInput.close();
      
      while ((line = errorInput.readLine()) != null) {
        stdErr.append(line).append(Utils.ENDLINE);
      }
      errorInput.close();
      
      p.waitFor();
      return p.exitValue();
  }
    
  public String getStdIn(){
    return stdIn.toString();
  }
  
  public String getStdErr(){
    return stdErr.toString();
  }
}
