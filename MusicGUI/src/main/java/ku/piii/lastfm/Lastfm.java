/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ku.piii.lastfm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mike
 */
public final class Lastfm {
    
    String baseURL = "http://ws.audioscrobbler.com/2.0/?method=";
    String APIKey = "&api_key=c8f8b63297eaad5c7a3e00a461144870&format=json";
    
    public Lastfm() {
        getArtistInfo("Taylor Swift");
    }
    
    public Artist getArtistInfo(String artistName) {
        
        String JSONString = callREST(baseURL+"artist.getinfo&artist="+artistName+APIKey);
        
        Artist artist = new Artist();
        JSONObject obj = new JSONObject(JSONString).getJSONObject("artist");
        
        artist.setName(obj.getString("name"));
        artist.setImgURL(((JSONObject)obj.getJSONArray("image").get(3)).getString("#text"));
        artist.setListeners(obj.getJSONObject("stats").getInt("listeners"));
        artist.setPlayCount(obj.getJSONObject("stats").getInt("playcount"));
        
        JSONArray tags = obj.getJSONObject("tags").getJSONArray("tag");
        List<String> Tags = new ArrayList<String>();
        for (int i=0; i< tags.length(); i++) {
            Tags.add(tags.getJSONObject(i).getString("name"));
        }
        artist.setTags(Tags);
        artist.setBioSummary(obj.getJSONObject("bio").getString("summary"));
  
        return artist;
    }
    
    public Album getAlbum(String albumName) {
        Album album;
        album = new Album();
        return album;
    }
    
    public String callREST(String stringURL) {
        String output = null;
        try {

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
            return output;
        }
    }
    
}
