import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Dyllon Enos, Andrew Logan, Trong Nguyen, and Dane Norville
 *
 */
@SuppressWarnings("deprecation")
public class PodcastView extends Application implements Observer {
	private PodcastModel model;
	private PodcastController controller;
	private String rssUrl;
	private String mostRecentlyPlayed;
	
	private Stage stage;
	private Scene scene; 
	private MenuBar menuBar;
	private BorderPane pane;
	private Background blackBackground;
	private Background grayBackground;
	// We have three gridPane in total
	private GridPane leftGrid;
	private GridPane topGrid;
	private GridPane bottomGrid;
	
	// Field for Button play
	private Button play;
	
	// flag for replay and shuffle button
	private int replayFlag = 0;
	private int shuffledFlag = 0;
	private int isPlaying = 0; 
	private int hasStarted = 0;
	private String currentPlayed;	
	
	private static final double PADDING = 10;
	
	/**
	 * This method will be used to start the GUI view of the podcast
	 * @param inStage: This is the primaryStage that is used to draw
	 * graphics on.
	 * @throws Exception throws suitable exception
	 */
	
	@Override
	public void start(Stage inStage) throws Exception {
		stage = inStage;
		model = new PodcastModel();
		controller = new PodcastController(model);
		model.addObserver(this);
		setStage();
		makeMenu();
		setRecentPlay();
		stage.show();
	}

