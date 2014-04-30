Movie Renamer
=============

User-friendly application to rename movies, get informations like actors, genre,..., images, generate NFO file for media center and more.

### Why Movie Renamer ? 

Maintaining and keeping organized movies is not an easy task.  
Media center like [XBMC](http://xbmc.org/), [MediaPortal](http://www.team-mediaportal.com/), [YAMJ](http://www.yamj.org|),... can really help you with that task but unfortunately if you want to have great results you need to rename your files properly and then you will be able to get informations about it, but due to automatic process,... you can have some mistakes or uncompleted informations and you need to edit database or NFO files with another software.  
**Movie Renamer** is designed especially to make this task as simple as possible and doing all this stuff in one process or only what you need.

### Some features

  * A user-friendly application, easy to use
  * Cross platform (Windows, Linux, Mac OS) 
  * Detect and find movie even if the name is "dirty"
  * Download images (fanart, thumb, cdart, ..)
  * Get informations(actor, genre, country, ...) from multiple sources
  * Drag and drop files (movies, images from hard drive web browser)
  * Get informations, images in many languages
  * Rename files as you want
  * Generate NFO for media center

### Requirements

Only java version >= 7 is required to run **Movie Renamer**.  
You can also install [MediaInfo](http://mediaarea.net/fr/MediaInfo) to get file container informations and use it in file name (optional)

### Want to help ?

You can help **Movie Renamer** in different way.

#### You are a java programmer

You can send a pull request or directly join the project, just send me an email : [contact](mailto:contact@movie-renamer.fr)

#### You are not a java programmer

  * Talk about it to your friend on social network or on web site or whatever
  * Improved informations/images directly on website used (see below)
  * Make a donation to one of those website
    - [The movie db (Tmdb)](http://www.themoviedb.org)
    - [Fanart.tv](http://fanart.tv/)

### How to build

The project is in 3 parts (core, ui, updater) and you can compile it with "ant"  
`ant` or  `ant mr`

List of target :  

  * `ant core` Build only the core part
  * `ant updater` Build updater part
  * `ant installer` Build and create installer /!\ [IzPack](http://izpack.org/) is required
