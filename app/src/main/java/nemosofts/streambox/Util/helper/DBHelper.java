package nemosofts.streambox.Util.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import nemosofts.streambox.Util.EncryptData;
import nemosofts.streambox.item.ItemCat;
import nemosofts.streambox.item.ItemDns;
import nemosofts.streambox.item.ItemSeries;
import nemosofts.streambox.item.ItemSingleURL;
import nemosofts.streambox.item.ItemUsersDB;
import nemosofts.streambox.item.live.ItemLive;
import nemosofts.streambox.item.movie.ItemMovies;


public class DBHelper extends SQLiteOpenHelper {

    EncryptData encryptData;
    static String DB_NAME = "sbox1.db";

    SQLiteDatabase db;
    final Context context;

    private static final String TAG_ID = "id";

    // Table Name
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CAT_LIVE = "cat_live";
    private static final String TABLE_MOVIE_SEEK = "movie_seek";
    private static final String TABLE_SINGLE = "single";
    private static final String TABLE_DNS = "tbl_dns";

    // Fav
    private static final String TABLE_FAV_LIVE = "fav_live";
    private static final String TABLE_FAV_MOVIE = "fav_movie";
    private static final String TABLE_FAV_SERIES = "fav_series";

    // DNS
    private static final String TAG_DNS_TITLE = "dns_title";
    private static final String TAG_DNS_BASE = "dns_base";

    // single
    private static final String TAG_SINGLE_ANY_NAME = "any_name";
    private static final String TAG_SINGLE_URL = "single_url";

    // User
    private static final String TAG_USERS_ANY_NAME = "any_name";
    private static final String TAG_USERS_NAME = "user_name";
    private static final String TAG_USERS_PASSWORD = "user_pass";
    private static final String TAG_USERS_URL = "user_url";

    // Category
    private static final String TAG_CAT_ID = "cid";
    private static final String TAG_CAT_NAME = "cname";

    // Movie
    private static final String TAG_MOVIE_STREAM_ID = "stream_id";
    private static final String TAG_MOVIE_TITLE = "title";
    private static final String TAG_MOVIE_SEEK = "seek";

    // FAV ------------------------------------------------------------------------------------------
    // LIVE
    private static final String TAG_LIVE_NAME = "name";
    private static final String TAG_LIVE_ID = "stream_id";
    private static final String TAG_LIVE_ICON = "stream_icon";
    // MOVIE
    private static final String TAG_MOVIE_NAME = "name";
    private static final String TAG_MOVIE_ID = "stream_id";
    private static final String TAG_MOVIE_ICON = "stream_icon";
    private static final String TAG_MOVIE_RATING = "rating";
    //SERIES
    private static final String TAG_SERIES_NAME = "name";
    private static final String TAG_SERIES_ID = "series_id";
    private static final String TAG_SERIES_COVER = "cover";
    private static final String TAG_SERIES_RATING = "rating";

    // Fav
    private final String[] columns_fav_live = new String[]{TAG_ID, TAG_LIVE_NAME, TAG_LIVE_ID, TAG_LIVE_ICON};
    private final String[] columns_fav_movie = new String[]{TAG_ID, TAG_MOVIE_NAME, TAG_MOVIE_ID, TAG_MOVIE_ICON, TAG_MOVIE_RATING};
    private final String[] columns_fav_series = new String[]{TAG_ID, TAG_SERIES_NAME, TAG_SERIES_ID, TAG_SERIES_COVER, TAG_SERIES_RATING};

    private final String[] columns_single = new String[]{TAG_ID, TAG_SINGLE_ANY_NAME, TAG_SINGLE_URL};
    private final String[] columns_movie_seek = new String[]{TAG_ID, TAG_MOVIE_STREAM_ID, TAG_MOVIE_TITLE, TAG_MOVIE_SEEK};
    private final String[] columns_cat = new String[]{TAG_ID, TAG_CAT_ID, TAG_CAT_NAME};
    private final String[] columns_users = new String[]{TAG_ID, TAG_USERS_ANY_NAME, TAG_USERS_NAME, TAG_USERS_PASSWORD, TAG_USERS_URL};
    private final String[] columns_dns = new String[]{TAG_ID, TAG_DNS_TITLE, TAG_DNS_BASE};

