package pro.sketchware.activities.resourceseditor.components.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import pro.sketchware.activities.resourceseditor.ResourcesEditorActivity;
import pro.sketchware.activities.resourceseditor.components.models.DimenModel;
import pro.sketchware.databinding.PalletCustomviewBinding;

public class DimensAdapter extends RecyclerView.Adapter<DimensAdapter.ViewHolder> {

    private final ArrayList<DimenModel> originalData; // full list
    private final ArrayList<DimenModel> data;         // filtered list
    private final HashMap<Integer, String> notesMap;
    private final ResourcesEditorActivity activity;

    public DimensAdapter(ArrayList<DimenModel> data,
                         ResourcesEditorActivity activity,
                         HashMap<Integer, String> notesMap) {

        this.originalData = new ArrayList<>(data);
        this.data = data;
        this.activity = activity;
        this.notesMap = notesMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PalletCustomviewBinding binding = PalletCustomviewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DimenModel model = data.get(position);

        holder.binding.title.setText(model.getDimenName());
        holder.binding.sub.setText(model.getDimenValue() + model.getDimenUnit());

        // notes
        int originalIndex = originalData.indexOf(model);
        if (originalIndex >= 0 && notesMap.containsKey(originalIndex)) {
            holder.binding.tvTitle.setText(notesMap.get(originalIndex));
            holder.binding.tvTitle.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvTitle.setVisibility(View.GONE);
        }

        holder.binding.color.setVisibility(View.GONE);

        holder.binding.backgroundCard.setOnClickListener(v ->
                activity.dimensEditor.showEdit(position)
        );
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // ✅ SAFE FILTER (no data loss)
    public void filter(String text) {

        data.clear();

        if (text == null || text.isEmpty()) {
            data.addAll(originalData);
        } else {
            text = text.toLowerCase();

            for (DimenModel m : originalData) {
                if (m.getDimenName().toLowerCase().contains(text)
                        || (m.getDimenValue() + m.getDimenUnit()).toLowerCase().contains(text)) {
                    data.add(m);
                }
            }
        }

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public PalletCustomviewBinding binding;

        public ViewHolder(PalletCustomviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}