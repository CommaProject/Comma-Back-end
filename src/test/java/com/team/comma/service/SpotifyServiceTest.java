package com.team.comma.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.IOException;

import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team.comma.spotify.CreateAccessToken;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.exceptions.detailed.UnauthorizedException;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.search.SearchItemRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

@ExtendWith(MockitoExtension.class)
public class SpotifyServiceTest {

	@InjectMocks
	SpotifyService spotifyService;

	SpotifyApi spotifyApi = null;

	@BeforeEach
	public void reissueAccessToken() {
		CreateAccessToken accessToken = new CreateAccessToken();

		spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken.accesstoken()).build();
	}

	@Test
	@DisplayName("유효하지 않은 토큰")
	public void exceptionUnauthorized() {
		// given
		SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken("Token").build();
		SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists("unknown Artist").build();

		// when
		Throwable thrown = catchThrowable(() -> searchArtistsRequest.execute());

		// then
		assertThat(thrown).isInstanceOf(UnauthorizedException.class).hasMessageContaining("Invalid access token");
	}

	@Test
	@DisplayName("새로운 토큰 발급")
	public void reissueToken() {
		// given
		CreateAccessToken token = new CreateAccessToken();
		String accessToken = null;

		// when
		accessToken = token.accesstoken();

		// then
		assertThat(accessToken).isNotNull();
	}

	@Test
	@DisplayName("가수 목록 API 호출")
	public void executeArtistList() {
		// given
		SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists("박효신").build();

		// when
		Throwable thrown = catchThrowable(() -> searchArtistsRequest.execute());

		// then
		assertThat(thrown).doesNotThrowAnyException();
	}

	@Test
	@DisplayName("가수 목록 가져오기")
	public void getArtistList() throws ParseException, SpotifyWebApiException, IOException {
		// given
		SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists("박효신").build();
		Paging<Artist> artistsPaging = null;
		
		// when
		artistsPaging = searchArtistsRequest.execute();

		// then
		assertThat(artistsPaging).isNotNull();
	}
	
	@Test
	@DisplayName("트랙 목록 API 호출")
	public void executeTrackList() {
		// given
		SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks("사랑의 시작은 고백으로부터").build();

		// when
		Throwable thrown = catchThrowable(() -> searchTracksRequest.execute());

		// then
		assertThat(thrown).doesNotThrowAnyException();
	}
	
	@Test
	@DisplayName("트랙 목록 가져오기")
	public void getTrackList() throws ParseException, SpotifyWebApiException, IOException {
		// given
		SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks("사랑의 시작은 고백으로부터").build();
		Paging<Track> tracksPaging = null;
		
		// when
		tracksPaging = searchTracksRequest.execute();

		// then
		assertThat(tracksPaging).isNotNull();
	}
	
	@Test
	@DisplayName("트랙 목록 API 호출")
	public void executeItemList() {
		// given
		SearchItemRequest searchItemRequest = spotifyApi.searchItem("야생화" , "track").build();

		// when
		Throwable thrown = catchThrowable(() -> searchItemRequest.execute());

		// then
		assertThat(thrown).doesNotThrowAnyException();
	}
	
	@Test
	@DisplayName("트랙 목록 가져오기")
	public void getItemList() throws ParseException, SpotifyWebApiException, IOException {
		// given
		SearchItemRequest searchItemRequest = spotifyApi.searchItem("야생화" , "track").build();
		SearchResult tracksPaging = null;
		
		// when
		tracksPaging = searchItemRequest.execute();

		// then
		assertThat(tracksPaging).isNotNull();
	}

}
