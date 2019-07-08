package androidnews.kiloproject.entity.net;

import java.util.List;

public class CnbetaCommentData {

    /**
     * code : 0
     * msg :
     * status : success
     * result : [{"tid":"15001715","pid":"0","username":"上面有人","content":"这要是华为设备出现思科代码，美苟们肯定又要高潮了","created_time":"2019-07-05 20:26:17","support":"0","against":"1"}]
     */

    private int code;
    private String msg;
    private String status;
    private List<ResultBean> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * tid : 15001715
         * pid : 0
         * username : 上面有人
         * content : 这要是华为设备出现思科代码，美苟们肯定又要高潮了
         * created_time : 2019-07-05 20:26:17
         * support : 0
         * against : 1
         */

        private String tid;
        private String pid;
        private String username;
        private String content;
        private String created_time;
        private String support;
        private String against;

        public String getTid() {
            return tid;
        }

        public void setTid(String tid) {
            this.tid = tid;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreated_time() {
            return created_time;
        }

        public void setCreated_time(String created_time) {
            this.created_time = created_time;
        }

        public String getSupport() {
            return support;
        }

        public void setSupport(String support) {
            this.support = support;
        }

        public String getAgainst() {
            return against;
        }

        public void setAgainst(String against) {
            this.against = against;
        }
    }
}
