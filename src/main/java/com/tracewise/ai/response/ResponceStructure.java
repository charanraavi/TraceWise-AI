package com.tracewise.ai.response;

public class ResponceStructure<T> {

    private int status;
    private String message;
    private T data;

    public ResponceStructure(int status, String message, T data){
        this.status=status;
        this.message=message;
        this.data =data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
