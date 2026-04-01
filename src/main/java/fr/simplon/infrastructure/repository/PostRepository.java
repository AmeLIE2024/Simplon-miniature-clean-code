package fr.simplon.infrastructure.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.simplon.domain.models.Post;
import fr.simplon.domain.repository.PostRepositoryInterface;

public class PostRepository implements PostRepositoryInterface {

    private List<Post> postList = new ArrayList<>();

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
    public void save(Post post) {
        post.setId(postList.size() + 1L);
        postList.add(post);
        postList.sort(Comparator.comparing(Post::getCreatedAt).reversed());
    }

    @Override
    public void update(Post post) {
        Post existing = findPostById(post.getId());
        if (existing != null) {
            postList.set(postList.indexOf(existing), post);
        }
    }

    @Override
    public void delete(long postId) {
        postList.removeIf(p -> p.getId() == postId);
    }
}