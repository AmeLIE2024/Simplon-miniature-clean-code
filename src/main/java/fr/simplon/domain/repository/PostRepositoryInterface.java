package fr.simplon.domain.repository;

import java.util.List;

import fr.simplon.domain.models.Post;

public interface PostRepositoryInterface {

    List<Post> findAllPosts();

    Post findPostById(long postId);

    void save(Post post);

    void update(Post post);

    void delete(long postId);
}
