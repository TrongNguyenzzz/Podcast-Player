import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author Dyllon Enos, Andrew Logan, Trong Nguyen, and Dane Norville
 *
 */
@SuppressWarnings("deprecation")
public class PodcastModel extends Observable {
	
	/**
	 * A hashmap that has names of episodes as keys, episodes as values.
	 */
	private HashMap<String, MediaPlayer> episodePlaylist = new HashMap<>();
	
	/**
	 * A hashmap that has names of podcasts as keys, and podcasts as values
	 */
	private HashMap<String, HashMap<String, MediaPlayer>> allPodcasts = new HashMap<>();
	
	/**
	 * The name of the current episode
	 */
	private String currentEpisodeName;
	
	/**
	 * The current episode being played, initialized to null
	 */
	private MediaPlayer currentEpisode = null;
	
	/**
	 * Hashmap that has names of episodes as keys, and episodes as values
	 */
	private HashMap<String, MediaPlayer> unShuffledPlaylist = new HashMap<>();
	
	/**
	 * The playback speed, default is 1.00
	 */
	private double currentSpeed = 1.00;
	private boolean XMLFound = false;
	
	// Use this URL for testing: https://feeds.fireside.fm/bibleinayear/rss
	// https://rss.art19.com/apology-line
	
	/**
	 * This addEpisode method will add an episode to all podcast episodes.
	 * This method is for test purposes ONLY.
	 * 
	 * @param name: This is the name of the episodes that we want to add.
	 * @param url: This is the string url for the episode.
	 */
	public void addEpisode(String name, String url) {
		URL newUrl = null;
		
		try {
			newUrl = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		}
		Media media = new Media(newUrl.toString()); 
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		episodePlaylist.put(name, mediaPlayer);
	}
	
	/**
	 * This method will remove an episode from the collection of episodes.
	 * 
	 * Returns true if episode exists and is removed from the collection.
	 * Returns false if the episode does not exist in the collection.
	 * 
	 * @param episodeName the name of the episode to be removed 
	 * @return true if the episode was in the playlist, false otherwise
	 */
	public boolean removeEpisode(String episodeName) {
		if (episodePlaylist.containsKey(episodeName)) {
			episodePlaylist.remove(episodeName);
			return true;
		}
		return false; 
	}
	
	/**
	 * This method will play an episode with the specified episode name.
	 * @param episodeName The name of the episode to be played.
	 */
	public void playEpisode(String episodeName) {
		currentEpisodeName = episodeName;
		currentEpisode = episodePlaylist.get(episodeName);
		if (currentEpisode.equals(null))
			return;
		currentEpisode.play();
		fastForward(currentSpeed); // TODO Andrew
		setChanged();
		notifyObservers(new PodcastMessage(currentEpisodeName, "play")); 
	}
	
	/**
	 * This method will pause the current episode that is playing.
	 */
	public void pauseEpisode() {
		if (currentEpisode.equals(null))
			return;
		double currentTime = currentEpisode.getCurrentTime().toSeconds();
		currentEpisode.pause();
		currentEpisode.setStartTime(Duration.seconds(currentTime));
		setChanged();
		notifyObservers(new PodcastMessage(currentEpisodeName, "pause"));
	}
	
	/**
	 * This method will stop (pause and rewind to start) the current 
	 * episode that is playing.
	 */
	public void stopEpisode() {
		currentEpisode.stop();
		setChanged();
		notifyObservers(new PodcastMessage(currentEpisodeName, "stop"));
	}
	
	
	/**
	 * Sets the playback speed for the next episode to play at. This 
	 * method is used when an episode is not currently playing.
	 * @param rate The speed of playback (must be in [0.0, 8.0]).
	 */
	public void setRate(double rate) {
		this.currentSpeed = rate;
	}
	
	/**
	 * Plays the current episode with the new playback speed entered.
	 * @param rate new playback speed of the episode. Must be in [0.0, 8.0].
	 */
	public void fastForward(double rate) {
		currentEpisode.setRate(rate); 
		this.currentSpeed = rate;
		setChanged();
		notifyObservers(new PodcastMessage(currentEpisodeName, "fast forward"));
	}
	
