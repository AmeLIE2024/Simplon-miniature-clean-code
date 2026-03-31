package fr.simplon.infrastructure.services.post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.simplon.domain.models.Post;
import fr.simplon.domain.models.User;
import fr.simplon.domain.gateway.PostService;
import fr.simplon.domain.models.ImageExtension;
import fr.simplon.domain.models.VideoExtension;
import fr.simplon.infrastructure.repository.PostRepository;

public class PostServiceImpl implements PostService {

    private ImageExtension imageExtension;
    private VideoExtension videoExtension;

    private PostRepository postRepository;

    private List<Post> postList = new ArrayList<>();

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void checkExtensions() {
        if (!imageExtension.equals(imageExtension) && !videoExtension.equals(videoExtension)) {
            return;
        }
    }

    @Override
    public List<Post> getPostsByFeedType(String feedType, User currentUser, List<Post> allPosts) {

        if ("subscriptions".equals(feedType) && currentUser != null) {
            for (Post post : postList) {
                if (currentUser.isFollowing(post.getOwner())) {
                    postList.add(post);
                }
            }
        }
        return allPosts;
    }

    @Override
    public void createPost(Post post) {
        postList.add(post);
        Collections.sort(postList, Comparator.reverseOrder());
    }

    @Override
    public void toggleLike(long postId, User currentUser) {
        Post post = postRepository.findPostById(postId);

        if (post != null) {
            post.toggleLike(currentUser.getId());
            postRepository.update(post);
        }
    }

    @Override
    public void addComment(long postId, User author, String content) {
        Post post = postRepository.findPostById(postId);
        if (post != null) {
            post.addComment(author.getId(), author.getUsername(), content.trim());
            postRepository.update(post);
        }
    }
}
