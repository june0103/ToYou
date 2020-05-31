package rhwns0103.com.naver.blog.toyou.Item;

public class GalleryData {

    private String UserName, profileImageUrl, img;

    public String getUserName() {
        return UserName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
