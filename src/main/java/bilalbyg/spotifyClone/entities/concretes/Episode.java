package bilalbyg.spotifyClone.entities.concretes;

import java.util.Date;

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

@Table(name="episodes")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Episode {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="episode_id")
	private int episodeId;
	
	@Column(name="episode_name")
	private String episodeName;
	
	@Column(name="episode_duration")
	private double episodeDuration;
	
	@Column(name="episode_url")
	private String episodeUrl;
	
	@Column(name="episode_release_date")
	private Date episodeReleaseDate;
	
	@Column(name="episode_description")
	private String episodeDescription;

	@ManyToOne()
	@JoinColumn(name="podcast_id")
	private Podcast podcast;
}

