public class PodcastMessage {

	private String episodeName;
	private String command;
	
	public PodcastMessage(String episodeName, String command) {
		this.episodeName = episodeName;
		this.command = command;
	}
	
	public String getEpisodeName() {
		return this.episodeName;
	}
	
	public String getCommand() {
		return this.command;
	}

}