package org.macross.AppleStore_Common_Config.exception;

public class RunTimeException extends RuntimeException {

    private Integer code;

    private String msg;

    public  RunTimeException(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
