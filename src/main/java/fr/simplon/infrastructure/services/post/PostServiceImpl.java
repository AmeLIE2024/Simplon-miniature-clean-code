package fr.simplon.infrastructure.services.post;

import java.util.ArrayList;
import java.util.List;

import fr.simplon.domain.models.Post;
import fr.simplon.domain.models.User;
import fr.simplon.domain.gateway.PostService;
import fr.simplon.domain.models.ImageExtension;
import fr.simplon.domain.models.VideoExtension;

public class PostServiceImpl implements PostService {

    private ImageExtension imageExtension;
    private VideoExtension videoExtension;

    private List<Post> postList = new ArrayList<>();

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
}
