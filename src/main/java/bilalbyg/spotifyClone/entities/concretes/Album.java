package bilalbyg.spotifyClone.entities.concretes;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="albums")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer","handler","songs"})
public class Album{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="album_id")
	private int albumId;
	
	@Column(name="name")
	private String name;
	
	@Column(name="releaseYear")
	private String releaseYear;
	
	@Column(name="cover_image_url")
	private String coverImageUrl;
	
	@OneToMany(mappedBy="album")
	private List<Song> songs;
	
	@ManyToOne()
	@JoinColumn(name="artist_id")
	private Artist artist;

}
