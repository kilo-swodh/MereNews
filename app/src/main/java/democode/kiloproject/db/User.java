//package democode.kiloproject.sqlite;
//
//import org.litepal.annotation.Encrypt;
//import org.litepal.crud.DataSupport;
//
///**
// * ListPal加密相关
// *
// * 1. 调用 LitePal.aesKey() 可以自行设置AES机密密钥
// * 2. 加密后的字段无法被检索
// * 3. 加密字段仅限 String 类型
// */
//
//
//
//public class User extends DataSupport {
//
//
//    /**
//     *MD5加密
//     */
//
//    @Encrypt(algorithm = MD5)
//    private String password;
//
//    /**
//     * AES加密
//     */
//    @Encrypt(algorithm = AES)
//    private String name;
//
//
//    //本字段不加密
//    private String talk;
//
//
//}