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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import ku.piii.model.MusicMedia;
import ku.piii.model.MusicMediaCollection;
import ku.piii.model.MusicMediaColumnInfo;
import ku.piii.model.MusicMediaPlaylist;
import ku.piii.music.MusicService;
import ku.piii.music.MusicServiceFactory;

public class FXMLController implements Initializable {

    String pathScannedOnLoad = "../test-music-files/collection-A";
    
    private final static MusicService MUSIC_SERVICE = MusicServiceFactory.getMusicServiceInstance();

    //GLOBAL VARIABLES
    int ROW_HEIGHT = 24;
    
    //DATA
    private ObservableList<MusicMedia> dataForTableView;
    
    private final static List<MusicMediaPlaylist> playlists = new ArrayList<>();
   
    
    //FX Components
    
    @FXML
    private Label addressBook;

    @FXML
    private TableView<MusicMedia> tableView;
    @FXML
    private ListView playlistView;
    @FXML
    private ListView musicView;
    
    //FX Functions

    @FXML
    private void handleButtonAction(ActionEvent event) {
        
        
        

        // TODO
    }

    @FXML
    private void handleAboutAction(final ActionEvent event) {

    }

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
        
        final MusicMediaCollection collection = MUSIC_SERVICE
                .createMusicMediaCollection(Paths.get(pathScannedOnLoad));
        dataForTableView = FXCollections.observableArrayList(collection.getMusic());
        dataForTableView.addListener(makeChangeListener(collection));
        
        List<MusicMediaColumnInfo> songColumnInfo = TableViewFactory.makeColumnInfoList();

        tableView.setItems(dataForTableView);
        TableViewFactory.makeTable(tableView, songColumnInfo);
        tableView.setEditable(true);
        
        ObservableList<String> musicOptions = FXCollections.observableArrayList (
        "Songs", "Artists", "Albums", "Genres");
        musicView.setItems(musicOptions);
        musicView.setPrefHeight(musicOptions.size() * ROW_HEIGHT + 2);
        musicView.setOnMouseClicked(SELECT_MUSIC_MENU);
        
        playlistView.setEditable(true);
        playlistView.setCellFactory(TextFieldListCell.forListView());
        playlistView.setOnEditCommit(EDIT_PLAYLIST);
        
        tableView.setOnMouseClicked(PLAY_MUSIC);
         
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
    
    EventHandler<MouseEvent> SELECT_MUSIC_MENU = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(musicView.getSelectionModel().getSelectedIndex());
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
    
    EventHandler<MouseEvent> PLAY_MUSIC = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            
        }
    };
    
    EventHandler<ListView.EditEvent<String>> EDIT_PLAYLIST = new EventHandler<ListView.EditEvent<String>>() {
	@Override
            public void handle(ListView.EditEvent<String> t) {
                t.getSource().getItems().set(t.getIndex(), t.getNewValue());
            }
	};

}
