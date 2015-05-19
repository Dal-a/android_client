package com.eattle.phoket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.eattle.phoket.device.BlockDevice;
import com.eattle.phoket.helper.DatabaseHelper;
import com.eattle.phoket.model.Folder;
import com.eattle.phoket.model.Media;
import com.eattle.phoket.model.Media_Tag;
import com.eattle.phoket.model.Tag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AlbumGridActivity extends ActionBarActivity {

    DatabaseHelper db;

    TextView titleText;
    ImageView titleImage;

    GridView mGrid;
    ImageAdapter Adapter;
    List<Media> mMediaList;

    int Id;
    int kind;
    String titleName;
    String titleImagePath;
    String titleImageId;

    int totalPictureNum;

    private BlockDevice blockDevice;

    @Override
    protected void onResume() {
        super.onResume();
        //grid view를 업데이트 한다
        if (kind == CONSTANT.FOLDER) {//스토리(폴더)로 보고있을 때
            Log.d("AlbumGridActivity", "Grid onResume() 호출");
            Folder f = db.getFolder(Id);
            titleName = f.getName();
            titleImagePath = f.getImage();
            titleImageId = f.getThumbNail_name();
            totalPictureNum = f.getPicture_num();

            //폴더(스토리)의 제목 등록
            titleText.setText(titleName);
            //폴더(스토리)의 대표사진 등록
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = 4;//기존 해상도의 1/4로 줄인다
            Bitmap bitmap = BitmapFactory.decodeFile(titleImagePath, opt);
            titleImage.setImageBitmap(bitmap);
            titleImage.setAlpha(0.4f);

            mMediaList = db.getAllMediaByFolder(Id);

            Adapter.notifyDataSetChanged();
        }
        else{//태그로 보고 있을 때

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_grid);
        Log.d("asdf","Grid onCreate() 호출");

        // fileSystem.incaseSearchTable(blockDevice);//탐색테이블 만듬 초기화

        db = DatabaseHelper.getInstance(getApplicationContext());

        titleText = (TextView) findViewById(R.id.titleText);
        titleImage = (ImageView) findViewById(R.id.titleImage);


        Intent intent = new Intent(this.getIntent());
        //인텐트로부터 사진 검색을 위한 (folderId) 초기화
        Id = intent.getIntExtra("id", -1);//folderId가 될수도 있고 TagId가 될 수도 있다
        kind = intent.getIntExtra("kind", -1);

        if (kind == CONSTANT.FOLDER) {
            Folder f = db.getFolder(Id);
            mMediaList = db.getAllMediaByFolder(Id);

            titleName = CONSTANT.convertFolderNameToStoryName(f.getName());
            titleImagePath = f.getImage();//대표 이미지의 경로를 얻는다
            titleImageId = f.getThumbNail_name();//대표 사진의 아이디를 얻는다
            totalPictureNum = f.getPicture_num();//폴더(스토리)의 총 사진 개수
        } else if (intent.getIntExtra("kind", -1) == CONSTANT.DEFAULT_TAG) {//기본 태그(날짜, 장소)를 타고 들어왔을 경우
            Log.d("asdf","2번째 else");
            Media m = db.getMediaById(intent.getIntExtra("mediaId", -1));
            String tagName = intent.getStringExtra("tagName");
            if (tagName.contains("년")) {
                mMediaList = db.getAllMediaByYear(m.getYear());
            } else if (tagName.contains("월")) {
                mMediaList = db.getAllMediaByMonth(m.getMonth());
            } else if (tagName.contains("일")) {
                mMediaList = db.getAllMediaByDay(m.getDay());
            }
            titleName = tagName + "의 추억";
            titleImagePath = mMediaList.get(0).getPath();
            totalPictureNum = mMediaList.size();
        } else {
            Log.d("asdf","세번째 else");
            Tag t = db.getTagByTagId(Id);
            Media m = db.getMediaById(intent.getIntExtra("mediaId", -1));
            Folder f = db.getFolder(m.getFolder_id());
            List<Media_Tag> temp = db.getAllMediaTag();
            mMediaList = db.getAllMediaByTagId(Id);

            titleName = t.getName();
            titleImagePath = mMediaList.get(0).getPath();
            totalPictureNum = mMediaList.size();
            //titleImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/"+f.getName()+"/"+ m.getName()+".jpg";
            //titleImagePath = f.getImage();//대표 이미지의 경로를 얻는다
            //titleImageId = f. getThumbNail_name();//대표 이미지의 아이디
            //totalPictureNum = f.getPicture_num();//폴더(스토리)의 총 사진 개수를 얻는다
        }

        //폴더(스토리)의 제목 등록
        titleText.setText(titleName);
        //폴더(스토리)의 대표사진 등록
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = 4;//기존 해상도의 1/4로 줄인다
        Bitmap bitmap = BitmapFactory.decodeFile(titleImagePath, opt);
        titleImage.setImageBitmap(bitmap);
        titleImage.setAlpha(0.4f);

        //그리드 뷰 등록
        mGrid = (GridView) findViewById(R.id.imagegrid);

        Adapter = new ImageAdapter(this);
        mGrid.setAdapter(Adapter);

        mGrid.setOnItemClickListener(mItemClickListener);

//        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
//        LinearLayout albumLayout = (LinearLayout) findViewById(R.id.albumLayout);
//        albumLayout.startAnimation(animationFadeIn);

        //스토리 시작 버튼
        Button storyStart = (Button)findViewById(R.id.storyStart);
        storyStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlbumFullActivity.class);
                intent.putParcelableArrayListExtra("mediaList", new ArrayList<Parcelable>(mMediaList));
                intent.putExtra("position",-1);//-1을 넘겨주면 스토리 '맨 처음'부터 시작(제목화면부터)
                intent.putExtra("folderID", Id);// folder ID
                //intent.putExtra("titleName",titleName);//대표사진의 이름
                //intent.putExtra("titleImagePath",titleImagePath);//대표사진의 경로
                //intent.putExtra("titleImageId",titleImageId);//대표사진의 ID
                //intent.putExtra("totalPictureNum",totalPictureNum);//총 사진 개수
                startActivity(intent);
            }
        });
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), AlbumFullActivity.class);
            intent.putParcelableArrayListExtra("mediaList", new ArrayList<Parcelable>(mMediaList));
            intent.putExtra("position", position);//어디에서 시작할지
            intent.putExtra("folderID", Id);// folder ID

            //intent.putExtra("titleName",titleName);//대표사진의 이름
            //intent.putExtra("titleImagePath",titleImagePath);//대표사진의 경로
            //intent.putExtra("titleImageId",titleImageId);//대표사진의 ID
            //intent.putExtra("totalPictureNum",totalPictureNum);//총 사진 개수
            startActivity(intent);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ImageAdapter extends BaseAdapter {
        private Context mContext;
        public ImageAdapter(Context context) {
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
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
            } else {
                imageView = (ImageView) convertView;
            }

            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + "thumbnail" + "/" + mMediaList.get(position).getName() + ".jpg";

            imageView.setImageURI(Uri.parse(path));
            //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, GridView.AUTO_FIT);
            GridView.LayoutParams params = new GridView.LayoutParams(GridView.AUTO_FIT, 400);
            imageView.setLayoutParams(params);

            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            return imageView;
        }
    }
}
