package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;

/**
 * Created by daxia on 2016/12/14.
 */

public class DiaryBlockFragment extends Fragment implements View.OnClickListener {

    private LinearLayout LL_diary_block_page_content;
    private TextView TV_diary_block_page_title, TV_diary_block_page_subtitle, TV_diary_block_page_url;
    private String title, subTitle, url;

    public static DiaryBlockFragment newInstance(String title, String subTitle, String url) {
        Bundle args = new Bundle();
        DiaryBlockFragment fragment = new DiaryBlockFragment();
        args.putString("title", title);
        args.putString("subTitle", subTitle);
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("title", "");
        subTitle = getArguments().getString("subTitle", "");
        url = getArguments().getString("url", "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary_block_page, container, false);
        LL_diary_block_page_content = (LinearLayout) rootView.findViewById(R.id.LL_diary_block_page_content);
        LL_diary_block_page_content.setOnClickListener(this);
        TV_diary_block_page_title = (TextView) rootView.findViewById(R.id.TV_diary_block_page_title);
        TV_diary_block_page_title.setText(title);
        TV_diary_block_page_subtitle = (TextView) rootView.findViewById(R.id.TV_diary_block_page_subtitle);
        TV_diary_block_page_subtitle.setText(subTitle);
        TV_diary_block_page_url = (TextView) rootView.findViewById(R.id.TV_diary_block_page_url);
        SpannableString urlText = new SpannableString(url);
        urlText.setSpan(new UnderlineSpan(), 0, urlText.length(), 0);
        TV_diary_block_page_url.setText(urlText);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (url != null && !"".equals(url)) {
            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                url = "http://" + url;
            }
            Intent i = new Intent(Intent.ACTION_VIEW, (Uri.parse(url)));
            startActivity(i);
        }
    }
}
