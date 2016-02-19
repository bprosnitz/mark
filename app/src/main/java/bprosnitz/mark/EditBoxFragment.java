package bprosnitz.mark;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditBoxFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditBoxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditBoxFragment extends Fragment {
    private static final int REQUEST_CODE_SELECT_PICTURE = 1;
    private static final String TAG = "EditBoxFragment";

    private OnFragmentInteractionListener mListener;

    public EditBoxFragment() {
    }

    public static EditBoxFragment newInstance(String param1, String param2) {
        EditBoxFragment fragment = new EditBoxFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_box, container, false);
        view.findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });
        view.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationDialogFragment dialog = new LocationDialogFragment();
                dialog.show(getFragmentManager(), "NoticeDialogFragment");
            }
        });
        ((EditText)(view.findViewById(R.id.entrytext))).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "s " + s + " start " + start + " before " + before + " count "+ count);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    private static String getUniqueImageFilename() {
        return getRandomString(10);
    }

    private void openImageIntent() {
        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "mark_photos" + File.separator);
        root.mkdirs();
        final String fname = getUniqueImageFilename();
        final File sdImageMainDirectory = new File(root, fname);
        final Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        if (outputFileUri != null) {
            chooserIntent.putExtra("file_uri", outputFileUri.toString());
        }

        startActivityForResult(chooserIntent, REQUEST_CODE_SELECT_PICTURE);
    }


    private void insertItemInText(Object span) {
        EditText text = (EditText)getActivity().findViewById(R.id.entrytext);
        String str = text.getText()
                .toString();
        int cursorStartPosition = text.getSelectionStart();
        int cursorEndPosition = text.getSelectionEnd();
        String x = str.substring(0, cursorStartPosition) + '!' +
                str.substring(cursorEndPosition, str.length());
        SpannableString spannable = new SpannableString(x);
        spannable.setSpan(span, cursorStartPosition, cursorStartPosition + 1,
                Spannable.SPAN_POINT_POINT);
        text.setText(spannable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_PICTURE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    String outputFileUriStr = data.getStringExtra("file_uri");
                    selectedImageUri = Uri.parse(outputFileUriStr);
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }

                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                            selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                ImageSpan span = new ImageSpan(getActivity(), bmp);
                insertItemInText(span);
            }
        }
    }
}
