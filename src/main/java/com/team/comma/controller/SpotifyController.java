package com.team.comma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.team.comma.service.SpotifyService;

@RequestMapping(value = "/spotify")
@RestController
public class SpotifyController {
	
	@Autowired SpotifyService spotifyService;

	@RequestMapping(value = "/artist" , method = RequestMethod.GET)
	public void getArtist() {
		spotifyService.searchArtist_Sync("박효신");
	}
	
	@RequestMapping(value = "/track" , method = RequestMethod.GET)
	public void getTrack() {
		spotifyService.searchTrack_Sync("사랑의 시작은 고백으로부터");
	}
	
	@RequestMapping(value = "/search" , method = RequestMethod.GET)
	public void searchItem() {
		spotifyService.searchItem_Sync("야생화" , "EPISODE");
	}
	
}
