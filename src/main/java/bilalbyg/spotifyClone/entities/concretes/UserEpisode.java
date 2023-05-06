package bilalbyg.spotifyClone.entities.concretes;


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
@Table(name="user_episodes")
public class UserEpisode {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_episode_id")
	private int userEpisodeId;
	
	@Column(name="user_id")
	private int userId;
	
	@Column(name="episode_id")
	private int episodeId;
}
