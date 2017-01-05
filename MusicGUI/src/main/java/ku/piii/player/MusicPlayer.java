/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ku.piii.player;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import ku.piii.model.MusicMedia;
import ku.piii.model.MusicMediaCollection;

/**
 *
 * @author Mike
 */
public class MusicPlayer {
    
    private MusicMediaCollection playQueue;
    private MediaPlayer mediaPlayer;
    private MusicMedia currentlyPlaying;
    public MusicPlayer() {
        
    }
    
    public void PlayNewSong(MusicMedia song) {
        currentlyPlaying = song;
        Media songURL = new Media(song.getPath());
        mediaPlayer = new MediaPlayer(songURL);
        mediaPlayer.play();
    }
    public boolean NextTrack() {
        
        int trackNo = playQueue.getMusic().indexOf(currentlyPlaying) + 1;
        
        if (trackNo > playQueue.getMusic().size()) {
            throw new IllegalArgumentException("No Next Track");
        }
        MusicMedia song = playQueue.getMusic().get(trackNo+1);
        Media songURL = new Media(song.getPath());
        mediaPlayer = new MediaPlayer(songURL);
        currentlyPlaying = song;
        
        return true;
    }
    
    public boolean LastTrack() {
        return false;
    }
    
    public int getTrackNo() {
        return playQueue.getMusic().indexOf(currentlyPlaying);
    }
}
