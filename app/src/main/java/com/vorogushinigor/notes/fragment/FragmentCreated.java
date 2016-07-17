package com.vorogushinigor.notes.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.vorogushinigor.notes.R;
import com.vorogushinigor.notes.other.CustomAlertDialog;
import com.vorogushinigor.notes.other.CustomImageView;
import com.vorogushinigor.notes.other.Notes;
import com.vorogushinigor.notes.other.Operation;

import java.util.ArrayList;

/**
 * Created by viv on 07.07.2016.
 */
public class FragmentCreated extends Fragment {

    public static final String MYLOG = "LogFragmentCreated";
    public static final int INT_TYPE_CREATED = 1;
    public static final int INT_TYPE_CHANGE = 2;

    private Context mContext;
    private View mView;
    private int mType;
    private CustomAlertDialog alertDialog;
    private AsyncTaskLoadBitmapCreated asyncTaskLoadBitmapCreated;
    private AsyncTaskLoadBitmapEdit asyncTaskLoadBitmapEdit;
    private boolean isChangeEdit = false;
    private EditText mEditName;
    private EditText mEditMain;
    private String startName;
    private HorizontalScrollView mHorizontalScrollView;
    private String startMain;
    private LinearLayout mLinearLayoutPhoto;
    private RelativeLayout mRelativGlobal;
    private Notes mNotes;
    private String mPath[];
    private ArrayList<String> mArrayListPath = new ArrayList<>();
    private Intent mData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_created, container, false);
            mContext = getActivity();
            Log.i(MYLOG, "onCreate");
            mEditName = (EditText) mView.findViewById(R.id.createnotes_edit_name);
            mEditMain = (EditText) mView.findViewById(R.id.createnotes_edit_main);
            mLinearLayoutPhoto = (LinearLayout) mView.findViewById(R.id.createnotes_linerPhoto);
            mHorizontalScrollView = (HorizontalScrollView) mView.findViewById(R.id.createnotes_horizontal);
            Intent intent = getActivity().getIntent();
            mType = intent.getIntExtra(FragmentShow.TAG_TYPE, INT_TYPE_CREATED);
            mNotes = Operation.getNotesFromIntent(intent);
            mEditName.setText(mNotes.getName());
            mEditMain.setText(mNotes.getMain());
            startName = mNotes.getName();
            startMain = mNotes.getMain();
            if (startMain == null) startMain = "";
            if (startName == null) startName = "";

            alertDialog = new CustomAlertDialog(mContext, getString(R.string.createnotes_dialog_title), getString(R.string.createnotes_dialog_text), new CustomAlertDialog.OnClickListener() {
                @Override
                public void positiveButton() {
                    getActivity().finish();
                }
            });

            if (mType == INT_TYPE_CHANGE) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mContext.getString(R.string.activity_change_note));
                mPath = Operation.getArrayFromString(mNotes.getPathPhoto());
                asyncTaskLoadBitmapEdit = new AsyncTaskLoadBitmapEdit();
                asyncTaskLoadBitmapEdit.execute();
            } else {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mContext.getString(R.string.activity_create_note));

                RelativeLayout relativeLayout = createRelativeLayout();
                relativeLayout.addView(createImageViewBitmap());
                relativeLayout.addView(createImageViewIcons(true));
                mLinearLayoutPhoto.addView(relativeLayout);
            }
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (alertDialog != null) if (alertDialog.isShow()) alertDialog.show();
    }

    private class AsyncTaskLoadBitmapEdit extends AsyncTask<Void, Bitmap, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < mPath.length; i++) {
                if (mPath[i].toString() != "") {
                    mArrayListPath.add(mPath[i]);
                    int width = Operation.getBitmapWidth(mPath[i]);
                    int height = Operation.getBitmapHeight(mPath[i]);
                    Bitmap bitmap = Operation.getBitmapCrop(width, height, mPath[i], 200);
                    if (!isCancelled()) {
                        publishProgress(bitmap);
                    } else {
                        break;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);
            if (!isCancelled() || asyncTaskLoadBitmapEdit.getStatus() != Status.FINISHED) {
                ImageView imageView1 = createImageViewBitmap();
                imageView1.setImageBitmap(values[0]);
                imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                RelativeLayout relativeLayout = createRelativeLayout();
                relativeLayout.addView(imageView1);
                relativeLayout.addView(createImageViewIcons(false));
                mLinearLayoutPhoto.addView(relativeLayout);
                mHorizontalScrollView.postDelayed(new Runnable() {
                    public void run() {
                        mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    }
                }, 150);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isCancelled()) {
                RelativeLayout relativeLayout = createRelativeLayout();
                relativeLayout.addView(createImageViewBitmap());
                relativeLayout.addView(createImageViewIcons(true));
                mLinearLayoutPhoto.addView(relativeLayout);
            }
        }
    }

    private class AsyncTaskLoadBitmapCreated extends AsyncTask<Void, Integer, Void> {
        private Bitmap bitmap;

        @Override
        protected Void doInBackground(Void... params) {
            String path = getPathFromIntent(mData);
            int id = getIdFromIntent(mData);
            mArrayListPath.add(path);
            bitmap = (Operation.getThumbnails(mContext, id));
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isCancelled() || asyncTaskLoadBitmapCreated.getStatus() != Status.FINISHED) {
                mRelativGlobal.removeAllViews();
                CustomImageView imageViewBitmap = createImageViewBitmap();
                imageViewBitmap.setImageBitmap(bitmap);
                imageViewBitmap.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mRelativGlobal.addView(imageViewBitmap);
                mRelativGlobal.addView(createImageViewIcons(false));
                RelativeLayout relativeLayout = createRelativeLayout();
                relativeLayout.addView(createImageViewBitmap());
                relativeLayout.addView(createImageViewIcons(true));
                mLinearLayoutPhoto.addView(relativeLayout);
                isChangeEdit = true;
                mHorizontalScrollView.postDelayed(new Runnable() {
                    public void run() {
                        mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    }
                }, 150);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FragmentMain.INT_ACTIVITY_GALLERY && resultCode == Activity.RESULT_OK && null != data) {
            mData = data;
            asyncTaskLoadBitmapCreated = new AsyncTaskLoadBitmapCreated();
            asyncTaskLoadBitmapCreated.execute();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (asyncTaskLoadBitmapEdit != null) asyncTaskLoadBitmapEdit.cancel(false);
        if (asyncTaskLoadBitmapCreated != null) asyncTaskLoadBitmapCreated.cancel(false);
    }

    private RelativeLayout createRelativeLayout() {
        RelativeLayout rl = new RelativeLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                Operation.dpToPx(mContext, 78),
                Operation.dpToPx(mContext, 78));
        int margin = 5;
        params.setMargins(margin, margin, margin, margin);
        rl.setLayoutParams(params);
        return rl;
    }

    private CustomImageView createImageViewBitmap() {
        final CustomImageView i = new CustomImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        i.setLayoutParams(params);
        i.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            i.setBackground(ContextCompat.getDrawable(mContext, R.drawable.createnotes_imageview_bitmap));
        else
            i.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.createnotes_imageview_bitmap));


        return i;
    }

    private CustomImageView createImageViewIcons(boolean isAdd) {
        final CustomImageView i = new CustomImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        i.setLayoutParams(params);
        Bitmap bitmap;
        if (isAdd) {
            i.setAlpha(1.0f);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_a_photo_white_48dp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                i.setBackground(ContextCompat.getDrawable(mContext, R.drawable.createnotes_imageview_add));
            else
                i.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.createnotes_imageview_add));
        } else {
            i.setAlpha(0.8f);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_clear_white_36dp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                i.setBackground(ContextCompat.getDrawable(mContext, R.drawable.createnotes_imageview_remove));
            else
                i.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.createnotes_imageview_remove));
        }
        i.setImageBitmap(bitmap);
        i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = mLinearLayoutPhoto.getChildCount();
                int n1 = i.hashCode();
                int n2 = (((RelativeLayout) mLinearLayoutPhoto.getChildAt(count - 1)).getChildAt(1)).hashCode();
                // Log.i(MYLOG, "hash: " + String.valueOf(n1) + " " + String.valueOf(n2));
                if (n1 == n2) {
                    mRelativGlobal = (RelativeLayout) mLinearLayoutPhoto.getChildAt(count - 1);
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, FragmentMain.INT_ACTIVITY_GALLERY);
                } else {
                    for (int indexDelete = 0; indexDelete < count; indexDelete++) {
                        int n = ((RelativeLayout) mLinearLayoutPhoto.getChildAt(indexDelete)).getChildAt(1).hashCode();
                        if (i.hashCode() == n) {
                            mArrayListPath.remove(indexDelete);
                            RelativeLayout relativeLayout = (RelativeLayout) i.getParent();
                            ((ViewGroup) relativeLayout.getParent()).removeView(relativeLayout);
                            break;
                        }
                    }
                }
            }
        });

        return i;
    }

    private String getPathFromIntent(Intent data) {

        Uri uri = data.getData();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    private int getIdFromIntent(Intent data) {
        Uri uri = data.getData();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        cursor.close();
        return id;
    }

    public void menu_items_done() {
        mNotes.setName(mEditName.getText().toString());
        mNotes.setMain(mEditMain.getText().toString());
        boolean bol = true;
        if ((mNotes.getName().length() == 0)) {
            mEditName.setError(getString(R.string.error_text_null));
            bol = false;
        }
        if ((mNotes.getMain().length() == 0)) {
            mEditMain.setError(getString(R.string.error_text_null));
            bol = false;
        }
        if (bol) {
            if (mNotes.getTimeCreated() == null)
                mNotes.setTimeCreated(Operation.getTime());
            else
                mNotes.setTimeChanged(Operation.getTime());
            mNotes.setPathPhoto(Operation.getStringFromArray(mArrayListPath));
            Intent intent = new Intent();
            Operation.setIntentFromNotes(mNotes, intent);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

    public void onBackPressed() {
        if (!mEditMain.getText().toString().equals(startMain) || !mEditName.getText().toString().equals(startName) || isChangeEdit)
            alertDialog.show();
        else {
            getActivity().finish();
        }
    }

}
