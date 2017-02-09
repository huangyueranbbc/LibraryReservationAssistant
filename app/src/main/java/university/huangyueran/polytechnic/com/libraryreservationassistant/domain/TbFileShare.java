package university.huangyueran.polytechnic.com.libraryreservationassistant.domain;

import java.io.Serializable;
import java.util.Date;

public class TbFileShare implements Serializable {
    private Long id;

    private String title;

    private String fielPath;

    private String downloadUrl;

    private Long size;

    private String fileDesc;

    private Integer laudCount;

    private Integer favoriteCount;

    private Long userId;

    private Date created;

    private Integer fileType;

    private Long downloads;

    private String hobbyId;

    private String authorName;

    private String realName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getFielPath() {
        return fielPath;
    }

    public void setFielPath(String fielPath) {
        this.fielPath = fielPath == null ? null : fielPath.trim();
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl == null ? null : downloadUrl.trim();
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc) {
        this.fileDesc = fileDesc == null ? null : fileDesc.trim();
    }

    public Integer getLaudCount() {
        return laudCount;
    }

    public void setLaudCount(Integer laudCount) {
        this.laudCount = laudCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public Long getDownloads() {
        return downloads;
    }

    public void setDownloads(Long downloads) {
        this.downloads = downloads;
    }

    public String getHobbyId() {
        return hobbyId;
    }

    public void setHobbyId(String hobbyId) {
        this.hobbyId = hobbyId == null ? null : hobbyId.trim();
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName == null ? null : authorName.trim();
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName == null ? null : realName.trim();
    }

    @Override
    public String toString() {
        return "TbFileShare{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", fielPath='" + fielPath + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", size=" + size +
                ", fileDesc='" + fileDesc + '\'' +
                ", laudCount=" + laudCount +
                ", favoriteCount=" + favoriteCount +
                ", userId=" + userId +
                ", created=" + created +
                ", fileType=" + fileType +
                ", downloads=" + downloads +
                ", hobbyId='" + hobbyId + '\'' +
                ", authorName='" + authorName + '\'' +
                ", realName='" + realName + '\'' +
                '}';
    }
}