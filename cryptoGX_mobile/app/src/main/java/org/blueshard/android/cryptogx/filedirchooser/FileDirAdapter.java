package org.blueshard.android.cryptogx.filedirchooser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.blueshard.android.cryptogx.R;


public class FileDirAdapter extends RecyclerView.Adapter<FileDirAdapter.FileDirHolder> {

    private FileDirData[] dataSet;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    public FileDirAdapter(FileDirData[] dataSet, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        this.dataSet = dataSet;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    public class FileDirHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final ImageView imageView;

        public FileDirHolder(View view) {
            super(view);
            this.textView = view.findViewById(R.id.fileDirText);
            this.imageView = view.findViewById(R.id.fileDirImage);
        }
    }

    @NonNull
    @Override
    public FileDirHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chooserItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_dir_item, parent, false);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) chooserItem.getLayoutParams();
        params.height = 200;
        chooserItem.setLayoutParams(params);

        chooserItem.setOnClickListener(onClickListener);
        chooserItem.setOnLongClickListener(onLongClickListener);

        return new FileDirHolder(chooserItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FileDirHolder holder, int position) {
        FileDirData data = dataSet[position];
        if (data != null) {
            holder.textView.setText(data.getFile().getName());
            holder.imageView.setImageDrawable(data.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    public FileDirData getData(int index) {
        return dataSet[index];
    }
}
