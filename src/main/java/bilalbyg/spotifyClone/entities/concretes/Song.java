package bilalbyg.spotifyClone.entities.concretes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="songs")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Song {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="song_id")
	private int songId;
	
	@Column(name="name")
	private String name;
	
	@Column(name="duration")
	private double duration;
	
	@Column(name="song_url")
	private String songUrl;
	
	@ManyToOne()
	@JoinColumn(name="album_id")
	private Album album;
}
