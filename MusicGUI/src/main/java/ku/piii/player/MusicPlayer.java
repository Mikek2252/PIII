/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ku.piii.player;

import java.io.File;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
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
    
    Label infoLabel, cTimeLabel, tLeftLabel;
    GridPane play, pause;
    Slider playBar;
    
    public MusicPlayer(Label infoLabel, GridPane play, GridPane pause, Slider mediaBar, Label cTimeLabel, Label tLeftLabel) {
        this.infoLabel = infoLabel;
        this.play = play;
        this.pause = pause;
        this.playBar = mediaBar;
        this.cTimeLabel = cTimeLabel;
        this.tLeftLabel = tLeftLabel;
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
        infoLabel.setText(currentlyPlaying.getTitle()+" - "+currentlyPlaying.getArtist());        
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
        
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> ov, Duration t, Duration t1) {
                Double newPos = t1.toSeconds()/songURL.getDuration().toSeconds();
                playBar.setValue((int) Math.round(newPos*100));
                int timeLeft = (int) Math.round(songURL.getDuration().toSeconds() - t1.toSeconds());
                tLeftLabel.setText("-"+timeToString(timeLeft));
                cTimeLabel.setText(timeToString((int)Math.round(t1.toSeconds())));
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
    
    private String timeToString(int time) {
        int minutes = (int) Math.floor(time/60);
        int seconds = (int) (Math.round(time) - (minutes*60));
        String timeMid;
        if (seconds < 10 && seconds >= 0) timeMid = ":0";
        else timeMid = ":";
        return (minutes + timeMid + seconds);
    }
}
