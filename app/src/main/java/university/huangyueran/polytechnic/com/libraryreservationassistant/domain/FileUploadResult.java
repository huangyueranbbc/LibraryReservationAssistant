package university.huangyueran.polytechnic.com.libraryreservationassistant.domain;

import java.io.Serializable;

/**
 * Created by huangyueran on 2017/2/7. 图片上传结果pojo
 */
public class FileUploadResult implements Serializable {

	private String error; // 状态码 0正常 1错误
	private String message; // 错误消息
	private String path; // 上传成功的文件路径
	private String realName; // 真实文件名
	private Long filesize; // 文件大小

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Long getFilesize() {
		return filesize;
	}

	public void setFilesize(Long filesize) {
		this.filesize = filesize;
	}

	@Override
	public String toString() {
		return "FileUploadResult [error=" + error + ", message=" + message + ", path=" + path + ", realName=" + realName
				+ ", filesize=" + filesize + "]";
	}

}
