package com.example.myfutsal.Model;

public class Pemains extends PemainId {

    public String nama_pemain;
    public String umur_pemain;
    public String pemain_id;
    public String foto_pemain;

    public Pemains() {
    }

    public Pemains(String nama_pemain, String umur_pemain, String pemain_id, String foto_pemain) {
        this.nama_pemain = nama_pemain;
        this.umur_pemain = umur_pemain;
        this.pemain_id = pemain_id;
        this.foto_pemain = foto_pemain;
    }

    public String getNama_pemain() {
        return nama_pemain;
    }

    public void setNama_pemain(String nama_pemain) {
        this.nama_pemain = nama_pemain;
    }

    public String getUmur_pemain() {
        return umur_pemain;
    }

    public void setUmur_pemain(String umur_pemain) {
        this.umur_pemain = umur_pemain;
    }

    public String getPemain_id() {
        return pemain_id;
    }

    public void setPemain_id(String pemain_id) {
        this.pemain_id = pemain_id;
    }

    public String getFoto_pemain() {
        return foto_pemain;
    }

    public void setFoto_pemain(String foto_pemain) {
        this.foto_pemain = foto_pemain;
    }
}
