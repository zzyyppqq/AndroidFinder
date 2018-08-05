package com.zyp.filemanager;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FileAdapter.OnItemClickListener {

    private LinearLayout llFile;
    private HorizontalScrollView hsv;
    private TextView tv_path;
    private File rootDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llFile = findViewById(R.id.ll_file);
        tv_path = findViewById(R.id.tv_path);
        hsv = findViewById(R.id.hsv);

        initData();
    }

    private void initData() {
        rootDirectory = Environment.getExternalStorageDirectory();
        tv_path.setText(rootDirectory.getAbsolutePath());
        loadFileData(rootDirectory);

    }

    @Override
    public void onItemClick(int position, FileData fileData, View view, FileAdapter adapter) {
        File file = new File(fileData.getFilePath(), fileData.getFileName());
        tv_path.setText(file.getAbsolutePath());
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        adapter.notifyDataSetChangedBg(position);
        deleteView(file);
        if (fileData.isDirectory()) {
            loadFileData(new File(fileData.getFilePath(), fileData.getFileName()));
        }

    }


    private void loadFileData(File file) {
        List<FileData> fileDataList = getFileData(file);
        if (fileDataList.size() > 0) {
            addRecyclerView(fileDataList, file);
        }
    }

    private List<FileData> getFileData(File file) {
        List<FileData> fileList = new ArrayList<>();
        if (file != null && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                FileData fileData = new FileData();
                fileData.setFileName(f.getName());
                fileData.setFilePath(file.getAbsolutePath());
                fileData.setFileSize(FileUtil.displayFileSize(f.length()));
                fileData.setIsDirectory(f.isDirectory());
                fileList.add(fileData);
            }

        }
        return fileList;
    }


    private void addRecyclerView(List<FileData> fileDataList, File parentFile) {
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FileAdapter fileAdapter = new FileAdapter(this);
        recyclerView.setAdapter(fileAdapter);
        fileAdapter.setDatas(fileDataList);
        fileAdapter.setOnItemClickListener(this);

        recyclerView.setTag(parentFile.getAbsolutePath());
        llFile.addView(recyclerView);
        llFile.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                hsv.setSmoothScrollingEnabled(true);
                hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });

    }

    private void deleteView(File file) {
        mFileDepth = 0;
        int filedepth = getFiledepth(file, rootDirectory);
        int childCount = llFile.getChildCount();
        Log.e("AAA", "childs:" + childCount + ", fileDepth:" + filedepth);
        for (int i = childCount - 1; i >= filedepth; i--) {
            llFile.removeViewAt(i);
        }

    }


    private int mFileDepth;

    /**
     * 递归计算文件深度
     *
     * @param file
     * @param rootDirectory
     * @return
     */
    public int getFiledepth(File file, File rootDirectory) {
        if (rootDirectory.getAbsolutePath().equals(file.getAbsolutePath())) {
            return mFileDepth;
        }
        mFileDepth++;
        return getFiledepth(file.getParentFile(), rootDirectory);
    }
}
