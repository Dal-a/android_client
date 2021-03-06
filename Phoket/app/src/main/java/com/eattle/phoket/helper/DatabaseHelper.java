package com.eattle.phoket.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.eattle.phoket.CONSTANT;
import com.eattle.phoket.model.Folder;
import com.eattle.phoket.model.Folder_Tag;
import com.eattle.phoket.model.Manager;
import com.eattle.phoket.model.Media;
import com.eattle.phoket.model.Media_Tag;
import com.eattle.phoket.model.NotificationM;
import com.eattle.phoket.model.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by GA on 2015. 3. 19..
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private String TAG = " DatabaseHelper";

    private static DatabaseHelper Instance;
    private SQLiteDatabase mDatabase;
    private AtomicInteger mOpenCounter = new AtomicInteger();//DB open현황을 체크

    private static final int DATABASE_VERSION = 33;

    public static final String DATABASE_NAME = "PhoketDB";

    private static final String TABLE_MEDIA = "media";
    private static final String TABLE_FOLDER = "folder";
    private static final String TABLE_MANAGER = "manager";
    private static final String TABLE_TAG = "tag";
    private static final String TABLE_MEDIA_TAG = "media_tag";
    private static final String TABLE_FOLDER_TAG = "folder_tag";
    private static final String TABLE_NOTIFICATION = "notification";
    private static final String TABLE_GUIDE = "guide";

    //media(사진)
    private static final String KEY_ID = "id";             //전체에서의 사진 id **primary key**
    private static final String KEY_FOLDER_ID = "folder_id";   //폴더 id (속한 스토리의 id)
    private static final String KEY_NAME = "name";        //
    private static final String KEY_PICTURETAKEN = "picturetaken";      //사진이 촬영된 시간
    private static final String KEY_YEAR = "year";           //년
    private static final String KEY_MONTH = "month";          //월
    private static final String KEY_DAY = "day";            //일
    private static final String KEY_LATITUDE = "latitude";       //위도
    private static final String KEY_LONGITUDE = "longitude";      //경도
    private static final String KEY_PLACENAME = "placename";        //장소명
    private static final String KEY_PATH = "path";      //사진 경로
    private static final String KEY_THUMBNAILPATH = "thumbnail_path";   //안드로이드 내장 썸네일 경로
    //tag
    //private static final String KEY_ID = "id";
    //private static final String KEY_NAME = "name";
    private static final String KEY_COLOR = "color";


    //media_tag
    //private static final String KEY_ID = "id";
    private static final String KEY_TAG_ID = "tag_id";
    private static final String KEY_MEDIA_ID = "media_id";

    //folder_tag
    //private static final String KEY_ID = "id";
