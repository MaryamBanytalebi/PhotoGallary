package com.example.photogallary.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY="search_query";
    private static final String PREF_TEST_IS_SEARCH="isSearchQuery";
    private static final String PREF_LAST_ID="LastId";

    public static String getSearchQuery(Context context){
        //mode=private that nobody can use prefrene
        //find query PREF_SEARCH_QUERY if there is not key or shareprefernce set default null
        //this query is type string
        return getSharedPreferences(context).getString(PREF_SEARCH_QUERY,null);
    }

    public static void setPrefSearchQuery(Context context,String query){
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_SEARCH_QUERY,query);
        editor.apply();
        getSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY,query)
                .apply();
    }

    public static String getPopularhQuery(Context context){
        return getSharedPreferences(context).getString(PREF_SEARCH_QUERY,null);
    }

    public static void setPrefPopularQuery(Context context,String query){
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_SEARCH_QUERY,query);
        editor.apply();
        getSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY,query)
                .apply();
    }

    /*public static void setTestQuery(Context context,boolean isSearch){
        getSharedPreferences(context)
                .edit()
                .putBoolean(PREF_TEST_IS_SEARCH,isSearch)
                .apply();
    }
*/

    public static String getLastId(Context context){

        return getSharedPreferences(context).getString(PREF_LAST_ID,null);
    }

    public static void setLastId(Context context,String query){
        getSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_ID,query)
                .apply();
    }
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getPackageName(),context.MODE_PRIVATE);
    }

    /*public static void removeSearchQuery(Context context){
        getSharedPreferences(context)
                .edit()
                .remove(PREF_SEARCH_QUERY)
                .apply();
    }*/
}
