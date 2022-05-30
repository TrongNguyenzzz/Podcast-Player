import java.util.HashMap;
import java.util.Set;

import javafx.scene.media.MediaPlayer;

/**
 * @author Dyllon Enos, Andrew Logan, Trong Nguyen, and Dane Norville
 *
 *
 */
public class PodcastController {
	
	/**
	 * the model instance used to construct the controller
	 */
	private PodcastModel model; 
	
	/**
	 * Constructs a PodcastController instance with input model.
	 * @param model the input model instance
	 */
	public PodcastController(PodcastModel model) {
		this.model = model;
	}
	
	/**
	 * Plays a given episode. 
	 * @param episodeName the name of the episode to be played
	 */
	public void playEpisode(String episodeName) {
		this.model.playEpisode(episodeName);
	}
	
	/**
	 * This method is for test purposes ONLY.
	 * @param name: This is the name of the episodes
	 * @param url: This is the url links for the episode
	 */
	public void addEpisode(String name, String url) {
		this.model.addEpisode(name, url);
	}
	public void pauseEpisode() {
		this.model.pauseEpisode();
	}
	
	/**
	 * Stops the current episode.
	 */
	public void stopEpisode() {
		this.model.stopEpisode();
	}
	
	/**
	 * Sets the rate of the episode to be played, which is only
	 * done if there is not a current episode or if the episode 
	 * is not currently playing.
	 * @param rate The speed of playback (must be in [0.0, 8.0]).
	 */
	public void setRate(double rate) {
		this.model.setRate(rate);
	}
	
	/**
	 * Plays the current episode at the input rate.
	 * @param rate The speed of playback (must be in [0.0, 8.0]).
	 */
	public void fastForward(double rate) {
		this.model.fastForward(rate);
	}
	
	/**
	 * Determines if an episode is currently playing.
	 * @return true if an episode is currently playing, false otherwise
	 */
	public boolean isEpisodePlaying() {
		return this.model.isEpisodePlaying();
	}
	
	/**
	 * Removes an input episode from the podcast album
	 * @param episodeName The name of the episode to be removed.
	 * @return true if the epsisode with name epsisodename existed before removing it
	 * 
	 */
	public boolean removeEpisode(String episodeName) {
		return this.model.removeEpisode(episodeName);
	}
  
	/**
	 * Sets the current episode to repeat indefinitely.
	 */
	public void repeatEpisode() {
		this.model.repeatingEpisode();
	}
	
	/**
	 * Returns the set of episodes for the current podcast
	 * @return the set of episodes for the current podcast
	 */
	public Set<String> getAllEpisodeNames() {
		return this.model.getCollection().keySet();
	}
	
	/**
	 * Returns the collection of entered podcasts 
	 * @return the collection of entered podcasts 
	 */
	public HashMap<String, HashMap<String, MediaPlayer>> getAllPodcasts(){
		return this.model.getAllPodcasts();
	}
	
	/**
	 * Reads in an RSS feed given an RSS url and extracts the first 
	 * five MP3 files and puts them in the podcast album. If there
	 * is more than one album, we add this album of podcasts to the 
	 * collection of podcasts.
	 * @param urlString the RSS feed url
	 */
	public void readFile(String urlString) {
		this.model.readFile(urlString);
	}
	
	/**
	 * This method will simulate skipping the current episode to the next episode in
	 * the playlist.
	 */
	public void nextEpisode() {
		this.model.skipEpisode();
	}
	
	/**
	 * This method will simulate going from the current episode to the previous episode in
	 * the playlist.
	 */
	public void previousEpisode() {
		this.model.previousEpisode();
	}
	
	/**
	 * This method will shuffle the order of the podcast episodes
	 * @param shuffleFlag tells whether the podcast is currently in shuffle mode
	 */
	public void shufflePodcast(boolean shuffleFlag) {
		this.model.shufflePodcast(shuffleFlag);
		
	}
	
	/**
	 * Returns the name of the current episode being played.
	 * @return the name of the current episode being played
	 */
	public String getCurrentEpisode() {
		return this.model.getCurrentEpisode();
	}
	
	/**
	 * Skips ahead or rewinds the current episode by a certain amount of seconds
	 * @param amount the number of seconds we skip or rewind
	 */
	public void skipOrRewind(double amount) {
		this.model.skipOrRewind(amount);
	}
}