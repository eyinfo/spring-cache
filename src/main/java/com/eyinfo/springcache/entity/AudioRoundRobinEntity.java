package com.eyinfo.springcache.entity;

import java.io.Serializable;

public class AudioRoundRobinEntity implements Serializable {

    //缓存id
    private String id;

    //来源数据id
    private String targetId;

    //生成内容
    private String content;

    //数据类型
    //0-诗词内容;1-诗词解说(译文+赏析);2-诗词市场营销音频;
    private int type;

    //生成音频文件语言格式
    private String speechStyle;

    //oss文件对象路径
    private String objectName;

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

    public String getSpeechStyle() {
        return speechStyle;
    }

    public void setSpeechStyle(String speechStyle) {
        this.speechStyle = speechStyle;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
