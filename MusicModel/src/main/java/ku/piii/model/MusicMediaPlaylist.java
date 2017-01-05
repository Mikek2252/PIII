/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ku.piii.model;

/**
 *
 * @author k1320968
 */
public class MusicMediaPlaylist extends MusicMediaCollection {
    
    private String playlistName;
    
    public MusicMediaPlaylist(final String name){
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        this.playlistName = name;
    }
    
    public String getName() {
        return this.playlistName;
    }
    
    public void setName(final String name) {
        this.playlistName = name;
    }
}
