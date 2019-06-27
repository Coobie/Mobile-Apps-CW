package com.example.jacob.chgraphex;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.jacob.chgraphex.model.graph.Edge;
import com.example.jacob.chgraphex.model.graph.Graph;
import com.example.jacob.chgraphex.model.graph.GraphBuilder;
import com.example.jacob.chgraphex.model.graph.Node;
import com.example.jacob.chgraphex.model.history.GItem;
import com.example.jacob.chgraphex.model.history.GItemViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Random;

public class GraphViewActivity extends AppCompatActivity {

    private Graph g;

    private int radius = 20;
    private int arrowWidth = 2;
    private int arrowWidthSelected = 3;
    private int arrowHead = 10;
    private int textSize = 25;

    private int maxNodes = 50;

    private ActionMode mActionMode;

    private Bitmap bitmap;

    private Node selected;

    private boolean drawFlag = false;

    private float touchX;
    private float touchY;

    private float translateX = 0f;
    private float translateY = 0f;

    private SharedPreferences prefs = null;

    /**
     * On create method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);

        prefs = getSharedPreferences("com.example.chgraphex", MODE_PRIVATE);

        //Get graph stuff
        this.g = (Graph) getIntent().getSerializableExtra("graph");

        //Save to db
        GItemViewModel mGItemViewModel = ViewModelProviders.of(this).get(GItemViewModel.class);
        GItem gItem = new GItem(g.getNodes().get(0).getItem());
        mGItemViewModel.insert(gItem);

        //Create and establish custom view
        MyView view = new MyView(this);

        view.setOnTouchListener(touchListener);
        view.setOnClickListener(clickListener);
        view.setOnLongClickListener(longClickListener);

        getSupportActionBar().setTitle("Nodes: "+g.getNodes().get(0).getItem().getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(view);


    }

    /**
     * override method to check if the device has been rotated
     * @param newConfig Configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            for (Node n : g.getNodes()){
                int x = n.getX();
                int y = n.getY();

                n.setX(y);
                n.setY(x);
            }
        }
    }

    /**
     * On resume override method
     * Checks if the first time running the activity
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            showInfoBox();
            prefs.edit().putBoolean("firstrun", false).commit();
        }
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nodes_main, menu);
        return true;
    }

    /**
     * Method to show the info box about how the graphs work
     */
    public void showInfoBox(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setIcon(R.drawable.ic_info_outline_black);
        alertDialogBuilder.setMessage(R.string.help_message);
        alertDialogBuilder.setTitle(R.string.help_title);
        alertDialogBuilder.setPositiveButton("OK",null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Handles different menu clicks
     * @param item - menuItem
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_share: //Share
                share();
                return true;
            case R.id.action_save: //Save
                saveImage();
                return true;
            case R.id.action_history: //History button
                Intent intent = new Intent(this, RecentActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_info: //Info for the user
                showInfoBox();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * On touch listener to get the coordinates of where the user pressed
     */
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // save the X,Y coordinates
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                touchX = event.getX();
                touchY = event.getY();
            } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE){ //Drag for pan
                translateX = event.getX() - touchX;
                translateY = event.getY() - touchY;
            }

            // let the touch event pass on to whoever needs it
            return false;
        }
    };

    /**
     * Long click listener
     */
    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            //Log.d("LONG PRESS", "LONG PRESS");

            for (Node n : g.getNodes()) {
                if (withinCircle(touchX-translateX, touchY-translateY, n.getX(), n.getY(), dp2px(getResources(),radius))) {
                    if (mActionMode != null) {
                        return false;
                    }
                    selected = n;
                    mActionMode = startSupportActionMode(mActionModeCallback);
                    return true;
                }
            }

            return true;
        }
    };

    /**
     * Action mode menu
     */
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_node_options, menu);
            mode.setTitle(selected.getItem().getTitle());
            return true;
        }

        /**
         *
         * @param actionMode
         * @param menu
         * @return false
         */
        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        /**
         * Deal with actions in action menu
         * @param mode
         * @param menuItem - selected item
         * @return boolean
         */
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
            //Buttons in menu clicked
            boolean r = false;

            switch (menuItem.getItemId()) {
                case R.id.explode_option:
                    if (!selected.isExploded()) {

                        expandNode();
                    } else {
                        Toast.makeText(getApplicationContext(), "Node is already exploded", Toast.LENGTH_LONG).show();
                    }
                    mode.finish();
                    r = true;
                    break;
                case R.id.info_option:
                    Intent intent = new Intent(GraphViewActivity.this, NodeInfoActivity.class);
                    intent.putExtra("node_item", (Serializable) selected);
                    intent.putExtra("edges",(Serializable) g.getEdges());
                    startActivity(intent);
                    mode.finish();
                    r = true;
                    break;
                default:
                    break;
            }

            return r;
        }

        /**
         * Method for expanding nodes
         */
        private void expandNode() {
            if (g.getNodes().size() < maxNodes) { //Check number of nodes does not exceed max
                drawFlag = true;
                GraphViewActivity.AsyncTaskRunner runner = new GraphViewActivity.AsyncTaskRunner(g, selected);
                runner.execute();
            } else {
                Toast.makeText(getApplicationContext(), "Max nodes reached (" + maxNodes + ")", Toast.LENGTH_LONG).show();
            }
        }


        /**
         *
         * @param actionMode
         */
        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            selected = null;
            mActionMode = null;
        }
    };

    /**
     * Click listener to close action menu
     */
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
            clickedCanvas(touchX, touchY);
        }
    };

    /**
     * Get text for selected node
     * @param x - x of user touch
     * @param y - y of user touch
     */
    private void clickedCanvas(float x, float y) {
        //Work out whether click was inside node

        for (Node n : g.getNodes()) {
            if (withinCircle(x-translateX, y-translateY, n.getX(), n.getY(), dp2px(getResources(),radius))) {
                Toast.makeText(getApplicationContext(), n.getItem().getTitle(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Check if coordinates are in circle
     * @param pX - check x
     * @param pY - check y
     * @param cX - circle x
     * @param cY - circle y
     * @param r - circle radius
     * @return boolean - true -> within circle
     */
    public boolean withinCircle(double pX, double pY, double cX, double cY, double r) {
        if (Math.pow(pX - cX, 2) + Math.pow(pY - cY, 2) <= Math.pow(r, 2)) {
            return true;
        }
        return false;
    }

    /**
     * Make sure circle is within rect - with room to spare
     * @param x - x of circle
     * @param y - y of circle
     * @param w - width of rect
     * @param h - height of rect
     * @param r - radius of circle
     * @return boolean - true -> within
     */
    public boolean nodeWithinScreen(int x, int y, int w, int h, int r) {
        r = (int) (r * 1.25);
        if ((x > 0 + r) && (y > 0 + r) && (x < w - r) && (y < h - r)) {
            return true;
        }
        return false;
    }

    /**
     * Method for saving the image to local storage
     */
    private void saveImage() {
        if (isStoragePermissionGranted()) {
            try {
                Uri uri = Uri.parse("content://media/external/images/media");
                
                // create a package provider string suggested by the error message.
                String provider = "com.android.providers.media.MediaProvider";
                grantUriPermission(provider, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                grantUriPermission(provider, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                grantUriPermission(provider, uri, Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

                String savedImageURL = MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        bitmap,
                        "nodes",
                        "Image of Nodes"
                );
                System.out.println(savedImageURL);
                Toast.makeText(getApplicationContext(),"Image was saved successfully",Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Check storage permission
     * @return boolean true -> permission granted
     */
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Log.v("TAG", "Permission is granted");
                return true;
            } else {

                //Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            //Log.v("TAG", "Permission is granted");
            return true;
        }
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
            saveImage();
        } else {
            Toast.makeText(getApplicationContext(),"COULD NOT SAVE IMAGE",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method for sharing
     */
    private void share() {
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            File file = new File(this.getExternalCacheDir(), "nodes.jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/jpg");
            startActivity(Intent.createChooser(intent, "Share these nodes via"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Code from https://stackoverflow.com/a/38408749
     * @param resource
     * @param dp
     * @return
     */
    public static int dp2px(Resources resource, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,   dp,resource.getDisplayMetrics());
    }

    /**
     * Code from https://stackoverflow.com/a/38408749
     * @param resource
     * @param px
     * @return
     */
    public static float px2dp(Resources resource, float px)  {
        return (float) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,resource.getDisplayMetrics());
    }

    /**
     * Custom view class
     */
    public class MyView extends View {

        private Paint paint = null;

        /**
         * Constructor
         * @param context
         */
        public MyView(Context context) {
            super(context);
            setDrawingCacheEnabled(true);
            paint = new Paint();
        }

        /**
         * on draw method
         * @param canvas - drawing
         */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.WHITE);

            canvas.save();
            canvas.translate(translateX,translateY);


            int x = getWidth();
            int y = getHeight();
            int r = dp2px(getResources(),radius);

            Paint pLine = new Paint();
            pLine.setColor(Color.BLACK);
            pLine.setStrokeWidth(arrowWidth);

            //Set points for nodes
            for (Node n : g.getNodes()) {
                Random rand = new Random();

                if (n.getX() == 0 && n.getY() == 0) {
                    if (n.isRoot()) {
                        n.setX(x / 2);
                        n.setY(y / 2);
                    } else {
                        int tempX = (x / 2);
                        int tempY = (y / 2);

                        //Check node will not be drawn inside another
                        boolean valid = false;

                        while (!valid) {
                            tempX = rand.nextInt(x);
                            tempY = rand.nextInt(y);

                            valid = true;
                            for (Node a : g.getNodes()) {
                                if (withinCircle(tempX, tempY, a.getX(), a.getY(), r + r)) {
                                    valid = false;
                                    break;
                                } else if (!nodeWithinScreen(tempX, tempY, getWidth(), getHeight(), r)) {
                                    valid = false;
                                    break;
                                }
                            }

                        }


                        n.setX(tempX);
                        n.setY(tempY);
                    }
                }

            }

            int rad = dp2px(getResources(),radius);

            //Draw the edges
            for (Edge e : g.getEdges()) {
                //canvas.drawLine(e.getStart().getX(), e.getStart().getY(), e.getEnd().getX(), e.getEnd().getY(), pLine);

                double bearing = angleOf(e.getStart().getX(), e.getStart().getY(), e.getEnd().getX(), e.getEnd().getY());

                double endX = e.getEnd().getX() + rad * Math.cos(bearing * (Math.PI / 180));
                double endY = e.getEnd().getY() + rad * Math.sin(bearing * (Math.PI / 180));

                if (e.contains(selected)){
                    pLine.setColor(getResources().getColor(R.color.selectedArrows));
                    pLine.setStrokeWidth(dp2px(getResources(),arrowWidthSelected));
                } else{
                    pLine.setColor(getResources().getColor(R.color.arrowEdge));
                    pLine.setStrokeWidth(dp2px(getResources(),arrowWidth));
                }

                fillArrow(pLine, canvas, e.getStart().getX(), e.getStart().getY(), (float) endX, (float) endY);
            }

            //Draw the nodes
            for (Node n : g.getNodes()) {
                Random rand = new Random();
                //paint.setColor(Color.rgb(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)));
                if (n == selected){
                    paint.setColor(getResources().getColor(R.color.selectedNode));
                }
                else if (n.isRoot()) {
                    paint.setColor(getResources().getColor(R.color.rootNode));
                } else if (n.getType() == 0) {
                    paint.setColor(getResources().getColor(R.color.companyNode));
                } else if (n.getType() == 1) {
                    paint.setColor(getResources().getColor(R.color.officerNode));
                }
                canvas.drawCircle(n.getX(), n.getY(), rad, paint);

                if (n == selected){
                    Paint t = new Paint();
                    t.setColor(getResources().getColor(R.color.selectedNodeText));
                    t.setTextSize(dp2px(getResources(),textSize));
                    t.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(String.valueOf(getResources().getText(R.string.selected_node)),n.getX(),n.getY() - ((t.descent() + t.ascent()) / 2),t);
                }
            }

            //Draw the labels
            for (Edge e : g.getEdges()){
                //Draw text
                if (e.contains(selected)){
                    int mX = (e.getStart().getX() + e.getEnd().getX())/2;
                    int mY = (e.getStart().getY() + e.getEnd().getY())/2;
                    Paint text = new Paint();
                    text.setColor(getResources().getColor(R.color.arrowLabel));
                    text.setTextSize(dp2px(getResources(),textSize));

                    text.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(e.getLink(),mX,mY,text);
                }
            }

           canvas.restore();

            bitmap = getDrawingCache();
            if (!drawFlag) {
                invalidate();
            }
        }

    }

    /**
     * Get angle of line
     * @param x0 - first x
     * @param y0 - first y
     * @param x1 - second x
     * @param y1 - second y
     * @return double - angle from 0 ANTICLOCKWISE
     */
    public static double angleOf(int x0, int y0, int x1, int y1) {

        final double deltaY = (y0 - y1);
        final double deltaX = (x0 - x1);
        final double result = Math.toDegrees(Math.atan2(deltaY, deltaX));
        return (result < 0) ? (360d + result) : result;
    }

    /**
     * Create arrow method
     * Credit: https://stackoverflow.com/a/29971068
     *
     * @param paint - paint wanted to draw arrows
     * @param canvas - canvas to draw the arrows
     * @param x0 - first x
     * @param y0 - first y
     * @param x1 - second x
     * @param y1 - second y
     */
    private void fillArrow(Paint paint, Canvas canvas, float x0, float y0, float x1, float y1) {
        paint.setStyle(Paint.Style.STROKE);

        int arrowHeadLength = dp2px(getResources(),arrowHead);
        int arrowHeadAngle = 45;
        float[] linePts = new float[]{x1 - arrowHeadLength, y1, x1, y1};
        float[] linePts2 = new float[]{x1, y1, x1, y1 + arrowHeadLength};
        Matrix rotateMat = new Matrix();

        //get the center of the line
        float centerX = x1;
        float centerY = y1;

        //set the angle
        double angle = Math.atan2(y1 - y0, x1 - x0) * 180 / Math.PI + arrowHeadAngle;

        //rotate the matrix around the center
        rotateMat.setRotate((float) angle, centerX, centerY);
        rotateMat.mapPoints(linePts);
        rotateMat.mapPoints(linePts2);

        canvas.drawLine(linePts[0], linePts[1], linePts[2], linePts[3], paint);
        canvas.drawLine(linePts2[0], linePts2[1], linePts2[2], linePts2[3], paint);
        canvas.drawLine(x0, y0, x1, y1, paint);
    }

    /**
     * Class for background task
     */
    class AsyncTaskRunner extends AsyncTask<String, String, String> {

        Graph gr;
        Node n;

        /**
         * Constructor
         * @param gr - Graph
         * @param n - Node to expand
         */
        public AsyncTaskRunner(Graph gr, Node n) {
            super();
            this.gr = gr;
            this.n = n;
        }

        /**
         *
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {
            //Send data to graph view
            GraphBuilder gb = new GraphBuilder();

            Graph graph = gb.expandGraph(gr, n);
            Intent intent = new Intent(getApplicationContext(), GraphViewActivity.class);
            intent.putExtra("graph", (Serializable) graph);
            startActivity(intent);


            return "";
        }
    }
}
