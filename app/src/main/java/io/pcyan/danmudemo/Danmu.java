package io.pcyan.danmudemo;

/**
 * 弹幕实体类
 */
public class Danmu {
    public long   id;
    public int    userId;
    public String type;
    public int    avatarUrl;
    public String content;

    public String imgUrl;

    public Danmu(long id, int userId, String type, String imgUrl,String content) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.imgUrl = imgUrl;
    }

    public Danmu(long id, int userId, String type, int avatarUrl, String content) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.avatarUrl = avatarUrl;
        this.content = content;
    }

    public Danmu() {
    }
}
