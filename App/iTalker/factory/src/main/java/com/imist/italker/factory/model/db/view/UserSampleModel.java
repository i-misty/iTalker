package com.imist.italker.factory.model.db.view;

import com.imist.italker.factory.model.Author;
import com.imist.italker.factory.model.db.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;

@QueryModel(database = AppDatabase.class)
public class UserSampleModel implements Author{
    @Column
    public String id;
    @Column
    public String name;
    @Column
    public String portrait;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
