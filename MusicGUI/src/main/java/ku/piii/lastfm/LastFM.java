/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ku.piii.lastfm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mike
 */
public final class LastFM {
    
    String baseURL = "http://ws.audioscrobbler.com/2.0/?method=";
    String APIKey = "&api_key=c8f8b63297eaad5c7a3e00a461144870";
    String format = "&format=json";
    
    public LastFM() {
        
    }
    
    public Artist getArtistInfo(String artistName) {
        
        if (artistName == null || artistName.isEmpty()) {
            throw new IllegalArgumentException("Invalid Artist Name");
        }
        
        
        String artistQuery = callREST(baseURL+"artist.getinfo&artist="+artistName+APIKey+format);
        String topTracksQuery = callREST(baseURL+"artist.gettoptracks&artist="+artistName+APIKey+format);
        String albumsQuery = callREST(baseURL+"artist.gettopalbums&artist="+artistName+APIKey+format);
        
        
        Artist artist = new Artist();
        JSONObject artistObj = new JSONObject(artistQuery).getJSONObject("artist");
        JSONArray topTracks = new JSONObject(topTracksQuery).getJSONObject("toptracks").getJSONArray("track");
        JSONArray topAlbums = new JSONObject(albumsQuery).getJSONObject("topalbums").getJSONArray("album");
        
        List<Track> tracks = new ArrayList<Track>();
        List<Album> albums = new ArrayList<Album>();
        
        int ALBUM_COUNT = 5;
        int TRACK_COUNT = 5;
        
        if(topTracks.length() < TRACK_COUNT) {
            TRACK_COUNT = topTracks.length() -1;
        }
        if(topAlbums.length() < ALBUM_COUNT) {
            ALBUM_COUNT = topAlbums.length() -1;
        }
        
        for (int i=0; i< ALBUM_COUNT; i++) {
            JSONObject topAlbum = topAlbums.getJSONObject(i);
            Album album = new Album();
            album.setName(topAlbum.getString("name"));
            album.setArtist(artistName);
            album.setPlayCount(topAlbum.getInt("playcount"));
            album.setImage(((JSONObject)topAlbum.getJSONArray("image").get(0)).getString("#text"));
            albums.add(album);
        }
        
        for (int i=0; i< TRACK_COUNT; i++) {
            JSONObject topTrack = topTracks.getJSONObject(i);
            Track track = new Track();
            track.setName(topTrack.getString("name"));
            track.setListeners(topTrack.getInt("listeners"));
            track.setPlayCount(topTrack.getInt("playcount"));
            track.setImage(((JSONObject)topTrack.getJSONArray("image").get(0)).getString("#text"));
            tracks.add(track);
        }
        
        artist.setName(artistObj.getString("name"));
        artist.setImgURL(((JSONObject)artistObj.getJSONArray("image").get(2)).getString("#text"));
        artist.setListeners(artistObj.getJSONObject("stats").getInt("listeners"));
        artist.setPlayCount(artistObj.getJSONObject("stats").getInt("playcount"));
        artist.setTags(getTagList(artistObj));
        artist.setBioSummary(artistObj.getJSONObject("bio").getString("summary"));
        artist.setTracks(tracks);
        artist.setAlbums(albums);
        return artist;
    }
    
    public Album getAlbum(String albumName, String artistName) {
        
        if (albumName == null || albumName.isEmpty()) {
            throw new IllegalArgumentException("Invalid Album Name");
        } 
        
        Album album = new Album();
        
        try {
            artistName = URLEncoder.encode(artistName, "UTF-8");
            albumName = URLEncoder.encode(albumName, "UTF-8");
        }  catch (Exception e) {
            
        }
        
        String albumResult = callREST(baseURL+"album.getinfo"+APIKey+"&artist="+artistName+"&album="+albumName+format);
        
        JSONObject albumObj = new JSONObject(albumResult).getJSONObject("album");
        
        album.setArtist(artistName);
        album.setName(albumName);
        album.setListeners(albumObj.getInt("listeners"));
        album.setPlayCount(albumObj.getInt("playcount"));
        album.setImage(((JSONObject)albumObj.getJSONArray("image").get(0)).getString("#text"));
        album.setTags(getTagList(albumObj));
        
        JSONArray tracks = albumObj.getJSONObject("tracks").getJSONArray("track");
        List<Track> trackList = new ArrayList<Track>();
        for (int i=0; i< tracks.length(); i++) {
            JSONObject currentTrack = tracks.getJSONObject(i);
            Track track = new Track();
            track.setName(currentTrack.getString("name"));
            track.setDuration(timeToString(currentTrack.getInt("duration")));
            track.setArtist(currentTrack.getJSONObject("artist").getString("name").replaceAll("\\\\", ""));
            trackList.add(track);
        }
        album.setTracks(trackList);
        
        return album;
    }
    
    public List<String> getTagList(JSONObject tags){
        JSONArray tag = tags.getJSONObject("tags").getJSONArray("tag");
        List<String> tagList = new ArrayList<String>();
        for (int i=0; i< tag.length(); i++) {
            tagList.add(tag.getJSONObject(i).getString("name"));
        }
        return tagList;
    }
    
    
    public String callREST(String stringURL) {
        
        String output = null;
        try {
            stringURL.replaceAll("\\\\", "");
            URL url = new URL(stringURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : error : " + conn.getResponseCode());
            }
            
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            output = br.readLine();
            conn.disconnect();       

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            JSONObject obj = new JSONObject(output);
            if (obj.has("error")) {
                throw new IllegalArgumentException(obj.getString("message"));
            }
            return output;
        }
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
