import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;

public class PodcastTests {
	@Test
	public void test() {
		PodcastModel model1 = new PodcastModel();
		PodcastController controller = new PodcastController(model1);
		assertFalse(controller.isEpisodePlaying());
		controller.addEpisode("MURDERED: Candace Rough Surface", "https://pdst.fm/e/chtbl.com/track/95538/traffic.megaphone.fm/ADL5960835323.mp3?updated=1638151237");
		controller.addEpisode("MISSING: Kierra Coles and Keeshae Jacobs", "https://pdst.fm/e/chtbl.com/track/95538/traffic.megaphone.fm/ADL1437767226.mp3?updated=1636728094");
		controller.addEpisode("MURDERED: Janet Abaroa", "https://pdst.fm/e/chtbl.com/track/95538/traffic.megaphone.fm/ADL7319678464.mp3?updated=1637121575");
		controller.playEpisode("MURDERED: Candace Rough Surface");
		controller.isEpisodePlaying();
		controller.readFile("https://feeds.megaphone.fm/ADL9840290619");
		controller.readFile("");
		PodcastMessage podcastMessage = new PodcastMessage("Episode Name", "Test");
		assertEquals("Episode Name",podcastMessage.getEpisodeName());
		assertEquals("Test", podcastMessage.getCommand());
		controller.readFile("notafile");
		controller.readFile("https://www.facebook.com/");
		assertNotNull(controller.getAllEpisodeNames());
		assertNotNull(controller.getAllPodcasts());
		controller.setRate(0.5);
		controller.setRate(1.0);
		controller.fastForward(1.5);
		controller.fastForward(1.0);
		controller.nextEpisode();
		controller.nextEpisode();
		controller.nextEpisode();
		controller.nextEpisode();
		controller.nextEpisode();
		controller.previousEpisode();
		controller.previousEpisode();
		controller.previousEpisode();
		controller.shufflePodcast(true);
		controller.shufflePodcast(false);
		assertNotNull(controller.getCurrentEpisode());
		controller.pauseEpisode();
		controller.skipOrRewind(30);
		controller.skipOrRewind(-30);
		controller.stopEpisode();
		controller.repeatEpisode();
		controller.addEpisode("MURDERED: Janet Abaroa", "https://pdst.fm/e/chtbl.com/track/95538/traffic.megaphone.fm/ADL7319678464.mp3?updated=1637121575");
		controller.removeEpisode("MURDERED: Janet Abaroa");
		controller.removeEpisode("MURDERED: Janet Abaroa");
	}
}