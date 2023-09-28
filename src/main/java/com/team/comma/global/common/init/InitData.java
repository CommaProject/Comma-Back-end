package com.team.comma.global.common.init;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.repository.ArtistRepository;
import com.team.comma.domain.favorite.artist.domain.FavoriteArtist;
import com.team.comma.domain.favorite.artist.repository.FavoriteArtistRepository;
import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import com.team.comma.domain.favorite.track.repository.FavoriteTrackRepository;
import com.team.comma.domain.playlist.archive.domain.Archive;
import com.team.comma.domain.playlist.archive.repository.ArchiveRepository;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.domain.track.artist.repository.TrackArtistRepository;
import com.team.comma.domain.track.playcount.domain.TrackPlayCount;
import com.team.comma.domain.track.playcount.repository.TrackPlayCountRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class InitData {

    private final UserRepository userRepository;
    private final TrackRepository trackRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TrackPlayCountRepository trackPlayCountRepository;
    private final PlaylistRepository playlistRepository;
    private final ArchiveRepository archiveRepository;
    private final FavoriteTrackRepository favoriteTrackRepository;
    private final FavoriteArtistRepository favoriteArtistRepository;
    private final ArtistRepository artistRepository;
    private final TrackArtistRepository trackArtistRepository;

    @Transactional
    @PostConstruct
    public void init() {
        User user1 = User.builder().email("testEmail").password(bCryptPasswordEncoder.encode("password")).role(UserRole.USER).delFlag(false).type(UserType.GENERAL_USER).joinDate(Date.valueOf(LocalDate.of(2023 , 8 , 21))).build();
        User user2 = User.builder().email("toUserEmail").password(bCryptPasswordEncoder.encode("password")).role(UserRole.USER).delFlag(false).type(UserType.GENERAL_USER).joinDate(Date.valueOf(LocalDate.of(2023 , 8 , 25))).build();
        UserDetail userDetail1 = UserDetail.builder().name("name").nickname("nickname").profileImageUrl("no profile image").popupAlertFlag(true).favoritePublicFlag(true).calenderPublicFlag(true).allPublicFlag(true).user(user1).build();
        UserDetail userDetail2 = UserDetail.builder().name("name2").nickname("nickname2").profileImageUrl("no profile image").popupAlertFlag(true).favoritePublicFlag(true).calenderPublicFlag(true).allPublicFlag(true).user(user2).build();
        user1.setUserDetail(userDetail1);
        user2.setUserDetail(userDetail2);
        userRepository.save(user1);
        userRepository.save(user2);

        Artist iu = Artist.builder().spotifyArtistId("3HqSLMAZ3g3d5poNaI7GOU").artistName("IU").artistImageUrl("https://i.scdn.co/image/ab6761610000e5eb006ff3c0136a71bfb9928d34").build();
        artistRepository.save(iu);

        Track lilac = Track.builder().id(1L).trackTitle("LILAC").durationTimeMs(214253).recommendCount(0L).albumImageUrl("https://i.scdn.co/image/ab67616d0000b2734ed058b71650a6ca2c04adff").spotifyTrackId("5xrtzzzikpG3BLbo4q1Yul").spotifyTrackHref("https://api.spotify.com/v1/tracks/5xrtzzzikpG3BLbo4q1Yul").build();
        Track celebrity = Track.builder().id(2L).trackTitle("Celebrity").durationTimeMs(195546).recommendCount(0L).albumImageUrl("https://i.scdn.co/image/ab67616d0000b2734ed058b71650a6ca2c04adff").spotifyTrackId("5nCwjUUsmBuNZKn9Xu10Os").spotifyTrackHref("https://api.spotify.com/v1/tracks/5nCwjUUsmBuNZKn9Xu10Os").build();
        trackRepository.save(lilac);
        trackRepository.save(celebrity);

        TrackArtist lilacIU = TrackArtist.builder().track(lilac).artist(iu).build();
        TrackArtist celebrityIU = TrackArtist.builder().track(celebrity).artist(iu).build();
        trackArtistRepository.save(lilacIU);
        trackArtistRepository.save(celebrityIU);

        TrackPlayCount lilacCount1 = TrackPlayCount.builder().track(lilac).user(user1).build();
        TrackPlayCount lilacCount2 = TrackPlayCount.builder().track(lilac).user(user1).build();
        TrackPlayCount lilacCount3 = TrackPlayCount.builder().track(lilac).user(user1).build();
        TrackPlayCount celebrityCount1 = TrackPlayCount.builder().track(celebrity).user(user1).build();
        TrackPlayCount celebrityCount2 = TrackPlayCount.builder().track(celebrity).user(user1).build();
        trackPlayCountRepository.save(lilacCount1);
        trackPlayCountRepository.save(lilacCount2);
        trackPlayCountRepository.save(lilacCount3);
        trackPlayCountRepository.save(celebrityCount1);
        trackPlayCountRepository.save(celebrityCount2);

        Playlist playlist1 = Playlist.builder().playlistTitle("test playlist1").alarmStartTime(LocalTime.of(12 , 00)).alarmFlag(true).delFlag(false).user(user1).build();
        Playlist playlist2 = Playlist.builder().playlistTitle("test playlist2").alarmStartTime(LocalTime.of(13 , 00)).alarmFlag(true).delFlag(false).user(user1).build();
        PlaylistTrack playlist1_Track1 = PlaylistTrack.builder().playSequence(1).trackAlarmFlag(true).playlist(playlist1).track(lilac).build();
        playlist1.getPlaylistTrackList().add(playlist1_Track1);
        PlaylistTrack playlist1_Track2 = PlaylistTrack.builder().playSequence(1).trackAlarmFlag(true).playlist(playlist1).track(celebrity).build();
        playlist2.getPlaylistTrackList().add(playlist1_Track2);
        PlaylistTrack playlist2_Track1 = PlaylistTrack.builder().playSequence(1).trackAlarmFlag(true).playlist(playlist2).track(lilac).build();
        playlist2.getPlaylistTrackList().add(playlist2_Track1);
        playlistRepository.save(playlist1);
        playlistRepository.save(playlist2);

        Archive archive1 = Archive.builder().comment("archive1").publicFlag(false).createDate(LocalDateTime.of(2023 , 8 , 28 , 21 , 0 , 0)).user(user1).playlist(playlist1).build();
        Archive archive2 = Archive.builder().comment("archive2").publicFlag(false).createDate(LocalDateTime.of(2023 , 8 , 29 , 21 , 0 , 0)).user(user1).playlist(playlist1).build();
        archiveRepository.save(archive1);
        archiveRepository.save(archive2);

        FavoriteTrack favoriteTrack1 = FavoriteTrack.builder().user(user1).track(lilac).build();
        FavoriteTrack favoriteTrack2 = FavoriteTrack.builder().user(user1).track(celebrity).build();
        favoriteTrackRepository.save(favoriteTrack1);
        favoriteTrackRepository.save(favoriteTrack2);

        FavoriteArtist favoriteArtist = FavoriteArtist.builder().user(user1).artist(iu).build();
        favoriteArtistRepository.save(favoriteArtist);
    }

}