//    private static final String KEY_TAG_ID = "tag_id";
//    private static final String KEY_FOLDER_ID = "folder_id";
    private static final String KEY_COUNT = "count";


    //folder
    //private static final String KEY_ID = "id";
    //private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_PICTURE_NUM_IN_STORY = "picture_num";
    private static final String KEY_TITLEIMAGEID = "titleImageID";
    private static final String KEY_ISFIXED = "isFixed";//고정 스토리이면 1,일반 스토리는 0

    //manager
    private static final String KEY_TOTALPICTURENUM = "totalPictureNum";
    private static final String KEY_REALPICTURENUM = "realPictureNum";
    private static final String KEY_AVERAGEINTERVAL = "averageInterval";
    private static final String KEY_STANDARDDERIVATION = "standardDerivation";
    //notification
    private static final String KEY_LASTNOTIFICATION = "lastNotification";
    private static final String KEY_LASTPICTUREID = "lastPictureID";
    //guide(최초실행시 가이드)
    private static final String KEY_GUIDEENDED = "guideEnded";//가이드가 정상적으로 완료되었으면 1, 아니면 0


    private static final String CREATE_TABLE_MEDIA =
            "CREATE TABLE " + TABLE_MEDIA + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + KEY_FOLDER_ID + " INTEGER NOT NULL, "
                    + KEY_NAME + " VARCHAR(100) NOT NULL, "
                    + KEY_PICTURETAKEN + " LONG NOT NULL, "
                    + KEY_YEAR + " INTEGER NOT NULL, "
                    + KEY_MONTH + " INTEGER NOT NULL, "
                    + KEY_DAY + " INTEGER NOT NULL, "
                    + KEY_LATITUDE + " DOUBLE, "
                    + KEY_LONGITUDE + " DOUBLE, "
                    + KEY_PLACENAME + " VARCHAR(100), "
                    + KEY_PATH + " VARCHAR(255), "
                    + KEY_THUMBNAILPATH + " VARCHAR(255), "
                    + KEY_ISFIXED + " INTEGER NOT NULL "
                    + ")";

    private static final String CREATE_TABLE_TAG =
            "CREATE TABLE " + TABLE_TAG + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + KEY_NAME + " VARCHAR(100) NOT NULL, "
                    + KEY_COLOR + " INTEGER NOT NULL "
                    + ")";

    private static final String CREATE_TABLE_MEDIA_TAG =
            "CREATE TABLE " + TABLE_MEDIA_TAG + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + KEY_TAG_ID + " LONG NOT NULL, "
                    + KEY_MEDIA_ID + " LONG NOT NULL "
                    + ")";

    private static final String CREATE_TABLE_FOLDER_TAG =
            "CREATE TABLE " + TABLE_FOLDER_TAG + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + KEY_TAG_ID + " LONG NOT NULL, "
                    + KEY_FOLDER_ID + " LONG NOT NULL, "
                    + KEY_COUNT + " INTEGER NOT NULL"
                    + ")";

    private static final String CREATE_TABLE_FOLDER =
            "CREATE TABLE " + TABLE_FOLDER + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + KEY_NAME + " VARCHAR(255) NOT NULL, "
                    + KEY_IMAGE + " VARCHAR(255) NOT NULL, "
                    + KEY_THUMBNAILPATH + " VARCHAR(255), "
                    + KEY_PICTURE_NUM_IN_STORY + " INTEGER NOT NULL, "
                    + KEY_TITLEIMAGEID + " INTEGER NOT NULL, "
                    + KEY_ISFIXED + " INTEGER NOT NULL "
                    + ")";

    private static final String CREATE_TABLE_MANAGER =
            "CREATE TABLE " + TABLE_MANAGER + " ("
                    + KEY_TOTALPICTURENUM + " INTEGER PRIMARY KEY NOT NULL, "
                    + KEY_REALPICTURENUM + " INTEGER NOT NULL, "
                    + KEY_AVERAGEINTERVAL + " LONG, "
                    + KEY_STANDARDDERIVATION + " LONG "
                    + ")";

    private static final String CREATE_TABLE_NOTIFICATION =
            "CREATE TABLE " + TABLE_NOTIFICATION + " ("
                    + KEY_LASTNOTIFICATION + " LONG NOT NULL, "
                    + KEY_LASTPICTUREID + " INTEGER NOT NULL "
                    + ")";

    private static final String CREATE_TABLE_GUIDE =
            "CREATE TABLE " + TABLE_GUIDE + " ("
                    + KEY_GUIDEENDED + " INTEGER NOT NULL "
                    + ")";

    public static DatabaseHelper getInstance(Context context) {
        if (Instance == null) {
            Instance = new DatabaseHelper(context);
        }
        return Instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    //적절한 때에 databasehelper를 닫아주기위한 함수
    public synchronized SQLiteDatabase openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            mDatabase = Instance.getWritableDatabase();
        }
        return mDatabase;
    }
    public synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0) {
            mDatabase.close();
        }
    }

    @Override
    public synchronized void close() {
        if (Instance != null)
            Instance.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Database Helper onCreate 함수 호출");


        db.execSQL(CREATE_TABLE_MEDIA);
        db.execSQL(CREATE_TABLE_TAG);
        db.execSQL(CREATE_TABLE_MEDIA_TAG);
        db.execSQL(CREATE_TABLE_FOLDER_TAG);
        db.execSQL(CREATE_TABLE_FOLDER);
        db.execSQL(CREATE_TABLE_MANAGER);
        db.execSQL(CREATE_TABLE_NOTIFICATION);
        db.execSQL(CREATE_TABLE_GUIDE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade() 호출 oldVersion : " + oldVersion + " newVersion : " + newVersion);
        //기존의 테이블들을 일단 삭제하고 새로 만든다
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA_TAG);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDER_TAG);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MANAGER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GUIDE);
        } catch (Exception ex) {
            Log.e("DatabaseHelper", "Exception in DROP_SQL", ex);
        }

        onCreate(db);
    }


    /**
     * **************** FOLDER ******************
     */
    /*
     * Creating folder
     */
    public int createFolder(Folder folder) {
        SQLiteDatabase db = this.openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, folder.getId());
        values.put(KEY_NAME, folder.getName());
        values.put(KEY_IMAGE, folder.getImage());
        values.put(KEY_THUMBNAILPATH, folder.getThumbNail_path());
        values.put(KEY_PICTURE_NUM_IN_STORY, folder.getPicture_num());
        values.put(KEY_TITLEIMAGEID, folder.getTitleImageID());
        values.put(KEY_ISFIXED, folder.getIsFixed());

        int result = (int) db.insert(TABLE_FOLDER, null, values);

        this.closeDatabase();
        return result;
    }

    /*
     * getting all folders
     */
    public List<Folder> getAllFolders() {
        List<Folder> folders = new ArrayList<Folder>();
        String selectQuery = "SELECT * FROM " + TABLE_FOLDER;

        SQLiteDatabase db = this.openDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);


            if (c.moveToFirst()) {
                do {
                    Folder f = new Folder();
                    f.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    f.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                    f.setImage(c.getString(c.getColumnIndex(KEY_IMAGE)));
                    f.setThumbNail_path(c.getString(c.getColumnIndex(KEY_THUMBNAILPATH)));
                    f.setPicture_num(c.getInt(c.getColumnIndex(KEY_PICTURE_NUM_IN_STORY)));
                    f.setTitleImageID(c.getInt(c.getColumnIndex(KEY_TITLEIMAGEID)));
                    f.setIsFixed(c.getInt(c.getColumnIndex(KEY_ISFIXED)));
                    folders.add(f);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null)
                c.close();

            this.closeDatabase();
        }
        return folders;
    }

    /*
     * getting folder by id
     */
    public Folder getFolder(int id) {
        Folder folder = new Folder();
        String selectQuery = "SELECT * FROM " + TABLE_FOLDER + " WHERE " + KEY_ID + " = " + id;

        SQLiteDatabase db = this.openDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);


            if (c.moveToFirst()) {
                folder.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                folder.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                folder.setImage(c.getString(c.getColumnIndex(KEY_IMAGE)));
                folder.setThumbNail_path(c.getString(c.getColumnIndex(KEY_THUMBNAILPATH)));
                folder.setPicture_num(c.getInt(c.getColumnIndex(KEY_PICTURE_NUM_IN_STORY)));
                folder.setTitleImageID(c.getInt(c.getColumnIndex(KEY_TITLEIMAGEID)));
                folder.setIsFixed(c.getInt(c.getColumnIndex(KEY_ISFIXED)));
            }
        } finally {
            if (c != null)
                c.close();

            this.closeDatabase();
        }
        return folder;
    }


    /*
     * Updating a folder by id
     * return number of updated row
     */
    public int updateFolder(Folder folder) {
        SQLiteDatabase db = this.openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, folder.getId());
        values.put(KEY_NAME, folder.getName());
        values.put(KEY_IMAGE, folder.getImage());
        values.put(KEY_THUMBNAILPATH, folder.getThumbNail_path());
        values.put(KEY_PICTURE_NUM_IN_STORY, folder.getPicture_num());
        values.put(KEY_TITLEIMAGEID, folder.getTitleImageID());
        values.put(KEY_ISFIXED, folder.getIsFixed());

        int result = db.update(TABLE_FOLDER, values, KEY_ID + " = ? ", new String[]{String.valueOf(folder.getId())});
        this.closeDatabase();

        return result;
    }

    /*
     * deleting all folder with media or not
     */

    public void deleteFolder(Folder folder, boolean should_delete_all_media_in_that_folder) {
        SQLiteDatabase db = this.openDatabase();

        //check if media in that folder should also be deleted
        if (should_delete_all_media_in_that_folder) {
            List<Media> allMedia = getAllMediaByFolder(folder.getId());

            for (Media m : allMedia) {
                deleteMedia(m.getId());
            }
        }

        db.delete(TABLE_MEDIA, KEY_ID + " = ? ", new String[]{String.valueOf(folder.getId())});
        this.closeDatabase();
    }

    public void deleteFolder(int folderId, boolean should_delete_all_media_in_that_folder) {
        Log.d("DatabaseHelper", "deleteFolder() 호출");
        SQLiteDatabase db = this.openDatabase();

        //check if media in that folder should also be deleted
        if (should_delete_all_media_in_that_folder) {
            List<Media> allMedia = getAllMediaByFolder(folderId);

            for (Media m : allMedia) {
                deleteMedia(m.getId());
            }
        }

        db.delete(TABLE_MEDIA, KEY_ID + " = ? ", new String[]{String.valueOf(folderId)});
        this.closeDatabase();
    }

    public void deleteAllFolder() {
        Log.d("DatabaseHelper", "deleteAllFolder() 호출");
        SQLiteDatabase db = this.openDatabase();
        //KEY_ISFIXED ==  1인 폴더는 지우지 않는다
        db.execSQL("DELETE FROM " + TABLE_FOLDER + " WHERE " + KEY_ISFIXED + " = 0");
        this.closeDatabase();
    }

    //고정된 스토리의 아이디값들을 얻어온다
    public List<Folder> getFixedFolder() {
        Log.d("DatabaseHelper", "getFixedFolder() 호출");


        List<Folder> folders = new ArrayList<Folder>();


        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_FOLDER + " WHERE " + KEY_ISFIXED + " = 1";//static이 1인 폴더의 아이디를 얻어온다
        Cursor c = null;

        try {
            c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    Folder f = new Folder();
                    f.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    f.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                    f.setImage(c.getString(c.getColumnIndex(KEY_IMAGE)));
                    f.setThumbNail_path(c.getString(c.getColumnIndex(KEY_THUMBNAILPATH)));
                    f.setPicture_num(c.getInt(c.getColumnIndex(KEY_PICTURE_NUM_IN_STORY)));
                    f.setTitleImageID(c.getInt(c.getColumnIndex(KEY_TITLEIMAGEID)));
                    f.setIsFixed(c.getInt(c.getColumnIndex(KEY_ISFIXED)));
                    folders.add(f);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null)
                c.close();

            this.closeDatabase();
        }
        return folders;
    }


    /**
     * **************** MEDIA ******************
     */

    /*
     * creating media
     */
    public int createMedia(Media media) {
        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, media.getId());
        values.put(KEY_FOLDER_ID, media.getFolder_id());
        values.put(KEY_NAME, media.getName());
        values.put(KEY_PICTURETAKEN, media.getPictureTaken());
        values.put(KEY_YEAR, media.getYear());
        values.put(KEY_MONTH, media.getMonth());
        values.put(KEY_DAY, media.getDay());
        values.put(KEY_LATITUDE, media.getLatitude());
        values.put(KEY_LONGITUDE, media.getLongitude());
        values.put(KEY_PLACENAME, media.getPlaceName());
        values.put(KEY_PATH, media.getPath());
        values.put(KEY_THUMBNAILPATH, media.getThumbnail_path());
        values.put(KEY_ISFIXED, media.getIsFixed());

        int result = (int) db.insert(TABLE_MEDIA, null, values);
        this.closeDatabase();
        return result;
    }

    //Media 여러개를 넣는다(속도 향상을 위해)
    //주고 받는 Media 배열의 크기가 커지면 복사하는 과정도 속도 저하의 요인
    //insert를 할때 ContentValues를 통해 하는 것도 속도 저하의 요인
    public int createSeveralMedia(ArrayList<Media> medias) {
        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.openDatabase();

        for (int i = 0; i < medias.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, medias.get(i).getId());
            values.put(KEY_FOLDER_ID, medias.get(i).getFolder_id());
            values.put(KEY_NAME, medias.get(i).getName());
            values.put(KEY_PICTURETAKEN, medias.get(i).getPictureTaken());
            values.put(KEY_YEAR, medias.get(i).getYear());
            values.put(KEY_MONTH, medias.get(i).getMonth());
            values.put(KEY_DAY, medias.get(i).getDay());
            values.put(KEY_LATITUDE, medias.get(i).getLatitude());
            values.put(KEY_LONGITUDE, medias.get(i).getLongitude());
            values.put(KEY_PLACENAME, medias.get(i).getPlaceName());
            values.put(KEY_PATH, medias.get(i).getPath());
            values.put(KEY_ISFIXED, medias.get(i).getIsFixed());

            db.insert(TABLE_MEDIA, null, values);
        }
        this.closeDatabase();
        return 0;
    }

    /*
    * getting Single Media by Media id
    */
    public Media getMediaById(int media_id) {
        Media media = new Media();
        Log.d("MediaDB", "media_id : " + media_id);

        String selectQuery = "SELECT * FROM " + TABLE_MEDIA + " WHERE " + KEY_ID + " = " + media_id;

        SQLiteDatabase db = this.openDatabase();
        //SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);


            if (c.moveToFirst()) {//media_id에 해당하는 사진이 있을 때

                media.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                media.setFolder_id(c.getInt(c.getColumnIndex(KEY_FOLDER_ID)));
                media.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                media.setPictureTaken(c.getLong(c.getColumnIndex(KEY_PICTURETAKEN)));
                media.setYear(c.getInt(c.getColumnIndex(KEY_YEAR)));
                media.setMonth(c.getInt(c.getColumnIndex(KEY_MONTH)));
                media.setDay(c.getInt(c.getColumnIndex(KEY_DAY)));
                media.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                media.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                media.setPlaceName(c.getString(c.getColumnIndex(KEY_PLACENAME)));
                media.setPath(c.getString(c.getColumnIndex(KEY_PATH)));
                media.setThumbnail_path(c.getString(c.getColumnIndex(KEY_THUMBNAILPATH)));
                media.setIsFixed(c.getInt(c.getColumnIndex(KEY_ISFIXED)));

            } else
                media = null;
        } finally {
            if (c != null)
                c.close();
            this.closeDatabase();
        }
        return media;
    }

    /*
    * getting Single Media by path
    */
    public Media getMediaByPath(String path) {
        Media media = new Media();
        Log.d("MediaDB", "path : " + path);

        String selectQuery = "SELECT * FROM " + TABLE_MEDIA + " WHERE " + KEY_PATH + " = '" + path+"'";

        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);


            if (c.moveToFirst()) {//media_id에 해당하는 사진이 있을 때

                media.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                media.setFolder_id(c.getInt(c.getColumnIndex(KEY_FOLDER_ID)));
                media.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                media.setPictureTaken(c.getLong(c.getColumnIndex(KEY_PICTURETAKEN)));
                media.setYear(c.getInt(c.getColumnIndex(KEY_YEAR)));
                media.setMonth(c.getInt(c.getColumnIndex(KEY_MONTH)));
                media.setDay(c.getInt(c.getColumnIndex(KEY_DAY)));
                media.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                media.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                media.setPlaceName(c.getString(c.getColumnIndex(KEY_PLACENAME)));
                media.setPath(c.getString(c.getColumnIndex(KEY_PATH)));
                media.setThumbnail_path(c.getString(c.getColumnIndex(KEY_THUMBNAILPATH)));
                media.setIsFixed(c.getInt(c.getColumnIndex(KEY_ISFIXED)));

            } else
                media = null;
        } finally {
            if (c != null)
                c.close();

            this.closeDatabase();
        }
        return media;
    }
    /*
     * getting all media
     */
    public List<Media> getAllMedia() {
        List<Media> media = new ArrayList<Media>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDIA;

        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    Media m = new Media();
                    m.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    m.setFolder_id(c.getInt(c.getColumnIndex(KEY_FOLDER_ID)));
                    m.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                    m.setPictureTaken(c.getLong(c.getColumnIndex(KEY_PICTURETAKEN)));
                    m.setYear(c.getInt(c.getColumnIndex(KEY_YEAR)));
                    m.setMonth(c.getInt(c.getColumnIndex(KEY_MONTH)));
                    m.setDay(c.getInt(c.getColumnIndex(KEY_DAY)));
                    m.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                    m.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                    m.setPlaceName(c.getString(c.getColumnIndex(KEY_PLACENAME)));
                    m.setPath(c.getString(c.getColumnIndex(KEY_PATH)));
                    m.setThumbnail_path(c.getString(c.getColumnIndex(KEY_THUMBNAILPATH)));
                    m.setIsFixed(c.getInt(c.getColumnIndex(KEY_ISFIXED)));

                    media.add(m);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null)
                c.close();

            this.closeDatabase();
        }
        return media;
    }

    /*
     * getting all media by folder
     */
    public List<Media> getAllMediaByFolder(int folder_id) {
        Log.d("DatabaseHelper", "getAllMediaByFolder() 호출");
        List<Media> media = new ArrayList<Media>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDIA + " WHERE " + KEY_FOLDER_ID + " = " + folder_id;

        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    Media m = new Media();
                    m.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    m.setFolder_id(c.getInt(c.getColumnIndex(KEY_FOLDER_ID)));
                    m.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                    m.setPictureTaken(c.getLong(c.getColumnIndex(KEY_PICTURETAKEN)));
                    m.setYear(c.getInt(c.getColumnIndex(KEY_YEAR)));
                    m.setMonth(c.getInt(c.getColumnIndex(KEY_MONTH)));
                    m.setDay(c.getInt(c.getColumnIndex(KEY_DAY)));
                    m.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                    m.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                    m.setPlaceName(c.getString(c.getColumnIndex(KEY_PLACENAME)));
                    m.setPath(c.getString(c.getColumnIndex(KEY_PATH)));
                    m.setThumbnail_path(c.getString(c.getColumnIndex(KEY_THUMBNAILPATH)));
                    m.setIsFixed(c.getInt(c.getColumnIndex(KEY_ISFIXED)));

                    media.add(m);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null)
                c.close();

            this.closeDatabase();
        }
        return media;
    }

    /*
    * getting Media by Tag id
    */
    public Media getMediaByFolderRandomly(int folder_id) {
        Media m = new Media();
        String selectQuery = "SELECT * FROM " + TABLE_MEDIA + " WHERE " + KEY_FOLDER_ID + " = " + folder_id + " ORDER BY RANDOM() LIMIT 1";


        Cursor c = null;
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            SQLiteDatabase db = this.openDatabase();
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                m.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                m.setFolder_id(c.getInt(c.getColumnIndex(KEY_FOLDER_ID)));
                m.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                m.setPictureTaken(c.getLong(c.getColumnIndex(KEY_PICTURETAKEN)));
                m.setYear(c.getInt(c.getColumnIndex(KEY_YEAR)));
                m.setMonth(c.getInt(c.getColumnIndex(KEY_MONTH)));
                m.setDay(c.getInt(c.getColumnIndex(KEY_DAY)));
                m.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                m.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                m.setPlaceName(c.getString(c.getColumnIndex(KEY_PLACENAME)));
                m.setPath(c.getString(c.getColumnIndex(KEY_PATH)));
                m.setThumbnail_path(c.getString(c.getColumnIndex(KEY_THUMBNAILPATH)));
                m.setIsFixed(c.getInt(c.getColumnIndex(KEY_ISFIXED)));
            }
        } finally {
            c.close();
            this.closeDatabase();
        }
        return m;
    }


    /*
     * getting Media by Tag id
     */
    public List<Media> getAllMediaByTagId(int tag_id) {
        List<Media> media = new ArrayList<Media>();
        String selectQuery = "SELECT " + TABLE_MEDIA + ".* FROM " + TABLE_MEDIA + " INNER JOIN " + TABLE_MEDIA_TAG + " ON " + TABLE_MEDIA + "." + KEY_ID + " = " + TABLE_MEDIA_TAG + "." + KEY_MEDIA_ID + " AND " + TABLE_MEDIA_TAG + "." + KEY_TAG_ID + " = " + tag_id;

        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    Media m = new Media();
                    m.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    m.setFolder_id(c.getInt(c.getColumnIndex(KEY_FOLDER_ID)));
                    m.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                    m.setPictureTaken(c.getLong(c.getColumnIndex(KEY_PICTURETAKEN)));
                    m.setYear(c.getInt(c.getColumnIndex(KEY_YEAR)));
                    m.setMonth(c.getInt(c.getColumnIndex(KEY_MONTH)));
                    m.setDay(c.getInt(c.getColumnIndex(KEY_DAY)));
                    m.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                    m.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                    m.setPlaceName(c.getString(c.getColumnIndex(KEY_PLACENAME)));
                    m.setPath(c.getString(c.getColumnIndex(KEY_PATH)));
                    m.setThumbnail_path(c.getString(c.getColumnIndex(KEY_THUMBNAILPATH)));
                    m.setIsFixed(c.getInt(c.getColumnIndex(KEY_ISFIXED)));

                    media.add(m);

                } while (c.moveToNext());
            }
        } finally {
            if (c != null)
                c.close();
            this.closeDatabase();
        }
        return media;
    }

    /*
     * getting all media by year
     */
    public List<Media> getAllMediaByYear(int year) {
        List<Media> media = new ArrayList<Media>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDIA + " WHERE " + KEY_YEAR + " = " + year;

        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);


            if (c.moveToFirst()) {
                do {
                    Media m = new Media();
                    m.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    m.setFolder_id(c.getInt(c.getColumnIndex(KEY_FOLDER_ID)));
                    m.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                    m.setPictureTaken(c.getLong(c.getColumnIndex(KEY_PICTURETAKEN)));
                    m.setYear(c.getInt(c.getColumnIndex(KEY_YEAR)));
                    m.setMonth(c.getInt(c.getColumnIndex(KEY_MONTH)));
                    m.setDay(c.getInt(c.getColumnIndex(KEY_DAY)));
                    m.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                    m.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                    m.setPlaceName(c.getString(c.getColumnIndex(KEY_PLACENAME)));
                    m.setPath(c.getString(c.getColumnIndex(KEY_PATH)));
                    m.setThumbnail_path(c.getString(c.getColumnIndex(KEY_THUMBNAILPATH)));
                    m.setIsFixed(c.getInt(c.getColumnIndex(KEY_ISFIXED)));

                    media.add(m);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null)
                c.close();

            this.closeDatabase();
        }
        return media;
    }

    /*
     * getting all media by month
     */
    public List<Media> getAllMediaByMonth(int month) {
        List<Media> media = new ArrayList<Media>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDIA + " WHERE " + KEY_MONTH + " = " + month;

        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    Media m = new Media();
                    m.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    m.setFolder_id(c.getInt(c.getColumnIndex(KEY_FOLDER_ID)));
                    m.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                    m.setPictureTaken(c.getLong(c.getColumnIndex(KEY_PICTURETAKEN)));
                    m.setYear(c.getInt(c.getColumnIndex(KEY_YEAR)));
                    m.setMonth(c.getInt(c.getColumnIndex(KEY_MONTH)));
                    m.setDay(c.getInt(c.getColumnIndex(KEY_DAY)));
                    m.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                    m.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                    m.setPlaceName(c.getString(c.getColumnIndex(KEY_PLACENAME)));
                    m.setPath(c.getString(c.getColumnIndex(KEY_PATH)));
                    m.setThumbnail_path(c.getString(c.getColumnIndex(KEY_THUMBNAILPATH)));
                    m.setIsFixed(c.getInt(c.getColumnIndex(KEY_ISFIXED)));

                    media.add(m);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null)
                c.close();

            this.closeDatabase();
        }
        return media;
    }

    /*
     * getting all media by day
     */
    public List<Media> getAllMediaByDay(int day) {
        List<Media> media = new ArrayList<Media>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDIA + " WHERE " + KEY_DAY + " = " + day;

        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    Media m = new Media();
                    m.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    m.setFolder_id(c.getInt(c.getColumnIndex(KEY_FOLDER_ID)));
                    m.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                    m.setPictureTaken(c.getLong(c.getColumnIndex(KEY_PICTURETAKEN)));
                    m.setYear(c.getInt(c.getColumnIndex(KEY_YEAR)));
                    m.setMonth(c.getInt(c.getColumnIndex(KEY_MONTH)));
                    m.setDay(c.getInt(c.getColumnIndex(KEY_DAY)));
                    m.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                    m.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                    m.setPlaceName(c.getString(c.getColumnIndex(KEY_PLACENAME)));
                    m.setPath(c.getString(c.getColumnIndex(KEY_PATH)));
                    m.setThumbnail_path(c.getString(c.getColumnIndex(KEY_THUMBNAILPATH)));
                    m.setIsFixed(c.getInt(c.getColumnIndex(KEY_ISFIXED)));

                    media.add(m);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null)
                c.close();

            this.closeDatabase();
        }
        return media;
    }

    /*
     * updating media
     */
    public int updateMedia(Media media) {
        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FOLDER_ID, media.getFolder_id());
        values.put(KEY_NAME, media.getName());
        values.put(KEY_PICTURETAKEN, media.getPictureTaken());
        values.put(KEY_YEAR, media.getYear());
        values.put(KEY_MONTH, media.getMonth());
        values.put(KEY_DAY, media.getDay());
        values.put(KEY_LATITUDE, media.getLatitude());
        values.put(KEY_LONGITUDE, media.getLongitude());
        values.put(KEY_PLACENAME, media.getPlaceName());
        values.put(KEY_PATH, media.getPath());
        values.put(KEY_THUMBNAILPATH, media.getThumbnail_path());
        values.put(KEY_ISFIXED, media.getIsFixed());

        int result = db.update(TABLE_MEDIA, values, KEY_ID + " = ? ", new String[]{String.valueOf(media.getId())});
        this.closeDatabase();
        return result;
    }

    /*
     * Deleting media by id
     */
    public void deleteMedia(int id) {
        Log.d("DatabaseHelper", "deleteMedia(id) 호출");
        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.openDatabase();

        db.delete(TABLE_MEDIA, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        deleteMediaTagByMediaId(id);

        this.closeDatabase();
    }

    public void deleteAllMedia() {
        Log.d("DatabaseHelper", "deleteAllMedia() 호출");
        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.openDatabase();

        //KEY_ISFIXED = 1인 놈은 지우지 않는다
        db.execSQL("DELETE FROM " + TABLE_MEDIA + " WHERE " + KEY_ISFIXED + " = 0");
        deleteAllMediaTag();

        this.closeDatabase();
    }

    /**
     * **************** TAG ******************
     */

    /*
     * creating tag at media_id
     */
    public int createTag(String tag_name, int media_id) {
        CONSTANT.FLAG_REFRESH = true;//MainActivity에서 뷰를 새로 그릴 필요가 있음

        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.openDatabase();

        int tag_id = getTagIdByTagName(tag_name);
        if (tag_id == 0) {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, tag_name);
            if (this.getMediaById(media_id).getPath() != null) {//가이드가 아닐때에만
                Palette p = Palette.generate(BitmapFactory.decodeFile(this.getMediaById(media_id).getPath()));
                values.put(KEY_COLOR, p.getVibrantColor(0x88000000));
            }
            else{//가이드 일때
                values.put(KEY_COLOR, 0x88000000);
            }

            tag_id = (int) db.insert(TABLE_TAG, null, values);
        }

        int result = (createMediaTag(tag_id, media_id) == -1) ? -1 : tag_id;

        this.closeDatabase();
        return result;

    }

    public int createTag(String tag_name, int media_id, int folder_id) {
        CONSTANT.FLAG_REFRESH = true;//MainActivity에서 뷰를 새로 그릴 필요가 있음

        SQLiteDatabase db = this.openDatabase();

        int tag_id = getTagIdByTagName(tag_name);
        if (tag_id == 0) {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, tag_name);
            if (this.getMediaById(media_id).getPath() != null) {//가이드가 아닐때에만
                Palette p = Palette.generate(BitmapFactory.decodeFile(this.getMediaById(media_id).getPath()));
                values.put(KEY_COLOR, p.getSwatches().get(0).getRgb());
            }
            else{//가이드 일때
                values.put(KEY_COLOR, 0x88000000);
            }

            tag_id = (int) db.insert(TABLE_TAG, null, values);
        }

        createFolderTag(tag_id, folder_id);
        return (createMediaTag(tag_id, media_id) == -1) ? -1 : tag_id;
    }


    /*
    * creating tag at folder_id
    */
    public int createTagByFolder(String tag_name, int folder_id, int color) {
        CONSTANT.FLAG_REFRESH = true;//MainActivity에서 뷰를 새로 그릴 필요가 있음

        List<Media> media = getAllMediaByFolder(folder_id);

        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.openDatabase();

        //일단 태그 만들어줌
        int tag_id = getTagIdByTagName(tag_name);
        if (tag_id == 0) {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, tag_name);
            values.put(KEY_COLOR, color);

            tag_id = (int) db.insert(TABLE_TAG, null, values);
        }

        for (int i = 0, n = media.size(); i < n; i++) {
            createMediaTag(tag_id, media.get(i).getId());
        }

        this.closeDatabase();
        return tag_id;
    }

    /*
     * getting all tags
     */
    public List<Tag> getAllTags() {
        List<Tag> tags = new ArrayList<Tag>();
        String selectQuery = "SELECT * FROM " + TABLE_TAG;

        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    Tag t = new Tag();
                    t.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    t.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                    t.setColor(c.getInt(c.getColumnIndex(KEY_COLOR)));


                    tags.add(t);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null)
                c.close();

            this.closeDatabase();
        }
        return tags;
    }

    /*
     * getting Tag by Tag id
     */
    public Tag getTagByTagId(int tag_id) {
        Tag tag = new Tag();
//        List<Tag> tags = new ArrayList<Tag>();
        String selectQuery = "SELECT * FROM " + TABLE_TAG + " WHERE " + KEY_ID + " = " + tag_id;

        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                tag.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                tag.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                tag.setColor(c.getInt(c.getColumnIndex(KEY_COLOR)));

            }
        } finally {
            if (c != null)
                c.close();

            this.closeDatabase();
        }
        return tag;
    }

    /*
    * getting Tags by Media id
    */
    public List<Tag> getAllTagsByMediaId(int media_id) {
        List<Tag> tags = new ArrayList<Tag>();
//        List<Tag> tags = new ArrayList<Tag>();
        String selectQuery = "SELECT " + TABLE_TAG + "." + KEY_ID + ", " + TABLE_TAG + "." + KEY_NAME + ", " + TABLE_TAG + "." + KEY_COLOR + " FROM " + TABLE_TAG + " INNER JOIN " + TABLE_MEDIA_TAG + " ON " + TABLE_TAG + "." + KEY_ID + " = " + TABLE_MEDIA_TAG + "." + KEY_TAG_ID + " AND " + TABLE_MEDIA_TAG + "." + KEY_MEDIA_ID + " = " + media_id;

        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    Tag t = new Tag();
                    t.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    t.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                    t.setColor(c.getInt(c.getColumnIndex(KEY_COLOR)));

                    tags.add(t);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null)
                c.close();
            this.closeDatabase();
        }
        return tags;
    }

    /*
* getting Tags by Media id
*/
    public List<Tag> getAllTagsByFolderId(int folder_id) {
        List<Media> medias = getAllMediaByFolder(folder_id);

        List<Tag> tags = new ArrayList<Tag>();
//        List<Tag> tags = new ArrayList<Tag>();
        String selectQuery = "SELECT DISTINCT " + TABLE_TAG + "." + KEY_ID + ", " + TABLE_TAG + "." + KEY_NAME + ", " + TABLE_TAG + "." + KEY_COLOR + " FROM " + TABLE_TAG + " INNER JOIN " + TABLE_MEDIA_TAG + " ON " + TABLE_TAG + "." + KEY_ID + " = " + TABLE_MEDIA_TAG + "." + KEY_TAG_ID + " WHERE " + TABLE_MEDIA_TAG + "." + KEY_MEDIA_ID + " IN (";

        int mediaSize = medias.size();
        for (int i = 0; i < mediaSize; i++) {
            selectQuery += medias.get(i).getId();
            if (i != mediaSize - 1)
                selectQuery += ", ";
        }
        selectQuery += ")";

        //Log.d("adadsa", selectQuery);

        Cursor c = null;
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            SQLiteDatabase db = this.openDatabase();
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    Tag t = new Tag();
                    t.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    t.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                    t.setColor(c.getInt(c.getColumnIndex(KEY_COLOR)));

                    tags.add(t);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null)
                c.close();

            this.closeDatabase();
        }
        return tags;
    }


    /*
    * getting Tag by Tag name
    */
    public int getTagIdByTagName(String tag_name) {
        String selectQuery = "SELECT * FROM " + TABLE_TAG + " WHERE " + KEY_NAME + " = \"" + tag_name + "\"";

        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                return c.getInt(c.getColumnIndex(KEY_ID));
            }
        } finally {
            if (c != null)
                c.close();

            this.closeDatabase();
        }
        return 0;
    }


    /*
     * updating tag
     * 태그 이름이 바뀔 경우 사용
     */
    public int updateTag(Tag tag) {
        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, tag.getId());
        values.put(KEY_NAME, tag.getName());
        values.put(KEY_COLOR, tag.getColor());


        int result = db.update(TABLE_TAG, values, KEY_ID + " = ? ", new String[]{String.valueOf(tag.getId())});
        this.closeDatabase();
        return result;
    }

    /*
     * Deleting tag by id
     */
    public void deleteTagById(int id) {
        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.openDatabase();
        db.delete(TABLE_TAG, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        deleteMediaTagByTagId(id);
        this.closeDatabase();
    }

    /*
     * Deleting tag by name
     */
    public void deleteTagByName(String name) {
        int tag_id = getTagIdByTagName(name);
        deleteTagById(tag_id);
        deleteMediaTagByTagId(tag_id);
    }


    public void deleteAllTag() {
        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.openDatabase();
        db.execSQL("DELETE FROM " + TABLE_TAG);
        deleteAllMediaTag();
        this.closeDatabase();
    }


    /**
     * **************** MEDIA_TAG ******************
     */

    /*
     * creating media to tag relation
     */
    public int createMediaTag(Media_Tag relation) {
        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.openDatabase();

        int id = getMediaTagByIds(relation.getTag_id(), relation.getMedia_id());
        if (id == 0) {
            ContentValues values = new ContentValues();
            values.put(KEY_TAG_ID, relation.getTag_id());
            values.put(KEY_MEDIA_ID, relation.getMedia_id());

            id = (int) db.insert(TABLE_MEDIA_TAG, null, values);
        }
        this.closeDatabase();
        return id;
    }

    /*
     * creating media to tag relation
     */
    public int createMediaTag(int tag_id, int media_id) {
        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.openDatabase();

        int id = getMediaTagByIds(tag_id, media_id);
        if (id == 0) {
            ContentValues values = new ContentValues();
            values.put(KEY_TAG_ID, tag_id);
            values.put(KEY_MEDIA_ID, media_id);

            id = (int) db.insert(TABLE_MEDIA_TAG, null, values);
        } else {
            id = -1;
        }

        this.closeDatabase();
        return id;
    }

    public int getMediaTagByIds(int tag_id, int media_id) {
        String selectQuery = "SELECT * FROM " + TABLE_MEDIA_TAG + " WHERE " + KEY_MEDIA_ID + " = " + media_id + " AND " + KEY_TAG_ID + " = " + tag_id;

        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                return c.getInt(c.getColumnIndex(KEY_ID));
            }
        } finally {
            if (c != null)
                c.close();
            this.closeDatabase();
        }
        return 0;
    }

    public List<Media_Tag> getAllMediaTag() {
        List<Media_Tag> mediaTag = new ArrayList<Media_Tag>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDIA_TAG;

        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.openDatabase();

        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    Media_Tag t = new Media_Tag();
                    t.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    t.setTag_id(c.getInt(c.getColumnIndex(KEY_TAG_ID)));
                    t.setMedia_id(c.getInt(c.getColumnIndex(KEY_MEDIA_ID)));

                    mediaTag.add(t);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null)
                c.close();
            this.closeDatabase();
        }
        return mediaTag;
    }


    /*
     * deleting single media to tag relation
     */
    public void deleteMediaTag(int tag_id, int media_id) {
        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.openDatabase();
        db.delete(TABLE_MEDIA_TAG, KEY_TAG_ID + " = ? AND " + KEY_MEDIA_ID + " = ?", new String[]{String.valueOf(tag_id), String.valueOf(media_id)});
        this.closeDatabase();
    }

    public void deleteMediaTag(String tag_name, int media_id) {
        SQLiteDatabase db = this.openDatabase();
        int tag_id = getTagIdByTagName(tag_name);
        db.delete(TABLE_MEDIA_TAG, KEY_TAG_ID + " = ? AND " + KEY_MEDIA_ID + " = ?", new String[]{String.valueOf(tag_id), String.valueOf(media_id)});
        this.closeDatabase();
    }

    /*
     * deleting media to tag relation by tag_id
     */
    public void deleteMediaTagByTagId(int tag_id) {
        SQLiteDatabase db = this.openDatabase();

        db.delete(TABLE_MEDIA_TAG, KEY_TAG_ID + " = ?", new String[]{String.valueOf(tag_id)});
        this.closeDatabase();
    }

    /*
     * deleting media to tag relation by media_id
     */
    public void deleteMediaTagByMediaId(int media_Id) {
        SQLiteDatabase db = this.openDatabase();

        db.delete(TABLE_MEDIA_TAG, KEY_MEDIA_ID + " = ?", new String[]{String.valueOf(media_Id)});
        this.closeDatabase();
    }

    /*
     * deleting media to tag relation by media_id
     */
    public void deleteAllMediaTag() {
        SQLiteDatabase db = this.openDatabase();

        db.execSQL("DELETE FROM " + TABLE_MEDIA_TAG);
        this.closeDatabase();
    }

    /**
     * **************** FOLDER_TAG ******************
     */

    /*
     * creating media to tag relation
     */
    public int createFolderTag(int tag_id, int folder_id) {
        SQLiteDatabase db = this.openDatabase();

        int id;

        Folder_Tag ft = getFolderTagByIds(tag_id, folder_id);
        if (ft == null) {
            ContentValues values = new ContentValues();
            values.put(KEY_TAG_ID, tag_id);
            values.put(KEY_FOLDER_ID, folder_id);
            values.put(KEY_COUNT, 1);

            id = (int) db.insert(TABLE_FOLDER_TAG, null, values);
        } else {
            id = -1;
            ft.setCount(ft.getCount()+1);
            updateFolderTag(ft);
        }

        return id;
    }

    public Folder_Tag getFolderTagByIds(int tag_id, int folder_id) {
        String selectQuery = "SELECT * FROM " + TABLE_FOLDER_TAG + " WHERE " + KEY_FOLDER_ID + " = " + folder_id + " AND " + KEY_TAG_ID + " = " + tag_id;


        SQLiteDatabase db = this.openDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                Folder_Tag t = new Folder_Tag();
                t.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                t.setTag_id(c.getInt(c.getColumnIndex(KEY_TAG_ID)));
                t.setFolder_id(c.getInt(c.getColumnIndex(KEY_FOLDER_ID)));
                t.setCount(c.getInt(c.getColumnIndex(KEY_COUNT)));

                return t;
            }
        } finally {
            if (c != null)
                c.close();
        }

        return null;
    }

    public Folder_Tag getFolderTagById(int id) {
        String selectQuery = "SELECT * FROM " + TABLE_FOLDER_TAG + " WHERE " + KEY_ID + " = " + id;

        SQLiteDatabase db = this.openDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                Folder_Tag t = new Folder_Tag();
                t.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                t.setTag_id(c.getInt(c.getColumnIndex(KEY_TAG_ID)));
                t.setFolder_id(c.getInt(c.getColumnIndex(KEY_FOLDER_ID)));
                t.setCount(c.getInt(c.getColumnIndex(KEY_COUNT)));

                return t;
            }
        } finally {
            if (c != null)
                c.close();
        }

        return null;
    }


    /*
     * Updating a FolderTag by id
     * return number of updated row
     */
    public int updateFolderTag(Folder_Tag ft) {
        SQLiteDatabase db = this.openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, ft.getId());
        values.put(KEY_TAG_ID, ft.getTag_id());
        values.put(KEY_FOLDER_ID, ft.getFolder_id());
        values.put(KEY_COUNT, ft.getCount());
        return db.update(TABLE_FOLDER_TAG, values, KEY_ID + " = ? ", new String[]{String.valueOf(ft.getId())});
    }


    /*
     * deleting single media to tag relation
     */
    public void deleteFolderTag(int tag_id, int folder_id) {
        Folder_Tag ft = getFolderTagByIds(tag_id, folder_id);
        ft.setCount(ft.getCount() - 1);
        if(ft.getCount() <= 0){
            SQLiteDatabase db = this.getWritableDatabase();
            Log.d("db deleteFolderTag", " "+db.delete(TABLE_FOLDER_TAG, KEY_ID + " = ?", new String[]{String.valueOf(ft.getId())}));
        }else{
            updateFolderTag(ft);
        }
    }

