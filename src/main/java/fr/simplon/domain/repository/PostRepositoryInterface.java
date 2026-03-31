package fr.simplon.domain.repository;

import java.util.List;

import fr.simplon.domain.models.Post;

public interface PostRepositoryInterface {

    Post findPostById(long postId);

    List<Post> findAllPosts();

    boolean isLikedBy(long userId);

    int getLikeCount();

    List<Long> getLikedByUserIds();

    void update(Post post);

    void save(Post post);

    void delete(long postId);
}
