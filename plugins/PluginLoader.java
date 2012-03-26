/******************************************************************************
*                                                                             *
*    Movie Renamer                                                            *
*    Copyright (C) 2011 Magré Nicolas                                         *
*                                                                             *
*    Movie Renamer is free software: you can redistribute it and/or modify    *
*    it under the terms of the GNU General Public License as published by     *
*    the Free Software Foundation, either version 3 of the License, or        *
*    (at your option) any later version.                                      *
*                                                                             *
*    This program is distributed in the hope that it will be useful,          *
*    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
*    GNU General Public License for more details.                             *
*                                                                             *
*    You should have received a copy of the GNU General Public License        *
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
*                                                                             *
******************************************************************************/

package plugins;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarFile;

/**
 * Class PluginLoader
 * @author Nicolas MAgré
 */
public class PluginLoader {
  private String pluginPath = PluginLoader.class.getProtectionDomain().getCodeSource().getLocation().toString();
	private File[] files;

	private ArrayList<Class> classIPluginInfo;

	public PluginLoader(){
		this.classIPluginInfo = new ArrayList<Class>();
		if(pluginPath.contains(File.separator)){
      pluginPath = pluginPath.substring(0, pluginPath.lastIndexOf(File.separator));
      if(pluginPath.contains(File.separator)) pluginPath = pluginPath.substring(0, pluginPath.lastIndexOf(File.separator)+1);
    }
    pluginPath += "Plugin";
    System.out.println(pluginPath);
	}

	public IPluginInfo[] loadAllInfoPlugins() throws Exception {

		initializeLoader();

		IPluginInfo[] tmpPlugins = new IPluginInfo[this.classIPluginInfo.size()];

		for(int index = 0 ; index < tmpPlugins.length; index ++ ){
			tmpPlugins[index] = (IPluginInfo)((Class)this.classIPluginInfo.get(index)).newInstance() ;
		}

		return tmpPlugins;
	}


	private void initializeLoader() throws Exception{

    files = new File(new URL(pluginPath).toURI()).listFiles(new FilenameFilter() {

      @Override
      public boolean accept(File file, String string) {
        if(string.contains(".jar")) return true;
        return false;
      }
    });

		if(files == null || files.length == 0){
			return ;
		}

		URLClassLoader loader;
		String tmp = "";
		Enumeration enumeration;
		Class tmpClass = null;

		for(int index = 0 ; index < files.length ; index ++ ){

			URL u = files[index].toURI().toURL();
			loader = new URLClassLoader(new URL[] {u});
			JarFile jar = new JarFile(files[index].getAbsolutePath());
			enumeration = jar.entries();

			while(enumeration.hasMoreElements()){

				tmp = enumeration.nextElement().toString();
				if(tmp.length() > 6 && tmp.substring(tmp.length()-6).compareTo(".class") == 0) {

					tmp = tmp.substring(0,tmp.length()-6);
					tmp = tmp.replaceAll("/",".");

					tmpClass = Class.forName(tmp ,true,loader);

					for(int i = 0 ; i < tmpClass.getInterfaces().length; i ++ ){
						if(tmpClass.getInterfaces()[i].getName().toString().equals("plugins.IPluginInfo") ) {
							this.classIPluginInfo.add(tmpClass);
						}
					}
				}
			}
		}
	}
}
