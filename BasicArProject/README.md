This is the example of Basic Ar app which will render the 3D model into a plane surface using Anchor.

In this application we are using Sceneform sdk and plugin so that we do not need to worry about
OpenGl and we can easily use our 3D model and if we want, then could make some changes into it.

So we will start our implementation as following:

1. To use Sceneform, first we need to add the dependency for it. So open build.gradle file and make the following changes:

implementation 'com.google.ar.sceneform.ux:sceneform-ux:1.16.0'

We can only use AR from version 24 so the minimum sdk version should be 24(Nougat).

Also we need to add the support for java 8 so add these lines:

android{
...
 compileOptions {
         sourceCompatibility JavaVersion.VERSION_1_8
         targetCompatibility JavaVersion.VERSION_1_8
     }
}

2. Now just open Manifest file and add the required features and permissions into it:

    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-feature android:name="android.hardware.camera.ar" android:required="true"/>

and For google play to recognize your app as Augmented Reality app,
 inside the application tag, add the following meta data:

 <meta-data android:name="com.google.ar.core"
             android:value="required"/>

3. Now you need to install the sceneform plugin. So just open SDK manager and go to plugins and browse Repositiories.
Now search for Google Sceneform Tools and install the plugin and restart.

4. After restart you can now see a viewer in the right of your android studio. In this viewer, we can later see our 3D model.

5. Now you can download a 3D model from this website "poly.google.com" and you can download the obj file of any 3D model..
After download, Inside the Android Studio, create a Sample Data Directory folder inside the app folder. 

Note: The Sample Data Directory is used to put the data which you don't want to include into the apk file.

So now extract the downloaded file and copy the obj and mtl file and paste it into the sampleData directory.


6. Now you need to right click on the obj file and choose "Import Sceneform Assets". It will create .sfa and .sfb files which will be used as our 3D model.
You can open the .sfb file into the Viewer and can edit the file as well.

Note: Here if you will face any issue in creating the .sfa and .sfb file then there is an alternate solution to do this. Just copy and paste the following code into your build.gradle(app) file at bottom:

sceneform.asset('sampledata/ArcticFox_Posed.obj', // 'Source Asset Path' specified during import. Here put the name of your obj file(like "ArcticFox_Posed.obj")
        'default',                    // 'Material Path' specified during import.
        'sampledata/ArcticFox_Posed.sfa', // 'Output Path' specified during import. Here put the name of the sfa file(like ArcticFox_Posed.sfa)
        'src/main/assets/ArcticFox_Posed')  // this is the path where .sfb file will save. Just put the name for .sfb file like (ArcticFox_Posed)
		
After this syncNow and Rebuild your project. After the project will rebuild then you will see your .sfa and .sfb file into the respective folders.

7. Now open your activity_main.xml file and add this code into it:

<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

	<!--This will use as ArFragment-->
    <fragment
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/ar_fragment"
        android:name="com.google.ar.sceneform.ux.ArFragment"/>

</RelativeLayout>


8. Now open your MainActivity and add below code:

First you need to create an instance for ArFragment:
private ArFragment mArFragment;

Now inside the onCreate Method, add below code:

mArFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);

9. Now you need to add listener to notify when the user will tap on the plane surface.

//Registers a callback to be invoked when an ARCore Plane is tapped.
mArFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) ->{})

10. Now we need to create the Anchor using hitResult.
Anchor is a real-world position and orientation that can be used for placing objects in AR Scene
OR An anchor is used to described a fixed location and orientation in real world that can hold a 3D content in World space.

Anchor anchor = hitResult.createAnchor();

11. Now you need to build your model using ModelRenderable.builder().
and then to set source you need to pass context and the name of the .sfb file.
setSource(this, Uri.parse("ArcticFox_Posed.sfb"))

then add .build and after this you need to accept the model using
thenAccept(modelRenderable -> {}

Here you can also add the code to show the message if any exception will occur using
exceptionally(throwable -> {}

12. Now using modelRenderable object(provided by thenAccept() method) you need to add this model into the sceneView.

13. To add the model in scene view, first you need to create an Anchor Node object and pass anchor as
the parameter which we created earlier.
The Anchor Node is a Node that is automatically positioned in world space based on an ARCore Anchor.

AnchorNode anchorNode = new AnchorNode(anchor);  //By Default it can not move or resize

So If you want to create a 3D model which you can move or zoom-in, zoom-out
then you need to create an instance of TransformableNode.

  /*So To zoom-in, zoom-out or move our 3d model we will use TranformableNode*/
  TransformableNode transformableNode = new TransformableNode(mArFragment.getTransformationSystem());

then set anchorNode as its parent
transformableNode.setParent(anchorNode);  // Now this node is child of anchor node

Now we will add the model to the child node i.e transformableNode.
transformableNode.setRenderable(modelRenderable);  //Adding model to this node

14. Now the nodes have been created so you need to add this node into the scene view.
Here we will always put the parent node as a child and child node(transformableNode) will automatic include there.

mArFragment.getArSceneView().getScene().addChild(anchorNode);

15. Now just Sets this as the selected node in the TransformationSystem
transformableNode.select();



Now when you will run the app the app will ask for the camera permission.
After provide the Camera Permission the app will try to detect the plane surface.
When the app will detect the plane surface then it will show some white dots.
When you will tap on that dots then your 3D model will be render there and you will see your 3D model in the real world.
You can zoom-in, zoom-out and move the object.

That's it for this basic project.

