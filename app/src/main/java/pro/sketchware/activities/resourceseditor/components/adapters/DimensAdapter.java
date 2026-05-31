package pro.sketchware.activities.resourceseditor.components.adapters;

import android.view.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import pro.sketchware.activities.resourceseditor.ResourcesEditorActivity;
import pro.sketchware.activities.resourceseditor.components.models.DimenModel;
import pro.sketchware.databinding.PalletCustomviewBinding;

public class DimensAdapter extends RecyclerView.Adapter<DimensAdapter.VH> {

    private final ArrayList<DimenModel> original;
    private final ArrayList<DimenModel> data;
    private final HashMap<Integer, String> notes;
    private final ResourcesEditorActivity activity;

    public DimensAdapter(ArrayList<DimenModel> list,
                         ResourcesEditorActivity act,
                         HashMap<Integer, String> notes) {

        this.original = new ArrayList<>(list);
        this.data = list;
        this.activity = act;
        this.notes = notes;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(PalletCustomviewBinding.inflate(LayoutInflater.from(p.getContext()), p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {

        DimenModel m = data.get(pos);

        h.b.title.setText(m.getDimenName());
        h.b.sub.setText(m.getDimenValue() + m.getDimenUnit());

        int index = original.indexOf(m);

        if (index >= 0 && notes.containsKey(index)) {
            h.b.tvTitle.setText(notes.get(index));
            h.b.tvTitle.setVisibility(View.VISIBLE);
        } else {
            h.b.tvTitle.setVisibility(View.GONE);
        }

        h.b.color.setVisibility(View.GONE);

        h.b.backgroundCard.setOnClickListener(v ->
                activity.dimensEditor.showEdit(pos));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // ✅ REQUIRED for search
    public void filter(String text) {

        data.clear();

        if (text == null || text.isEmpty()) {
            data.addAll(original);
        } else {
            text = text.toLowerCase();

            for (DimenModel m : original) {
                if (m.getDimenName().toLowerCase().contains(text)
                        || (m.getDimenValue() + m.getDimenUnit()).toLowerCase().contains(text)) {
                    data.add(m);
                }
            }
        }

        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        PalletCustomviewBinding b;
        VH(PalletCustomviewBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}