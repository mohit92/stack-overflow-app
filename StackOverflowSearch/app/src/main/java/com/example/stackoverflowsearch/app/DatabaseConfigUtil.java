package com.example.stackoverflowsearch.app;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

/**
 * Created by mohit on 6/9/14.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt");
    }
}
