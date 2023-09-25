package com.team.comma.spotify.service;

import com.neovisionaries.i18n.CountryCode;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.spotify.dto.SearchArtistResponse;
import com.team.comma.spotify.support.SpotifyAuthorization;
import com.team.comma.spotify.support.SpotifySearchCommand;
import com.team.comma.spotify.dto.SearchTrackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumsTracksRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import se.michaelthelin.spotify.requests.data.browse.miscellaneous.GetAvailableGenreSeedsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import javax.security.auth.login.AccountException;
import java.time.LocalDate;
import java.util.ArrayList;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static com.team.comma.spotify.dto.SearchTrackResponse.createTrackResponse;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SpotifyAuthorization spotifyAuthorization;
    private final SpotifySearchCommand spotifySearchCommand;

    public MessageResponse searchArtistList(String artistName , String token) throws AccountException {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists(artistName).market(CountryCode.KR).build();
        Object executeResult = spotifySearchCommand.executeCommand(searchArtistsRequest);

        if(executeResult instanceof SpotifyApi) {
            return searchArtistList(artistName , token);
        }

        Paging<Artist> artistsPaging = (Paging<Artist>) executeResult;
        ArrayList<SearchArtistResponse> result = new ArrayList<>();
        for (Artist artist : artistsPaging.getItems()) {
            result.add(SearchArtistResponse.createArtistResponse(artist));
        }

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    public Artist getArtistByArtistId(String artistId) {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        GetArtistRequest getArtistRequest = spotifyApi.getArtist(artistId).build();

        Object executeResult = spotifySearchCommand.executeCommand(getArtistRequest);
        if(executeResult instanceof SpotifyApi) {
            return getArtistByArtistId(artistId);
        }

        return (Artist) executeResult;
    }

    public MessageResponse searchTrackList(String trackName , String token) throws AccountException {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        SearchTracksRequest searchTrackRequest = spotifyApi.searchTracks(trackName).market(CountryCode.KR).build();

        Object executeResult = spotifySearchCommand.executeCommand(searchTrackRequest);
        if(executeResult instanceof SpotifyApi) {
            return searchTrackList(trackName , token);
        }

        Paging<Track> tracksPaging = (Paging<Track>) executeResult;
        ArrayList<SearchTrackResponse> result = new ArrayList<>();
        for (Track track : tracksPaging.getItems()) {
            result.add(createTrackResponse(track));
        }

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    @Transactional
    public Track getTrackByTrackId(String trackId) {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackId).build();

        Object executeResult = spotifySearchCommand.executeCommand(getTrackRequest);
        if(executeResult instanceof SpotifyApi) {
            return getTrackByTrackId(trackId);
        }

        return (Track) executeResult;
    }

    public ArrayList<TrackSimplified> getTracksByAlbumId(String albumId) {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        GetAlbumsTracksRequest getAlbumsTracksRequest = spotifyApi.getAlbumsTracks(albumId).market(CountryCode.KR).build();
        Object executeResult = spotifySearchCommand.executeCommand(getAlbumsTracksRequest);

        if(executeResult instanceof SpotifyApi) {
            return getTracksByAlbumId(albumId);
        }

        Paging<TrackSimplified> tracksPaging = (Paging<TrackSimplified>) executeResult;
        ArrayList<TrackSimplified> result = new ArrayList<>();
        for (TrackSimplified track : tracksPaging.getItems()) {
            result.add(track);
        }

        return result;
    }
    public MessageResponse searchTrackListByArtist(String spotifyArtistId , String token) throws AccountException {
        ArrayList<SearchTrackResponse> result = new ArrayList<>();

        Paging<AlbumSimplified> artistsAlbums = getAlbumsByArtistId(spotifyArtistId);
        for(AlbumSimplified albumSimplified : artistsAlbums.getItems()) {
            ArrayList<TrackSimplified> trackSimplifiedList= getTracksByAlbumId(albumSimplified.getId());
            for(TrackSimplified trackSimplified : trackSimplifiedList) {
                result.add(createTrackResponse(trackSimplified, albumSimplified));
            }
        }

        return MessageResponse.of(REQUEST_SUCCESS, result);
    }

    public Paging<AlbumSimplified> getAlbumsByArtistId(String artistId) {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        GetArtistsAlbumsRequest getArtistsAlbumsRequest = spotifyApi.getArtistsAlbums(artistId).market(CountryCode.KR).build();

        Object executeResult = spotifySearchCommand.executeCommand(getArtistsAlbumsRequest);
        if(executeResult instanceof SpotifyApi) {
            return getAlbumsByArtistId(artistId);
        }

        Paging<AlbumSimplified> albumsPaging = (Paging<AlbumSimplified>) executeResult;

        return albumsPaging;
    }

    public MessageResponse searchGenreList() {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        GetAvailableGenreSeedsRequest genres = spotifyApi.getAvailableGenreSeeds().build();

        Object executeResult = spotifySearchCommand.executeCommand(genres);
        if(executeResult instanceof SpotifyApi) {
            return searchGenreList();
        }

        String[] result = (String[]) executeResult;
        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    public MessageResponse searchArtistListByYear(int offset) {
        int year = LocalDate.now().getYear();
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        SearchArtistsRequest artists = spotifyApi.searchArtists(String.format("year:%d", year))
                .offset(offset)
                .limit(10)
                .market(CountryCode.KR)
                .build();

        Object executeResult = spotifySearchCommand.executeCommand(artists);
        if(executeResult instanceof SpotifyApi) {
            return searchArtistListByYear(offset);
        }

        Paging<Artist> artistPaging = (Paging<Artist>) executeResult;
        ArrayList<String> artistNames = new ArrayList<>();
        for (Artist artist : artistPaging.getItems()) {
            artistNames.add(artist.getName());
        }

        return MessageResponse.of(REQUEST_SUCCESS ,artistNames);
    }

}
