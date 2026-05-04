package pro.sketchware.activities.resourceseditor.components.fragments;

import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.*;

import java.util.*;

import pro.sketchware.activities.resourceseditor.ResourcesEditorActivity;
import pro.sketchware.activities.resourceseditor.components.adapters.DimensAdapter;
import pro.sketchware.activities.resourceseditor.components.models.DimenModel;
import pro.sketchware.activities.resourceseditor.components.utils.DimensEditorManager;
import pro.sketchware.databinding.ResourcesEditorFragmentBinding;
import pro.sketchware.utility.*;

public class DimensEditor extends Fragment {

    public ArrayList<DimenModel> list = new ArrayList<>();
    public HashMap<Integer,String> notes = new HashMap<>();

    public DimensAdapter adapter;
    public DimensEditorManager manager = new DimensEditorManager();

    public boolean hasUnsavedChanges;
    public String path;

    private ResourcesEditorActivity activity;
    private ResourcesEditorFragmentBinding binding;

    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
        activity = (ResourcesEditorActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle b){
        binding = ResourcesEditorFragmentBinding.inflate(i,c,false);
        return binding.getRoot();
    }

    public void updateDimensList(String filePath, int mode, boolean unsaved){
        path = filePath;
        hasUnsavedChanges = unsaved;

        list = manager.parse(FileUtil.readFileIfExist(filePath));
        notes = manager.notesMap;

        activity.runOnUiThread(() -> {
            adapter = new DimensAdapter(list, activity, notes);
            binding.recyclerView.setAdapter(adapter);
            updateNoContentLayout();
        });
    }

    private void updateNoContentLayout(){
        if(list.isEmpty()){
            binding.noContentLayout.setVisibility(View.VISIBLE);
            binding.noContentTitle.setText("No Dimens");
            binding.noContentBody.setText("Create a dimen");
        } else {
            binding.noContentLayout.setVisibility(View.GONE);
        }
    }

    public void showAdd(){
        showEdit(-1);
    }

    public void showEdit(int pos){

        DimenModel m = pos>=0 ? list.get(pos) : null;

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(1);
        root.setPadding(40,40,40,0);

        TextInputLayout nameL = new TextInputLayout(getContext(),null,
                com.google.android.material.R.attr.textInputStyle);
        nameL.setHint("Name");

        TextInputEditText name = new TextInputEditText(nameL.getContext());
        name.setSingleLine(true);
        name.setFilters(new InputFilter[]{
                (s,a,b,d,c,e)->s.toString().matches("[a-zA-Z0-9_]*")?null:""
        });
        nameL.addView(name);

        TextInputLayout valL = new TextInputLayout(getContext(),null,
                com.google.android.material.R.attr.textInputStyle);
        valL.setHint("Value");

        TextInputEditText val = new TextInputEditText(valL.getContext());
        val.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        valL.addView(val);

        RadioGroup rg = new RadioGroup(getContext());
        rg.setOrientation(0);

        RadioButton dp = new RadioButton(getContext());
        dp.setText("dp");

        RadioButton sp = new RadioButton(getContext());
        sp.setText("sp");

        rg.addView(dp);
        rg.addView(sp);

        TextView preview = new TextView(getContext());

        val.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable e){
                String v=e.toString();
                String u=sp.isChecked()?"sp":"dp";
                preview.setText(v.isEmpty()?"":v+u);
            }
            public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            public void onTextChanged(CharSequence s,int a,int b,int c){}
        });

        rg.setOnCheckedChangeListener((g,id)->{
            String v=val.getText().toString();
            String u=sp.isChecked()?"sp":"dp";
            preview.setText(v.isEmpty()?"":v+u);
        });

        if(m!=null){
            name.setText(m.getDimenName());
            val.setText(m.getDimenValue());
            if("sp".equals(m.getDimenUnit())) sp.setChecked(true);
            else dp.setChecked(true);
        } else dp.setChecked(true);

        root.addView(nameL);
        root.addView(valL);
        root.addView(rg);
        root.addView(preview);

        new MaterialAlertDialogBuilder(getContext())
        .setTitle(m==null?"Create dimen":"Edit dimen")
        .setView(root)
        .setPositiveButton("Save",(d,w)->{

            String n=name.getText().toString();
            String v=val.getText().toString();
            String u=sp.isChecked()?"sp":"dp";

            if(n.isEmpty()||v.isEmpty()){
                SketchwareUtil.toastError("Fill all");
                return;
            }

            if(m!=null){
                m.setDimenName(n);
                m.setDimenValue(v);
                m.setDimenUnit(u);
            } else {
                list.add(new DimenModel(n,v,u));
            }

            adapter.notifyDataSetChanged();
            hasUnsavedChanges=true;
        })
        .setNegativeButton("Cancel",null)
        .show();
    }

    public void save(){
        if(hasUnsavedChanges){
            XmlUtil.saveXml(path, manager.build(list,notes));
            hasUnsavedChanges=false;
        }
    }
}