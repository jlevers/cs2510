//import tester.Tester;
//
//// a piece of media
//interface IMedia {
//
//  // is this media really old?
//  boolean isReallyOld();
//
//  // are captions available in this language?
//  boolean isCaptionAvailable(String language);
//
//  // a string showing the proper display of the media
//  String format();
//}
//
//// represents a movie
//class Movie implements IMedia {
//  String title;
//  int year;
//  ILoString captionOptions; // available captions
//
//  Movie(String title, int year, ILoString captionOptions) {
//    this.title = title;
//    this.year = year;
//    this.captionOptions = captionOptions;
//  }
//
//  /*
//   * Template:
//   * Fields:
//   * this.title ... String
//   * this.year ... int
//   * this.captionOptions ... ILoString
//   *
//   * Methods:
//   * this.isReallyOld() ... boolean
//   * this.isCaptionAvailable(String) ... boolean
//   * this.format() ... String
//   *
//   * Methods of fields:
//   * this.captionOptions.isCaptionAvailable(String) ... boolean
//   */
//
//  // Movies are really old if they were created before 1930
//  public boolean isReallyOld() {
//    return this.year < 1930;
//  }
//
//  // Checks if captions are available in the given language
//  public boolean isCaptionAvailable(String language) {
//    return this.captionOptions.isCaptionAvailable(language);
//  }
//
//  // Returns this Movie in the correct text format
//  public String format() {
//    return this.title + " (" + this.year + ")";
//  }
//}
//
//// represents a TV episode
//class TVEpisode implements IMedia {
//  String title;
//  String showName;
//  int seasonNumber;
//  int episodeOfSeason;
//  ILoString captionOptions; // available captions
//
//  TVEpisode(String title, String showName, int seasonNumber, int episodeOfSeason,
//      ILoString captionOptions) {
//    this.title = title;
//    this.showName = showName;
//    this.seasonNumber = seasonNumber;
//    this.episodeOfSeason = episodeOfSeason;
//    this.captionOptions = captionOptions;
//  }
//
//  /*
//   * Template:
//   * Fields:
//   * this.title ... String
//   * this.showName ... String
//   * this.seasonNumber ... int
//   * this.episodeOfSeason ... int
//   * this.captionOptions ... ILoString
//   *
//   * Methods:
//   * this.isReallyOld() ... boolean
//   * this.isCaptionAvailable(String) ... boolean
//   * this.format() ... String
//   *
//   * Methods of fields:
//   * this.captionOptions.isCaptionAvailable(String) ... boolean
//   */
//
//  // TVEpisodes haven't been around for long enough to ever be really old
//  public boolean isReallyOld() {
//    return false;
//  }
//
//  // Checks if captions are available in the given language
//  public boolean isCaptionAvailable(String language) {
//    return this.captionOptions.isCaptionAvailable(language);
//  }
//
//  // Returns the properly formatted text for this TVEpisode
//  public String format() {
//    return this.showName + " " + this.seasonNumber + "." + this.episodeOfSeason + " - " + this.title;
//  }
//}
//
//// represents a YouTube video
//class YTVideo implements IMedia {
//  String title;
//  String channelName;
//  ILoString captionOptions; // available captions
//
//  public YTVideo(String title, String channelName, ILoString captionOptions) {
//    this.title = title;
//    this.channelName = channelName;
//    this.captionOptions = captionOptions;
//  }
//
//  /*
//   * Template:
//   * Fields:
//   * this.title ... String
//   * this.channelName ... String
//   * this.captionOptions ... ILoString
//   *
//   * Methods:
//   * this.isReallyOld() ... boolean
//   * this.isCaptionAvailable(String) ... boolean
//   * this.format() ... String
//   *
//   * Methods of fields:
//   * this.captionOptions.isCaptionAvailable(String) ... boolean
//   */
//
//  // YouTube hasn't been around for long enough for any YouTube video to be really old
//  public boolean isReallyOld() {
//    return false;
//  }
//
//  // Checks if captions are available in the given language
//  public boolean isCaptionAvailable(String language) {
//    return this.captionOptions.isCaptionAvailable(language);
//  }
//
//  // Returns the properly formatted text for this YouTube video
//  public String format() {
//    return this.title + " by " + this.channelName;
//  }
//
//}
//
//// lists of strings
//interface ILoString {
//  // Checks if there are captions available in the given language
//  boolean isCaptionAvailable(String language);
//}
//
//// an empty list of strings
//class MtLoString implements ILoString {
//  // An empty list of strings has no captions in it
//  public boolean isCaptionAvailable(String language) {
//    return false;
//  }
//}
//
//// a non-empty list of strings
//class ConsLoString implements ILoString {
//  String first;
//  ILoString rest;
//
//  ConsLoString(String first, ILoString rest) {
//    this.first = first;
//    this.rest = rest;
//  }
//
//  /*
//   * Template:
//   * Fields:
//   * this.first ... String
//   * this.rest ... ILoString
//   *
//   * Methods:
//   *
//   * Methods of Fields:
//   */
//
//  // Checks if this list of strings has the given caption language in it
//  public boolean isCaptionAvailable(String language) {
//    return this.first.equals(language) || this.rest.isCaptionAvailable(language);
//  }
//}
//
//class ExamplesMedia {
//
//  ILoString wowsCaptions = new ConsLoString("English",
//          new ConsLoString("Spanish",
//                  new ConsLoString("Wallstreet-ese", new MtLoString())));
//  IMedia newMovie = new Movie("The Wolf of Wall Street", 2013, this.wowsCaptions);
//
//  ILoString citizenKaneCaptions = new ConsLoString("Old English", new MtLoString());
//  // This year is fake
//  IMedia oldMovie = new Movie("Citizen Kane", 1921, this.citizenKaneCaptions);
//
//  ILoString tvCaptions = new ConsLoString("English",
//          new ConsLoString("Spanish",
//                  new ConsLoString("Yiddish", new MtLoString())));
//  IMedia seinfeld = new TVEpisode("The Contest", "Seinfeld", 4, 11, this.tvCaptions);
//  IMedia sonsOfAnarchy = new TVEpisode("Red Rose", "Sons of Anarchy", 7, 12, this.tvCaptions);
//
//  IMedia doubleRainbow = new YTVideo("YosemiteBear Mountain Double Rainbow", "Yosemitebear62",
//          new ConsLoString("English", new MtLoString()));
//  ILoString gStyleCaptions = new ConsLoString("Korean",
//          new ConsLoString("English",
//                  new ConsLoString("German", new MtLoString())));
//  IMedia gangnamStyle = new YTVideo("Gangnam Style", "PSY", this.gStyleCaptions);
//
//
//  boolean testIsReallyOld(Tester t) {
//    return t.checkExpect(this.newMovie.isReallyOld(), false)
//            && t.checkExpect(this.oldMovie.isReallyOld(), true)
//            && t.checkExpect(this.seinfeld.isReallyOld(), false)
//            && t.checkExpect(this.doubleRainbow.isReallyOld(), false);
//  }
//
//  boolean testIsCaptionAvailable(Tester t) {
//    return t.checkExpect(this.oldMovie.isCaptionAvailable("Spanish"), false)
//            && t.checkExpect(this.sonsOfAnarchy.isCaptionAvailable("English"), true)
//            && t.checkExpect(this.gangnamStyle.isCaptionAvailable("French"), false);
//  }
//
//  boolean testIsCaptionAvailableStringList(Tester t) {
//    return t.checkExpect(this.tvCaptions.isCaptionAvailable("Spanish"), true)
//            && t.checkExpect(this.citizenKaneCaptions.isCaptionAvailable("German"), false);
//  }
//
//  boolean testFormat(Tester t) {
//    return t.checkExpect(this.newMovie.format(), "The Wolf of Wall Street (2013)")
//            && t.checkExpect(this.seinfeld.format(), "Seinfeld 4.11 - The Contest")
//            && t.checkExpect(this.doubleRainbow.format(), "YosemiteBear Mountain Double Rainbow " +
//            "by Yosemitebear62");
//  }
//}
