package in.reweyou.reweyouforums.model;

/**
 * Created by master on 24/2/17.
 */

public class IssueModel {
    private String topicid;
    private String description;
    private String created_by;
    private String created_on;
    private String reviews;
    private String category;
    private String name;
    private String image;
    private String video;
    private String gif;
    private String rating;
    private String headline;
    private String status;
    private String privacy;
    private String passcode;

    public String getPasscode() {
        return passcode;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {

        return status;
    }

    public String getCategory() {
        return category;
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getDescription() {
        return description;
    }

    public String getGif() {
        return gif;
    }

    public String getImage() {
        return image;
    }

    public String getRating() {
        return rating;
    }

    public String getReviews() {

        return reviews;
    }

    public String getTopicid() {
        return topicid;
    }

    public String getVideo() {
        return video;
    }

    public String getHeadline() {
        return headline;
    }
}
