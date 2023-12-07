package com.team.comma.global.jwt.support;

import com.team.comma.spotify.exception.SpotifyException;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;

public class CreationAccessToken {

    private String clientId = "9e82664657014649840297d86007bb09";
    private String clientSecret = "39d768fc6d624899ba13a3472ab8b353";
    private SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientId)
            .setClientSecret(clientSecret)
            .build();

    public String accessToken() {
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new SpotifyException(String.format("Spotify 에서 새로운 AccessToken을 생성하는 도중에 오류가 발생했습니다. %s", e.getMessage()));
        }

        return spotifyApi.getAccessToken();
    }
}
