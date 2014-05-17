package creations.icebox.recipecomposer;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RecipeCreatorFragment extends Fragment {

    final private static String TAG = "***RECIPE CREATOR FRAGMENT***:";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_recipe_creator, container, false);
        try {
            Button captureBtn = (Button) rootView.findViewById(R.id.captureRecipeBtn);
            captureBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean success = openCamera();
                }
            });
        } catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
        return rootView;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }



    private boolean openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        private Uri fileUri;
//        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        startActivityForResult(intent, 100);
        return true;
    }


}
