package com.htroy.cmccportcheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NetworkChecker {
    public static int isError651(){
        int connect = 0;
        Runtime runtime = Runtime.getRuntime();
        Process process;
        try {
            process = runtime.exec("rasdial aaa aaa aaa");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "gbk");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            isr.close();
            br.close();

            if (null != sb && !sb.toString().equals("")) {
                String logString = "";
                if (sb.toString().indexOf("651") > 0) {
                    connect = 2;
                } else {
                    connect = 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connect;
    }
}
