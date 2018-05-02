package cz.gattserver.grass3.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass3.songs.facades.SongsFacade;
import cz.gattserver.grass3.songs.model.dto.SongDTO;
import cz.gattserver.grass3.songs.model.dto.SongOverviewDTO;

@Controller
@RequestMapping("/songs")
public class SongsResource {

	@Autowired
	private SongsFacade songsFacade;

	@RequestMapping("/list")
	public ResponseEntity<List<SongOverviewDTO>> list(@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "pageSize", required = true) int pageSize) {
		int count = songsFacade.getSongsCount();
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(songsFacade.getSongsForREST(page, pageSize), HttpStatus.OK);
	}

	@RequestMapping("/count")
	public ResponseEntity<Integer> count() {
		return new ResponseEntity<>(songsFacade.getSongsCount(), HttpStatus.OK);
	}

	@RequestMapping("/song")
	public @ResponseBody SongDTO song(@RequestParam(value = "id", required = true) Long id) {
		return songsFacade.getSongById(id);
	}

}
