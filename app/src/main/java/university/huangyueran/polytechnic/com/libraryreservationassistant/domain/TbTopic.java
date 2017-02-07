package university.huangyueran.polytechnic.com.libraryreservationassistant.domain;

import java.io.Serializable;
import java.util.Date;

public class TbTopic implements Serializable {
    private Long id;

    private String content;

    private Long authorId;

    private String authorName;

    private Integer laudCount;

    private Integer replyCount;

    private Long lastreplyId;

    private String ipaddr;

    private Integer isdel;

    private Long hobbyId;

    private String hobbyName;

    private Date created;

    private Date updated;

    private String topicPic;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName == null ? null : authorName.trim();
    }

    public Integer getLaudCount() {
        return laudCount;
    }

    public void setLaudCount(Integer laudCount) {
        this.laudCount = laudCount;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public Long getLastreplyId() {
        return lastreplyId;
    }

    public void setLastreplyId(Long lastreplyId) {
        this.lastreplyId = lastreplyId;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr == null ? null : ipaddr.trim();
    }

    public Integer getIsdel() {
        return isdel;
    }

    public void setIsdel(Integer isdel) {
        this.isdel = isdel;
    }

    public Long getHobbyId() {
        return hobbyId;
    }

    public void setHobbyId(Long hobbyId) {
        this.hobbyId = hobbyId;
    }

    public String getHobbyName() {
        return hobbyName;
    }

    public void setHobbyName(String hobbyName) {
        this.hobbyName = hobbyName == null ? null : hobbyName.trim();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getTopicPic() {
        return topicPic;
    }

    public void setTopicPic(String topicPic) {
        this.topicPic = topicPic == null ? null : topicPic.trim();
    }

    @Override
    public String toString() {
        return "TbTopic{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", laudCount=" + laudCount +
                ", replyCount=" + replyCount +
                ", lastreplyId=" + lastreplyId +
                ", ipaddr='" + ipaddr + '\'' +
                ", isdel=" + isdel +
                ", hobbyId=" + hobbyId +
                ", hobbyName='" + hobbyName + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", topicPic='" + topicPic + '\'' +
                '}';
    }
}