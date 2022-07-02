package com.example.demo;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class Api {

    @Autowired
    HttpRequest httpRequest;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value(value = "\"${name}\"")
            String contractName;
    @Value(value = "${url}")
            String url;
    @Value(value = "\"${address}\"")
            String contractAddress;
    @Value(value = "${abi}")
            String contractAbi;
    @Value(value = "\"${adminAddress}\"")
            String adminAddress;
    @Value(value = "\"${distributorAddress}\"")
            String distributorAddress;
    @Value(value = "\"${producerAddress}\"")
            String producerAddress;
    @Value(value = "\"${retailerAddress}\"")
            String retailerAddress;

    /**
     * 得到请求的数据
     * @param userAddress String 用户地址
     * @param funcName String 调用的函数
     * @param funcParams ArrayList<Object> 调用函数的参数
     * @return 包含POST请求参数的键值对 HashMap<String,Object>
     * */
    HashMap<String,Object> getParamMap(String userAddress,String funcName,ArrayList<Object> funcParams){
        HashMap<String,Object> a = new HashMap<String,Object>();
        a.put("contractName", contractName);
        a.put("contractAddress", contractAddress);
        a.put("contractAbi",contractAbi);
        a.put("funcName","\"newFood\"");
        a.put("groupId",1);
        a.put("useCns", false);
        a.put("user",userAddress);
        a.put("funcName", "\"" + funcName + "\"");
        a.put("funcParam", funcParams);

        return a;
    }

    /**
     * 查询溯源码是否存在
     * @param traceNumber int 溯源码
     * @return 布尔类型，表示是否存在
     */
    boolean queryTraceNumberExists(int traceNumber){
        ArrayList<Object> funcParams = new ArrayList<>();
        funcParams.add(traceNumber);

        HashMap<String,Object> postData = getParamMap(adminAddress,"queryTraceNumberExists",funcParams);
        return Objects.equals(httpRequest.post(url, postData), "[true]");
    }

    /**
     * 该溯源码对应的食品在各节点的信息
     * @param traceNumber int 溯源码
     * @return 包含所有的节点信息，各节点是列表格式的字符串，可以被JS解析成Array
     * @throws JSONException 在JSON解析可能的异常
     */
    ArrayList<String> getInfo(int traceNumber) throws JSONException {
        ArrayList<Object> funcParams = new ArrayList<>();
        funcParams.add(traceNumber);

        HashMap<String,Object> postData1 = getParamMap(adminAddress,"queryProduceInfo", funcParams);
        HashMap<String,Object> postData2 = getParamMap(adminAddress,"queryDistributeInfo", funcParams);
        HashMap<String,Object> postData3 = getParamMap(adminAddress,"queryRetailInfo", funcParams);

        ArrayList returnData = new ArrayList();
        System.out.println(httpRequest.post(url,postData2).substring(0,4));
        if(! httpRequest.post(url,postData1).substring(0,4).equals("连接异常")){returnData.add(httpRequest.post(url,postData1));}
        if(! httpRequest.post(url,postData2).substring(0,4).equals("连接异常")){returnData.add(httpRequest.post(url,postData2));}
        if(! httpRequest.post(url,postData3).substring(0,4).equals("连接异常")){returnData.add(httpRequest.post(url,postData3));}

        return returnData;
    }

    /**
     * 厂商调用，用于给新生产的食物录入数据
     * http://localhost:8081/newFood
     * @param name 产品名
     * @param traceNumber 溯源码
     * @param operatorName 厂商名
     * @param quality 品质
     * @return 是否成功的消息
     * @throws JSONException
     */
    @RequestMapping(value = "/newFood",method = POST)
    String newFood(@RequestParam String name,@RequestParam String traceNumber,@RequestParam String operatorName,@RequestParam String quality) throws JSONException {
        ArrayList<Object> funcParams = new ArrayList<Object>();
        funcParams.add("\"" + name + "\"");
        funcParams.add(traceNumber);
        funcParams.add("\"" + operatorName + "\"");
        funcParams.add(quality);

        HashMap<String,Object> postData = getParamMap(producerAddress,"newFood",funcParams);

        JSONObject request = new JSONObject(httpRequest.post(url, postData));
        if((boolean) request.get("statusOK")){
            return "录入成功";
        }else{
            return (String) request.get("message");
        }
    }

    /**
     * 用于给分销商拿到商品后录入数据
     * http://localhost:8081/distributeFood
     * @param traceNumber 溯源码
     * @param operatorName 分销商名称
     * @param quality 品质
     * @return 是否成功的消息
     * @throws JSONException
     */
    @RequestMapping(value = "/distributeFood",method = POST)
    String distributeFood(@RequestParam int traceNumber, String operatorName, int quality) throws JSONException {
        ArrayList<Object> funcParams = new ArrayList<>();
        funcParams.add(traceNumber);
        funcParams.add("\"" +operatorName+ "\"");
        funcParams.add(quality);

        HashMap<String, Object> postParam = getParamMap(distributorAddress,"distributeFood",funcParams);
        JSONObject request = new JSONObject(httpRequest.post(url,postParam));
        if((boolean) request.get("statusOK")){
            return "录入成功";
        }else{
            return (String) request.get("message");
        }
    }

    /**
     * 用于给零售商拿到商品后录入数据
     * http://localhost:8081/retailFood
     * @param traceNumber 溯源码
     * @param operatorName 零售商名称
     * @param quality 品质
     * @return 是否成功的消息
     * @throws JSONException
     */
    @RequestMapping(value = "/retailFood",method = POST)
    String retailFood(@RequestParam int traceNumber, String operatorName, int quality) throws JSONException {
        ArrayList<Object> funcParams = new ArrayList<Object>();
        funcParams.add(traceNumber);
        funcParams.add("\"" +operatorName+ "\"");
        funcParams.add(quality);

        HashMap<String,Object> postParam = getParamMap(retailerAddress,"retailFood",funcParams);

        JSONObject request = new JSONObject(httpRequest.post(url,postParam));
        if((boolean) request.get("statusOK")){
            return "录入成功";
        }else{
            return (String) request.get("message");
        }
    }

    /**
     * 用于查询某个溯源码对应的商品的消息
     * http://localhost:8081/queryFoodTraceInfo
     * @param traceNumber 溯源码
     * @return 包含商品各节点消息的列表格式的字符串或错误信息
     * @throws JSONException
     */
    @RequestMapping(value = "/queryFoodTraceInfo",method = GET)
    String getTraceInfo(@RequestParam int traceNumber) throws JSONException {
        if(queryTraceNumberExists(traceNumber)){
            return getInfo(traceNumber).toString();
        }
        return "溯源码不存在！！";
    }

    /**
     * 返回溯源码是否存在
     * http://localhost:8081/queryFoodTraceExists
     * @param traceNumber 溯源码
     * @return 布尔类型，是否存在
     * @throws JSONException
     */
    @RequestMapping(value = "/queryFoodTraceExists",method = GET)
    boolean getTrace(@RequestParam int traceNumber) throws JSONException {
        return queryTraceNumberExists(traceNumber);
    }

    /**
     * 用户登入
     * @param user
     * @param password
     * @return 登入结果
     */
    @RequestMapping(value = "/login", method = POST)
    boolean login(@RequestParam String user,@RequestParam String password){
        List<Map<String, Object>> isExists = jdbcTemplate.queryForList(String.format("SELECT COUNT(*) as len from userPass WHERE user = '%s'", user));
        if((Long) isExists.get(0).get("len") == 0){
            return false;
        }else{
            List<Map<String, Object>> query = jdbcTemplate.queryForList(String.format("SELECT `password` from userPass WHERE user = '%s'", user));
            return query.get(0).get("password").equals(password);
        }
    }

    /**
     * 用户注册
     * @param user
     * @param password
     * @param type
     * @return 注册结果
     */
    @RequestMapping(value = "/logon",method = POST)
    boolean logon(@RequestParam String user,@RequestParam String password,@RequestParam String type){
        List<Map<String, Object>> isExists = jdbcTemplate.queryForList(String.format("SELECT COUNT('password') as len from userPass WHERE user = '%s'", user));
        if((Long) isExists.get(0).get("len") != 0) {
            return false;
        }else {
            jdbcTemplate.execute(
                    String.format("INSERT INTO userPass(user,password,userType) VALUES('%s','%s','%s')", user,password, type)
            );
            return true;
        }
    }

    /**
     * 用于返回该用户的类型
     * @param user 用户名
     * @return 用户类型
     */
    @RequestMapping(value = "/userType",method = POST)
    String logon(@RequestParam String user){
        System.out.println(String.format("SELECT COUNT(*) as len from userPass WHERE user = '%s'", user));
        List<Map<String, Object>> isExists = jdbcTemplate.queryForList(String.format("SELECT COUNT(*) as len from userPass WHERE user = '%s'", user));
        if((Long) isExists.get(0).get("len") == 0) {
            return "用户未注册";
        }else {
            List<Map<String, Object>> query = jdbcTemplate.queryForList(String.format("SELECT `userType` from userPass WHERE user = '%s'", user));
            return (String) query.get(0).get("userType");
        }
    }

}
