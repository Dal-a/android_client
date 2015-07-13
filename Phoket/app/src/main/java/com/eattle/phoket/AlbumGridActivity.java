package com.eattle.phoket;

import android.content.ComponentCallbacks2;
import android.content.ContentResolver;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.eattle.phoket.Card.manager.CardData;
import com.eattle.phoket.Card.manager.CardManager;
import com.eattle.phoket.device.BlockDevice;
import com.eattle.phoket.helper.DatabaseHelper;
import com.eattle.phoket.model.Folder;
import com.eattle.phoket.model.Media;
import com.eattle.phoket.model.Tag;
import com.eattle.phoket.view.SquareImageView;

import java.util.ArrayList;
import java.util.List;


public class AlbumGridActivity extends AppCompatActivity {
    private final static String EXTRA_TAG = "ALBUM_GRID";

//    String TAG="AlbumGridActivity";
    private DatabaseHelper db;

    private MaterialListView mGridView;
    private ProgressBar mProgressBar;

    //folderId or tagId
    private int id;
    //스토리로 들어왔는지, 태그로 들어왔는지, 디폴트 태그로 들어왔는지
    private int kind;

    private List<Media> mMediaList;


//    TextView titleText;
//    ImageView titleImage;

/*    GridView mGrid;
    ImageAdapter Adapter;
    List<Media> mMediaList;
    ArrayList<ImageView> mImageList;


    int Id;//folderId가 될수도 있고 TagId가 될수도 있다
    int kind;
*/
    String titleName;
    String titleImagePath;
    Media mediaByTag;//태그가 눌려진 사진
    int mediaId;//태그가 눌려진 사진의 아이디
    String tagName;//태그

    private BlockDevice blockDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CONSTANT.actList.add(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_grid);

        db = DatabaseHelper.getInstance(getApplicationContext());

//        titleText = (TextView) findViewById(R.id.titleText);
//        titleImage = (ImageView) findViewById(R.id.titleImage);

        Intent intent = new Intent(this.getIntent());

        id = intent.getIntExtra("id", -1);//folderId가 될수도 있고 TagId가 될 수도 있다
        kind = intent.getIntExtra("kind", -1);
        if(kind == CONSTANT.DEFAULT_TAG){
            mediaId = intent.getIntExtra("mediaId", -1);
            mediaByTag = db.getMediaById(mediaId);
            tagName = intent.getStringExtra("tagName");
        }
        refreshGrid();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //폴더(스토리)의 제목 등록
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(titleName);

        //폴더(스토리)의 대표사진 등록
        loadBackdrop();

        mGridView = (MaterialListView)findViewById(R.id.GridList);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        setupMaterialListView();


        mGridView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(CardItemView view, int position) {
                CardData tag = (CardData)view.getTag();

                Intent intent = new Intent(getApplicationContext(), AlbumFullActivity.class);
                intent.putParcelableArrayListExtra("mediaList", new ArrayList<Parcelable>(mMediaList));
                intent.putExtra("position", tag.getId());//어디에서 시작할지
                intent.putExtra("IDForStoryOrTag", id);// 스토리를 위한 folderID, 또는 사용자 태그를 위한 tagID
                intent.putExtra("tagName",tagName);// 디폴트 태그를 위한 tagName
                intent.putExtra("mediaId",mediaId);// 태그클릭->그리드뷰->풀픽쳐 인 경우에 필요
                intent.putExtra("kind", kind);// 그리드 종류(스토리,디폴트태그,태그)

                startActivity(intent);

            }

            @Override
            public void onItemLongClick(CardItemView view, int position) {
//                Log.d("LONG_CLICK", view.getTag().toString());
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.storyStart);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlbumFullActivity.class);
                intent.putParcelableArrayListExtra("mediaList", new ArrayList<Parcelable>(mMediaList));
                intent.putExtra("position",-1);//-1을 넘겨주면 스토리 '맨 처음'부터 시작(제목화면부터)
                intent.putExtra("IDForStoryOrTag", id);// folder ID
                intent.putExtra("tagName",tagName);//디폴트 태그는 tag이름을 통해서 작동
                intent.putExtra("mediaId",mediaId);// 태그클릭->그리드뷰->풀픽쳐 인 경우에 필요
                intent.putExtra("kind", kind);// 그리드 종류(스토리,디폴트태그,태그)
                startActivity(intent);
            }
        });

        new InitializeApplicationsTask().execute();

        //show progress
        mGridView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);




