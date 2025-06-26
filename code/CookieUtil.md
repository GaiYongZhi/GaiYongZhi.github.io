```java
import cn.hutool.core.util.StrUtil;
import net.cnki.standard.uaa.web.constant.exception.BizException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 操作cookie
 * @description 之所以对cookie进行加密,是因为cookie含有 空格等特殊字符会取不到
 */
public class CookieUtil {

    /**
     * 设置cookie base64加密
     */
    public static void setCookieWithEncode(String cookieName, String cookieValue, Long maxAge) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        cookieValue = new String(Base64.getEncoder().encode(cookieValue.getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8);
        ResponseCookie cookie = ResponseCookie.from(cookieName, cookieValue)
                .path("/") // 设置路径
                .maxAge(maxAge) // 设置有效期（秒）
                .httpOnly(false) // 设置HttpOnly标志
                .secure(false) // 设置Secure标志（仅在HTTPS请求中发送）
                .build();
        String cookieStr = StrUtil.utf8Str(cookie);
        response.addHeader(HttpHeaders.SET_COOKIE,cookieStr);
    }

    public static void removeCookie(String cookieName){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        ResponseCookie cookie = ResponseCookie.from(cookieName, null)
                .path("/") // 设置路径
                .maxAge(0) // 设置有效期（秒）
                .httpOnly(false) // 设置HttpOnly标志
                .secure(false) // 设置Secure标志（仅在HTTPS请求中发送）
                .build();
        String cookieStr = StrUtil.utf8Str(cookie);
        HttpServletResponse response = servletRequestAttributes.getResponse();
        response.addHeader(HttpHeaders.SET_COOKIE,cookieStr);
    }

    /**
     * 查找特定cookie值()
     * @param cookieName
     * @param request
     * @return
     */
    public static String getCookie(String cookieName, ServerHttpRequest request) {
        // 获取请求中的所有cookie
        MultiValueMap<String, org.springframework.http.HttpCookie> cookies = request.getCookies();
        // 获取指定名称的cookie
        org.springframework.http.HttpCookie cookie = cookies.getFirst(cookieName);
        // 如果找到了指定名称的cookie，则返回其值
        if (cookie != null) {
            return cookie.getValue();
        }
        // 如果没有找到指定名称的cookie，则返回空字符串
        return "";
    }

    /**
     * 查找特定cookie值 ,并进行Base64解密
     * @param cookieName
     * @return
     */
    public static String getCookieWithDecode(String cookieName) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Cookie[] cookies = request.getCookies();
        Cookie cookie = null;
        for (Cookie cookieItem : cookies) {
            if (cookieName.equals(cookieItem.getName())){
                cookie = cookieItem;
                break;
            }
        }
        if (cookie != null) {
            try {
                byte[] decodeCookie = Base64.getDecoder().decode(cookie.getValue().getBytes(StandardCharsets.UTF_8));
                return new String(decodeCookie, StandardCharsets.UTF_8);
            }catch (Exception e){
                throw new BizException("token解码异常");
            }
        }
        throw new BizException("未找到对应token");
    }
}

```
