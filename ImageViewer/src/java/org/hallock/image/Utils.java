/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.image;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Map;
import org.hallock.images.SqlSettings;
/**
 *
 * @author thallock
 */
public class Utils {
        public static int count(String str, char c)
        {
            int count = 0;
            for (int i=0;i<str.length();i++)
                if (str.charAt(i) == c)
                    count++;
            return count;
        }
        public static int roundUp(int num, int divisor) {
            int sign = (num > 0 ? 1 : -1) * (divisor > 0 ? 1 : -1);
            return sign * (Math.abs(num) + Math.abs(divisor) - 1) / Math.abs(divisor);
        }
        
        
        
        
        
        
    public static String urlDecode(String s)
    {
        try {
            return URLDecoder.decode(s, SqlSettings.createSettings().getEncoding());
        } catch (UnsupportedEncodingException | SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }
    private static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, SqlSettings.createSettings().getEncoding());
        } catch (UnsupportedEncodingException | SQLException e) {
            throw new UnsupportedOperationException(e);
        }
    }
    public static String urlEncodeUTF8(Map<?,?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                urlEncodeUTF8(entry.getKey().toString()),
                urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();       
    }
}
