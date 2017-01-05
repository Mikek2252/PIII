package ku.piii.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MusicMediaCollection {
    private List<MusicMedia> music = new CopyOnWriteArrayList<>();

    public List<MusicMedia> getMusic() {
        return music;
    }

    public void setMusic(List<MusicMedia> music) {
        this.music = music;
    }

    public void addMusicMedia(final MusicMedia musicMedia) {
        music.add(musicMedia);
    }

    public MusicMediaCollection mergeCollection(final MusicMediaCollection musicMedia) {
        final MusicMediaCollection merge = new MusicMediaCollection();
        final ArrayList<MusicMedia> mergedMusic = new ArrayList<>();
        mergedMusic.addAll(music);
        mergedMusic.addAll(musicMedia.getMusic());
        merge.setMusic(mergedMusic);
        return merge;
    }

    public void removeMusicMedia(MusicMedia removedChild) {
        if(music.remove(removedChild)==false)
        {
            throw new RuntimeException("tried to remove an object that wasn't in the list");
        }
    }
    
    public List<String> getArtists() {
        List<String> artists = new ArrayList();
        for (MusicMedia song: music) {
            if (!artists.contains(song.getArtist())) {
                artists.add(song.getArtist());
            }
        }
        return artists;
    }
    
    public List<String> getGenres() {
        List<String> genres = new ArrayList();
        for (MusicMedia song: music) {
            if (!genres.contains(song.getGenre())) {
                genres.add(song.getGenre());
            }
        }
        return genres;
    }
    
    public List<String> getAlbums() {
        List<String> albums = new ArrayList();
        for (MusicMedia song: music) {
            if (!albums.contains(song.getGenre())) {
                albums.add(song.getGenre());
            }
        }
        return albums;
    }
}
