package com.example.myfutsal.Model;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class LawanId {

    @Exclude
    public String LawanId;

    public <T extends LawanId> T withId(@NonNull final String id) {
        this.LawanId = id;
        return (T) this;
    }

}
