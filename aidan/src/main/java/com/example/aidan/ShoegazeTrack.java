package com.example.aidan;

import io.prometheus.client.Counter;
import io.prometheus.client.Summary;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Episode;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import org.apache.hc.core5.http.ParseException;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;


public class ShoegazeTrack {
    private static final String playlistId = "6i0HGU6npcXakgBxocOKUo";
    private static final Logger log = LoggerFactory.getLogger(ShoegazeTrack.class);
    private static final SpotifyApi spotifyApi = ClientCredentialsExample.getSpotifyApi();
    private static final GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi
            .getPlaylistsItems(playlistId)
//            .fields("previewUrl")
//          .limit(10)
//          .offset(0)
//          .market(CountryCode.SE)
//          .additionalTypes("track,episode")
            .build();

    // Prometheus instantiation
    public static final Counter spotifyApiRequests = Counter.build()
            .name("get_playlist_requests_total")
            .help("Total number of requests to Spotify API to get playlists")
            .register();

    public static final Summary spotifyApiRequestDuration = Summary.build()
            .name("get_playlist_request_duration_milliseconds")
            .help("Duration of requests to Spotify API to get playlists in milliseconds")
            .register();
    public static Track getRandomShoegaze() {
        try {
            log.info("Calling Spotify API to get playlist...");
            long startTime = System.currentTimeMillis();
            final Paging<PlaylistTrack> playlistTrackPaging = getPlaylistsItemsRequest.execute();
            long finishedTime = System.currentTimeMillis();
            if(playlistTrackPaging != null) {
                log.info("Recieved playlist successfully from Spotify API - took " + (finishedTime - startTime) + " ms.");
                int randomIndex = (int) (Math.random() * 100);
                return ((Track) playlistTrackPaging.getItems()[randomIndex].getTrack());
            } else {
                throw new NullPointerException();
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error: " + e.getMessage());
            return null;
        }
    }

}