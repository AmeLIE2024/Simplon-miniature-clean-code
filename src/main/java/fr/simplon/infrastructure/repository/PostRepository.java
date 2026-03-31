package fr.simplon.infrastructure.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.simplon.domain.models.Post;
import fr.simplon.domain.repository.PostRepositoryInterface;

public class PostRepository implements PostRepositoryInterface {

    private List<Post> postList = new ArrayList<>();
    private Set<Long> likedByUserIds = new HashSet<>();

    @Override
    public List<Post> findAllPosts() {
        return new ArrayList<>(postList);
    }

    @Override
    public Post findPostById(long postId) {
        for (Post post : postList) {
            if (post.getId() == postId) {
                return post;
            }
        }
        return null;
    }

    @Override
    public boolean isLikedBy(long userId) {
        return likedByUserIds.contains(userId);
    }

    @Override
    public int getLikeCount() {
        return likedByUserIds.size();
    }

    @Override
    public List<Long> getLikedByUserIds() {
        return new ArrayList<>(likedByUserIds);
    }

    @Override
    public void update(Post post) {
        Post existingPost = findPostById(post.getId());

        if (existingPost != null) {
            postList.set(postList.indexOf(existingPost), post);
        }
    }

    @Override
    public void save(Post post) {
        postList.add(post);
        postList.sort(Comparator.comparing(Post::getCreatedAt).reversed());
    }

    @Override
    public void delete(long postId) {
        postList.removeIf(p -> p.getId() == postId);
    }
}
