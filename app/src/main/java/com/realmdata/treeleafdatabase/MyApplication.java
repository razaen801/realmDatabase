package com.realmdata.treeleafdatabase;

import android.app.Application;
import android.content.Context;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class MyApplication extends Application {

    Context context;
    private static MyApplication instance;
    static RealmConfiguration config;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        Realm.init(this);
        config = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(2)
                .migration(new RealmMigration() {
                               @Override
                               public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                                   RealmSchema schema = realm.getSchema();
                                   //region Migrate from version 1 to version 2
                                   if (oldVersion == 1) {

                                       oldVersion++;
                                   }
                               }
                           }
                )
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);

    }
}
