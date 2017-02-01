package ku.piii.musictableviewfxml;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import ku.piii.lastfm.*;
import ku.piii.model.MusicMedia;
import ku.piii.model.MusicMediaCollection;
import ku.piii.model.MusicMediaColumnInfo;
import ku.piii.model.MusicMediaPlaylist;
import ku.piii.music.MusicService;
import ku.piii.music.MusicServiceFactory;
import ku.piii.player.MusicPlayer;

public class FXMLController implements Initializable {

    String collectionA = "../test-music-files";
    String collectionB = "../test-music-files/collection-B";
    
    private final static MusicService MUSIC_SERVICE = MusicServiceFactory.getMusicServiceInstance();

    //GLOBAL VARIABLES
    int ROW_HEIGHT = 24;
    
    //DATA
    private ObservableList<MusicMedia> dataForTableView;
    
    private final static List<MusicMediaPlaylist> playlists = new ArrayList<>();
    
    private MusicMediaCollection collection;
    private MusicPlayer player;
    private LastFM lastFM;
   
    
    //FX Components
    
    @FXML
    private Label addressBook;

    @FXML
    private TableView<MusicMedia> tableView;
    @FXML
    private ListView playlistView, musicView;
    //Play Controls
    @FXML
    private Label InfoLabel,cTime,timeLeft;
    @FXML
    private GridPane play, pause, previous, next;
    @FXML
    private ContextMenu clickMenu;
    @FXML
    private MenuItem importMusic;
    @FXML
    private Slider playBar;
    @FXML
    private GridPane songView;
    
    
    //ArtistView ID's
    @FXML
    private Label artistTitle, albumTitle,listeners, playCount;
    @FXML
    private TextArea biography;
    @FXML
    private ImageView artistImage;
    @FXML
    private TableView<Track> albumTable;
    @FXML
    private ListView albumList, topTrackList;
    @FXML 
    private GridPane artistView;
    //FX Functions



    @FXML
    private void createPlaylistAction(ActionEvent event) {
        String name = "Playlist" + (playlists.size()+1);
        MusicMediaPlaylist playlist = new MusicMediaPlaylist(name);
        playlists.add(playlist);
        
        int i = playlistView.getItems().size();
        playlistView.getItems().add(i, "Playlist " + (i+1));
        playlistView.layout();
        playlistView.edit(i);
    }
    
    @FXML
    private void handleKeyInput(final InputEvent event) {
        if (event instanceof KeyEvent) {
            final KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.A) {
                System.out.println("sfsds");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        collection = MUSIC_SERVICE
                .createMusicMediaCollection(Paths.get(collectionA));
        dataForTableView = FXCollections.observableArrayList(collection.getMusic());
        dataForTableView.addListener(makeChangeListener(collection));
        
        List<MusicMediaColumnInfo> songColumnInfo = TableViewFactory.makeColumnInfoList();

        tableView.setItems(dataForTableView);
        TableViewFactory.makeTable(tableView, songColumnInfo);
        TableViewFactory.makeTable(albumTable, TableViewFactory.makeAlbumInfoList());
        
        tableView.setContextMenu(clickMenu);
        tableView.setOnMouseClicked(TABLE_CLICK);
        //Play button on menu
        MenuItem menuPlay = clickMenu.getItems().get(0);
        Menu menuPlaylist = (Menu) clickMenu.getItems().get(1);
        MenuItem menuArtist = clickMenu.getItems().get(2);
        MenuItem menuAlbum = clickMenu.getItems().get(3);
        
        menuPlay.setOnAction( new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                player.play(tableView.getSelectionModel().getSelectedItem());
            }
        });
        
