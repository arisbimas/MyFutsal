package com.example.myfutsal.Model;


import java.util.Date;

public class Blog  extends BlogPostId{

    public String foto_post;
    public String keterangan;
    public String tim_id;
    public Date waktu;

    public Blog() {
    }

    public Blog(String foto_post, String keterangan, String tim_id, Date waktu) {
        this.foto_post = foto_post;
        this.keterangan = keterangan;
        this.tim_id = tim_id;
        this.waktu = waktu;
    }
    public String getFoto_post() {
        return foto_post;
    }

    public void setFoto_post(String foto_post) {
        this.foto_post = foto_post;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTim_id() {
        return tim_id;
    }

    public void setTim_id(String tim_id) {
        this.tim_id = tim_id;
    }

    public Date getWaktu() {
        return waktu;
    }

    public void setWaktu(Date waktu) {
        this.waktu = waktu;
    }
}