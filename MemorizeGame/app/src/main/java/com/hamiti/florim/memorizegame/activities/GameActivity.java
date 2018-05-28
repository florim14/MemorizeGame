package com.hamiti.florim.memorizegame.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hamiti.florim.memorizegame.R;
import com.hamiti.florim.memorizegame.adapters.ImageAdapter;
import com.hamiti.florim.memorizegame.interfaces.GetPhotoValues;
import com.hamiti.florim.memorizegame.models.Images;
import com.hamiti.florim.memorizegame.utils.FlickPhotosDetailsResponseFirst;
import com.hamiti.florim.memorizegame.utils.RetrofitClient;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity {

    GridView androidGridView;
    private TextView scorePlayerOne, scorePlayerTwo;
    private ImageView tryImage;

    private int firstOrNot = 0;
    private int lastImageID = -1;
    private int points = 0;
    private int pointsSecond = 0;
    String singleOrMulti = "";
    boolean singleMulti = true;

    List<Bitmap> bitmapListImages = new ArrayList<>();
    List<Integer> randomNumber = new ArrayList<>();
    ArrayList<Images> imagesDataSet = new ArrayList<>();
    private int numberOfSeconds = 120000;

    private String typeOfSearch, levelOfGame, timeOfGame;
    private ProgressBar loadImages;
    private CountDownTimer timerForGame = null;
    final String PREF_NAME = "high_score";
    final String PREF_NAME_2 = "options";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Initialize();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                singleOrMulti= null;
            } else {
                singleOrMulti= extras.getString("SingleOrNot");
            }
        } else {
            singleOrMulti= (String) savedInstanceState.getSerializable("SingleOrNot");
        }
        InitializeParameters();


        if (isNetworkAvailable()) {
            GetPhotoValues apiService =
                    RetrofitClient.getClient().create(GetPhotoValues.class);
            Call<FlickPhotosDetailsResponseFirst> call = apiService.getFlickPhotosDetails("?method=flickr.photos.search&" +
                    "api_key=5f5abcdabdbcc7b600e345788b8c41a6&" +
                    "tags=" + typeOfSearch +"&" +
                    "text=" + typeOfSearch + "&" +
                    "privacy_filter=1&" +
                    "content_type=" + typeOfSearch + "&" +
                    "per_page=" + levelOfGame + "&" +
                    "format=json&" +
                    "nojsoncallback=1");
            call.enqueue(new Callback<FlickPhotosDetailsResponseFirst>() {
                @Override
                public void onResponse(Call<FlickPhotosDetailsResponseFirst> call, Response<FlickPhotosDetailsResponseFirst> response) {

                    String url = "";
                    for (int i = 0; i < response.body().getPhotos().getPhoto().size(); i++) {
                        url = "http://farm" + response.body().getPhotos().getPhoto().get(i).getFarm() + ".staticflickr.com/" +
                                response.body().getPhotos().getPhoto().get(i).getServer() + "/" +
                                response.body().getPhotos().getPhoto().get(i).getId() + "_" +
                                response.body().getPhotos().getPhoto().get(i).getSecret() + "_n.jpg";

                        new DownloadImageTask(tryImage).execute(url);
                        Log.d("Link" , url);
                    }
//                    Toast.makeText(GameActivity.this, "Numri: " + numri, Toast.LENGTH_LONG).show();// + "\n" + new Gson().toJson(response.body()).toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<FlickPhotosDetailsResponseFirst> call, Throwable t) {
                    Toast.makeText(GameActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void InitializeParameters() {
        SharedPreferences sharedPref = getSharedPreferences(PREF_NAME_2, MODE_PRIVATE);
        typeOfSearch = sharedPref.getString("type", "cars");
        levelOfGame = sharedPref.getString("level", "4x4");
        if (levelOfGame.equals("4x5"))
            levelOfGame = 10 + "";
        else
            levelOfGame = 8 + "";

        timeOfGame = sharedPref.getString("time", "Easy");
        if (timeOfGame.equals("Hard"))
            numberOfSeconds = 60000;
        else if (timeOfGame.equals("Medium"))
            numberOfSeconds = 90000;
        else
            numberOfSeconds = 120000;
        
        for (int i = 0; i < Integer.parseInt(levelOfGame) * 2; i++) {
            if (i>=Integer.parseInt(levelOfGame))
                randomNumber.add(i-Integer.parseInt(levelOfGame));
            else
                randomNumber.add(i);
        }
        Collections.shuffle(randomNumber);
    }

    public void addImageToGridView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels - scorePlayerOne.getHeight() - getStatusBarHeight() - (levelOfGame.equals("4x4")?80:100);
        final int width = displayMetrics.widthPixels - 80;

        androidGridView.setAdapter(new ImageAdapter(getApplicationContext(), width, height, (levelOfGame.equals("10")?"5":"4")));
        //Prepare DataSet
        imagesDataSet = prepareDataSet();
        ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(), imagesDataSet, width, height, (levelOfGame.equals("10")?"5":"4"));
        //Now we Connect Adapter To GridView
        androidGridView.setAdapter(imageAdapter);

        //Add Listener For Grid View Item Click
        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {

                if (lastImageID != position &&
                        ((ImageView) parent.getChildAt(position).findViewById(R.id.photoView)).getVisibility() != View.INVISIBLE) {
                    if (firstOrNot == 0) {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            ImageView otherImages = (ImageView) parent.getChildAt(i).findViewById(R.id.photoView);
                            otherImages.setImageResource(R.drawable.back_card);
                        }
                        ImageView changeImage = (ImageView) view.findViewById(R.id.photoView);
                        ImageViewAnimatedChange(getApplicationContext(), changeImage, position);
                        lastImageID = position;
                        firstOrNot++;
                    } else {
                        final ImageView[] changeImage = {(ImageView) view.findViewById(R.id.photoView)};
                        ImageViewAnimatedChange(getApplicationContext(), changeImage[0], position);

                        final boolean truOrNot = (randomNumber.get(lastImageID).equals(randomNumber.get(position)));

                        new CountDownTimer(1000, 1000) {

                            public void onTick(long millisUntilFinished) {
                            }
                            public void onFinish() {

                                if (truOrNot) {
                                    changeImage[0].setVisibility(View.INVISIBLE);
                                    changeImage[0] = parent.getChildAt(lastImageID).findViewById(R.id.photoView);
                                    changeImage[0].setVisibility(View.INVISIBLE);
                                    if(singleOrMulti.equals("Multi")) {
                                        if (singleMulti) {
                                            points += 10;
                                        } else {
                                            pointsSecond += 10;
                                        }
                                        scorePlayerOne.setText("Score1: " + points);
                                        scorePlayerTwo.setText("Score2: " + pointsSecond);
                                    }else{
                                        points += 10;
                                        scorePlayerOne.setText("Score: " + points);
                                    }
                                } else {
                                    if (singleOrMulti.equals("Multi")) {
                                        if (singleMulti) {
                                            points -= 5;
                                            singleMulti = false;
                                        } else {
                                            pointsSecond -= 5;
                                            singleMulti = true;
                                        }
                                        scorePlayerOne.setText("Score1: " + points);
                                        scorePlayerTwo.setText("Score2: " + pointsSecond);
                                    }else{
                                        points -= 5;
                                        scorePlayerOne.setText("Score: " + points);
                                    }
                                    for (int i = 0; i < parent.getChildCount(); i++) {
                                        ImageView otherImages = (ImageView) parent.getChildAt(i).findViewById(R.id.photoView);
                                        otherImages.setImageResource(R.drawable.back_card);
                                    }
                                }
                                lastImageID = -1;

                                boolean winOrNot = true;
                                for (int i = 0; i < parent.getChildCount(); i++) {
                                    ImageView otherImages = (ImageView) parent.getChildAt(i).findViewById(R.id.photoView);
                                    if (otherImages.getVisibility() != View.INVISIBLE) {
                                        winOrNot = false;
                                        break;
                                    }
                                }
                                if (winOrNot) {
                                    if (singleOrMulti.equals("Multi"))
                                    {
                                        if (points > pointsSecond){
                                            Toast.makeText(GameActivity.this, "First player won the game!", Toast.LENGTH_SHORT).show();
                                            finish();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        }else if (points == pointsSecond){
                                            Toast.makeText(GameActivity.this, "Equal!", Toast.LENGTH_SHORT).show();
                                            finish();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        }else{
                                            Toast.makeText(GameActivity.this, "Second player won the game!", Toast.LENGTH_SHORT).show();
                                            finish();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        }
                                    }else {
                                        Toast.makeText(GameActivity.this, "You won the game!", Toast.LENGTH_SHORT).show();
                                        SharedPreferences sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();

                                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                        String date = sharedPref.getString("date", "");
                                        date = currentDateTimeString + "##" + date;
                                        editor.putString("date", date);

                                        date = sharedPref.getString("points", "");
                                        date = points + "##" + date;
                                        editor.putString("points", date);
                                        editor.commit();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    }
                                }
                            }

                        }.start();
                        firstOrNot = 0;
                    }
                }
            }
        });

        //Toast.makeText(getApplicationContext(), "Width: " + width + " Height: " + height, Toast.LENGTH_SHORT).show();


    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            result += TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());

        return result;
    }

    public void ImageViewAnimatedChange(Context context, final ImageView image, final int position) {//final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        final Animation anim_in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image.setImageBitmap(bitmapListImages.get(randomNumber.get(position)));
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });
                image.startAnimation(anim_in);
            }
        });
        image.startAnimation(anim_out);
    }

    private void Initialize() {
        androidGridView = (GridView) findViewById(R.id.gridview);

        scorePlayerOne = (TextView) findViewById(R.id.display_score);
        scorePlayerTwo = (TextView) findViewById(R.id.display_score_2);

        tryImage = (ImageView) findViewById(R.id.try_image);

        loadImages = (ProgressBar) findViewById(R.id.load_images);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //Creating Data Set By Adding 6 flower objects
    private ArrayList<Images> prepareDataSet() {

        ArrayList<Images> imageData = new ArrayList<>();
        Images image;

        int size = randomNumber.size();

        Log.d("Random" , randomNumber + "");
        for (int i = 0; i <size; i++) {
            image = new Images();
            image.setBitmapImage(bitmapListImages.get(randomNumber.get(i)));
            imageData.add(image);
        }

        Log.d("Random", randomNumber + "");

        return imageData;

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bitmapListImages.add(result);
            if (bitmapListImages.size() >= Integer.parseInt(levelOfGame)) {
                addImageToGridView();
                loadImages.setVisibility(View.GONE);
                if (!singleOrMulti.equals("Multi")){
                    timerForGame = new CountDownTimer(numberOfSeconds, 1000) {

                        public void onTick(long millisUntilFinished) {
                            scorePlayerTwo.setText((millisUntilFinished / 1000) + "");
                        }
                        public void onFinish() {
                            Toast.makeText(GameActivity.this, "Time is up!\nYou loose the game!", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }

                    };
                    timerForGame.start();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!singleOrMulti.equals("Multi")) {
            timerForGame.cancel();
            timerForGame = null;
        }
    }
}