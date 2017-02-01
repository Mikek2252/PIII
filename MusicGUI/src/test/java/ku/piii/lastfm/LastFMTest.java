/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ku.piii.lastfm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matcher;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Mike
 */
public class LastFMTest {
    
    private LastFM lf = new LastFM();
    private String baseURL = "http://ws.audioscrobbler.com/2.0/?method=";
    private String APIKey = "&api_key=c8f8b63297eaad5c7a3e00a461144870";
    private String format = "&format=json";
    
    @Test (expected=IllegalArgumentException.class)
    public void ArtistNameNull(){
        String artistName = null;
        Artist artist = lf.getArtistInfo(artistName);

    }
    
    @Test (expected=IllegalArgumentException.class)
    public void ArtistNameEmptyString(){
        String artistName = "";
        Artist artist = lf.getArtistInfo(artistName);

    }
    
    @Test
    public void getCorrectArtistInfo(){
        String artistName = "Muse";
        Artist testArtist = new Artist();
        //Define Artist with Expected Data
        testArtist.setName("Muse");
        testArtist.setBioSummary("Muse are an alternative rock band from Teignmouth, England, United Kingdom. The band consists of Matthew Bellamy on lead vocals, piano, keyboard and guitar, Chris Wolstenholme on backing vocals and bass guitar, and Dominic Howard on drums and percussion.  \n\nThey have been friends since their formation in early 1994 and changed band names a number of times (such as Gothic Plague, Fixed Penalty, and Rocket Baby Dolls) before adopting the name Muse. Since the release of their fourth album <a href=\"https://www.last.fm/music/Muse\">Read more on Last.fm</a>");
        List<String> tagList = Arrays.asList("alternative rock", "rock", "alternative", "Progressive rock", "seen live");
        testArtist.setTags(tagList);
        testArtist.setImgURL("https://lastfm-img2.akamaized.net/i/u/174s/242feb079cbd42a6b0e4d3dbd68c4f7b.png");
        
        //Get Artist Info from LastFM
        Artist artist = lf.getArtistInfo(artistName);
        
        assertEquals(artist.getName(), testArtist.getName());
        assertEquals(artist.getBioSummary(), testArtist.getBioSummary());
        assertTrue(artist.getTags().containsAll(tagList));
        assertEquals(artist.getImgURL(), testArtist.getImgURL());
        
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void albumNotFound(){
        String albumName = "23d2as23r";
        String artistName = "Random String";
        Album album = lf.getAlbum(albumName, artistName);
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void artistNotFound() {
        String artistName = "ahdashasdh";
        Artist artist = lf.getArtistInfo(artistName);
    }
    
    @Test
    public void parseTagList() {
        
        String tagString = "{\"tags\": {\n            \"tag\": [{\n                \"name\": \"alternative rock\",\n                \"url\": \"https://www.last.fm/tag/alternative+rock\"\n            }, {\n                \"name\": \"rock\",\n                \"url\": \"https://www.last.fm/tag/rock\"\n            }, {\n                \"name\": \"alternative\",\n                \"url\": \"https://www.last.fm/tag/alternative\"\n            }, {\n                \"name\": \"Progressive rock\",\n                \"url\": \"https://www.last.fm/tag/Progressive+rock\"\n            }, {\n                \"name\": \"seen live\",\n                \"url\": \"https://www.last.fm/tag/seen+live\"\n            }]\n        }}";
        JSONObject obj = new JSONObject(tagString);       
        List<String> testTags = Arrays.asList("alternative rock", "rock", "alternative", "Progressive rock", "seen live");
 
        List<String> tags = lf.getTagList(obj);
        
        assertTrue(tags.containsAll(testTags));
    }
}