/*        mGrid = (GridView) findViewById(R.id.imagegrid);
        Adapter = new ImageAdapter(this);
        mGrid.setAdapter(Adapter);
        mGrid.setOnItemClickListener(mItemClickListener);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        LinearLayout actionBarLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.actionbar_album, null);
        TextView actionBarTitle = (TextView)actionBarLayout.findViewById(R.id.actionbar_title);
        switch (kind){
            case CONSTANT.FOLDER:
                actionBarTitle.setText(getString(R.string.title_section2));
                break;
            case CONSTANT.TAG:
            case CONSTANT.DEFAULT_TAG:
                actionBarTitle.setText(getString(R.string.title_section3));
                break;
        }
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.LEFT);

        ImageView drawerImageView = (ImageView)actionBarLayout.findViewById(R.id.home_icon);

        //홈버튼
        drawerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int actSize = CONSTANT.actList.size();
                for (int i = 0; i < actSize; i++) {
                    CONSTANT.actList.get(i).finish();
                    finish();
                }
            }
        });

        ImageView drawerImageViewCheck = (ImageView)actionBarLayout.findViewById(R.id.search_icon);

        drawerImageViewCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        actionBar.setCustomView(actionBarLayout, params);
        actionBar.setDisplayHomeAsUpEnabled(false);*/


    }



    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this)
                .load(titleImagePath)
                .thumbnail(0.1f)
                .into(imageView);
    }

    public void setupMaterialListView() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mGridView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onResume() {
        refreshGrid();
        //변경사항 적용
        new InitializeApplicationsTask().execute();
        super.onResume();
    }

    @Override
    public void onStop() {
        Glide.get(this).clearMemory();
        Glide.get(this).trimMemory(ComponentCallbacks2.TRIM_MEMORY_MODERATE);
        super.onStop();
    }

    /*
    public void onClick(View v){
        switch(v.getId()){
            case R.id.storyStart://스토리 시작

                Intent intent = new Intent(getApplicationContext(), AlbumFullActivity.class);
                intent.putParcelableArrayListExtra("mediaList", new ArrayList<Parcelable>(mMediaList));
                intent.putExtra("position",-1);//-1을 넘겨주면 스토리 '맨 처음'부터 시작(제목화면부터)
                intent.putExtra("IDForStoryOrTag", id);// folder ID
                intent.putExtra("tagName",tagName);//디폴트 태그는 tag이름을 통해서 작동
                intent.putExtra("mediaId",mediaId);// 태그클릭->그리드뷰->풀픽쳐 인 경우에 필요
                intent.putExtra("kind", kind);// 그리드 종류(스토리,디폴트태그,태그)
                startActivity(intent);
                break;
        }
    }

    //그리드 뷰 아이템 클릭
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent = new Intent(getApplicationContext(), AlbumFullActivity.class);
            intent.putParcelableArrayListExtra("mediaList", new ArrayList<Parcelable>(mMediaList));
            intent.putExtra("position", position);//어디에서 시작할지
            intent.putExtra("IDForStoryOrTag", Id);// 스토리를 위한 folderID, 또는 사용자 태그를 위한 tagID
            intent.putExtra("tagName",tagName);// 디폴트 태그를 위한 tagName
            intent.putExtra("mediaId",mediaId);// 태그클릭->그리드뷰->풀픽쳐 인 경우에 필요
            intent.putExtra("kind", kind);// 그리드 종류(스토리,디폴트태그,태그)

            startActivity(intent);
        }
    };*/

    public void refreshGrid() {
        //grid view를 업데이트 한다(백버튼 또는 x버튼으로 들어왔을 때)
        //1. CONSTANT.FOLDER 2. CONSTANT.DEFAULT_TAG 3.CONSTANT.TAG
        if (kind == CONSTANT.FOLDER) {//스토리(폴더)로 보고있을 때
            Log.d("onResume()", "스토리(폴더)");
            Folder f = db.getFolder(id);
            mMediaList = db.getAllMediaByFolder(id);

            titleName = CONSTANT.convertFolderNameToStoryName(f.getName());
            titleImagePath = f.getImage();//대표 이미지의 경로를 얻는다
            //titleThumbnailPath = f.getThumbNail_path();//대표 사진의 썸네일 경로를 얻는다.

        } else if (kind == CONSTANT.DEFAULT_TAG) {//기본 태그(날짜, 장소)를 타고 들어왔을 경우
            Log.d("onResume()", "디폴트 태그");
            if (tagName.contains("년")) {
                mMediaList = db.getAllMediaByYear(mediaByTag.getYear());
            } else if (tagName.contains("월")) {
                mMediaList = db.getAllMediaByMonth(mediaByTag.getMonth());
            } else if (tagName.contains("일")) {
                mMediaList = db.getAllMediaByDay(mediaByTag.getDay());
            }
            titleName = tagName + "의 추억";
            titleImagePath = mMediaList.get(0).getPath();

        } else if (kind == CONSTANT.TAG) {
            Log.d("onResume()", "사용자가 등록한 태그");
            Tag t = db.getTagByTagId(id);
            //Media mediaByTag = db.getMediaById(intent.getIntExtra("mediaId", -1));
            mMediaList = db.getAllMediaByTagId(id);

            titleName = t.getName();
            titleImagePath = mMediaList.get(0).getPath();

        }
    }

    //백버튼을 눌렀을 때, 메모리 정리를 한다
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK://백버튼을 통제(비밀번호 유지를 위해)
                CONSTANT.actList.remove(this);

                finish();//현재 띄워져 있던 albumGridActivity 종료(메모리 확보를 위해)
                return false;
        }
        return true;
    }

    /**
     * A simple AsyncTask to load the list of applications and display them
     */
    private class InitializeApplicationsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Query the applications

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            mProgressBar.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);

            //set data for list
            mGridView.clear();

            int num = mMediaList.size();
            for(int i = 0; i < num; i++){
                addCard(mMediaList.get(i), i);
            }

            super.onPostExecute(result);
        }
    }

    private void addCard(Media m, int order){

        if(m.getThumbnail_path() == null)//내장 썸네일이 혹시 존재하지 않을 경우에만
            CardManager.setMediaItem(mGridView, getBaseContext(), m.getId(), order, m.getPath());
        else
            CardManager.setMediaItem(mGridView, getBaseContext(), m.getId(), order, m.getThumbnail_path());

    }
/*
    class ImageAdapter extends BaseAdapter {
        private Context mContext;
        public ImageAdapter(Context context) {
            mImageList = new ArrayList<>();
            mContext = context;
        }

        public int getCount() {
            return mMediaList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            SquareImageView imageView;
            if (convertView == null) {
                imageView = new SquareImageView(mContext);
            } else {
                imageView = (SquareImageView) convertView;
            }


            String path = mMediaList.get(position).getThumbnail_path();

            if(path == null){//내장 썸네일이 혹시 존재하지 않을 경우에만
                Log.d(TAG, "썸네일이 존재하지 않아 직접 생성");
                Bitmap bitmap = CONSTANT.decodeSampledBitmapFromPath(path, 10);//직접 만든다
                imageView.setImageBitmap(bitmap);
            }
            else {//내장 썸네일이 존재하는 경우
                Glide.with(getApplicationContext())
                        .load(path)
                        .thumbnail(0.1f)
                        .centerCrop()
                        .into(imageView);
                imageView.setAdjustViewBounds(true);
            }

            return imageView;
        }
    }
    //        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
//        LinearLayout albumLayout = (LinearLayout) findViewById(R.id.albumLayout);
//        albumLayout.startAnimation(animationFadeIn);
*/
}
