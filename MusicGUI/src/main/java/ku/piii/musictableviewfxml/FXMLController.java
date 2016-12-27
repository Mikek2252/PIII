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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
    private ListView listView;
    
    //FX Functions

    @FXML
    private void handleButtonAction(ActionEvent event) {
        final MusicMediaCollection collection = MUSIC_SERVICE
                .createMusicMediaCollection(Paths.get(pathScannedOnLoad));
        dataForTableView = FXCollections.observableArrayList(collection.getMusic());

        dataForTableView.addListener(makeChangeListener(collection));

        List<MusicMediaColumnInfo> myColumnInfoList = TableViewFactory.makeColumnInfoList();

        tableView.setItems(dataForTableView);
        TableViewFactory.makeTable(tableView, myColumnInfoList);
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
        
        int i = listView.getItems().size();
        listView.getItems().add(i, "Playlist " + (i+1));
        listView.layout();
        listView.edit(i);

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
        
        listView.setEditable(true);
        listView.setCellFactory(TextFieldListCell.forListView());
        listView.setOnEditStart(eh -> {
            System.out.println("STARTED");
        });
        listView.setOnEditCommit( eh -> {
            System.out.println("COMMITED");
            listView.layout();
            eh.consume();
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
