package com.replon.www.vsmadmin.Catalogues;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ContentsCatalogue implements Parcelable {

    String name;
    String fabric;
    String saree_length;
    String packaging;
    String pieces;
    int type;
    int tag;
    boolean home;
    String home_text;
    String home_image_url;
    String price;
    String catalogue_image_url;
    ArrayList<String> saree_image_url;
    String pdf_url;
    String date_created;
    String document_id;

    public ContentsCatalogue(String name, String fabric, String saree_length, String packaging, String pieces, int type, int tag, boolean home, String home_text, String home_image_url, String price, String catalogue_image_url, ArrayList<String> saree_image_url, String pdf_url, String date_created,String document_id) {
        this.name = name;
        this.fabric = fabric;
        this.saree_length = saree_length;
        this.packaging = packaging;
        this.pieces = pieces;
        this.type = type;
        this.tag = tag;
        this.home = home;
        this.home_text = home_text;
        this.home_image_url = home_image_url;
        this.price = price;
        this.catalogue_image_url = catalogue_image_url;
        this.saree_image_url = saree_image_url;
        this.pdf_url = pdf_url;
        this.date_created = date_created;
        this.document_id=document_id;
    }


    protected ContentsCatalogue(Parcel in) {
        name = in.readString();
        fabric = in.readString();
        saree_length = in.readString();
        packaging = in.readString();
        pieces = in.readString();
        type = in.readInt();
        tag = in.readInt();
        home = in.readByte() != 0;
        home_text = in.readString();
        home_image_url = in.readString();
        price = in.readString();
        catalogue_image_url = in.readString();
        saree_image_url = in.createStringArrayList();
        pdf_url = in.readString();
        date_created = in.readString();
        document_id=in.readString();

    }

    public static final Creator<ContentsCatalogue> CREATOR = new Creator<ContentsCatalogue>() {
        @Override
        public ContentsCatalogue createFromParcel(Parcel in) {
            return new ContentsCatalogue(in);
        }

        @Override
        public ContentsCatalogue[] newArray(int size) {
            return new ContentsCatalogue[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFabric() {
        return fabric;
    }

    public void setFabric(String fabric) {
        this.fabric = fabric;
    }

    public String getSaree_length() {
        return saree_length;
    }

    public void setSaree_length(String saree_length) {
        this.saree_length = saree_length;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getPieces() {
        return pieces;
    }

    public void setPieces(String pieces) {
        this.pieces = pieces;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public boolean isHome() {
        return home;
    }

    public void setHome(boolean home) {
        this.home = home;
    }

    public String getHome_text() {
        return home_text;
    }

    public void setHome_text(String home_text) {
        this.home_text = home_text;
    }

    public String getHome_image_url() {
        return home_image_url;
    }

    public void setHome_image_url(String home_image_url) {
        this.home_image_url = home_image_url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCatalogue_image_url() {
        return catalogue_image_url;
    }

    public void setCatalogue_image_url(String catalogue_image_url) {
        this.catalogue_image_url = catalogue_image_url;
    }

    public ArrayList<String> getSaree_image_url() {
        return saree_image_url;
    }

    public void setSaree_image_url(ArrayList<String> saree_image_url) {
        this.saree_image_url = saree_image_url;
    }

    public String getPdf_url() {
        return pdf_url;
    }

    public void setPdf_url(String pdf_url) {
        this.pdf_url = pdf_url;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(fabric);
        dest.writeString(saree_length);
        dest.writeString(packaging);
        dest.writeString(pieces);
        dest.writeInt(type);
        dest.writeInt(tag);
        dest.writeByte((byte) (home ? 1 : 0));
        dest.writeString(home_text);
        dest.writeString(home_image_url);
        dest.writeString(price);
        dest.writeString(catalogue_image_url);
        dest.writeStringList(saree_image_url);
        dest.writeString(pdf_url);
        dest.writeString(date_created);
        dest.writeString(document_id);
    }
}

