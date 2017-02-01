/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ku.piii.model;

import org.junit.Test;

/**
 *
 * @author k1320968
 */
public class MusicMediaPlaylistTest {
    
    
    
    @Test(expected=IllegalArgumentException.class)
    public void newNameIsNull() {
        String name = null;
        MusicMediaPlaylist playlist = new MusicMediaPlaylist(name);   
    }
    
    
}
