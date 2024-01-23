package com.example.aidan;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import org.apache.hc.core5.http.ParseException;

//Logger imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

//Prometheus imports
import io.prometheus.client.Counter;
import io.prometheus.client.Summary;

public class ClientCredentialsExample {
    private static final String clientId = "";
    private static final String clientSecret = "";

    public static final Counter spotifyApiRequests = Counter.build()
            .name("spotify_credential_requests_total")
            .help("Total number of requests to Spotify API")
            .register();

    public static final Summary spotifyApiRequestDuration = Summary.build()
            .name("spotify_credential_request_duration_milliseconds")
            .help("Duration of requests to Spotify API in milliseconds")
            .register();
    private static final Logger log = LoggerFactory.getLogger(ClientCredentialsExample.class);
    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .build();
    private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
            .build();

    public static SpotifyApi getSpotifyApi() {
        try {
            // Logs the external API usage & counts it as a metric
            log.info("Calling external API to get credentials...");
            spotifyApiRequests.inc();
            // Starts timer to measure the request duration
            Summary.Timer requestTimer = spotifyApiRequestDuration.startTimer();

            // Measures time internally for logs and executes API request
            long startTime = System.currentTimeMillis();
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            long finishedTime = System.currentTimeMillis();

            // Observing request duration for Prometheus
            requestTimer.observeDuration();

            // Logs API result
            if(clientCredentials != null) {
                log.info("Recieved access token from Spotify API - took " + (finishedTime - startTime) + " ms.");
            } else {
                log.error("Error recieving access token from Spotify API.");
            }

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error: " + e.getMessage());
        }
        return spotifyApi;
    }

}