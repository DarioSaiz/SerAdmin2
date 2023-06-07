//package com.example.seradmin.Tree;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.OpenableColumns;
//import android.view.Menu;
//import android.view.View;
//import android.widget.Button;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.lifecycle.Lifecycle;
//import androidx.viewpager2.adapter.FragmentStateAdapter;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.example.seradmin.ClienteDetalle;
//import com.example.seradmin.InterfazUsuari.InterfazUsuario;
//import com.example.seradmin.Login;
//import com.example.seradmin.R;
//import com.example.seradmin.Recycler.Cliente;
//import com.example.seradmin.calendario.Calendario;
//import com.example.seradmin.databinding.FragmentNotificationsBinding;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FieldValue;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//public class MainTree extends AppCompatActivity {
//    private static final int NUMBER_OF_FRAGMENTS = 4;
//    private static final int REQUEST_CODE = 1;
//    private FloatingActionButton selectButton;
//    Cliente cliente = new Cliente();
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main_tree);
//
//        selectButton = findViewById(R.id.selected1);
//
//        ViewPager2 viewPager = findViewById(R.id.view_pager);
//        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
//        viewPager.setAdapter(viewPagerAdapter);
//        selectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectPDF();
//            }
//        });
//
//        cliente = (Cliente) getIntent().getSerializableExtra("Cliente");
//
//    }
//
//    private void selectPDF() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("application/pdf");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(Intent.createChooser(intent, "Select PDF"), REQUEST_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            Uri selectedPDF = data.getData();
//            String fileName = getFileName(selectedPDF);
//            FirebaseStorage mStorageRef = FirebaseStorage.getInstance();
//            String idCliente = cliente.getDni_cliente();
//
//            // Aquí puedes realizar acciones con el archivo PDF seleccionado, como subirlo a un servidor
//            StorageReference storageRef = mStorageRef.getReference().child(idCliente).child("pdfs").child(fileName);
//
//            // Sube el archivo a Firebase Storage
//            UploadTask uploadTask = storageRef.putFile(selectedPDF);
//
//            // Registra un listener para la finalización de la carga
//            uploadTask.addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    // La carga del archivo se ha completado exitosamente
//                    // Puedes obtener la URL de descarga del archivo usando storageRef.getDownloadUrl()
//                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
//                        String fileUrl = downloadUrl.toString();
//                        DocumentReference clientRef = FirebaseFirestore.getInstance().collection("Clientes").document(idCliente);
//                        //DocumentReference clientRef = FirebaseFirestore.getInstance().collection("Clientes").document(cliente.getId());
//                        clientRef.update("archivos", FieldValue.arrayUnion(fileUrl))
//                                .addOnSuccessListener(aVoid -> {
//                                    // La URL del archivo se ha guardado exitosamente en Firestore
//                                })
//                                .addOnFailureListener(e -> {
//                                    // Ha ocurrido un error al guardar la URL del archivo en Firestore, maneja el error aquí
//                                });
//                    });
//                } else {
//                    // La carga del archivo ha fallado
//                    Exception exception = task.getException();
//                    // Maneja el error de carga del archivo aquí
//                }
//            });
//        }
//    }
//
//    @SuppressLint("Range")
//    private String getFileName(Uri uri) {
//        String result = null;
//        if (uri.getScheme().equals("content")) {
//            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//            try {
//                if (cursor != null && cursor.moveToFirst()) {
//                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                }
//            } finally {
//                cursor.close();
//            }
//        }
//        if (result == null) {
//            result = uri.getLastPathSegment();
//        }
//        return result;
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    private static class ViewPagerAdapter extends FragmentStateAdapter {
//
//        public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
//            super(fragmentManager, lifecycle);
//        }
//
//        @NonNull
//        @Override
//        public Fragment createFragment(int position) {
//            return new FileTreeFragment();
//            // Cambia el fragmento según la posición actual si es necesario
//        }
//
//        @Override
//        public int getItemCount() {
//            return NUMBER_OF_FRAGMENTS;
//        }
//    }
//}
package com.example.seradmin.Tree;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.seradmin.ClienteDetalle;
import com.example.seradmin.InterfazUsuari.InterfazUsuario;
import com.example.seradmin.Login;
import com.example.seradmin.R;
import com.example.seradmin.Recycler.Cliente;
import com.example.seradmin.Tree.FileTreeFragment;
import com.example.seradmin.calendario.Calendario;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.seradmin.ClienteDetalle;
import com.example.seradmin.InterfazUsuari.InterfazUsuario;
import com.example.seradmin.Login;
import com.example.seradmin.R;
import com.example.seradmin.Recycler.Cliente;
import com.example.seradmin.calendario.Calendario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainTree extends Fragment {
    private static final int NUMBER_OF_FRAGMENTS = 4;
    private static final int REQUEST_CODE = 1;
    private FloatingActionButton selectButton;
    Cliente cliente = new Cliente();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_tree, container, false);

        selectButton = rootView.findViewById(R.id.selected1);

        ViewPager2 viewPager = rootView.findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), getLifecycle());
        viewPager.setAdapter(viewPagerAdapter);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });

        cliente = (Cliente) requireActivity().getIntent().getSerializableExtra("Cliente");

        return rootView;
    }

    private void selectPDF() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            Uri selectedPDF = data.getData();
            String fileName = getFileName(selectedPDF);
            FirebaseStorage mStorageRef = FirebaseStorage.getInstance();
            String idCliente = cliente.getDni_cliente();

            // Aquí puedes realizar acciones con el archivo PDF seleccionado, como subirlo a un servidor
            StorageReference storageRef = mStorageRef.getReference().child(idCliente).child("pdfs").child(fileName);

            // Sube el archivo a Firebase Storage
            UploadTask uploadTask = storageRef.putFile(selectedPDF);

            // Registra un listener para la finalización de la carga
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // La carga del archivo se ha completado exitosamente
                    // Puedes obtener la URL de descarga del archivo usando storageRef.getDownloadUrl()
                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                        String fileUrl = downloadUrl.toString();
                        DocumentReference clientRef = FirebaseFirestore.getInstance().collection("Clientes").document(idCliente);
                        //DocumentReference clientRef = FirebaseFirestore.getInstance().collection("Clientes").document(cliente.getId());
                        clientRef.update("archivos", FieldValue.arrayUnion(fileUrl))
                                .addOnSuccessListener(aVoid -> {
                                    // La URL del archivo se ha guardado exitosamente en Firestore
                                })
                                .addOnFailureListener(e -> {
                                    // Ha ocurrido un error al guardar la URL del archivo en Firestore, maneja el error aquí
                                });
                    });
                } else {
                    // La carga del archivo ha fallado
                    Exception exception = task.getException();
                    // Maneja el error de carga del archivo aquí
                }
            });
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new FileTreeFragment();
            // Cambia el fragmento según la posición actual si es necesario
        }

        @Override
        public int getItemCount() {
            return NUMBER_OF_FRAGMENTS;
        }
    }
}