//    public void deleteFolderTag(int id) {
//        Folder_Tag ft = getFolderTagById(id);
//        ft.setCount(ft.getCount() - 1);
//        if(ft.getCount() <= 0){
//            SQLiteDatabase db = this.getWritableDatabase();
//            db.delete(TABLE_FOLDER_TAG, KEY_ID + " = ?", new String[]{String.valueOf(ft.getId())});
//        }else{
//            updateFolderTag(ft);
//        }
//
//    }
//
//
//    /*
//     * deleting media to tag relation by tag_id
//     */
//    public void deleteFolderTagByTagId(int tag_id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_FOLDER_TAG, KEY_TAG_ID + " = ?", new String[]{String.valueOf(tag_id)});
//    }
//
//    /*
//     * deleting media to tag relation by media_id
//     */
//    public void deleteFolderTagByFolderId(int folder_Id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_FOLDER_TAG, KEY_FOLDER_ID + " = ?", new String[]{String.valueOf(folder_Id)});
//    }
//
//    /*
//     * deleting media to tag relation by media_id
//     */
//    public void deleteAllFolderTag() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DELETE FROM " + TABLE_FOLDER_TAG);
//    }
//


    /**
     * **************** MANAGER ******************
     */
    /*
     * creating Manager
     */
    public int createManager(Manager manager) {
        SQLiteDatabase db = this.openDatabase();


        db.delete(TABLE_MANAGER, null, null);//기존의 데이터들을 모두 삭제한다.

        ContentValues values = new ContentValues();
        values.put(KEY_TOTALPICTURENUM, manager.getTotalPictureNum());
        values.put(KEY_REALPICTURENUM, manager.getRealPictureNum());
        values.put(KEY_AVERAGEINTERVAL, manager.getAverageInterval());
        values.put(KEY_STANDARDDERIVATION, manager.getStandardDerivation());

        int result = (int) db.insert(TABLE_MANAGER, null, values);
        this.closeDatabase();
        return result;
    }

    public Manager getManager() {
        String selectQuery = "SELECT * FROM " + TABLE_MANAGER;

        SQLiteDatabase db = this.openDatabase();


        Cursor c = null;
        Manager m = new Manager();
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                m.setTotalPictureNum(c.getInt(c.getColumnIndex(KEY_TOTALPICTURENUM)));
                m.setRealPictureNum(c.getInt(c.getColumnIndex(KEY_REALPICTURENUM)));
                m.setAverageInterval(c.getLong(c.getColumnIndex(KEY_AVERAGEINTERVAL)));
                m.setStandardDerivation(c.getLong(c.getColumnIndex(KEY_STANDARDDERIVATION)));
            }
        } finally {
            if (c != null)
                c.close();
            this.closeDatabase();
        }
        return m;
    }

    /**
     * **************** NOTIFICATION ******************
     */
    public int createNotification(NotificationM n) {
        SQLiteDatabase db = this.openDatabase();


        db.delete(TABLE_NOTIFICATION, null, null);//기존의 데이터들을 모두 삭제한다.

        ContentValues values = new ContentValues();
        values.put(KEY_LASTNOTIFICATION, n.getNotificationTime());
        values.put(KEY_LASTPICTUREID, n.getLastPictureID());

        int result = (int) db.insert(TABLE_NOTIFICATION, null, values);
        this.closeDatabase();
        return result;
    }

    public NotificationM getNotification() {
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATION;

        SQLiteDatabase db = this.openDatabase();


        Cursor c = null;
        NotificationM n = null;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToLast()) {//마지막으로 푸시한 시간을 얻어온다
                n = new NotificationM();
                n.setNotificationTime(c.getLong(c.getColumnIndex(KEY_LASTNOTIFICATION)));
                n.setLastPictureID(c.getInt(c.getColumnIndex(KEY_LASTPICTUREID)));
            }
        } finally {
            if (c != null)
                c.close();
            this.closeDatabase();
        }
        return n;
    }

    /**
     * **************** GUIDE ******************
     */
    public int createGuide(int guide) {
        SQLiteDatabase db = this.openDatabase();


        db.delete(TABLE_GUIDE, null, null);//기존의 데이터들을 모두 삭제한다.

        ContentValues values = new ContentValues();
        values.put(KEY_GUIDEENDED, guide);

        int result = (int) db.insert(TABLE_GUIDE, null, values);
        this.closeDatabase();
        return result;
    }

    public int getGuide() {
        String selectQuery = "SELECT * FROM " + TABLE_GUIDE;

        SQLiteDatabase db = this.openDatabase();

        Cursor c = null;
        int guideEnded = 0;
        try {
            c = db.rawQuery(selectQuery, null);

            if (c.moveToLast()) {
                guideEnded = c.getInt(c.getColumnIndex(KEY_GUIDEENDED));
            }
        } finally {
            if (c != null)
                c.close();
            this.closeDatabase();
        }
        return guideEnded;//가이드가 완료되지 않았다면 0을 반환, 완료되었으면 1을 반환함
    }
}
