package ku.piii.musictableviewfxml;

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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import ku.piii.model.MusicMedia;
import ku.piii.model.MusicMediaCollection;
import ku.piii.model.MusicMediaColumnInfo;
import ku.piii.model.MusicMediaPlaylist;
import ku.piii.music.MusicService;
import ku.piii.music.MusicServiceFactory;
import ku.piii.player.MusicPlayer;
import org.kordamp.ikonli.javafx.FontIcon;

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
   
    
    //FX Components
    
    @FXML
    private Label addressBook;

    @FXML
    private TableView<MusicMedia> tableView;
    @FXML
    private ListView playlistView, musicView;
    //Play Controls
    @FXML
    private Label InfoLabel;
    @FXML
    private GridPane play, pause, previous, next;
    @FXML
    private ContextMenu clickMenu;
    @FXML
    private MenuItem importMusic;
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
        tableView.setContextMenu(clickMenu);
        tableView.setOnMouseClicked(TABLE_CLICK);
        
        musicView.setPrefHeight(musicView.getItems().size() * ROW_HEIGHT + 2);
        musicView.setOnMouseClicked(SELECT_MUSIC_MENU);
        
        playlistView.setEditable(true);
        playlistView.setCellFactory(TextFieldListCell.forListView());
        playlistView.setOnEditCommit(EDIT_PLAYLIST);
        
        player = new MusicPlayer(InfoLabel,play,pause);
        
        play.setOnMouseClicked(PLAY);
        pause.setOnMouseClicked(PAUSE);
        pause.managedProperty().bind(pause.visibleProperty());
        pause.setVisible(false);
        previous.setOnMouseClicked(PREVIOUS_SONG);
        next.setOnMouseClicked(NEXT_SONG); 
        importMusic.setOnAction(IMPORT_MUSIC);
        
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
            
        }
        
    };
    
    EventHandler<MouseEvent> SELECT_MUSIC_MENU = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                switch(musicView.getSelectionModel().getSelectedIndex()) {
                    case 0:
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
    
    EventHandler<ListView.EditEvent<String>> EDIT_PLAYLIST = new EventHandler<ListView.EditEvent<String>>() {
	@Override
            public void handle(ListView.EditEvent<String> t) {
                t.getSource().getItems().set(t.getIndex(), t.getNewValue());
            }
	};

}
