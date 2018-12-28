package androidnews.kiloproject.entity.data;

import com.chad.library.adapter.base.entity.MultiItemEntity;


public class CommentLevel implements MultiItemEntity {

  private String imgUrl;
  private String name;
  private String text;
  private String time;
  private int type;

  public CommentLevel(String imgUrl, String name, String text, String time, int type) {
    this.imgUrl = imgUrl;
    this.name = name;
    this.text = text;
    this.time = time;
    this.type = type;
  }

  @Override
  public int getItemType() {
    return type;
  }

  public String getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }
}