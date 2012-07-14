package fr.free.movierenamer.worker;

import javax.swing.event.SwingPropertyChangeSupport;

public abstract class MovieSearchWorker extends MediaSearchWorker {


  public MovieSearchWorker(SwingPropertyChangeSupport errorSupport, String searchTitle) {
    super(errorSupport, searchTitle);
  }

}
