package pro.sketchware.activities.resourceseditor.components.adapters;

import android.view.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import pro.sketchware.activities.resourceseditor.ResourcesEditorActivity;
import pro.sketchware.activities.resourceseditor.components.models.DimenModel;
import pro.sketchware.databinding.PalletCustomviewBinding;

public class DimensAdapter extends RecyclerView.Adapter<DimensAdapter.VH> {

    private final ArrayList<DimenModel> data;
    private final ResourcesEditorActivity activity;
    private final HashMap<Integer, String> notes;

    public DimensAdapter(ArrayList<DimenModel> data,
                         ResourcesEditorActivity act,
                         HashMap<Integer, String> notes) {
        this.data = data;
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
        h.b.sub.setText(m.getDimenValue() + m.getDimenUnit()); // preview

        if (notes.containsKey(pos)) {
            h.b.tvTitle.setText(notes.get(pos));
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

    static class VH extends RecyclerView.ViewHolder {
        PalletCustomviewBinding b;
        VH(PalletCustomviewBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}