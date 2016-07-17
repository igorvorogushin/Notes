package com.vorogushinigor.notes.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vorogushinigor.notes.R;
import com.vorogushinigor.notes.activity.ActivityCreated;
import com.vorogushinigor.notes.other.CustomAlertDialog;
import com.vorogushinigor.notes.other.DataBase;
import com.vorogushinigor.notes.other.Notes;
import com.vorogushinigor.notes.other.Operation;


public class FragmentShow extends Fragment {

    public static final String MYLOG = "LogFragmentShow";
    public static final int INT_TYPE_SHOW = 0;
    public static final int INT_TYPE_DELETE = 1;
    public static final int INT_TYPE_EDIT = 2;
    public static final String TAG_CURRENT_NUMBER = "mPositionItems";
    public static final String TAG_TYPE = "type";

    private Context mContext;
    private View mView;
    private int mPositionItems;
    private int mScreenWidth;
    private Notes mNotes;
    private CustomAlertDialog alertDialog;
    private TextView mTextName;
    private TextView mTextMain;
    private AsyncTaskLoadBitmap asyncTaskLoadBitmap;
    private TextView mTextTime1;
    private TextView mTextTime2;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearLayout;
    private String mPath[];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_show, container, false);
            Log.i(MYLOG, "onCreate");
            mContext = getActivity();
            mTextName = (TextView) mView.findViewById(R.id.show_title);
            mTextMain = (TextView) mView.findViewById(R.id.show_text);
            mTextTime1 = (TextView) mView.findViewById(R.id.show_created);
            mTextTime2 = (TextView) mView.findViewById(R.id.show_changed);
            mLinearLayout = (LinearLayout) mView.findViewById(R.id.show_liner_photo);
            mProgressBar = (ProgressBar) mView.findViewById(R.id.show_progressBar);
            Intent intent = getActivity().getIntent();
            mPositionItems = intent.getIntExtra(TAG_CURRENT_NUMBER, 0);
            mNotes = Operation.getNotesFromIntent(intent);
            updateInterface();
            alertDialog = new CustomAlertDialog(mContext, mContext.getString(R.string.main_dialog_title2), null, new CustomAlertDialog.OnClickListener() {
                @Override
                public void positiveButton() {
                    DataBase dataBase = new DataBase(mContext);
                    dataBase.delete(mNotes);
                    dataBase.close();
                    Intent intent = new Intent();
                    intent.putExtra(TAG_TYPE, INT_TYPE_DELETE);
                    Operation.setIntentFromNotes(mNotes, intent);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
            });

        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (alertDialog != null) if (alertDialog.isShow()) alertDialog.show();
    }
    private class AsyncTaskLoadBitmap extends AsyncTask<Void, Bitmap, Void> {
        private int i;

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < mPath.length; i++) {
                if (mPath[i] != "") {
                    int height = Operation.getBitmapHeight(mPath[i]);
                    int width = Operation.getBitmapWidth(mPath[i]);
                    Bitmap bitmap = Operation.getBitmapCrop(width, height, mPath[i], 720);
                    this.i = i;
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
        protected void onProgressUpdate(Bitmap... bitmap) {
            super.onProgressUpdate(bitmap);
            if (bitmap[0] != null) {

                ImageView imageView = createImageView();
                imageView.setImageBitmap(bitmap[0]);
                LinearLayout linearLayout=createLinearLayout();
                linearLayout.addView(imageView);
                CardView cardView=createCardView();
                cardView.addView(linearLayout);
                mLinearLayout.addView(cardView);
            } else {
                TextView textView = createTextView(mPath[i]);
                mLinearLayout.addView(textView);
            }
        }
    }


    private ImageView createImageView() {
        final ImageView i = new ImageView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = 16;
        params.setMargins(margin, margin, margin, margin);
        i.setLayoutParams(params);
        i.setScaleType(ImageView.ScaleType.CENTER_CROP);
        i.setAdjustViewBounds(true);
        return i;
    }

    private LinearLayout createLinearLayout(){
        final LinearLayout l = new LinearLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        l.setLayoutParams(params);
        return l;
    }

    private TextView createTextView(String path) {
        TextView t = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = 3;
        params.setMargins(margin, margin, margin, margin);
        t.setLayoutParams(params);
        t.setText(getString(R.string.error_loading) + " — " + getString(R.string.path) + " " + path);
        t.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextHint));
        t.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_small));
        return t;
    }

    private CardView createCardView() {
        CardView cardView = new CardView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardView.setLayoutParams(params);
        float elevation=2.0f;
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cardView.setElevation(elevation);
        } else {
            cardView.setCardElevation(elevation);
        }
        cardView.setUseCompatPadding(true);
        cardView.setRadius(2.0f);
        return cardView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && null != data) {
            mNotes = Operation.getNotesFromIntent(data);
            DataBase mDataBase = new DataBase(mContext);
            mDataBase.update(mNotes);
            mDataBase.close();
            updateInterface();
        }
    }

    private void updateInterface() {
        mTextName.setText(mNotes.getName());
        mTextMain.setText(mNotes.getMain());
        mTextTime1.setText(mContext.getString(R.string.created) + " " + mNotes.getTimeCreated());
        if (mNotes.getTimeChanged() != null) {
            mTextTime2.setText(mContext.getString(R.string.change) + " " + mNotes.getTimeChanged());
            mTextTime2.setVisibility(View.VISIBLE);
        } else {
            mTextTime2.setVisibility(View.INVISIBLE);
        }
        mPath = Operation.getArrayFromString(mNotes.getPathPhoto());
        mLinearLayout.removeAllViews();
        if (mPath != null) {
            asyncTaskLoadBitmap = new AsyncTaskLoadBitmap();
            asyncTaskLoadBitmap.execute();
        }
    }

    public void menu_items_edit() {
        Intent intent = new Intent(mContext, ActivityCreated.class);
        intent.putExtra(TAG_TYPE, FragmentCreated.INT_TYPE_CHANGE);
        Operation.setIntentFromNotes(mNotes, intent);
        startActivityForResult(intent, 0);
    }

    public void menu_items_delete() {
        alertDialog.show();
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(TAG_TYPE, INT_TYPE_EDIT);
        Operation.setIntentFromNotes(mNotes, intent);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (asyncTaskLoadBitmap != null) asyncTaskLoadBitmap.cancel(false);
    }
}
