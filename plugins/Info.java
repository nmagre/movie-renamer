
package plugins;

public class Info {

  private int key;
  private Object obj;
  
  public Info(int key, Object obj){
    this.key = key;
    this.obj = obj;
  }

  public int getKey(){
    return key;
  }

  public Object getObject(){
    return obj;
  }  
}