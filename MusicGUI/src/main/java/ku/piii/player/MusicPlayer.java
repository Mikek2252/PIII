/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ku.piii.player;

import java.io.File;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import ku.piii.model.MusicMedia;
import ku.piii.model.MusicMediaCollection;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 *
 * @author Mike
 */
public class MusicPlayer {
    
    private MusicMediaCollection playQueue;
    private MediaPlayer mediaPlayer;
    private MusicMedia currentlyPlaying;
    
    Label songLabel, artistLabel;
    FontIcon play, pause;
    
    public MusicPlayer(Label songLabel, Label artistLabel, FontIcon play, FontIcon pause) {
        this.songLabel = songLabel;
        this.artistLabel = artistLabel;
        this.play = play;
        this.pause = pause;
    }
    
    public void setPlayQueue(MusicMediaCollection playQueue){
        this.playQueue = playQueue;
    }
    
    public MusicMediaCollection getPlayQueue(){
        return playQueue;
    }
    
    public MusicMedia getCurrentlyPlaying() {
        return currentlyPlaying;
    }
    
    public void pause() {
        if (mediaPlayer != null) {
            pause.setVisible(false);
            play.setVisible(true);
            mediaPlayer.pause();
        }
    }
    
    public void play() {
        if (mediaPlayer != null) {
            pause.setVisible(true);
            play.setVisible(false);
            mediaPlayer.play();
        }
    }
    
    public void play(int trackNo) {
        play(playQueue.getMusic().get(trackNo));
    }
    
    public void play(MusicMedia song) {
        if (mediaPlayer != null) { 
            mediaPlayer.stop();
            mediaPlayer.pause();
            mediaPlayer.dispose();
        }
        
        currentlyPlaying = song;
        songLabel.setText(currentlyPlaying.getTitle());
        artistLabel.setText(currentlyPlaying.getArtist());
               
        File songFile = new File(song.getPath());
        String songPath = songFile.toURI().toString();
        Media songURL = new Media(songPath);
        mediaPlayer = new MediaPlayer(songURL);
        play();
        
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.stop();
                nextTrack();
            }
        });   
    }
    public void nextTrack() {
        int trackNo = getTrackNo();
        if (trackNo >= playQueue.getMusic().size()-1) trackNo = 0;
        else trackNo++;
        play(playQueue.getMusic().get(trackNo));
    }
    
    public void previousTrack() {
        int trackNo = getTrackNo();
        if (trackNo-1 < 0 ) trackNo = 0;
        else trackNo--;
        play(playQueue.getMusic().get(trackNo));
    }
    
    public int getTrackNo() {
        return playQueue.getMusic().indexOf(currentlyPlaying);
    }
}
