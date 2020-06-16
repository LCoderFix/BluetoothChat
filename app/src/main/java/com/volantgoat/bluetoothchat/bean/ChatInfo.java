package com.volantgoat.bluetoothchat.bean;

/**
 * Create by dong
 * Data:2019/12/11
 */
public class ChatInfo {
    public static final int TAG_Text_LEFT = 0;
    public static final int TAG_Text_RIGHT = 1;
    public static final int TAG_IMG_LEFT=2;
    public static final int TAG_IMG_RIGHT=3;
    private int tag;
    private String name; //名称
    private String content; //内容
    private String imgUrl;

    public ChatInfo(int tag, String name, String content) {
        this.tag = tag;
        this.name = name;
        this.content = content;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
