package university.huangyueran.polytechnic.com.libraryreservationassistant.domain;

import java.io.Serializable;

/**
 * Created by huangyueran on 2017/2/7.
 * 图片上传结果pojo
 */
public class PicUploadResult implements Serializable {

    private String error; // 状态码 0正常 1错误
    private String message; // 错误消息
    private String url; // 上传成功的图片url

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "PicUploadResult{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
