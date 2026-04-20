# Spotify Clone Entities

This document outlines the entities implemented in the Spotify Clone project and their relationships.

## Auth Service Entities

*   **`User`**: Represents a system user. Fields: `id`, `email`, `username`, `password`, `role`, `dateOfBirth`, `gender`.
*   **`UserFollowsUser`**: Represents the many-to-many relationship of users following other users. Fields: `id`, `follower` (User), `followed` (User), `followedAt`.

## Catalog Service Entities

*   **`Artist`**: Represents a music artist. Fields: `id`, `name`, `bio`, `imageUrl`, `popularity`.
*   **`Album`**: Represents a music album. Fields: `id`, `title`, `releaseYear`, `coverImageUrl`, `artist` (ManyToOne to Artist).
*   **`Song`**: Represents a single music track. Fields: `id`, `title`, `duration`, `audioUrl`, `album` (ManyToOne to Album), `genres` (ManyToMany to Genre), `collaboratingArtists` (ManyToMany to Artist).
*   **`Genre`**: Represents a music genre/category. Fields: `id`, `name`, `iconUrl`.
*   **`Playlist`**: Represents a user-created playlist. Fields: `id`, `name`, `description`, `coverImageUrl`, `isPublic`, `ownerId` (UUID referencing Auth User), `songs` (ManyToMany to Song).
*   **`Podcast`**: Represents a podcast series. Fields: `id`, `title`, `description`, `publisher`, `coverImageUrl`.
*   **`Episode`**: Represents a single podcast episode. Fields: `id`, `title`, `description`, `duration`, `audioUrl`, `releaseDate`, `podcast` (ManyToOne to Podcast).
*   **`Lyrics`**: Represents the lyrics for a song. Fields: `id`, `text`, `song` (OneToOne to Song).
*   **`LikedSong`**: Represents a song liked by a user. Fields: `id`, `userId` (UUID referencing Auth User), `song` (ManyToOne to Song), `likedAt`.
*   **`SavedAlbum`**: Represents an album saved by a user. Fields: `id`, `userId` (UUID referencing Auth User), `album` (ManyToOne to Album), `savedAt`.
*   **`SavedPlaylist`**: Represents a playlist saved by a user. Fields: `id`, `userId` (UUID referencing Auth User), `playlist` (ManyToOne to Playlist), `savedAt`.
*   **`UserFollowsArtist`**: Represents an artist followed by a user. Fields: `id`, `userId` (UUID referencing Auth User), `artist` (ManyToOne to Artist), `followedAt`.
