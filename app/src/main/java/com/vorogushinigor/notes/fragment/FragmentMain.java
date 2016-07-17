package com.vorogushinigor.notes.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.vorogushinigor.notes.R;
import com.vorogushinigor.notes.activity.ActivityCreated;
import com.vorogushinigor.notes.activity.ActivityShow;
import com.vorogushinigor.notes.adapter.AdapterMain;
import com.vorogushinigor.notes.other.CustomAlertDialog;
import com.vorogushinigor.notes.other.DataBase;
import com.vorogushinigor.notes.other.Notes;
import com.vorogushinigor.notes.other.Operation;

import java.util.ArrayList;

public class FragmentMain extends Fragment {

    public static final String MYLOG = "LogFragmentMain";
    public static final int INT_ACTIVITY_CREATE_NOTES = 1;
    public static final int INT_ACTIVITY_SHOW_NOTES = 3;
    public static final int INT_ACTIVITY_GALLERY = 2;

    private Context mContext;
    private View mView;
    private DataBase mDataBase;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Notes> mNotesList;
    private AdapterMain mAdapterMain;
    private CustomAlertDialog alertDialogDeleteAll;
    private CustomAlertDialog alertDialogDelete;
    private int mPositionItems;
    private boolean load = true;
    private ProgressDialog progressDialog;
    private FloatingActionButton mFloatingActionButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_main, container, false);
            mContext = getActivity();
            Log.i(MYLOG, "onCreate");
            mFloatingActionButton = (FloatingActionButton) mView.findViewById(R.id.main_fab);
            mRecyclerView = (RecyclerView) mView.findViewById(R.id.main_recyclerView);
            mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startIntentAddNotes();
                }
            });
            mFloatingActionButton.hide();
            mDataBase = new DataBase(mContext);
            mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            alertDialogDeleteAll = new CustomAlertDialog(mContext, mContext.getString(R.string.main_dialog_title), null, new CustomAlertDialog.OnClickListener() {
                @Override
                public void positiveButton() {
                    mDataBase.deleteAll();
                    int number = mNotesList.size();
                    for (int i = 0; i < number; i++)
                        mNotesList.remove(0);
                    mAdapterMain.update();
                }
            });

            alertDialogDelete = new CustomAlertDialog(mContext, mContext.getString(R.string.main_dialog_title2), null, new CustomAlertDialog.OnClickListener() {
                @Override
                public void positiveButton() {
                    delete(mPositionItems);
                }
            });

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) mFloatingActionButton.hide();
                    if (dy <= 0) mFloatingActionButton.show();
                }
            });
        }

        return mView;
    }

    @Override
    public void onPause() {
        if (load) if (progressDialog != null) progressDialog.dismiss();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (alertDialogDelete != null) if (alertDialogDelete.isShow()) alertDialogDelete.show();
        if (alertDialogDeleteAll != null)
            if (alertDialogDeleteAll.isShow()) alertDialogDeleteAll.show();

        if (load) {
            progressDialog = new ProgressDialog(mContext, R.style.CustomAlertDialogStyle);
            parameterProgressDialog(progressDialog);
            progressDialog.show();
            new AsynsTaskLoadBD().execute();
        }


    }

    private void parameterProgressDialog(ProgressDialog _progressDialog) {
        _progressDialog.setMessage(getString(R.string.Loading_notes));
        Window window = _progressDialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        _progressDialog.setCancelable(false);
        _progressDialog.setIndeterminate(true);

    }

    private class AsynsTaskLoadBD extends AsyncTask<Void, Integer, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            load = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
          //  for (int i = 0; i < 10000; i++) // load emulation for test
                mNotesList = mDataBase.readAll();
            mAdapterMain = new AdapterMain(mContext, mNotesList);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            load = false;

            mAdapterMain.setOnItemsClickListener(new AdapterMain.OnItemsClickListener() {
                @Override
                public void OnClick(int itemPosition) {
                    Notes notes = mNotesList.get(itemPosition);
                    Intent intent = new Intent(mContext, ActivityShow.class);
                    mPositionItems = itemPosition;
                    intent.putExtra(FragmentShow.TAG_CURRENT_NUMBER, mPositionItems);
                    Operation.setIntentFromNotes(notes, intent);
                    startActivityForResult(intent, INT_ACTIVITY_SHOW_NOTES);
                }

                @Override
                public void OnRemove(final int itemPosition) {

                    mPositionItems = itemPosition;
                    alertDialogDelete.show();


                }
            });
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapterMain);
            progressDialog.dismiss();
            mFloatingActionButton.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && null != data)
            switch (requestCode) {
                case INT_ACTIVITY_CREATE_NOTES:
                    Notes notes = Operation.getNotesFromIntent(data);
                    mDataBase.insert(notes);
                    mNotesList.add(mNotesList.size(), notes);
                    mAdapterMain.insert(mNotesList.size());
                    break;

                case INT_ACTIVITY_SHOW_NOTES:
                    int type = data.getIntExtra(FragmentShow.TAG_TYPE, FragmentShow.INT_TYPE_SHOW);
                    switch (type) {
                        case FragmentShow.INT_TYPE_DELETE:
                            delete(mPositionItems);
                            break;
                        case FragmentShow.INT_TYPE_EDIT:
                            mNotesList.set(mPositionItems, Operation.getNotesFromIntent(data));
                            mAdapterMain.update();
                            break;
                    }
                    break;
            }
    }

    public void deleteAll() {
        if (mNotesList.size() < 1) {
            Toast toast = Toast.makeText(mContext, R.string.notes_not_found, Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        alertDialogDeleteAll.show();
    }

    private void delete(int number) {
        mDataBase.delete(mNotesList.get(number));
        mNotesList.remove(number);
        mAdapterMain.remove(number);
    }

    public void startIntentAddNotes() {
        Intent intent = new Intent(mContext, ActivityCreated.class);
        intent.putExtra(FragmentShow.TAG_TYPE, FragmentCreated.INT_TYPE_CREATED);
        startActivityForResult(intent, INT_ACTIVITY_CREATE_NOTES);
    }


}
