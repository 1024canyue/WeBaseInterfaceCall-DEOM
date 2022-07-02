package com.example.demo;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * 自己写的用来替简单代PythonRequest库
* @author 20软件技术林泰圣
* @version 1.0
*/

@Service
public class HttpRequest {
    /**
    * 用于发送POST请求
    * @param url String 请求Url
    * @param params HashMap<String,Object> 请求时发送的参数
    * @return 以字符串形式返回请求的返回
    * */
    public String post(String url, HashMap<String,Object> params){
        try{
            URL postUrl = new URL(url);        //创建URL实例，后面都依赖它
            URLConnection connection = postUrl.openConnection();
            connection.setDoOutput(true);      //使用POST方法
            connection.setRequestProperty("Content-Type","application/json");
            OutputStream out = connection.getOutputStream();
            OutputStream buff = new BufferedOutputStream(out);
            OutputStreamWriter write = new OutputStreamWriter(buff);
            /*
            * 将HashMap转成类JSON字符串
            * 结果类似于 "key1":12,"key2":"22"
            * */
            String paramStr = params.entrySet().stream().map(a -> "\"" + a.getKey() + "\"" + ":" + a.getValue()).reduce((a,b) -> a +","+ b).get();
            System.out.println(paramStr);
            write.write("{" + paramStr + "}");
            write.flush();
            write.close();

            try(InputStream in = connection.getInputStream()){
                InputStream inBuffer = new BufferedInputStream(in);
                Reader reader = new InputStreamReader(inBuffer);
                int cache;
                String requestStr  = "";
                while ((cache = reader.read()) != -1){
                    requestStr += (char) cache;    //int => char 不然都是数字
                }
                return requestStr;
            }catch (Exception connEx){
                return "连接异常" + connEx.getMessage();
            }
        }catch (Exception ex){
            return  "连接异常" + ex.getMessage();
        }
    }
}
