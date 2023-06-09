package bilalbyg.spotifyClone.entities.concretes;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="artists")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer","handler","albums"})
public class Artist {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="artist_id")
	private int artistId;
	
	@Column(name="artist_name")
	private String artistName;
	
	@Column(name="artist_cover_image_url")
	private String artistCoverImageUrl;
	
	@Column(name="artist_background_image_url")
	private String artistBackgroundImageUrl;
	
	@OneToMany(mappedBy="artist")
	private List<Album> albums;
}
