package com.eyinfo.springcache.entity;

import java.io.Serializable;

public class HtmlGenerateDataEntity implements Serializable {

    //缓存id
    private String id;

    //来源数据id
    private String targetId;

    //标题
    private String title;

    //生成内容
    private String content;

    //数据类型
    //0-作者人物生平;1-作者背景;2-作者历史价值;3-作者成就;4-作者贡献;5-作者分析;6-诗词注解;7-诗词译文;诗词赏析;诗词写作背景;
    private int type;

    //oss文件对象路径
    private String objectName;

    //字拼音
    private String words;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }
}