    // Creating table query
    private static final String CREATE_TABLE_FAV_SERIES = "create table " + TABLE_FAV_SERIES + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_SERIES_NAME + " TEXT," +
            TAG_SERIES_ID + " TEXT," +
            TAG_SERIES_COVER + " TEXT," +
            TAG_SERIES_RATING + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_FAV_MOVIE = "create table " + TABLE_FAV_MOVIE + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_MOVIE_NAME + " TEXT," +
            TAG_MOVIE_ID + " TEXT," +
            TAG_MOVIE_ICON + " TEXT," +
            TAG_MOVIE_RATING + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_FAV_LIVE = "create table " + TABLE_FAV_LIVE + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_LIVE_NAME + " TEXT," +
            TAG_LIVE_ID + " TEXT," +
            TAG_LIVE_ICON + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_DNS = "create table " + TABLE_DNS + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_DNS_TITLE + " TEXT," +
            TAG_DNS_BASE + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_SINGLE = "create table " + TABLE_SINGLE + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_SINGLE_ANY_NAME + " TEXT," +
            TAG_SINGLE_URL + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_USERS = "create table " + TABLE_USERS + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_USERS_ANY_NAME + " TEXT," +
            TAG_USERS_NAME + " TEXT," +
            TAG_USERS_PASSWORD + " TEXT," +
            TAG_USERS_URL + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_CAT_LIVE = "create table " + TABLE_CAT_LIVE + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_CAT_ID + " TEXT," +
            TAG_CAT_NAME + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_MOVIE_SEEK = "create table " + TABLE_MOVIE_SEEK + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_MOVIE_STREAM_ID + " TEXT," +
            TAG_MOVIE_TITLE + " TEXT," +
            TAG_MOVIE_SEEK + " TEXT);";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        encryptData = new EncryptData(context);
        this.context = context;
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // SINGLE URl
            db.execSQL(CREATE_TABLE_SINGLE);

            // LIVE
            db.execSQL(CREATE_TABLE_FAV_LIVE);
            db.execSQL(CREATE_TABLE_CAT_LIVE);

            // MOVIES
            db.execSQL(CREATE_TABLE_FAV_MOVIE);
            db.execSQL(CREATE_TABLE_MOVIE_SEEK);

            // SERIES
            db.execSQL(CREATE_TABLE_FAV_SERIES);

            // DNS
            db.execSQL(CREATE_TABLE_DNS);

            // Users
            db.execSQL(CREATE_TABLE_USERS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fav -----------------------------------------------------------------------------------------
    @SuppressLint("Range")
    public ArrayList<ItemLive> getFavLive() {
        ArrayList<ItemLive> arrayList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_FAV_LIVE, columns_fav_live, null, null, null, null, TAG_ID + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {

                    String streamIcon = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_LIVE_ICON)));

                    String name = cursor.getString(cursor.getColumnIndex(TAG_LIVE_NAME));
                    String streamID = cursor.getString(cursor.getColumnIndex(TAG_LIVE_ID));

