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

    
    //DATA
    private ObservableList<MusicMedia> dataForTableView;
    
    private final static List<MusicMediaPlaylist> playlists = new ArrayList<>();
   
    
    //FX Components
    
    @FXML
    private Label addressBook;

    @FXML
    private TableView<MusicMedia> tableView;
    @FXML
    private GridPane options;
    @FXML
    private ListView playlistView;
    
    //FX Functions

    @FXML
    private void handleButtonAction(ActionEvent event) {
        final MusicMediaCollection collection = MUSIC_SERVICE
                .createMusicMediaCollection(Paths.get(pathScannedOnLoad));
        dataForTableView = FXCollections.observableArrayList(collection.getMusic());
        
        
        
        dataForTableView.addListener(makeChangeListener(collection));

        List<MusicMediaColumnInfo> songColumnInfo = TableViewFactory.makeColumnInfoList();

        tableView.setItems(dataForTableView);
        TableViewFactory.makeTable(tableView, songColumnInfo);
        tableView.setEditable(true);
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
        
        playlistView.setEditable(true);
        playlistView.setCellFactory(TextFieldListCell.forListView());
        
        playlistView.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
	@Override
            public void handle(ListView.EditEvent<String> t) {
                t.getSource().getItems().set(t.getIndex(), t.getNewValue());
            }
	});
        
        options.setOnMouseClicked((MouseEvent e) -> {
            String selected = null;
            for( Node node: options.getChildren()) {
                if( node instanceof Label) {
                    if( node.getBoundsInParent().contains(e.getX(),  e.getY())) {
                        selected =  ((Label) node).textProperty().toString();
                        node.getStyleClass().add("active");
                    } else {
                        node.getStyleClass().remove("active");
                    }
                }
            }
            
            switch (selected) {
                case "Songs":
                    break;
                case "Albums":
                    break;
                case "Artists":
                    break;
                case "Genres":
                    break;
            }
        });
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

}
