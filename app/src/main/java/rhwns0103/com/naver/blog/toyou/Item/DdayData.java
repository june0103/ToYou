package rhwns0103.com.naver.blog.toyou.Item;

public class DdayData {

    private String tv_dday_100, tv_dday_ymd;

    public DdayData(String tv_dday_100, String tv_dday_ymd) {
        this.tv_dday_100 = tv_dday_100;
        this.tv_dday_ymd = tv_dday_ymd;
    }

    public String getTv_dday_100() {
        return tv_dday_100;
    }

    public void setTv_dday_100(String tv_dday_100) {
        this.tv_dday_100 = tv_dday_100;
    }

    public String getTv_dday_ymd() {
        return tv_dday_ymd;
    }

    public void setTv_dday_ymd(String tv_dday_ymd) {
        this.tv_dday_ymd = tv_dday_ymd;
    }

    @Override
    public String toString() {
        return "Post_dday{" +
                "tv_dday_100='" + tv_dday_100 + '\'' +
                ", tv_dday_ymd='" + tv_dday_ymd + '\'' +
                '}';
    }
}
