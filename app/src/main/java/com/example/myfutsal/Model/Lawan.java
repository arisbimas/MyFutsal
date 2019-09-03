package com.example.myfutsal.Model;

public class Lawan extends LawanId {

    public String nama_tim;
    public String foto_tim;
    public String tim_id;
    public String siap_main;

    public Lawan() {
    }

    public Lawan(String nama_tim, String foto_tim,String tim_id, String siap_main) {
        this.nama_tim = nama_tim;
        this.foto_tim = foto_tim;
        this.tim_id = tim_id;
        this.siap_main = siap_main;
    }

    public String getNama_tim() {
        return nama_tim;
    }

    public void setNama_tim(String nama_tim) {
        this.nama_tim = nama_tim;
    }

    public String getFoto_tim() {
        return foto_tim;
    }

    public void setFoto_tim(String foto_tim) {
        this.foto_tim = foto_tim;
    }

    public String getTim_id() {
        return tim_id;
    }

    public void setTim_id(String tim_id) {
        this.tim_id = tim_id;
    }

    public String getSiap_main() {
        return siap_main;
    }

    public void setSiap_main(String siap_main) {
        this.siap_main = siap_main;
    }
}
