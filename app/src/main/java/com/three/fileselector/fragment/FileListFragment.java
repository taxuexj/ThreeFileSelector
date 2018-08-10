package com.three.fileselector.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.three.fileselector.FileSelectorActivity;
import com.three.fileselector.R;
import com.three.fileselector.adapter.FileListAdapter;
import com.three.fileselector.bean.FileInfoBean;
import com.three.fileselector.util.FileUtil;

import java.io.File;
import java.util.ArrayList;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;

/**
 * Created by three on 2018/8/8.
 */

@RuntimePermissions
public class FileListFragment extends LazyFragment{

    public static FileListFragment newInstance(int tabIndex, boolean isLazyLoad,String title) {
        Bundle bundle = new Bundle();
        bundle.putInt(LazyFragment.TAB_INDEX, tabIndex);
        bundle.putString(LazyFragment.TAB_INDEX_TITLE,title);
        bundle.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        FileListFragment fragment = new FileListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private String fileType;
    private RecyclerView recyclerView;
    private TextView tvLoading;
    private ProgressBar progressBar;
    private FileListAdapter adapter;
    private LoadFileTask loadFileTask;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        try {
            setContentView(R.layout.fragment_file_list);
            initView();
            initListener();
            initValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        if (loadFileTask != null) {
            loadFileTask.cancel(true);
        }
    }

    private void initView() {
        fileType = getArguments().getString(LazyFragment.TAB_INDEX_TITLE);
        recyclerView = $(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        tvLoading = $(R.id.tv_text);
        progressBar = $(R.id.progressBar);
    }

    private void initListener() {
        tvLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initValue();
            }
        });
    }

    private void initValue() {
        FileListFragmentPermissionsDispatcher.onGetFileWithPermissionCheck(FileListFragment.this);
    }

    private void onGettingFile(String fileType) {
        loadFileTask = new LoadFileTask();
        loadFileTask.execute(fileType);
    }

    class LoadFileTask extends AsyncTask<String,Integer,ArrayList<FileInfoBean>>{

        /**
         * 在onPreExecute()方法执行完后，会马上执行这个方法，这个方法就是来处理异步任务的方法，
         * Android操作系统会在后台的线程池当中开启一个worker thread来执行这个方法（
         * 即在worker thread当中执行），执行完后将执行结果发送给最后一个 onPostExecute
         * 方法,在这个方法里，我们可以从网络当中获取数据等一些耗时的操作
         * @param strings
         * @return
         */
        @Override
        protected ArrayList<FileInfoBean> doInBackground(String... strings) {
            ArrayList<FileInfoBean> mDataList = new ArrayList<>();
            String fileType = strings[0];
            String[] columns = new String[]{MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.DATA};
            String select = "";
            if ("word".equals(fileType)) {
                select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.doc'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.docx'" + ")";
            } else if ("xls".equals(fileType)) {
                select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.xls'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.xlsx'" + ")";
            } else if ("txt".equals(fileType)) {
                select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.txt'" + ")";
            } else if ("ppt".equals(fileType)) {
                select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.ppt'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.pptx'" + ")";
            } else if ("pdf".equals(fileType)) {
                select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.pdf'" + ")";
            } else if ("zip/rar".equals(fileType)) {
                select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.zip'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.rar'"+ ")";
            }else {
                select = "(" + MediaStore.Files.FileColumns.DATA + ")";
            }
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), columns, select, null, null);
            int columnIndexOrThrow_DATA = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String path = cursor.getString(columnIndexOrThrow_DATA);
                    FileInfoBean document = FileUtil.getFileInfoFromFile(new File(path));
                    mDataList.add(document);
                }
                cursor.close();
                cursor = null;
            }
            return mDataList;
        }

        /**
         * 个方法是在执行异步任务之前的时候执行，并且是在UI Thread当中执行的，通常我们在这个方法里做一些UI控件的初始化的操作，
         * 例如弹出ProgressDialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvLoading.setText("正在加载...");
            tvLoading.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * 这个方法也是在UI Thread当中执行的，在异步任务执行的时候，
         * 有时需要将执行的进度返回给UI界面
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int p = values[0];
            tvLoading.setText("正在加载" + p + "%");
        }

        /**
         * 当异步任务执行完之后，就会将结果返回给这个方法，这个方法也是在UI Thread当中调用的，
         * 我们可以将返回的结果显示在UI控件上
         * @param fileInfoBeans
         */
        @Override
        protected void onPostExecute(final ArrayList<FileInfoBean> fileInfoBeans) {
            super.onPostExecute(fileInfoBeans);
            progressBar.setVisibility(View.GONE);
            if (fileInfoBeans.size() == 0  || fileInfoBeans.isEmpty()) {
                tvLoading.setText("暂无文件，点击重试!");
                tvLoading.setVisibility(View.VISIBLE);
            } else {
                tvLoading.setVisibility(View.GONE);
                adapter = new FileListAdapter(fileInfoBeans,getActivity());
                adapter.setOnItemClickListener(new FileListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        ArrayList<FileInfoBean> selList = new ArrayList<>();
                        selList.add(fileInfoBeans.get(position));
                        Intent resultIntent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(FileSelectorActivity.SELECTED_RESULT,selList);
                        resultIntent.putExtras(bundle);
                        getActivity().setResult(RESULT_OK, resultIntent);
                        getActivity().finish();
                    }
                });
                recyclerView.setAdapter(adapter);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            resetView();
        }
    }

    private void resetView() {
        progressBar.setVisibility(View.GONE);
        tvLoading.setVisibility(View.VISIBLE);
        tvLoading.setText("任务取消，点击重试!");
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onGetFile() {
        onGettingFile(fileType);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FileListFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onShowRationForGetFile(final PermissionRequest request) {
        new AlertDialog.Builder(getActivity())
                .setTitle("温馨提示")
                .setMessage(getString(R.string.app_name)+"正在尝试使用手机的外部存储,您是否允许？")
                .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        request.proceed();
                    }
                })
                .setNegativeButton("禁止", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        request.cancel();
                    }
                })
                .create()
                .show();
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onDeniedForGetFile() {
        tvLoading.setText("你已选择了该应用禁止使用手机的外部存储权限，你将无法正常使用一些功能!");
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onNeverAskForGetFile() {
    }
}
