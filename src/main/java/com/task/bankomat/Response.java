package com.task.bankomat;

import java.util.Map;

public class Response {
    private Map<String,Object> model;
    private String returnValue;

    public Response(Map<String, Object> model, String returnValue) {
        this.model = model;
        this.returnValue = returnValue;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }
}
