package androidnews.kiloproject.entity.net;

public class UpdateInfoData {

    /**
     * message : 测试版下载地址
     * is_force : false
     * version_code : 1206
     * version_name : 1.2.5-beta3
     * address : https://www.lanzous.com/b00t809ah
     */

    private String message;
    private boolean is_force;
    private int version_code;
    private String version_name;
    private String address;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIs_force() {
        return is_force;
    }

    public void setIs_force(boolean is_force) {
        this.is_force = is_force;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
