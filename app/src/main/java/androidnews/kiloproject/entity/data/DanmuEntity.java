package androidnews.kiloproject.entity.data;

import com.orzangleli.xdanmuku.Model;

public class DanmuEntity extends Model {
    public String content;
    public String userName;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}