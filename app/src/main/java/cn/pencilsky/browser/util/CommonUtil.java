package cn.pencilsky.browser.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenlin on 21/06/2017.
 */
public class CommonUtil {
    final private static HashMap<String, String> mimeMap = new HashMap<String, String>() {{
        put("application/vnd.android.package-archive", "apk");
        put("video/3gpp", "3gp");
        put("application/postscript", "ai");
        put("audio/x-aiff", "aif");
        put("audio/x-aiff", "aifc");
        put("audio/x-aiff", "aiff");
        put("text/plain", "asc");
        put("application/atom+xml", "atom");
        put("audio/basic", "au");
        put("video/x-msvideo", "avi");
        put("application/x-bcpio", "bcpio");
        put("application/octet-stream", "bin");
        put("image/bmp", "bmp");
        put("application/x-netcdf", "cdf");
        put("image/cgm", "cgm");
        put("application/octet-stream", "class");
        put("application/x-cpio", "cpio");
        put("application/mac-compactpro", "cpt");
        put("application/x-csh", "csh");
        put("text/css", "css");
        put("application/x-director", "dcr");
        put("video/x-dv", "dif");
        put("application/x-director", "dir");
        put("image/vnd.djvu", "djv");
        put("image/vnd.djvu", "djvu");
        put("application/octet-stream", "dll");
        put("application/octet-stream", "dmg");
        put("application/octet-stream", "dms");
        put("application/msword", "doc");
        put("application/xml-dtd", "dtd");
        put("video/x-dv", "dv");
        put("application/x-dvi", "dvi");
        put("application/x-director", "dxr");
        put("application/postscript", "eps");
        put("text/x-setext", "etx");
        put("application/octet-stream", "exe");
        put("application/andrew-inset", "ez");
        put("video/x-flv", "flv");
        put("image/gif", "gif");
        put("application/srgs", "gram");
        put("application/srgs+xml", "grxml");
        put("application/x-gtar", "gtar");
        put("application/x-gzip", "gz");
        put("application/x-hdf", "hdf");
        put("application/mac-binhex40", "hqx");
        put("text/html", "htm");
        put("text/html", "html");
        put("x-conference/x-cooltalk", "ice");
        put("image/x-icon", "ico");
        put("text/calendar", "ics");
        put("image/ief", "ief");
        put("text/calendar", "ifb");
        put("model/iges", "iges");
        put("model/iges", "igs");
        put("application/x-java-jnlp-file", "jnlp");
        put("image/jp2", "jp2");
        put("image/jpeg", "jpe");
        put("image/jpeg", "jpeg");
        put("image/jpeg", "jpg");
        put("application/x-javascript", "js");
        put("audio/midi", "kar");
        put("application/x-latex", "latex");
        put("application/octet-stream", "lha");
        put("application/octet-stream", "lzh");
        put("audio/x-mpegurl", "m3u");
        put("audio/mp4a-latm", "m4a");
        put("audio/mp4a-latm", "m4p");
        put("video/vnd.mpegurl", "m4u");
        put("video/x-m4v", "m4v");
        put("image/x-macpaint", "mac");
        put("application/x-troff-man", "man");
        put("application/mathml+xml", "mathml");
        put("application/x-troff-me", "me");
        put("model/mesh", "mesh");
        put("audio/midi", "mid");
        put("audio/midi", "midi");
        put("application/vnd.mif", "mif");
        put("video/quicktime", "mov");
        put("video/x-sgi-movie", "movie");
        put("audio/mpeg", "mp2");
        put("audio/mpeg", "mp3");
        put("video/mp4", "mp4");
        put("video/mpeg", "mpe");
        put("video/mpeg", "mpeg");
        put("video/mpeg", "mpg");
        put("audio/mpeg", "mpga");
        put("application/x-troff-ms", "ms");
        put("model/mesh", "msh");
        put("video/vnd.mpegurl", "mxu");
        put("application/x-netcdf", "nc");
        put("application/oda", "oda");
        put("application/ogg", "ogg");
        put("video/ogv", "ogv");
        put("image/x-portable-bitmap", "pbm");
        put("image/pict", "pct");
        put("chemical/x-pdb", "pdb");
        put("application/pdf", "pdf");
        put("image/x-portable-graymap", "pgm");
        put("application/x-chess-pgn", "pgn");
        put("image/pict", "pic");
        put("image/pict", "pict");
        put("image/png", "png");
        put("image/x-portable-anymap", "pnm");
        put("image/x-macpaint", "pnt");
        put("image/x-macpaint", "pntg");
        put("image/x-portable-pixmap", "ppm");
        put("application/vnd.ms-powerpoint", "ppt");
        put("application/postscript", "ps");
        put("video/quicktime", "qt");
        put("image/x-quicktime", "qti");
        put("image/x-quicktime", "qtif");
        put("audio/x-pn-realaudio", "ra");
        put("audio/x-pn-realaudio", "ram");
        put("image/x-cmu-raster", "ras");
        put("application/rdf+xml", "rdf");
        put("image/x-rgb", "rgb");
        put("application/vnd.rn-realmedia", "rm");
        put("application/x-troff", "roff");
        put("text/rtf", "rtf");
        put("text/richtext", "rtx");
        put("text/sgml", "sgm");
        put("text/sgml", "sgml");
        put("application/x-sh", "sh");
        put("application/x-shar", "shar");
        put("model/mesh", "silo");
        put("application/x-stuffit", "sit");
        put("application/x-koan", "skd");
        put("application/x-koan", "skm");
        put("application/x-koan", "skp");
        put("application/x-koan", "skt");
        put("application/smil", "smi");
        put("application/smil", "smil");
        put("audio/basic", "snd");
        put("application/octet-stream", "so");
        put("application/x-futuresplash", "spl");
        put("application/x-wais-source", "src");
        put("application/x-sv4cpio", "sv4cpio");
        put("application/x-sv4crc", "sv4crc");
        put("image/svg+xml", "svg");
        put("application/x-shockwave-flash", "swf");
        put("application/x-troff", "t");
        put("application/x-tar", "tar");
        put("application/x-tcl", "tcl");
        put("application/x-tex", "tex");
        put("application/x-texinfo", "texi");
        put("application/x-texinfo", "texinfo");
        put("image/tiff", "tif");
        put("image/tiff", "tiff");
        put("application/x-troff", "tr");
        put("text/tab-separated-values", "tsv");
        put("text/plain", "txt");
        put("application/x-ustar", "ustar");
        put("application/x-cdlink", "vcd");
        put("model/vrml", "vrml");
        put("application/voicexml+xml", "vxml");
        put("audio/x-wav", "wav");
        put("image/vnd.wap.wbmp", "wbmp");
        put("application/vnd.wap.wbxml", "wbxml");
        put("video/webm", "webm");
        put("text/vnd.wap.wml", "wml");
        put("application/vnd.wap.wmlc", "wmlc");
        put("text/vnd.wap.wmlscript", "wmls");
        put("application/vnd.wap.wmlscriptc", "wmlsc");
        put("video/x-ms-wmv", "wmv");
        put("model/vrml", "wrl");
        put("image/x-xbitmap", "xbm");
        put("application/xhtml+xml", "xht");
        put("application/xhtml+xml", "xhtml");
        put("application/vnd.ms-excel", "xls");
        put("application/xml", "xml");
        put("image/x-xpixmap", "xpm");
        put("application/xml", "xsl");
        put("application/xslt+xml", "xslt");
        put("application/vnd.mozilla.xul+xml", "xul");
        put("image/x-xwindowdump", "xwd");
        put("chemical/x-xyz", "xyz");
        put("application/zip", "zip");
    }};

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

    /**
     * 根据mimetyoe为文件名添加扩展
     * 如果filename本身包含拓展名，则返回filename本身
     * 如果mimetype没有找到相应的拓展名，则返回filename本身
     * @param mimetype mime类型
     * @return
     */
    public static String addExtendName(String filename, String mimetype) {
        if (filename.contains(".")) {
            return filename;
        }

        String res = mimeMap.get(mimetype);
        if (res != null) {
            filename += "." + res;
        }
        return filename;
    }

}
