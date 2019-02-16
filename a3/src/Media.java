import tester.Tester;

// a piece of media
interface IMedia {

  // is this media really old?
  boolean isReallyOld();

  // are captions available in this language?
  boolean isCaptionAvailable(String language);

  // a string showing the proper display of the media
  String format();
}

abstract class AMedia implements IMedia {
  String title;
  ILoString captionOptions;

  AMedia(String title, ILoString captionOptions) {
    this.title = title;
    this.captionOptions = captionOptions;
  }

  /*
   * Template:
   * Fields:
   * this.title ... String
   * this.captionOptions ... ILoString
   *
   * Methods:
   * this.isReallyOld() ... boolean
   * this.isCaptionAvailable(String) ... boolean
   * this.format() ... String
   *
   * Methods of Fields:
   * this.captionOptions.isCaptionAvailable(String) ... boolean
   */

  // Checks if captions are available in the given language
  public boolean isCaptionAvailable(String language) {
    return this.captionOptions.isCaptionAvailable(language);
  }

  // Most media hasn't been around for long enough to ever be really old
  public boolean isReallyOld() {
    return false;
  }

  // Gets the media in the correct text format
  public abstract String format();
}

// represents a movie
class Movie extends AMedia {
  int year;

  Movie(String title, int year, ILoString captionOptions) {
    super(title, captionOptions);
    this.year = year;
  }

  /*
   * Template:
   * Fields:
   * this.year ... int
   *
   * Methods:
   * this.isReallyOld() ... boolean
   * this.format() ... String
   *
   * Methods of fields:
   * this.captionOptions.isCaptionAvailable(String) ... boolean
   */

  // Movies are really old if they were created before 1930
  public boolean isReallyOld() {
    return this.year < 1930;
  }

  // Returns this Movie in the correct text format
  public String format() {
    return this.title + " (" + this.year + ")";
  }
}

// represents a TV episode
class TVEpisode extends AMedia {
  String showName;
  int seasonNumber;
  int episodeOfSeason;

  TVEpisode(String title, String showName, int seasonNumber, int episodeOfSeason,
            ILoString captionOptions) {
    super(title, captionOptions);
    this.showName = showName;
    this.seasonNumber = seasonNumber;
    this.episodeOfSeason = episodeOfSeason;
  }

  /*
   * Template:
   * Fields:
   * this.showName ... String
   * this.seasonNumber ... int
   * this.episodeOfSeason ... int
   *
   * Methods:
   * this.format() ... String
   *
   * Methods of fields:
   * this.captionOptions.isCaptionAvailable(String) ... boolean
   */

  // Returns the properly formatted text for this TVEpisode
  public String format() {
    return this.showName + " " + this.seasonNumber + "." + this.episodeOfSeason + " - "
            + this.title;
  }
}

// represents a YouTube video
class YTVideo extends AMedia {
  String channelName;

  public YTVideo(String title, String channelName, ILoString captionOptions) {
    super(title, captionOptions);
    this.channelName = channelName;
  }

  /*
   * Template:
   * Fields:
   * this.channelName ... String
   *
   * Methods:
   * this.format() ... String
   *
   * Methods of fields:
   * this.captionOptions.isCaptionAvailable(String) ... boolean
   */

  // Returns the properly formatted text for this YouTube video
  public String format() {
    return this.title + " by " + this.channelName;
  }

}

// lists of strings
interface ILoString {
  // Checks if there are captions available in the given language
  boolean isCaptionAvailable(String language);
}

// an empty list of strings
class MtLoString implements ILoString {
  // An empty list of strings has no captions in it
  public boolean isCaptionAvailable(String language) {
    return false;
  }
}

// a non-empty list of strings
class ConsLoString implements ILoString {
  String first;
  ILoString rest;

  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * Template:
   * Fields:
   * this.first ... String
   * this.rest ... ILoString
   *
   * Methods:
   * this.isCaptionAvailable(String) ... boolean
   *
   * Methods of Fields:
   * this.rest.isCaptionAvailable(String) ... boolean
   */

  // Checks if this list of strings has the given caption language in it
  public boolean isCaptionAvailable(String language) {
    return this.first.equals(language) || this.rest.isCaptionAvailable(language);
  }
}

class ExamplesMedia {

  ILoString wowsCaptions = new ConsLoString("English",
          new ConsLoString("Spanish",
                  new ConsLoString("Wallstreet-ese", new MtLoString())));
  IMedia newMovie = new Movie("The Wolf of Wall Street", 2013, this.wowsCaptions);

  ILoString citizenKaneCaptions = new ConsLoString("Old English", new MtLoString());
  // This year is fake
  IMedia oldMovie = new Movie("Citizen Kane", 1921, this.citizenKaneCaptions);

  ILoString tvCaptions = new ConsLoString("English",
          new ConsLoString("Spanish",
                  new ConsLoString("Yiddish", new MtLoString())));
  IMedia seinfeld = new TVEpisode("The Contest", "Seinfeld", 4, 11, this.tvCaptions);
  IMedia sonsOfAnarchy = new TVEpisode("Red Rose", "Sons of Anarchy", 7, 12, this.tvCaptions);

  IMedia doubleRainbow = new YTVideo("YosemiteBear Mountain Double Rainbow", "Yosemitebear62",
          new ConsLoString("English", new MtLoString()));
  ILoString gStyleCaptions = new ConsLoString("Korean",
          new ConsLoString("English",
                  new ConsLoString("German", new MtLoString())));
  IMedia gangnamStyle = new YTVideo("Gangnam Style", "PSY", this.gStyleCaptions);


  boolean testIsReallyOld(Tester t) {
    return t.checkExpect(this.newMovie.isReallyOld(), false)
            && t.checkExpect(this.oldMovie.isReallyOld(), true)
            && t.checkExpect(this.seinfeld.isReallyOld(), false)
            && t.checkExpect(this.doubleRainbow.isReallyOld(), false);
  }

  boolean testIsCaptionAvailable(Tester t) {
    return t.checkExpect(this.oldMovie.isCaptionAvailable("Spanish"), false)
            && t.checkExpect(this.sonsOfAnarchy.isCaptionAvailable("English"), true)
            && t.checkExpect(this.gangnamStyle.isCaptionAvailable("French"), false);
  }

  boolean testIsCaptionAvailableStringList(Tester t) {
    return t.checkExpect(this.tvCaptions.isCaptionAvailable("Spanish"), true)
            && t.checkExpect(this.citizenKaneCaptions.isCaptionAvailable("German"), false);
  }

  boolean testFormat(Tester t) {
    return t.checkExpect(this.newMovie.format(), "The Wolf of Wall Street (2013)")
            && t.checkExpect(this.seinfeld.format(), "Seinfeld 4.11 - The Contest")
            && t.checkExpect(this.doubleRainbow.format(), "YosemiteBear Mountain Double Rainbow "
            + "by Yosemitebear62");
  }
}