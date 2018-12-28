package androidnews.kiloproject.util;

import android.text.TextUtils;

import androidnews.kiloproject.entity.net.ITHomeListData;

import static androidnews.kiloproject.system.AppConfig.HOST_IT_HOME_URL;

public class XmlParseUtil {
    public static ITHomeListData getITHomeListData(String xml) {
        ITHomeListData data = new ITHomeListData();
        int lastIndex = 0;
        while (xml.contains("</item>")) {
            if (lastIndex > 0){
                xml = xml.substring(lastIndex,xml.length());
            }
            ITHomeListData.ItemBean item = new ITHomeListData.ItemBean();
            item.setNewsid(getStrElement("<newsid>", "</newsid>", xml));
            item.setTitle(getStrElement("<title><![CDATA[", "]]></title>", xml));
            item.setUrl(getStrElement("<url><![CDATA[", "]]></url>", xml));
            item.setPostdate(getStrElement("<postdate>", "</postdate>", xml));
            item.setImage(getStrElement("<image>", "</image>", xml));
            item.setDescription(getStrElement("<description><![CDATA[", "]]></description>", xml));

            if (!item.getUrl().contains(HOST_IT_HOME_URL)){
                item.setUrl(HOST_IT_HOME_URL + item.getUrl());
            }

            if (!TextUtils.isEmpty(item.getNewsid()) &&
                    !TextUtils.isEmpty(item.getTitle()) &&
                    !TextUtils.isEmpty(item.getImage()))
            data.getChannel().add(item);

            lastIndex = xml.indexOf("</item>") + "</item>".length();
            if (lastIndex == -1)
                break;
        }
        return data;
    }

    private static String getStrElement(String startTag, String endTag, String xml) {
        if (xml.contains(startTag) && xml.contains(endTag)) {
            int start = xml.indexOf(startTag) + startTag.length();
            int end = xml.indexOf(endTag);
            return xml.substring(start, end);
        } else return "";
    }

    public static String getXmlElement(String tag, String xml) {
        if (xml.contains("<" + tag + ">") && xml.contains("</" + tag + ">")) {
            int start = xml.indexOf("<" + tag + ">") + ("<" + tag + ">").length();
            int end = xml.indexOf("</" + tag + ">");
            return (xml.substring(start, end));
        } else return "";
    }
}
