package com.kiminonawa.mydiary.entries.entries;

import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.entries.diary.DiaryInfoHelper;
import com.kiminonawa.mydiary.shared.EditMode;
import com.kiminonawa.mydiary.shared.ThemeManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by daxia on 2016/10/17.
 */

public class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.EntriesViewHolder> implements EditMode {


    private List<EntriesEntity> entriesList;
    private EntriesFragment mFragment;
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private String[] daysSimpleName;
    private ThemeManager themeManager;

    private boolean isEditMode = false;

    public EntriesAdapter(EntriesFragment fragment, List<EntriesEntity> topicList) {
        this.mFragment = fragment;
        this.entriesList = topicList;
        this.daysSimpleName = mFragment.getResources().getStringArray(R.array.days_simple_name);
        this.themeManager = ThemeManager.getInstance();
    }


    @Override
    public EntriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_entries_item, parent, false);
        return new EntriesViewHolder(view, themeManager.getThemeDarkColor(mFragment.getActivity()));
    }

    @Override
    public int getItemCount() {
        return entriesList.size();
    }

    @Override
    public void onBindViewHolder(EntriesViewHolder holder, final int position) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(entriesList.get(position).getCreateDate());

        if (showHeader(position)) {
            holder.getHeader().setVisibility(View.VISIBLE);
            holder.getHeader().setText(String.valueOf(calendar.get(Calendar.MONTH) + 1));
        } else {
            holder.getHeader().setVisibility(View.GONE);
        }

        holder.getTVDate().setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        holder.getTVDay().setText(daysSimpleName[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
        holder.getTVTime().setText(String.valueOf(dateFormat.format(calendar.getTime())));
        holder.getTVTitle().setText(entriesList.get(position).getTitle());
        holder.getTVSummary().setText(entriesList.get(position).getSummary());

        holder.getIVWeather().setImageResource(DiaryInfoHelper.getWeatherResourceId(entriesList.get(position).getWeatherId()));
        holder.getIVMood().setImageResource(DiaryInfoHelper.getMoodResourceId(entriesList.get(position).getMoodId()));

        if (entriesList.get(position).hasAttachment()) {
            holder.getIVAttachment().setVisibility(View.VISIBLE);
        } else {
            holder.getIVAttachment().setVisibility(View.GONE);
        }
    }


    private boolean showHeader(final int position) {
        if (position == 0) {
            return true;
        } else {
            Calendar previousCalendar = new GregorianCalendar();
            previousCalendar.setTime(entriesList.get(position - 1).getCreateDate());
            Calendar currentCalendar = new GregorianCalendar();
            currentCalendar.setTime(entriesList.get(position).getCreateDate());
            if (previousCalendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR)) {
                return true;
            } else {
                if (previousCalendar.get(Calendar.MONTH) != currentCalendar.get(Calendar.MONTH)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    @Override
    public boolean isEditMode() {
        return isEditMode;
    }

    @Override
    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }


    protected class EntriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView TV_entries_item_header;
        private TextView TV_entries_item_date, TV_entries_item_day, TV_entries_item_time,
                TV_entries_item_title, TV_entries_item_summary;

        private ImageView IV_entries_item_weather, IV_entries_item_mood, IV_entries_item_bookmark, IV_entries_item_attachment;

        private RelativeLayout RL_entries_item_content;

        protected EntriesViewHolder(View rootView, @ColorInt int color) {
            super(rootView);
            this.TV_entries_item_header = (TextView) rootView.findViewById(R.id.TV_entries_item_header);

            this.TV_entries_item_date = (TextView) rootView.findViewById(R.id.TV_entries_item_date);
            this.TV_entries_item_day = (TextView) rootView.findViewById(R.id.TV_entries_item_day);
            this.TV_entries_item_time = (TextView) rootView.findViewById(R.id.TV_entries_item_time);
            this.TV_entries_item_title = (TextView) rootView.findViewById(R.id.TV_entries_item_title);
            this.TV_entries_item_summary = (TextView) rootView.findViewById(R.id.TV_entries_item_summary);

            this.IV_entries_item_weather = (ImageView) rootView.findViewById(R.id.IV_entries_item_weather);
            this.IV_entries_item_mood = (ImageView) rootView.findViewById(R.id.IV_entries_item_mood);
            this.IV_entries_item_bookmark = (ImageView) rootView.findViewById(R.id.IV_entries_item_bookmark);
            this.IV_entries_item_attachment = (ImageView) rootView.findViewById(R.id.IV_entries_item_attachment);

            this.RL_entries_item_content = (RelativeLayout) rootView.findViewById(R.id.RL_entries_item_content);
            this.itemView.setOnClickListener(this);
            this.itemView.setOnLongClickListener(this);

            initThemeColor(color);
        }

        private void initThemeColor(@ColorInt int color) {
            this.TV_entries_item_date.setTextColor(color);
            this.TV_entries_item_day.setTextColor(color);
            this.TV_entries_item_time.setTextColor(color);
            this.TV_entries_item_title.setTextColor(color);
            this.TV_entries_item_summary.setTextColor(color);

            this.IV_entries_item_weather.setColorFilter(color);
            this.IV_entries_item_mood.setColorFilter(color);
            this.IV_entries_item_bookmark.setColorFilter(color);
            this.IV_entries_item_attachment.setColorFilter(color);

        }

        public ImageView getIVWeather() {
            return IV_entries_item_weather;
        }

        public ImageView getIVMood() {
            return IV_entries_item_mood;
        }

        public ImageView getIVBookmark() {
            return IV_entries_item_bookmark;
        }

        public TextView getHeader() {
            return TV_entries_item_header;
        }

        public TextView getTVDate() {
            return TV_entries_item_date;
        }

        public TextView getTVDay() {
            return TV_entries_item_day;
        }

        public TextView getTVTime() {
            return TV_entries_item_time;
        }

        public TextView getTVTitle() {
            return TV_entries_item_title;
        }

        public TextView getTVSummary() {
            return TV_entries_item_summary;
        }

        public ImageView getIVAttachment() {
            return IV_entries_item_attachment;
        }

        public RelativeLayout getRLContent() {
            return RL_entries_item_content;
        }

        @Override
        public void onClick(View v) {
            //single click to open diary
            DiaryViewerDialogFragment diaryViewerDialog =
                    DiaryViewerDialogFragment.newInstance(entriesList.get(getAdapterPosition()).getId(),
                            isEditMode);
            diaryViewerDialog.setTargetFragment(mFragment, 0);
            //Revert the icon
            if (isEditMode) {
                mFragment.setEditModeUI(isEditMode);
            }
            diaryViewerDialog.show(mFragment.getFragmentManager(), "diaryViewerDialog");
        }

        @Override
        public boolean onLongClick(View v) {
            //Long click is always going to edit diary
            DiaryViewerDialogFragment diaryViewerDialog =
                    DiaryViewerDialogFragment.newInstance(entriesList.get(getAdapterPosition()).getId(),
                            true);
            diaryViewerDialog.setTargetFragment(mFragment, 0);
            //Revert the icon
            if (isEditMode) {
                mFragment.setEditModeUI(isEditMode);
            }
            diaryViewerDialog.show(mFragment.getFragmentManager(), "diaryViewerDialog");
            return true;
        }
    }
}
