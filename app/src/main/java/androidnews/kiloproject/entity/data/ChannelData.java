package androidnews.kiloproject.entity.data;

import java.util.ArrayList;

public class ChannelData {

  /** 其它栏目列表 */
  ArrayList<ChannelItem> otherChannelList = new ArrayList<ChannelItem>();
  /** 用户栏目列表 */
  ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();

  public ArrayList<ChannelItem> getOtherChannelList() {
    return otherChannelList;
  }

  public void setOtherChannelList(ArrayList<ChannelItem> otherChannelList) {
    this.otherChannelList = otherChannelList;
  }

  public ArrayList<ChannelItem> getUserChannelList() {
    return userChannelList;
  }

  public void setUserChannelList(ArrayList<ChannelItem> userChannelList) {
    this.userChannelList = userChannelList;
  }
}
