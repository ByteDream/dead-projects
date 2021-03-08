package org.blueshard.android.cryptogx.filedirchooser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.blueshard.android.cryptogx.R;

import java.util.ArrayList;

public class SelectedFileDirsAdapter extends RecyclerView.Adapter<SelectedFileDirsAdapter.SelectedFileDirsHolder>{

    private ArrayList<FileDirData> dataSet;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    public SelectedFileDirsAdapter(ArrayList<FileDirData> dataSet, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        this.dataSet = dataSet;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    public class SelectedFileDirsHolder extends RecyclerView.ViewHolder {
        public final TextView textView;
        public final ImageView imageView;

        public SelectedFileDirsHolder(View view) {
            super(view);
            this.textView = view.findViewById(R.id.fileImage);
            this.imageView = view.findViewById(R.id.fileName);
        }
    }

    @NonNull
    @Override
    public SelectedFileDirsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View selectedItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_file_dirs_item, parent, false);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) selectedItem.getLayoutParams();
        params.width = 120;
        selectedItem.setLayoutParams(params);

        selectedItem.setOnClickListener(onClickListener);
        selectedItem.setOnLongClickListener(onLongClickListener);

        return new SelectedFileDirsHolder(selectedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedFileDirsHolder holder, int position) {
        FileDirData data = dataSet.get(position);
        if (data != null) {
            holder.imageView.setImageDrawable(data.getImage());
            holder.textView.setText(data.getFile().getName());
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public FileDirData getData(int index) {
        return dataSet.get(index);
    }

}
