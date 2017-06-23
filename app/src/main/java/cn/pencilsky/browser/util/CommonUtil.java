package cn.pencilsky.browser.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenlin on 21/06/2017.
 */
public class CommonUtil {
    /**
     * 从url中获取文件名
     * @param url
     * @return 若url中不存在文件名，则返回一个随机md5序列作为文件名
     */
    public static String getFileNameFromUrl(String url) {
        int index = url.lastIndexOf('/');
        if (index == -1) return null;

        url = url.substring(index + 1);

        String reg = "(.+)\\.([a-z]+)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(new Date().toString().getBytes());
            return new String(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
