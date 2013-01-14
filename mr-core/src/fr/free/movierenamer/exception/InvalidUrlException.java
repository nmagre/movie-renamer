
package fr.free.movierenamer.exception;


public class InvalidUrlException extends Exception {
  private static final long serialVersionUID = 1L;

  public InvalidUrlException(String e) {
    super(e);
  }

  public InvalidUrlException(String msg, Throwable e) {
    super(msg, e);
  }
}