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

@Table(name="podcasts")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer","handler","episodes"})
public class Podcast{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="podcast_id")
	private int podcastId;
	
	@Column(name="podcast_name")
	private String podcastName;
	
	@Column(name="podcast_about")
	private String podcastAbout;
	
	@Column(name="podcast_cover_image_url")
	private String podcastCoverImageUrl;
	
	@Column(name="podcast_publisher")
	private String podcastPublisher;
	
	@OneToMany(mappedBy="podcast")
	private List<Episode> episodes;
}