                    ItemLive objItem = new ItemLive(name,streamID,streamIcon);
                    arrayList.add(objItem);

                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public void addToFavLive(ItemLive itemLive) {
        if (itemLive != null){
            String image = encryptData.encrypt(itemLive.getStreamIcon().replace(" ", "%20"));
            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_LIVE_NAME, itemLive.getName());
            contentValues.put(TAG_LIVE_ID, itemLive.getStreamID());
            contentValues.put(TAG_LIVE_ICON, image);
            db.insert(TABLE_FAV_LIVE, null, contentValues);
        }
    }
    public void removeFavLive(String stream_id) {
        if (Boolean.TRUE.equals(checkFavLive(stream_id))){
            db.delete(TABLE_FAV_LIVE, TAG_LIVE_ID + "=" + stream_id, null);
        }
    }
    public Boolean checkFavLive(String streamID) {
        Cursor cursor = db.query(TABLE_FAV_LIVE, columns_fav_live, TAG_LIVE_ID + "=" + streamID, null, null, null, null);
        boolean isFav = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return isFav;
    }

    @SuppressLint("Range")
    public ArrayList<ItemMovies> getFavMovies() {
        ArrayList<ItemMovies> arrayList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_FAV_MOVIE, columns_fav_movie, null, null, null, null, TAG_ID + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {

                    String streamIcon = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_MOVIE_ICON)));

                    String name = cursor.getString(cursor.getColumnIndex(TAG_MOVIE_NAME));
                    String streamID = cursor.getString(cursor.getColumnIndex(TAG_MOVIE_ID));
                    String rating = cursor.getString(cursor.getColumnIndex(TAG_MOVIE_RATING));

                    ItemMovies objItem = new ItemMovies(name,streamID,streamIcon,rating);
                    arrayList.add(objItem);

                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public void addToFavMovie(String streamId, String streamName, String streamIcon, String streamRating) {
        if (streamIcon == null){
            streamIcon = "";
        }
        String image = encryptData.encrypt(streamIcon.replace(" ", "%20"));
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_MOVIE_NAME, streamName);
        contentValues.put(TAG_MOVIE_ID, streamId);
        contentValues.put(TAG_MOVIE_ICON, image);
        contentValues.put(TAG_MOVIE_RATING, streamRating);
        db.insert(TABLE_FAV_MOVIE, null, contentValues);
    }
    public void removeFavMovie(String stream_id) {
        if (Boolean.TRUE.equals(checkFavMovie(stream_id))){
            db.delete(TABLE_FAV_MOVIE, TAG_MOVIE_ID + "=" + stream_id, null);
        }
    }
    public Boolean checkFavMovie(String streamID) {
        Cursor cursor = db.query(TABLE_FAV_MOVIE, columns_fav_movie, TAG_MOVIE_ID + "=" + streamID, null, null, null, null);
        boolean isFav = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return isFav;
    }

    @SuppressLint("Range")
    public ArrayList<ItemSeries> getFavSeries() {
        ArrayList<ItemSeries> arrayList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_FAV_SERIES, columns_fav_series, null, null, null, null, TAG_ID + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {

                    String cover = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_SERIES_COVER)));

                    String name = cursor.getString(cursor.getColumnIndex(TAG_SERIES_NAME));
                    String seriesID = cursor.getString(cursor.getColumnIndex(TAG_SERIES_ID));
                    String rating = cursor.getString(cursor.getColumnIndex(TAG_SERIES_RATING));

                    ItemSeries objItem = new ItemSeries(name, seriesID, cover, rating);
                    arrayList.add(objItem);

                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public void addToFavSeries(String seriesName, String seriesId, String seriesCover, String seriesRating) {
        if (seriesCover == null){
            seriesCover = "";
        }
        String image = encryptData.encrypt(seriesCover.replace(" ", "%20"));
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_SERIES_NAME, seriesName);
        contentValues.put(TAG_SERIES_ID, seriesId);
        contentValues.put(TAG_SERIES_COVER, image);
        contentValues.put(TAG_SERIES_RATING, seriesRating);
        db.insert(TABLE_FAV_SERIES, null, contentValues);
    }
    public void removeFavSeries(String seriesID) {
        if (Boolean.TRUE.equals(checkFavMovie(seriesID))){
            db.delete(TABLE_FAV_SERIES, TAG_SERIES_ID + "=" + seriesID, null);
        }
    }
    public Boolean checkFavSeries(String seriesID) {
        Cursor cursor = db.query(TABLE_FAV_SERIES, columns_fav_series, TAG_SERIES_ID + "=" + seriesID, null, null, null, null);
        boolean isFav = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return isFav;
    }

    // DNS -----------------------------------------------------------------------------------------
    @SuppressLint("Range")
    public ArrayList<ItemDns> loadDNS() {
        ArrayList<ItemDns> arrayList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_DNS, columns_dns, null, null, null, null, TAG_ID + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {

                    String name = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_DNS_TITLE)));
                    String url = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_DNS_BASE)));

                    ItemDns objItem = new ItemDns(name, url);
                    arrayList.add(objItem);

                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public void addToDNS(ItemDns itemDns) {
        try {
            if (itemDns != null) {
                String name = encryptData.encrypt(itemDns.getTitle());
                String url = encryptData.encrypt(itemDns.getBase().replace(" ", "%20"));
                ContentValues contentValues = new ContentValues();
                contentValues.put(TAG_DNS_TITLE , name);
                contentValues.put(TAG_DNS_BASE, url);
                db.insert(TABLE_DNS, null, contentValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void removeAllDNS() {
        try {
            db.delete(TABLE_DNS, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Single --------------------------------------------------------------------------------------
    @SuppressLint("Range")
    public ArrayList<ItemSingleURL> loadSingleURL() {
        ArrayList<ItemSingleURL> arrayList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_SINGLE, columns_single, null, null, null, null, TAG_ID + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {

                    String id = cursor.getString(cursor.getColumnIndex(TAG_ID));
                    String any_name = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_SINGLE_ANY_NAME)));
                    String url = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_SINGLE_URL)));

                    ItemSingleURL objItem = new ItemSingleURL(id, any_name, url);
                    arrayList.add(objItem);

                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public void addToSingleURL(ItemSingleURL itemSingle) {
        try {
            if (itemSingle != null) {
                String any_name = encryptData.encrypt(itemSingle.getAnyName());
                String url = encryptData.encrypt(itemSingle.getSingleURL().replace(" ", "%20"));
                ContentValues contentValues = new ContentValues();
                contentValues.put(TAG_SINGLE_ANY_NAME , any_name);
                contentValues.put(TAG_SINGLE_URL, url);
                db.insert(TABLE_SINGLE, null, contentValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Users ---------------------------------------------------------------------------------------
    @SuppressLint("Range")
    public ArrayList<ItemUsersDB> loadUsersDB() {
        ArrayList<ItemUsersDB> arrayList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_USERS, columns_users, null, null, null, null, TAG_ID + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {

                    String id = cursor.getString(cursor.getColumnIndex(TAG_ID));
                    String any_name = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_USERS_ANY_NAME)));
                    String user_name = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_USERS_NAME)));
                    String user_pass = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_USERS_PASSWORD)));
                    String user_url = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_USERS_URL)));

                    ItemUsersDB objItem = new ItemUsersDB(id, any_name, user_name, user_pass,user_url);
                    arrayList.add(objItem);

                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public void addToUserDB(ItemUsersDB itemUsersDB) {
        try {
            if (itemUsersDB != null) {
                String any_name = encryptData.encrypt(itemUsersDB.getAnyName());
                String user_name = encryptData.encrypt(itemUsersDB.getUseName());
                String user_pass = encryptData.encrypt(itemUsersDB.getUserPass());
                String user_url = encryptData.encrypt(itemUsersDB.getUserURL().replace(" ", "%20"));

                ContentValues contentValues = new ContentValues();
                contentValues.put(TAG_USERS_ANY_NAME , any_name);
                contentValues.put(TAG_USERS_NAME, user_name);
                contentValues.put(TAG_USERS_PASSWORD, user_pass);
                contentValues.put(TAG_USERS_URL, user_url);
                db.insert(TABLE_USERS, null, contentValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Category Live  ------------------------------------------------------------------------------
    @SuppressLint("Range")
    public ArrayList<ItemCat> getCategoryLive(String limit) {
        ArrayList<ItemCat> arrayList = new ArrayList<>();
        try {
            String OrderBY = TAG_ID + " ASC";
            if (limit != null){
                OrderBY = "RANDOM()";
            }
            Cursor cursor = db.query(TABLE_CAT_LIVE, columns_cat, null, null, null, null, OrderBY, limit);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {

                    String cid = cursor.getString(cursor.getColumnIndex(TAG_CAT_ID));
                    String cname = cursor.getString(cursor.getColumnIndex(TAG_CAT_NAME));

                    ItemCat itemCat = new ItemCat(cid, cname);
                    arrayList.add(itemCat);

                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public void addToCatLiveList(ItemCat itemCat) {
        try {
            if (itemCat != null){
                ContentValues contentValues = new ContentValues();
                contentValues.put(TAG_CAT_ID, itemCat.getId());
                contentValues.put(TAG_CAT_NAME, itemCat.getName());
                db.insert(TABLE_CAT_LIVE, null, contentValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void removeAllCatLive() {
        try {
            db.delete(TABLE_CAT_LIVE, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void removeCatLiveID(String catId) {
        try {
            if (Boolean.TRUE.equals(checkCat(catId))) {
                db.delete(TABLE_CAT_LIVE, TAG_CAT_ID + "=" + catId, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @NonNull
    private Boolean checkCat(String id) {
        Cursor cursor = db.query(TABLE_CAT_LIVE, columns_cat, TAG_CAT_ID + "=" + id, null, null, null, null);
        Boolean isFav = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return isFav;
    }

    // Seek Movie ----------------------------------------------------------------------------------
    @SuppressLint("Range")
    public int getSeekMovie(String streamId, String stream_name) {
        String seekTo = "0";
        try {
            String where = TAG_MOVIE_STREAM_ID + "=? AND " + TAG_MOVIE_TITLE + "=?";
            String[] args = new String[]{streamId, stream_name.replace("'", "%27")};
            Cursor cursor = db.query(TABLE_MOVIE_SEEK, columns_movie_seek, where, args, null, null,  null,null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (!cursor.getString(cursor.getColumnIndex(TAG_MOVIE_SEEK)).isEmpty()){
                    seekTo = cursor.getString(cursor.getColumnIndex(TAG_MOVIE_SEEK));
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Integer.parseInt(seekTo);
    }
    public void addToSeekMovie(String currentPosition, String streamId, String stream_name) {
        try {
            String where = TAG_MOVIE_STREAM_ID + "=? AND " + TAG_MOVIE_TITLE + "=?";
            String[] args = new String[]{streamId, stream_name.replace("'", "%27")};

            if (Boolean.TRUE.equals(checkSeekMovie(streamId, stream_name))) {
                db.delete(TABLE_MOVIE_SEEK, where, args);
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_MOVIE_STREAM_ID, streamId);
            contentValues.put(TAG_MOVIE_TITLE, stream_name);
            contentValues.put(TAG_MOVIE_SEEK, currentPosition);
            db.insert(TABLE_MOVIE_SEEK, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Boolean checkSeekMovie(String streamId, String stream_name) {
        boolean isSeekMovie = false;
        try {
            String where = TAG_MOVIE_STREAM_ID + "=? AND " + TAG_MOVIE_TITLE + "=?";
            String[] args = new String[]{streamId, stream_name.replace("'", "%27")};
            Cursor cursor = db.query(TABLE_MOVIE_SEEK, columns_movie_seek, where, args, null, null, null);
            isSeekMovie = cursor != null && cursor.getCount() > 0;
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSeekMovie;
    }

    // Remove All Data -----------------------------------------------------------------------------
    public void removeAllData() {
        try {
            db.delete(TABLE_CAT_LIVE, null, null);
            db.delete(TABLE_MOVIE_SEEK, null, null);

            db.delete(TABLE_FAV_LIVE, null, null);
            db.delete(TABLE_FAV_MOVIE, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Upgrade -------------------------------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public synchronized void close () {
        if (db != null) {
            db.close();
            super.close();
        }
    }
}