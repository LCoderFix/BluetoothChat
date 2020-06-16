package com.volantgoat.bluetoothchat.bean;

/**
 * Create by dong
 * Date on 2020/6/10  20:51
 */
public class MessageEvent {
    int MSG_TYPE;
    Class aClass;
    String MSG_CONTENT;

    public MessageEvent(Class aClass,int MSG_TYPE){
        this(aClass,MSG_TYPE,null);
    }
    public MessageEvent( Class aClass,int MSG_TYPE,String MSG_CONTENT) {
        this.MSG_TYPE = MSG_TYPE;
        this.aClass = aClass;
        this.MSG_CONTENT=MSG_CONTENT;
    }

    public int getMSG_TYPE() {
        return MSG_TYPE;
    }

    public void setMSG_TYPE(int MSG_TYPE) {
        this.MSG_TYPE = MSG_TYPE;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }

    public String getMSG_CONTENT() {
        return MSG_CONTENT;
    }

    public void setMSG_CONTENT(String MSG_CONTENT) {
        this.MSG_CONTENT = MSG_CONTENT;
    }
}