	/**
	 * Returns the name of the current episode being played.
	 * @return the name of the current episode being played
	 */
	public String getCurrentEpisode() {
		return this.currentEpisodeName;
	}
	
	/**
	 * Determines whether the episode is currently playing
	 * @return true if the episode is playing, false otherwise
	 */
	public boolean isEpisodePlaying() {
		if (currentEpisode == null) {
			return false;
		}
		return currentEpisode.getStatus() == MediaPlayer.Status.PLAYING;
	}
	
	/**
	 * Returns the collection of episodes for a given podcast  
	 * @return the collection of episodes for a given podcast
	 */
	public HashMap<String, MediaPlayer> getCollection() {
		return this.episodePlaylist;
	}
	
	/**
	 * Returns the collection of entered podcasts 
	 * @return the collection of entered podcasts 
	 */
	public HashMap<String, HashMap<String, MediaPlayer>> getAllPodcasts() {
		return this.allPodcasts;
	}
  
	/**
	 * Sets the current episode to repeat indefinitely.
	 */
	public void repeatingEpisode() {
		if (currentEpisode != null) {
			currentEpisode.setCycleCount((int) Math.round(Duration.INDEFINITE.toSeconds()));
		}
	}
	
	/**
	 * Reads in an RSS feed given an RSS url and extracts the first 
	 * five MP3 files and puts them in the podcast album. If there
	 * is more than one album, we add this album of podcasts to the 
	 * collection of podcasts.
	 * @param urlString the RSS feed url
	 */
	public void readFile(String urlString) {
		XMLFound = false;
		if (urlString.isEmpty()) {
			return;
		}
		HashMap<String, MediaPlayer> podcast_album = new HashMap<>();
		URL url = null;
		Scanner scanner = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e1) {
			setChanged();
			notifyObservers(new PodcastMessage("", "BADURL"));
			return;    }
		try {
			scanner = new Scanner(url.openStream());
		} catch (IOException e) {
			setChanged();
			notifyObservers(new PodcastMessage("", "BADURL"));
			return;
		}
		
		String line;
		String currentTitle = null;
		String albumTitle = null;
		String currentMP3;
		int endMP3Index = 16; // initialize to be start of string
		int endTitleIndex = 7;
		int i = 0;
		while (scanner.hasNextLine() && i < 5 && episodePlaylist.size() < 9) { // keep i small to not break program
			line = scanner.nextLine();
			line = line.trim();
			// extract title of episode
			if (line.length() >= 7 && (line.substring(0, 7)).equals("<title>")) {
				String previousline = currentTitle;
				endTitleIndex = line.indexOf("</title", 7);
				currentTitle = line.substring(7, endTitleIndex);
				if (previousline == null) {
					albumTitle = currentTitle;
				}
			}
			// extract url of episode
			else if (line.length() >= 16 && (line.substring(0,16)).equals("<enclosure url=\"")) {
				XMLFound = true;
				endMP3Index = line.indexOf("\"", 16);
				currentMP3  = line.substring(16, endMP3Index);
				URL newUrl = null;
				try {
					newUrl = new URL(currentMP3);
				} catch (MalformedURLException e) {
					setChanged();
					notifyObservers(new PodcastMessage("", "BADURL"));
					return;
				}
				Media media = new Media(newUrl.toString()); 
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				podcast_album.put(currentTitle, mediaPlayer); 
				episodePlaylist.put(currentTitle, mediaPlayer);
				unShuffledPlaylist.put(currentTitle, mediaPlayer);
				i++;
			}
		}
		
		// check if there is at least one episode in the podcast
		if (episodePlaylist.keySet().size() > 0) {
			allPodcasts.put(albumTitle, podcast_album);
			List<Object> keys = Arrays.asList(episodePlaylist.keySet().toArray());
			currentEpisodeName = (String) keys.get(0);
			currentEpisode = episodePlaylist.get((String) keys.get(0));
		}
		
