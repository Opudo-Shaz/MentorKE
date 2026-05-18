package app.framework;

import java.io.Serializable;
import java.util.List;

public class ActionResponse implements Serializable {

    private Class<?> responseClazz;
    private List<?> responseDataList;
    private String responseText;

    // Use this when you want the framework to auto-generate an HTML table
    public ActionResponse(Class<?> responseClazz, List<?> responseDataList) {
        this.responseClazz = responseClazz;
        this.responseDataList = responseDataList;
    }

    // Use this when you have both a data list AND custom HTML to show
    public ActionResponse(Class<?> responseClazz, List<?> responseDataList, String responseText) {
        this.responseClazz = responseClazz;
        this.responseDataList = responseDataList;
        this.responseText = responseText;
    }

    // Use this when you are returning fully custom HTML (forms, dashboards, etc.)
    public ActionResponse(String responseText) {
        this.responseText = responseText;
    }

    public Class<?> getResponseClazz()                  { return responseClazz; }
    public void setResponseClazz(Class<?> responseClazz){ this.responseClazz = responseClazz; }

    public List<?> getResponseDataList()                { return responseDataList; }
    public void setResponseDataList(List<?> list)       { this.responseDataList = list; }

    public String getResponseText()                     { return responseText; }
    public void setResponseText(String responseText)    { this.responseText = responseText; }
}