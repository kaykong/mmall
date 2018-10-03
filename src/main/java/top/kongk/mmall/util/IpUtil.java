package top.kongk.mmall.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述：
 *
 * @author kk
 * @date 2018/10/2 19:12
 */
public class IpUtil {
    /** 
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址; 
     *  
     * @param request 
     * @return 
     * @throws IOException 
     */
    public  static String getIpAddress(HttpServletRequest request)  {

        String ip = request.getHeader("X-Forwarded-For");

        String unknown = "unknown";
        int ipLength = 15;

        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }

            if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }

            if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }

            if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } else if (ip.length() > ipLength) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }
}
