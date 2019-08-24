package com.example.myfutsal.Model;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class PemainId {

    @Exclude
    public String PemainId;

    public <T extends PemainId> T withId(@NonNull final String id) {
        this.PemainId = id;
        return (T) this;
    }

}