        menuArtist.setOnAction( SHOW_ARTIST );
        menuAlbum.setOnAction( new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String album = tableView.getSelectionModel().getSelectedItem().getAlbum();
            }
        });
        
        lastFM = new LastFM();
        
        songView.setVisible(true);
        
        musicView.setPrefHeight(musicView.getItems().size() * ROW_HEIGHT + 2);
        musicView.setOnMouseClicked(SELECT_MUSIC_MENU);
        
        playlistView.setEditable(true);
        playlistView.setCellFactory(TextFieldListCell.forListView());
        playlistView.setOnEditCommit(EDIT_PLAYLIST);
        playlistView.setOnEditCommit(SHOW_ARTIST);
        player = new MusicPlayer(InfoLabel,play,pause,playBar,cTime,timeLeft);
        
        play.setOnMouseClicked(PLAY);
        pause.setOnMouseClicked(PAUSE);
        pause.managedProperty().bind(pause.visibleProperty());
        pause.setVisible(false);
        previous.setOnMouseClicked(PREVIOUS_SONG);
        next.setOnMouseClicked(NEXT_SONG); 
        importMusic.setOnAction(IMPORT_MUSIC);
        
        //ArtistView 
        biography.setWrapText(true);
        biography.setEditable(false);
        
        albumList.setCellFactory(param -> {
            return new ListCell<Album>() {
                @Override
                protected void updateItem(Album album, boolean empty) {
                    super.updateItem(album, empty);
                    
                    if (empty || album == null || album.getName() == null) {
                        setText(null);
                    } else {
                        setText(album.getName());
                    }
                }
            };
        });
        topTrackList.setCellFactory( param -> { 
            return new ListCell<Track>() { 
                @Override
                protected void updateItem(Track track, boolean empty) {
                    super.updateItem(track, empty);
                    if (empty || track == null || track.getName() == null) {
                        setText(null);
                    } else {
                        setText(track.getName());
                    }
                }
            };
        });
        tableView.setVisible(true);
        artistView.setVisible(false);
        albumList.setOnMouseClicked(GET_ALBUM);
    }

    private static ListChangeListener<MusicMedia> makeChangeListener(final MusicMediaCollection collection) {
        return new ListChangeListener<MusicMedia>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends MusicMedia> change) {
                while (change.next()) {
                    if (change.wasAdded()) {
                        for (MusicMedia addedChild : change.getAddedSubList()) {
                            collection.addMusicMedia(addedChild);
                        }
                    }
                    if (change.wasRemoved()) {
                        for (MusicMedia removedChild : change.getRemoved()) {
                            collection.removeMusicMedia(removedChild);
                        }
                    }
                }
            }
        };
    }
    
    EventHandler<ActionEvent> IMPORT_MUSIC = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            DirectoryChooser chooser = new DirectoryChooser();
            File file = chooser.showDialog(new Stage());
            if (file != null ) {
            MusicMediaCollection NewCollection = MUSIC_SERVICE
                    .createMusicMediaCollection(Paths.get(file.getAbsolutePath()));
            collection.getMusic().addAll(NewCollection.getMusic());
            dataForTableView.addAll(NewCollection.getMusic());
            }
        }
        
    };
    
    EventHandler<MouseEvent> SELECT_MUSIC_MENU = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.print("hello");
                switch(musicView.getSelectionModel().getSelectedIndex()) {
                    case 0:
                        
                        songView.setVisible(true);
                        artistView.setVisible(false);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
                //tableView.
            }
        };
    
    EventHandler<MouseEvent> TABLE_CLICK = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            
            if (event.isSecondaryButtonDown()) {
                clickMenu.show(tableView, event.getScreenX(), event.getScreenY());
            }
            
            if (event.getClickCount() == 2) {
                player.setPlayQueue(collection);
                int selected = tableView.getSelectionModel().getSelectedIndex();
                player.play(selected);   
            }            
        }
    };
    
    EventHandler<MouseEvent> PLAY = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            player.play();
        }
    };
    
    EventHandler<MouseEvent> PAUSE = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            player.pause();
        }
    };
    
    EventHandler<MouseEvent> PREVIOUS_SONG = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            player.previousTrack();
        }
    };
    
    EventHandler<MouseEvent> NEXT_SONG = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            player.nextTrack();
        }
    };
    
    EventHandler<MouseEvent> GET_ALBUM = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            Album album = (Album) albumList.getSelectionModel().getSelectedItem();
            Album fullAlbum = lastFM.getAlbum(album.getName(), album.getArtist());
            albumTitle.setText(fullAlbum.getName());
            albumTable.setItems(FXCollections.observableArrayList(fullAlbum.getTracks()));
        }
    };
    
    EventHandler<ListView.EditEvent<String>> EDIT_PLAYLIST = new EventHandler<ListView.EditEvent<String>>() {
	@Override
            public void handle(ListView.EditEvent<String> t) {
                t.getSource().getItems().set(t.getIndex(), t.getNewValue());
            }
	};
    
    //ArtistView
    EventHandler<ActionEvent> SHOW_ARTIST = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            MusicMedia media = tableView.getSelectionModel().getSelectedItem();
//            MusicMedia media = collection.getMusic().get(1);
            Artist artist = lastFM.getArtistInfo(media.getArtist());
            
            artistTitle.setText(artist.getName());
            biography.setText(artist.getBioSummary());
            artistImage.setImage(new Image(artist.getImgURL()));
            albumList.setItems(FXCollections.observableArrayList(artist.getAlbums()));
            topTrackList.setItems(FXCollections.observableArrayList(artist.getTracks()));
            playCount.setText("Play Count: "+artist.getPlayCount());
            listeners.setText("Listeners: "+artist.getListeners());
            
            songView.setVisible(false);
            artistView.setVisible(true);
        }
    };
    
}
