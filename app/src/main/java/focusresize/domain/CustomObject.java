package focusresize.domain;

/**
 * Created by huangyueran on 2017/2/5.
 */
public class CustomObject {
    private String author; //作者
    private String posttime; // 发表
    private String replys; // 回复数
    private String content; // 主题内容
    private String url; // 图片url

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPosttime() {
        return posttime;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }

    public String getReplys() {
        return replys;
    }

    public void setReplys(String replys) {
        this.replys = replys;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "CustomObject{" +
                "author='" + author + '\'' +
                ", posttime='" + posttime + '\'' +
                ", replys='" + replys + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