	/**
	 * This method will be used to set all the button to 3 main gridPane
	 * on the primaryStage and fill the background of for the Scene.
	 */
	private void setStage() {
		pane = new BorderPane();
		topGrid = new GridPane();
		topGrid.setHgap(50);
		topGrid.setVgap(25);
		pane.setCenter(topGrid);
		blackBackground = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));
		topGrid.setBackground(blackBackground);
		topGrid.setPadding(new Insets(PADDING));
		grayBackground = new Background(new BackgroundFill(Color.DIMGRAY, CornerRadii.EMPTY, Insets.EMPTY));
		bottomGrid = new GridPane();
		bottomGrid.setBackground(grayBackground);
		bottomGrid.setPadding(new Insets(PADDING));
		bottomGrid.setPrefHeight(70);
		pane.setBottom(bottomGrid);
		leftGrid = new GridPane();
		leftGrid.setBackground(blackBackground);
		leftGrid.setPrefWidth(130);
		leftGrid.setVgap(30);
		bottomGrid.setPadding(new Insets(PADDING));
		pane.setLeft(leftGrid);
		setSideGrid();
		scene = new Scene(pane, 1000, 600);
		stage.setScene(scene);
		File file = new File("src/icons/logo_icon.png");
		Image img = new Image(file.toURI().toString());
		stage.getIcons().add(img);
		stage.setTitle("Podcast Player");
	}
	
	/**
	 * This method will be called to set all the button and image
	 * to the leftGrid. Buttons would be "All Podcasts" and
	 * "All Episodes".
	 */
	private void setSideGrid() {
		//Image allSongIcon = new Image(getClass().getResourceAsStream("https://www.freepik.com/free-icon/java_14132691.htm"));
		Button allPodcasts = new Button("All Podcasts", getSpawnView("album"));
		allPodcasts.setPrefWidth(115);
		allPodcasts.setBackground(blackBackground);
		allPodcasts.setTextFill(Color.WHITE);
		allPodcasts.setOnMouseClicked((event) -> showPodcasts());
		
		Button library = new Button("All Episodes", getSpawnView("song"));
		library.setPrefWidth(115);
		library.setBackground(blackBackground);
		library.setTextFill(Color.WHITE);
		library.setOnMouseClicked((event) -> showEpisodes("ALL"));
		
//		Button newPlaylist = new Button("New Playlists");
//		newPlaylist.setPrefWidth(100);
//		newPlaylist.setBackground(blackBackground);
//		newPlaylist.setTextFill(Color.WHITE);
//		newPlaylist.setOnMouseClicked((event) -> showNewPlaylists());
		
		leftGrid.add(allPodcasts, 0, 1);
		leftGrid.add(library, 0, 2);
//		leftGrid.add(newPlaylist, 0, 3);
	}

	/**
	 * This method will be used to show the second stage when the users
	 * click on the button "All Podcasts". In this second stage,
	 * all the podcasts which have been added will be shown and
	 * the users can play them.
	 */
	private void showPodcasts() {
		stage.hide();
		Stage apStage = new Stage();
		BorderPane apPane = new BorderPane();
		GridPane apLeftGrid = new GridPane();
		
		apPane.setLeft(apLeftGrid);
		apLeftGrid.setBackground(blackBackground);
		apLeftGrid.setPrefWidth(80);
		apLeftGrid.setVgap(30);
		apLeftGrid.setPrefWidth(150);
		apLeftGrid.setPadding(new Insets(PADDING));

		Button back = new Button("Back to home");
		back.setBackground(grayBackground);
		back.setTextFill(Color.WHITE);
		back.setOnMouseClicked((event) -> {
			apStage.close();
			pane.setTop(menuBar);
			pane.setBottom(bottomGrid);
			stage.show();
		});
		apLeftGrid.add(back, 0, 0);
				
		GridPane apCenterGrid = new GridPane();
		apCenterGrid.setHgap(50);
		apCenterGrid.setVgap(25);
		apCenterGrid.setBackground(blackBackground);
		apCenterGrid.setPadding(new Insets(PADDING));
		apPane.setCenter(apCenterGrid);
		
		Text apTitle = new Text("Podcasts");
		apTitle.setFill(Color.WHITE);
		apCenterGrid.add(apTitle, 0, 0);
		apTitle.setFont(Font.font(null, FontWeight.BOLD, 20));
		
		Scene apScene = new Scene(apPane, 1000, 600);
		apStage.setScene(apScene);
		apStage.setTitle("Podcast Player");
		apStage.show();
		
		Set<String> allPodcastNames = controller.getAllPodcasts().keySet();
		int i = 0;
		for (String name : allPodcastNames) {
			String specificName = name;
			i++;
			Button playlistName = new Button(specificName);
			playlistName.setBackground(grayBackground);
			playlistName.setTextFill(Color.WHITE);
			playlistName.setOnMouseClicked((event) -> {
				showPodcastEpisodes(apCenterGrid, specificName);
			});
			apCenterGrid.add(playlistName, 0, i);
		}
		apPane.setBottom(bottomGrid);
		apPane.setTop(menuBar);
	}

	/**
	 * This function will be used to show all the episode within a Podcast.
	 * When the users click on any Podcasts in the stage, all the episodes 
	 * will be shown.
	 * @param apCenterGrid: This is the gridPane we use to show the podcasts/episodes.
	 * @param specificName: This is the name of the podcast that the users just click on.
	 */
	private void showPodcastEpisodes(GridPane apCenterGrid, String specificName) {
		apCenterGrid.getChildren().clear();
		Text apTitle = new Text(specificName + " Episodes: ");
		apTitle.setFill(Color.WHITE);
		apCenterGrid.add(apTitle, 0, 0);
		apTitle.setFont(Font.font(null, FontWeight.BOLD, 20));
		
		Set<String> allEpisodeNames = controller.getAllPodcasts().get(specificName).keySet();
		int j = 0;
		for (String episode : allEpisodeNames) {
			j++;
			Button episodeName = new Button(episode);
			episodeName.setBackground(grayBackground);
			episodeName.setTextFill(Color.WHITE);
			episodeName.setOnMouseClicked((event2)->{
				hasStarted = 1;
				changePlayStatus();
				if(!controller.isEpisodePlaying()) {
					controller.playEpisode(episode);
					currentPlayed = episode;
					play.setGraphic(getSpawnView("pause"));
				} else {
					controller.stopEpisode();
					controller.playEpisode(episode);
					currentPlayed = episode;
					play.setGraphic(getSpawnView("pause"));
				}		
			});
			apCenterGrid.add(episodeName, 0, j);
		}
	}
  
	/**
	 * This function will be used all the episodes which had been added to
	 * the podcasts. When the users click on the button "All Episodes" on the 
	 * main stage, all the episodes will be shown.
	 * @param decision: A string represents for the decision of the users
	 */
	private void showEpisodes(String decision) { // TODO consider streamlining this method and showLibary together
		stage.hide();
		Stage eStage = new Stage();
		BorderPane ePane = new BorderPane();
		GridPane eLeftGrid = new GridPane();
		
		ePane.setLeft(eLeftGrid);
		eLeftGrid.setBackground(blackBackground);
		eLeftGrid.setPrefWidth(80);
		eLeftGrid.setVgap(30);
		eLeftGrid.setPrefWidth(150);
		eLeftGrid.setPadding(new Insets(PADDING));

		Button back = new Button("Back to home");
		back.setBackground(grayBackground);
		back.setTextFill(Color.WHITE);
		back.setOnMouseClicked((event) -> {
			eStage.close();
			pane.setTop(menuBar);
			pane.setBottom(bottomGrid);
			stage.show();
		});
		eLeftGrid.add(back, 0, 0);
				
		GridPane eCenterGrid = new GridPane();
		eCenterGrid.setHgap(50);
		eCenterGrid.setVgap(25);
		eCenterGrid.setBackground(blackBackground);
		eCenterGrid.setPadding(new Insets(PADDING));
		ePane.setCenter(eCenterGrid);
		
		Text eTitle = new Text("Episodes:");
		eTitle.setFill(Color.WHITE);
		eCenterGrid.add(eTitle, 0, 0);
		eTitle.setFont(Font.font(null, FontWeight.BOLD, 20));
		
		Scene eScene = new Scene(ePane, 1000, 600);
		eStage.setScene(eScene);
		eStage.setTitle("Podcast Player");
		eStage.show();
		
		Set<String> allEpisodeNames;
		if (decision.equals("ALL")) {
			allEpisodeNames = controller.getAllEpisodeNames();
		}
		else {
			allEpisodeNames = controller.getAllPodcasts().get(decision).keySet();
		}
		int i = 0;
		for (String name : allEpisodeNames) {
			String specificName = name;
			i++;
			Button playlistName = new Button(specificName);
			playlistName.setBackground(grayBackground);
			playlistName.setTextFill(Color.WHITE);
			playlistName.setOnMouseClicked((event) -> {
				hasStarted = 1;
				changePlayStatus();
				if(!controller.isEpisodePlaying()) {
					controller.playEpisode(specificName);
					currentPlayed = specificName;
					play.setGraphic(getSpawnView("pause"));
				} else {
					controller.stopEpisode();
					controller.playEpisode(specificName);
					currentPlayed = specificName;
					play.setGraphic(getSpawnView("pause"));
				}		
			});
			eCenterGrid.add(playlistName, 0, i);
		}
		ePane.setBottom(bottomGrid);
		ePane.setTop(menuBar);
	}
	
	/**
	 * This method will be called when the users stop or
	 * play any episode. If they stop a song then the 
	 * variable to keep track will become 0, otherwise, it
	 * will be 1.
	 */
	private void changePlayStatus() {
		if (isPlaying == 0) {
			isPlaying = 1;
		} else {
			isPlaying = 0;
		}
	}

	/**
	 * This method is used to create the menu on the top left
	 * of the stage. The users can exit or create new playlist 
	 * from the menu.
	 */
	private void makeMenu() {
		Menu menu = new Menu("Menu");
		Menu playbackSpeed =  new Menu("Playback Speed");
		menuBar = new MenuBar();
		menuBar.getMenus().add(menu);
		menuBar.getMenus().add(playbackSpeed);
		MenuItem close = new MenuItem("Close");
		close.setOnAction((event) -> stage.close());
		MenuItem newPlaylist = new MenuItem("New Playlist");
		newPlaylist.setOnAction((event) -> {
			dialogBox db = new dialogBox();
			db.showAndWait();
			controller.readFile(rssUrl);
			currentPlayed = controller.getCurrentEpisode();
		});
		for (int i = 0; i < 16; i++) {
			String speedString = String.valueOf((i+1) * 0.25);
			MenuItem speed = new MenuItem(speedString);
			speed.setOnAction((event) -> {
				for (MenuItem item : playbackSpeed.getItems()) {
					item.setGraphic(null);
					if (controller.isEpisodePlaying()) {
						controller.fastForward(Double.parseDouble(speed.getText()));
					}
					else {
						controller.setRate(Double.parseDouble(speed.getText()));
					}
				}
				speed.setGraphic(getSpawnView("checkmark"));
			});
			if (i == 3) { // set speed of 1.0 to be default 
				speed.setGraphic(getSpawnView("checkmark"));
			}
			else {
				speed.setGraphic(null);
			}
			playbackSpeed.getItems().add(speed);
			 
		}
		
		menu.getItems().add(newPlaylist);
		menu.getItems().add(close);
		pane.setTop(menuBar);
	}
	
	

	/**
	 * This method will be used to set all the buttons for
	 * the recent played episodes. The users can play those 
	 * episodes by clicking on those buttons.
	 */
	private void setRecentPlay() {
		DropShadow ds = new DropShadow();
		ds.setOffsetY(3.0f);
		ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
	
		Text title = new Text();
		title.setEffect(ds);
		title.setCache(true);
		title.setX(10.0f);
		title.setY(270.0f);
		title.setFill(Color.WHITE);
		title.setText("Recently played"); 
		title.setFont(Font.font(null, FontWeight.BOLD, 20));

		Button recentSong1 = new Button();
		recentSong1.setEffect(ds);
		recentSong1.setCache(true);
		recentSong1.setText("                      ");
		recentSong1.setFont(Font.font(null, FontWeight.BOLD, 17));
		
		Button recentSong2 = new Button();
		recentSong2.setEffect(ds);
		recentSong2.setCache(true);
		recentSong2.setText("                      ");
		recentSong2.setFont(Font.font(null, FontWeight.BOLD, 17));
		
		Button recentSong3 = new Button();
		recentSong3.setEffect(ds);
		recentSong3.setCache(true);
		recentSong3.setText("                      ");
		recentSong3.setFont(Font.font(null, FontWeight.BOLD, 17));
		
		Button recentSong4 = new Button();
		recentSong4.setEffect(ds);
		recentSong4.setCache(true);
		recentSong4.setText("                      ");
		recentSong4.setFont(Font.font(null, FontWeight.BOLD, 17));
		
		topGrid.add(title, 0, 0);
		topGrid.add(recentSong1, 0, 1);
		topGrid.add(recentSong2, 1, 1);
		topGrid.add(recentSong3, 2, 1);
		topGrid.add(recentSong4, 3, 1);
		
		File file = new File("src/icons/hotshots_icon.png");
		Image img = new Image(file.toURI().toString());
		ImageView hotshotsView = new ImageView(img);
	    topGrid.add(hotshotsView, 3, 2);
	    GridPane.setHalignment(hotshotsView, HPos.CENTER);
	    hotshotsView.setFitHeight(200);
	    hotshotsView.setFitWidth(200);
	    
		setBottomGrid();
	}
	
	/**
	 * This method will be used to set all the buttons such as
	 * play, next song, previous song, ... at the bottom of the 
	 * grid. The title of the current song will also be shown 
	 * on the left of the bottom grid.
	 */
	public void setBottomGrid() {
		bottomGrid.setHgap(20);
		Text podcastTitle = new Text("Nothing playing");
		podcastTitle.setFill(Color.WHITE);
		bottomGrid.add(podcastTitle, 0, 0);
		
		Button shuffle = new Button();
		ImageView shuffleView = getSpawnView("random");
		shuffle.setGraphic(shuffleView);
		shuffle.setOnAction((event)->{
			ImageView newShuffle = null;
			changedShuffle();
			if (shuffledFlag == 0) {
				newShuffle = getSpawnView("random");
				controller.shufflePodcast(false);
			} else {
				newShuffle = getSpawnView("shuffle");
				controller.shufflePodcast(true);
			}
			shuffle.setGraphic(newShuffle);
		});
		
		Button previous = new Button();
		ImageView previousView = getSpawnView("previous");
		previous.setGraphic(previousView);
		previous.setOnAction((event)->{
			if(hasStarted == 1) {
				controller.previousEpisode();
		    	currentPlayed = controller.getCurrentEpisode();
		    	controller.playEpisode(currentPlayed);
		    	play.setGraphic(getSpawnView("pause"));
			}
		});
		play = new Button();
		ImageView view = getSpawnView("play");
	    play.setGraphic(view);
	    play.setOnAction((event)->{
	    	if(hasStarted == 1) {
		    	if (!controller.isEpisodePlaying()) {
		    		controller.playEpisode(currentPlayed);
					play.setGraphic(getSpawnView("pause"));
		    	}
		    	
		    	else {
		    		controller.pauseEpisode();
					play.setGraphic(getSpawnView("play"));
		    	}
	    	}
//	    	if(hasStarted == 1) {
//	    		changePlayStatus();
//				if(isPlaying == 1) {
//					controller.playEpisode(currentPlayed);
//					play.setGraphic(getSpawnView("pause"));
//				} else {
//					controller.pauseEpisode();
//					play.setGraphic(getSpawnView("play"));
//				}		
//	    	}
		});
	    Button nextSong = new Button();
	    ImageView songView = getSpawnView("next");
	    nextSong.setGraphic(songView);
	    nextSong.setOnAction((event)->{
	    	if(hasStarted == 1) {
		    	controller.nextEpisode();
		    	currentPlayed = controller.getCurrentEpisode();
		    	controller.playEpisode(currentPlayed);
		    	play.setGraphic(getSpawnView("pause"));
	    	}
	    });
	    Button replay = new Button();
	    ImageView replayView = getSpawnView("replay");
	    replay.setGraphic(replayView);
	    replay.setOnAction((event)->{
			ImageView newReplay = null;
			changedReplay();
			if (replayFlag == 0) {
				controller.repeatEpisode();
				newReplay = getSpawnView("replay");
			} else {
				controller.repeatEpisode();
				newReplay = getSpawnView("again");
			}
			replay.setGraphic(newReplay);
		});
	    Button rewind = new Button();
	    ImageView rewingView = getSpawnView("rewind");
	    rewind.setGraphic(rewingView);
	    rewind.setOnAction((event)->{
	    	controller.skipOrRewind(-30);
	    });
	    Button skip = new Button();
	    ImageView skipView = getSpawnView("skip");
	    skip.setGraphic(skipView);
	    skip.setOnAction((event)->{
	    	controller.skipOrRewind(30);
	    });
	    HBox hbox = new HBox();
	    hbox.setSpacing(10);
	    hbox.getChildren().addAll(shuffle, rewind, previous, play, nextSong, skip, replay);
	    bottomGrid.add(hbox, 14, 0);
//	    bottomGrid.setHalignment(hbox, HPos.CENTER);	
	}
	
	/**
	 * This will changed the shuffle flag which represent
	 * for the demand of the users. If they want to do shuffle
	 * then the flag will be 1 and 0 if not.
	 */
	private void changedShuffle() {
		if (shuffledFlag == 0) {
			shuffledFlag = 1;
		} else {
			shuffledFlag = 0;
		}
	}
	
	/**
	 * This will changed the replay flag which represent
	 * for the demand of the users. If they want to do replay
	 * then the flag will be 1 and 0 if not.
	 */
	private void changedReplay() {
		if (replayFlag == 0) {
			replayFlag = 1;
		} else {
			replayFlag = 0;
		}
	}
	
	/**
	 * This method will be used to load the url links to
	 * the image and then return the suitable ImageView based
	 * on the link.
	 * @param character: This is the name of the logo/image
	 * @return the suitable ImageView based on the link
	 */
	private ImageView getSpawnView(String character) {
		character = character.toLowerCase();
		String url = "src/icons/" + character +"_icon.png";
		File file = new File(url);
		Image img = new Image(file.toURI().toString());
		ImageView imgView = new ImageView(img);
		imgView.setFitHeight(25);
		imgView.setFitWidth(25);
		return imgView;
	}
	
	/**
	 * This will be used to update the GUI view based on the
	 * all the changes from the model and controller.
	 */
	@Override
	public void update(Observable o, Object arg) {
		PodcastMessage pm = (PodcastMessage) arg;
		String episodeName = pm.getEpisodeName();
		String command = pm.getCommand();
		Text bottomLeft = (Text)bottomGrid.getChildren().get(0);
		String newBottomLeft = bottomLeft.getText();
//		if (episodeName.length() >= 30) {
//			episodeName = episodeName.substring(0, 31) + "...";
//		}
		if (command.equals("play") || command.equals("fast forward")) {
			newBottomLeft = episodeName + " (playing)"; // TODO format aint work
		}
		if (command.equals("pause"))
			newBottomLeft = episodeName + "  (paused)"; // TODO format aint work
		if (command.equals("stop"))
			newBottomLeft = episodeName + " (stopped)"; // TODO format aint work
		if (!episodeName.equals(mostRecentlyPlayed))
			updateRecents(episodeName);
		if (command.equals("BADURL")) {
			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setTitle("Message");
			a.setHeaderText("Message");
			a.setContentText("Bad URL. No RSS feed found.");
			a.showAndWait();
		}
//		int dif = 40 - newBottomLeft.length();
//		if (dif > 0) {
//			 
//		} else if (dif < 0) {
//			
//		}
		bottomLeft.setText(newBottomLeft);
	}
	
	/**
	 * This function will be used to update all the recent episodes that the
	 * users just play. All the buttons will be filled and the users can play 
	 * those episodes again by clicking on those buttons.
	 * @param episodeName: This is the name of the episode that was played
	 */
	private void updateRecents(String episodeName) {
		mostRecentlyPlayed = episodeName;
		ObservableList<Node> tgKids = topGrid.getChildren();
		Button b1 = (Button) tgKids.get(1);
		Button b2 = (Button) tgKids.get(2);
		Button b3 = (Button) tgKids.get(3);
		Button b4 = (Button) tgKids.get(4);
		String b1s = b1.getText();
		String b2s = b2.getText();
		String b3s = b3.getText();
		b1.setText(episodeName);
		b2.setText(b1s);
		b3.setText(b2s);
		b4.setText(b3s);
		b1.setOnMouseClicked((event) -> controller.playEpisode(episodeName));
		if (!b1s.equals("                      ")) {
			b2.setOnMouseClicked((event) -> {
	    		controller.pauseEpisode();
	    		controller.playEpisode(b1s);
			});
		}
		if (!b2s.equals("                      ")) {
			b3.setOnMouseClicked((event) -> {
	    		controller.pauseEpisode();
	    		controller.playEpisode(b2s);
			});		}
		if (!b3s.equals("                      ")) {
			b4.setOnMouseClicked((event) -> {
	    		controller.pauseEpisode();
	    		controller.playEpisode(b3s);
			});	
		}		
	}

	/**
	 * This class is extended by the Stage and will be used to show
	 * the dialogBox to the users.
	 * @author Dyllon Enos, Andrew Logan, Trong Nguyen, and Dane Norville
	 *
	 */
	private class dialogBox extends Stage {
		/**
		 * This is the constructor of the dialogBox
		 */
		public dialogBox() {			
			this.initModality(Modality.APPLICATION_MODAL);
			BorderPane dbPane = new BorderPane();
			dbPane.setPadding(new Insets(PADDING));
			Scene DBscene = new Scene(dbPane, 300, 110);
			this.setScene(DBscene);
			this.setTitle("Download New Podcast");
			
			Text linkText = new Text("RSS Feed URL:   ");
			TextField linkTF = new TextField("enter here");
			HBox link = new HBox(linkText, linkTF);
			link.setPadding(new Insets(PADDING));

			Button addPodcast = new Button("Add Podcast");
			addPodcast.setOnAction((event) -> {
				rssUrl = linkTF.getText();
				this.close();
			}); // close dialog box
			
			VBox middle = new VBox(link, addPodcast);
			middle.setAlignment(Pos.CENTER);
			dbPane.setCenter(middle);
		}
	}
}