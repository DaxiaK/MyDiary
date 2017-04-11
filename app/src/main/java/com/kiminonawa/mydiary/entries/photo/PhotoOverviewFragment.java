/*
 * This file provided by Facebook is for non-commercial testing and evaluation
 * purposes only.  Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.kiminonawa.mydiary.entries.photo;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.kiminonawa.mydiary.shared.FileManager.DIARY_ROOT_DIR;

/**
 * Simple drawee recycler view fragment that displays a grid of images.
 */
public class PhotoOverviewFragment extends Fragment {

    /**
     * Total number of images displayed
     */
    private static final int TOTAL_NUM_ENTRIES = 200;
    /**
     * Number of recycler view spans
     */
    private static final int SPAN_COUNT = 3;
    /**
     * The topic info
     */
    private long topicId;
    private List<File> theDairyPhotoList;

    /**
     * The bind UI
     */
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;


    public static PhotoOverviewFragment newInstance(long topicId) {
        Bundle args = new Bundle();
        PhotoOverviewFragment fragment = new PhotoOverviewFragment();
        args.putLong("topicId", topicId);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary_photo_overview, container, false);
        unbinder = ButterKnife.bind(this, view);
        topicId = getArguments().getLong("topicId");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FileManager diaryRoot = new FileManager(getActivity(), DIARY_ROOT_DIR);
        File topicRootFile = new File(diaryRoot.getDirAbsolutePath() + "/" + topicId);
        //Load all file form topic dir
        theDairyPhotoList = getFilesList(topicRootFile);
        initRecyclerView();
    }

    private List<File> getFilesList(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getFilesList(file));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new SimpleAdapter(theDairyPhotoList));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static class SimpleAdapter extends RecyclerView.Adapter<SimpleViewHolder> {

        private final List<File> mFileList;

        public SimpleAdapter(List<File> fileList) {
            mFileList = fileList;
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(
                ViewGroup parent,
                int viewType) {
            View itemView = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.rv_diary_photo_overview_item, parent, false);
            return new SimpleViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            holder.mSimpleDraweeView.setImageURI(Uri.fromFile(mFileList.get(position)));
        }

        @Override
        public int getItemCount() {
            return mFileList.size();
        }
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        private final SimpleDraweeView mSimpleDraweeView;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            mSimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.drawee_view);
        }
    }
}
