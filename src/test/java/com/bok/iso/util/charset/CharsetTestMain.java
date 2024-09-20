package com.bok.iso.util.charset;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

public class CharsetTestMain {

    public static void main(String [] args) throws UnsupportedEncodingException {
        String inputStr = "더샾아파트";
        byte [] testBytes = null;
        
        testBytes = inputStr.getBytes("cp949c");
        System.out.println(printHexaStr(testBytes));

        /*
        Map<String, Charset> map = Charset.availableCharsets();
        for (String key : map.keySet()) {
            try {
                testBytes = inputStr.getBytes(key);
                System.out.println(key+"|"+printHexaStr(testBytes));
                
            } catch (Exception uee) {
                System.out.println(key+"|"+uee.getMessage());
                continue;
            }
        } */
    }

    public static String printHexaStr(byte [] input) {
        StringBuilder result = new StringBuilder();
        for (byte b : input) {
            result.append(String.format("%02X|", b));
        }
        String hexStr = result.toString();
        return hexStr;
    }

}
