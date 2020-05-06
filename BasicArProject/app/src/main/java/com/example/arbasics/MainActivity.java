package com.example.arbasics;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private ArFragment mArFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mArFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);

        mArFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            /*
            * Anchor is a real-world position and orientation that can be used for placing objects in AR Scene
            * OR An anchor is used to described a fixed location and orientation in real world that can hold a 3D content in World space.
            * */
            Anchor anchor = hitResult.createAnchor();

            ModelRenderable.builder()
                    .setSource(this, Uri.parse("ArcticFox_Posed.sfb"))
                    .build()
                    .thenAccept(modelRenderable -> {
                        addModelToScene(anchor, modelRenderable);
                    })
                    .exceptionally(throwable -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(throwable.getMessage())
                                .show();
                        return null;
                    });
        });
    }

    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {
        /*Node that is automatically positioned in world space based on an ARCore Anchor.*/
        AnchorNode anchorNode = new AnchorNode(anchor);  //By Default it can not move or resize
        /*So To zoom-in, zoom-out or move our 3d model we will use TranformableNode*/
        TransformableNode transformableNode = new TransformableNode(mArFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);  // Now this node is child of anchor node
        transformableNode.setRenderable(modelRenderable);  //Adding model to this node
        /*Nodes have been created. Now need to add this node in scene view*/
        mArFragment.getArSceneView().getScene().addChild(anchorNode); //Here we will always put parent node as a child and child node will automatic  include there
        transformableNode.select();
    }
}
