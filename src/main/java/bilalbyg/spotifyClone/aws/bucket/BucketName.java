package bilalbyg.spotifyClone.aws.bucket;

public enum BucketName {

    PLAYLIST_COVER_IMAGE("spotify-clone-123");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