		if (!XMLFound) {
			setChanged();
			notifyObservers(new PodcastMessage("", "BADURL"));
			return;
		}
	}
	
	/**
	 * This method will simulate skipping the current episode to the next episode in
	 * the playlist.
	 */
	public void skipEpisode() {
		// Checks if we have a podcast
		if (!episodePlaylist.isEmpty()) {
			List<Object> keys = Arrays.asList(episodePlaylist.keySet().toArray());
			currentEpisode.stop();
			for (int index = 0; index < keys.size(); index++) {
				// If we found the episode we were just at
				if (keys.get(index).equals(currentEpisodeName)) {
					String nextEpisode;
					// If the episode is the last in the playlist, loop back to top of playlist
					if (index == keys.size() - 1) {
						nextEpisode = (String) keys.get(0);
					}
					// Otherwise, get next episode name
					else {
						nextEpisode = (String) keys.get(index + 1);
					}
					// Updates current episode name and current media player to next episode
					currentEpisodeName = nextEpisode;
					currentEpisode = episodePlaylist.get(nextEpisode);
					setChanged();
					notifyObservers(new PodcastMessage(currentEpisodeName, "next")); // TODO: May change this
					break;
				}
			}
		}
	}
	
	/**
	 * This method will simulate going from the current episode to the previous episode in
	 * the playlist.
	 */
	public void previousEpisode() {
		// Checks if we have a podcast
		if (!episodePlaylist.isEmpty()) {
			List<Object> keys = Arrays.asList(episodePlaylist.keySet().toArray());
			currentEpisode.stop();
			for (int index = 0; index < keys.size(); index++) {
				// If we found the episode we were just at
				if (keys.get(index).equals(currentEpisodeName)) {
					String nextEpisode;
					// Makes sure we are not at the beginning of the list of episodes
					if (index > 0) {
						// Gets previous episode's name
						nextEpisode = (String) keys.get(index - 1);
						// Updates current episode name and current media player to next episode
						currentEpisodeName = nextEpisode;
						currentEpisode = episodePlaylist.get(nextEpisode);
						setChanged();
						notifyObservers(new PodcastMessage(currentEpisodeName, "previous")); // TODO: May change this
					}
					if (index == 0) {
						// Gets previous episode's name
						nextEpisode = (String) keys.get(keys.size()-1);
						// Updates current episode name and current media player to next episode
						currentEpisodeName = nextEpisode;
						currentEpisode = episodePlaylist.get(nextEpisode);
						setChanged();
						notifyObservers(new PodcastMessage(currentEpisodeName, "previous")); // TODO: May change this
					}
					break;
				}
			}
		}
	}
	
	/**
	 * This method will shuffle the order of the podcast episodes
	 * @param shuffleFlag tells whether the podcast is currently in shuffle mode
	 */
	public void shufflePodcast(boolean shuffleFlag) {
		// Checks if we have a podcast
		if (!episodePlaylist.isEmpty()) {
			if (shuffleFlag == true) {
				// Get all the entries in the map into a list
				ArrayList<HashMap.Entry<String, MediaPlayer>> entries = new ArrayList<>(episodePlaylist.entrySet());
				 
				// Shuffle the list
				Collections.shuffle(entries);
				 
				// Insert them all into a LinkedHashMap
				HashMap<String, MediaPlayer> shuffledWindow = new LinkedHashMap<>();
				for (HashMap.Entry<String, MediaPlayer> entry : entries) {
				    shuffledWindow.put(entry.getKey(), entry.getValue());
				}
				episodePlaylist = shuffledWindow;
			}
			
			else {
				episodePlaylist = unShuffledPlaylist;
				
			}
		}
	}
	
	/**
	 * Skips ahead or rewinds the current episode by a certain amount of seconds
	 * @param amount the number of seconds we skip or rewind
	 */
	public void skipOrRewind(double amount) {
		Duration currentTime = currentEpisode.getCurrentTime();
		currentTime = currentTime.add(Duration.seconds(amount));
		currentEpisode.seek(currentTime);
	}
}