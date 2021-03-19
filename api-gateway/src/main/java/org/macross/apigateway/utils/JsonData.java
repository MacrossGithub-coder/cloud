package org.macross.apigateway.utils;

public class JsonData {

    private Integer code;

    private Object data;

    private String msg;

    public JsonData(){}

    public JsonData(Integer code,Object data,String msg){
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static JsonData buildSuccess(){
        return new JsonData(0,null,null);
    }

    public static JsonData buildSuccess(Object data){
        return  new JsonData(0,data,null);
    }

    public static JsonData buildError(String msg){
        return new JsonData(-1,null,msg);
    }

    public static JsonData buildError(Integer code,String msg){
        return new JsonData(code,null,msg);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
