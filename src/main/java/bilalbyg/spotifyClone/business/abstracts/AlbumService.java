package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.entities.concretes.Album;

public interface AlbumService {
	List<Album> getAll();
	void add(Album album);
}
