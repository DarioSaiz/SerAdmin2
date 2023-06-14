package com.example.seradmin.Tree;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seradmin.Login;
import com.example.seradmin.R;
import com.example.seradmin.Recycler.Cliente;
import com.example.seradmin.Tree.ControladoresTree.TreeNode;
import com.example.seradmin.Tree.ControladoresTree.TreeViewAdapter;
import com.example.seradmin.Tree.ControladoresTree.TreeViewHolderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileTreeFragment extends Fragment {
    private TreeViewAdapter treeViewAdapter;
    private static final String TAG = "FileTreeFragment";

    Cliente cliente = new Cliente();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        idCliente = getActivity().getIntent().getStringExtra(Login.EXTRA_ID_CLIENTE);
//        idSociedad = (String) getActivity().getIntent().getSerializableExtra(Login.EXTRA_SOCIEDAD);
        cliente = (Cliente) getActivity().getIntent().getSerializableExtra("Cliente");
        Log.e(TAG, "Error retrieving PDF files from Firebase Storage"+cliente.getSociedad());

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_arbol, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.files_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setNestedScrollingEnabled(false);

        TreeViewHolderFactory factory = (v, layout) -> new FileViewHolder(v);
        treeViewAdapter = new TreeViewAdapter(factory);
        recyclerView.setAdapter(treeViewAdapter);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Obtener el clientId de los extras del intent
        //String idCliente = getIntent().getStringExtra(Login.EXTRA_ID_CLIENTE);

        // Crea una referencia al directorio "pdfs" en Firebase Storage
        StorageReference storageRef = storage.getReference().child(cliente.getDni_cliente()).child("pdfs");
        // Filtra los archivos por el ID del cliente
        //StorageReference clientRef = storageRef.child(idCliente);

        TreeNode pdfNode;
        TreeNode pdfNode1;

        switch (cliente.getSociedad()) {
            case "Autónomo":
                pdfNode = new TreeNode("Mod 131", R.layout.list_item_file);
                break;
            case "Sociedad Limitada":
                pdfNode = new TreeNode("Mod 132", R.layout.list_item_file);
                break;
            case "Sociedad Anónima":
                pdfNode = new TreeNode("Mod 133", R.layout.list_item_file);
                break;
            case "Sociedad Civil":
                pdfNode = new TreeNode("Mod 134", R.layout.list_item_file);
                break;
            case "Cooperativa":
                pdfNode = new TreeNode("Mod 135", R.layout.list_item_file);
                pdfNode1 = new TreeNode("Mod 135", R.layout.list_item_file);
                break;
            default:
                pdfNode = new TreeNode("Default Node", R.layout.list_item_file);
                break;
        }

        //pdfNode = new TreeNode("Mod 131", R.layout.list_item_file);
        // Recupera la lista de archivos PDF en el directorio "pdfs"
        storageRef.listAll().addOnSuccessListener(listResult -> {
        //clientRef.listAll().addOnSuccessListener(listResult -> {
            List<TreeNode> fileRoots = new ArrayList<>();

            // Por cada archivo PDF recuperado, crea un nodo en el árbol
            for (StorageReference item : listResult.getItems()) {
                String fileName = item.getName();
                //TreeNode pdfNode = new TreeNode(fileName, R.layout.list_item_file);
                pdfNode.addChild(new TreeNode(fileName, R.layout.list_item_file));

            }

            fileRoots.add(pdfNode);
            // Actualiza los nodos del árbol con los archivos PDF recuperados
            treeViewAdapter.updateTreeNodes(fileRoots);

// Notifica al adaptador de RecyclerView que los datos han cambiado
            treeViewAdapter.notifyDataSetChanged();

        }).addOnFailureListener(e -> {
            // Maneja el error de recuperación de archivos desde Firebase Storage
            Log.e(TAG, "Error retrieving PDF files from Firebase Storage", e);
        });

//        TreeNode javaDirectory = new TreeNode("Java", R.layout.list_item_file);
//        javaDirectory.addChild(new TreeNode("FileJava1.java", R.layout.list_item_file));
//        javaDirectory.addChild(new TreeNode("FileJava2.java", R.layout.list_item_file));
//        javaDirectory.addChild(new TreeNode("FileJava3.java", R.layout.list_item_file));

//        TreeNode gradleDirectory = new TreeNode("Gradle", R.layout.list_item_file);
//        gradleDirectory.addChild(new TreeNode("FileGradle1.gradle", R.layout.list_item_file));
//        gradleDirectory.addChild(new TreeNode("FileGradle2.gradle", R.layout.list_item_file));
//        gradleDirectory.addChild(new TreeNode("FileGradle3.gradle", R.layout.list_item_file));
//
//        javaDirectory.addChild(gradleDirectory);
//
//        TreeNode lowLevelRoot = new TreeNode("LowLevel", R.layout.list_item_file);
//
//        List<TreeNode> fileRoots = new ArrayList<>();
//        fileRoots.add(javaDirectory);
////        fileRoots.add(lowLevelRoot);
////        fileRoots.add(cSharpDirectory);
////        fileRoots.add(gitFolder);
////
//        treeViewAdapter.updateTreeNodes(fileRoots);
        treeViewAdapter.setTreeNodeClickListener((treeNode, nodeView) -> {
            if (treeNode.getValue() instanceof String) {
                String fileName = (String) treeNode.getValue();
                downloadFile(fileName);
            }
        });

        treeViewAdapter.setTreeNodeLongClickListener((treeNode, nodeView) -> {
            if (treeNode.getValue() instanceof String) {
                String fileName = (String) treeNode.getValue();
                deleteFile(fileName);
                updateFileTree();
            }
            return false;
        });


//        treeViewAdapter.setTreeNodeClickListener((treeNode, nodeView) -> {
//            Log.d(TAG, "Click on TreeNode with value " + treeNode.getValue().toString());
//        });
//
//        treeViewAdapter.setTreeNodeLongClickListener((treeNode, nodeView) -> {
//            Log.d(TAG, "LongClick on TreeNode with value " + treeNode.getValue().toString());
//            return true;
//        });



        return view;

    }

    private void downloadFile(String fileName) {
        //File localFile = new File(requireContext().getFilesDir(), fileName);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(cliente.getDni_cliente()).child("pdfs").child(fileName);

        File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File localFile = new File(downloadsFolder, fileName);

        storageRef.getFile(localFile)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(requireContext(), "Archivo descargado: " + fileName, Toast.LENGTH_SHORT).show();
                    //updateFileTree();
                })
                .addOnFailureListener(exception -> {
                    // Ha ocurrido un error al descargar el archivo
                    // Maneja el error aquí
                });
    }
    private void deleteFile(String fileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmación");
        builder.setMessage("¿Estás seguro de que deseas eliminar el archivo: " + fileName + "?");
        builder.setPositiveButton("Sí", (dialog, which) -> {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference fileRef = storage.getReference().child(cliente.getDni_cliente()).child("pdfs").child(fileName);
        fileRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // El archivo se ha eliminado correctamente
                    Toast.makeText(requireContext(), "Archivo eliminado: " + fileName, Toast.LENGTH_SHORT).show();
                    // Actualiza la vista eliminando el nodo correspondiente
                    treeViewAdapter.removeNodeByValue(fileName);
                    //updateFileTree();
                })
                .addOnFailureListener(exception -> {
                    // Ha ocurrido un error al eliminar el archivo
                    // Maneja el error aquí
                });
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // El usuario ha cancelado la eliminación, no se realiza ninguna acción
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }





    private void updateFileTree() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(cliente.getDni_cliente()).child("pdfs");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            List<TreeNode> fileRoots = new ArrayList<>();

            // Por cada archivo PDF recuperado, crea un nodo en el árbol
            for (StorageReference item : listResult.getItems()) {
                String fileName = item.getName();
                fileRoots.add(new TreeNode(fileName, R.layout.list_item_file));
            }

            treeViewAdapter.updateTreeNodes(fileRoots);
            treeViewAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            // Maneja el error de recuperación de archivos desde Firebase Storage
            Log.e(TAG, "Error retrieving PDF files from Firebase Storage", e);
        });
    }





//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        final int menuId = item.getItemId();
//        if (menuId == R.id.expand_all_action) {
//            treeViewAdapter.expandAll();
//        }
//        else if (menuId == R.id.collapse_all_action) {
//            treeViewAdapter.collapseAll();
//        }
//        else if (menuId == R.id.expand_selected_action) {
//            treeViewAdapter.expandNode(treeViewAdapter.getSelectedNode());
//        }
//        else if (menuId == R.id.collapse_selected_action) {
//            treeViewAdapter.collapseNode(treeViewAdapter.getSelectedNode());
//        }
//        else if (menuId == R.id.expand_selected_branch_action) {
//            treeViewAdapter.expandNodeBranch(treeViewAdapter.getSelectedNode());
//        }
//        else if (menuId == R.id.collapse_selected_branch_action) {
//            treeViewAdapter.collapseNodeBranch(treeViewAdapter.getSelectedNode());
//        }
//        else if (menuId == R.id.expand_selected_level_action) {
//            treeViewAdapter.expandNodesAtLevel(2);
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
