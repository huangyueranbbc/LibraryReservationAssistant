package focusresize;

import android.widget.ImageView;

/**
 * Created by Administrator on 2016/8/16.
 */
public class PhotoToLoad {
    private String url;
    private ImageView imageView;

    public PhotoToLoad(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "PhotoToLoad{" +
                "url='" + url + '\'' +
                ", imageView=" + imageView +
                '}';
    }
}
