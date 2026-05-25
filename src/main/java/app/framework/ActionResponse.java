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

    public ActionResponse(Class<?> responseClazz, List<?> responseDataList, String responseText) {
        this.responseClazz = responseClazz;
        this.responseDataList = responseDataList;
        this.responseText = responseText;
    }

    public ActionResponse(String responseText) {

        this.responseText = responseText;
    }

    public Class<?> getResponseClazz()                  {
        return responseClazz; }
    public void setResponseClazz(Class<?> responseClazz){
        this.responseClazz = responseClazz; }

    public List<?> getResponseDataList()                {
        return responseDataList; }
    public void setResponseDataList(List<?> list)       {
        this.responseDataList = list; }

    public String getResponseText()                     {
        return responseText; }
    public void setResponseText(String responseText)    {
        this.responseText = responseText; }
}