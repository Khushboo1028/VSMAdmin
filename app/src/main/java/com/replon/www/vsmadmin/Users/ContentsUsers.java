package com.replon.www.vsmadmin.Users;

import android.os.Parcel;
import android.os.Parcelable;

public class ContentsUsers implements Parcelable {

    private String phone;
    private String name;
    private String gst_no;
    private String company_name;
    private String city;
    private String state;
    private Boolean current_status;
    private String date_created;
    private String document_id;

    public ContentsUsers(String phone, String name, String gst_no, String company_name, String city,String state, Boolean current_status, String date_created, String document_id) {
        this.phone = phone;
        this.name = name;
        this.gst_no = gst_no;
        this.company_name = company_name;
        this.city = city;
        this.state=state;
        this.current_status = current_status;
        this.date_created = date_created;
        this.document_id = document_id;
    }


    protected ContentsUsers(Parcel in) {
        phone = in.readString();
        name = in.readString();
        gst_no = in.readString();
        company_name = in.readString();
        city = in.readString();
        state=in.readString();
        byte tmpCurrent_status = in.readByte();
        current_status = tmpCurrent_status == 0 ? null : tmpCurrent_status == 1;
        date_created = in.readString();
        document_id = in.readString();
    }

    public static final Creator<ContentsUsers> CREATOR = new Creator<ContentsUsers>() {
        @Override
        public ContentsUsers createFromParcel(Parcel in) {
            return new ContentsUsers(in);
        }

        @Override
        public ContentsUsers[] newArray(int size) {
            return new ContentsUsers[size];
        }
    };

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGst_no() {
        return gst_no;
    }

    public void setGst_no(String gst_no) {
        this.gst_no = gst_no;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getCurrent_status() {
        return current_status;
    }

    public void setCurrent_status(Boolean current_status) {
        this.current_status = current_status;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(phone);
        parcel.writeString(name);
        parcel.writeString(gst_no);
        parcel.writeString(company_name);
        parcel.writeString(city);
        parcel.writeString(state);
        parcel.writeByte((byte) (current_status == null ? 0 : current_status ? 1 : 2));
        parcel.writeString(date_created);
        parcel.writeString(document_id);

    }
}
