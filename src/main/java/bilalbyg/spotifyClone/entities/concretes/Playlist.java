package bilalbyg.spotifyClone.entities.concretes;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="playlists")
public class Playlist {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="playlist_id")
	private int playlistId;
	
	@Column(name="playlist_name")
	private String playlistName;
	
	@Column(name="playlist_user_id")
	private int playlistUserId;
	
	@Column(name="playlist_cover_image_url")
	private String playlistCoverImageUrl;
	
	@Column(name="playlist_songs")
	private List<Integer> playlistSongs;
}
