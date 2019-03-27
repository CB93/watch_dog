package com.example.watch_dog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * <p>
 * MapsActivity
 * </p>
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * <p>
     * Logger
     * </p>
     */
    private static final String TAG = MapsActivity.class.getSimpleName();

    /**
     * <p>
     * GoogleMap obj
     * </p>
     */
    private GoogleMap mMap;

    /**
     * <p>
     * HeatmapTileProvider
     * </p>
     */

    private SearchView mSearchView;
    private Toolbar myToolbar;
    /**
     * Private instance of MyLocationListerner Class
     */
    private SearchView.SearchAutoComplete searchAutoComplete;
    private HeatmapTileProvider mProvider;

    /**
     * <p>
     * Polygon colour
     * </p>
     */
    private static final int POLGON_BORDER_COLOUR = 0xff4286f4;

    /**
     * <p>
     * Polygon line width
     * </p>
     */
    private static final int POLYGON_STROKE_WIDTH_PX = 5;

    /**
     * <p>
     * Transparency
     * </p>
     */
    private static final int POLYGON_ALPHA = 0x324286f4;
    private ArrayList<String> autoCompleteList;
    private LinkedHashMap<String, LatLng> test;

    /**
     * <p>
     * Initializes activity
     * </p>
     *
     * @param savedInstanceState - Reference to bundle obj
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new DataParser(this).execute(this);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        populateLists();

    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void successMoveMap(LatLng coordinates) {


        mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16f));


    }

    /**
     * Inflates Menu, Checks for action bar for input.
     * If input is valid, calls sendRequest() function
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        // Inflating Menu for toolbar
        getMenuInflater().inflate(R.menu.menu, menu);
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);


        searchAutoComplete = (SearchView.SearchAutoComplete) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.WHITE);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, autoCompleteList);
        searchAutoComplete.setAdapter(adapter);


        SearchManager searchManager =
                (SearchManager) getSystemService(this.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                String compareSchool = adapter.getItem(position);

                for (String key : test.keySet()) {

                    if (key.equals(compareSchool)) {

                        Toast.makeText(MapsActivity.this, "Redirecting to " +
                                        adapter.getItem(position).toString(),
                                Toast.LENGTH_SHORT).show();
                        hideKeyboard(MapsActivity.this);
                        successMoveMap(test.get(key));
                        return;


                    }

                }


            }
        });
        return true;
    }

    public void populateLists() {
        LatLng temp;
        test = new LinkedHashMap<String, LatLng>();
        autoCompleteList = new ArrayList<String>();


        // ALBION ELEMENTARY
        autoCompleteList.add("ALBION ELEMENTARY");
        temp = new LatLng(49.184756896931525, -122.55816321351014);
        test.put("ALBION ELEMENTARY", temp);

        // ALEXANDER ROBINSONS
        autoCompleteList.add("ALEXANDER ROBINSON ELEMENTARY");
        temp = new LatLng(49.21735173115143, -122.56165779138347);
        test.put("ALEXANDER ROBINSON ELEMENTARY", temp);

        // BLUE MOUNTAIN
        autoCompleteList.add("BLUE MOUNTAIN ELEMENTARY");
        temp = new LatLng(49.22341839489707, -122.53580698622471);
        test.put("BLUE MOUNTAIN ELEMENTARY", temp);

        // ERIC LANGTON
        autoCompleteList.add("ERIC LANGTON ELEMENTARY");
        temp = new LatLng(49.22326231396161, -122.5969419255582);
        test.put("ERIC LANGTON ELEMENTARY", temp);

        // FAIRVIEW
        autoCompleteList.add("FAIRVIEW ELEMENTARY");
        temp = new LatLng(49.22426687297897, -122.65089996017242);
        test.put("FAIRVIEW ELEMENTARY", temp);

        // GLENWOOD
        autoCompleteList.add("GLENWOOD ELEMENTARY");
        temp = new LatLng(49.222006223427854, -122.627629703313);
        test.put("GLENWOOD ELEMENTARY", temp);

        // GOLDEN EARS
        autoCompleteList.add("GOLDEN EARS ELEMENTARY");
        temp = new LatLng(49.2160591938419, -122.58147371253287);
        test.put("GOLDEN EARS ELEMENTARY", temp);

        // HAMMOND
        autoCompleteList.add("HAMMOND ELEMENTARY");
        temp = new LatLng(49.21245868441194, -122.65765796988252);
        test.put("HAMMOND ELEMENTARY", temp);

        // HARRY HOOGE
        autoCompleteList.add("HARRY HOOGE ELEMENTARY");
        temp = new LatLng(49.22527428531852, -122.58422838766069);
        test.put("HARRY HOOGE ELEMENTARY", temp);

        // KANAKA CREEK
        autoCompleteList.add("KANAKA CREEK ELEMENTARY");
        temp = new LatLng(49.20388295385043, -122.57210250782072);
        test.put("KANAKA CREEK ELEMENTARY", temp);

        // LAITY VIEW
        autoCompleteList.add("LAITY VIEW ELEMENTARY");
        temp = new LatLng(49.227185607329695, -122.63763295851034);
        test.put("LAITY VIEW ELEMENTARY", temp);

        // MAPLE RIDGE
        autoCompleteList.add("MAPLE RIDGE ELEMENTARY");
        temp = new LatLng(49.21231159323936, -122.64426066470743);
        test.put("MAPLE RIDGE ELEMENTARYY", temp);

        // MOUNT CRESCENT
        autoCompleteList.add("MOUNT CRESCENT ELEMENTARY");
        temp = new LatLng(49.224878780152075, -122.61657783325379);
        test.put("MOUNT CRESCENT ELEMENTARY", temp);

        // RIDGEMEADOWS COLLEGE
        autoCompleteList.add("RIDGEMEADOWS COLLEGE");
        temp = new LatLng(49.21531970161113, -122.65296249137248);
        test.put("RIDGEMEADOWS COLLEGE", temp);

        // WEBSTERS CORNER
        autoCompleteList.add("WEBSTER'S CORNERS ELEMENTARY");
        temp = new LatLng(49.21999964988263, -122.51350992235457);
        test.put("WEBSTER'S CORNERS ELEMENTARY", temp);

        // YENNANON
        autoCompleteList.add("YENNADON ELEMENTARY");
        temp = new LatLng(49.23579816878842, -122.57615970354276);
        test.put("YENNADON ELEMENTARY", temp);

        // GARIBALD
        autoCompleteList.add("GARIBALD SECONDARY");
        temp = new LatLng(49.22118241103326, -122.53660388022061);
        test.put("GARIBALD SECONDARY", temp);

        // MAPLE RIDGE SENIOR
        autoCompleteList.add("MAPLE RIDGE SENIOR SECONDARY");
        temp = new LatLng(49.22451106742064, -122.61489463204458);
        test.put("MAPLE RIDGE SENIOR SECONDARY", temp);

        autoCompleteList.add("WESTVIEW SECONDARY");
        temp = new LatLng(49.22435428861889, -122.64230673236665);
        test.put("WESTVIEW SECONDARY", temp);

        autoCompleteList.add("THOMAS HANEY SECONDARY");
        temp = new LatLng(49.21181745252135, -122.58228501282599);
        test.put("THOMAS HANEY SECONDARY", temp);

        autoCompleteList.add("ALOUETTE ELEMENTARY");
        temp = new LatLng(49.2297232123249, -122.60812115439084);
        test.put("ALOUETTE ELEMENTARY", temp);

        autoCompleteList.add("WHONNOCK ELEMENTARY");
        temp = new LatLng(49.20620076579272, -122.46050004203373);
        test.put("WHONNOCK ELEMENTARY", temp);

        autoCompleteList.add("MAPLE RIDGE CHRISTIAN SCHOOL");
        temp = new LatLng(49.22358547730036, -122.65355899950981);
        test.put("MAPLE RIDGE CHRISTIAN SCHOOL", temp);

        autoCompleteList.add("MAPLE RIDGE CHRISTIAN ACADEMY");
        temp = new LatLng(49.22093380649517, -122.65382906171803);
        test.put("MAPLE RIDGE CHRISTIAN ACADEMY", temp);

        autoCompleteList.add("MEADOWRIDGE PRIVATE SCHOOL");
        temp = new LatLng(49.224624335913916, -122.55523986494059);
        test.put("MEADOWRIDGE PRIVATE SCHOOL", temp);

        autoCompleteList.add("ST. PATRICK'S PRIVATE SCHOOL");
        temp = new LatLng(49.222695821926294, -122.59628195319723);
        test.put("ST. PATRICK'S PRIVATE SCHOOL", temp);

        autoCompleteList.add("SAMUEL ROBERTSON TECHNICAL SECONDARY");
        temp = new LatLng(49.192516532539216, -122.5432314511877);
        test.put("SAMUEL ROBERTSON TECHNICAL SECONDARY", temp);

        autoCompleteList.add("MAPLE RIDGE ALTERNATIVE ALOUETTE RIVER CAMPUS");
        temp = new LatLng(49.24341374707297, -122.58173092172648);
        test.put("MAPLE RIDGE ALTERNATIVE ALOUETTE RIVER CAMPUS", temp);


    }

    /**
     * <p>
     * Applies map settings
     * </p>
     *
     * @param googleMap - GoogleMap obj
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        /**
         * <p>
         *     Clicking a marker opens school site
         * </p>
         */
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String site = ((School) marker.getTag()).getSite();
                Intent intent = new Intent(MapsActivity.this, SchoolPageActivity.class);
                intent.putExtra("site", site);
                startActivity(intent);
                return false;
            }
        });

        this.focusMapView(mMap);
        this.drawPolygon(mMap);
    }

    /**
     * <p>
     * Focuses the view of the map on Maple Ridge
     * </p>
     *
     * @param googleMap - Googlemap obj
     */
    public void focusMapView(GoogleMap googleMap) {
        LatLng mapleRidge = new LatLng(49.254450, -122.536980);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapleRidge, 11f));
    }

    /**
     * <p>
     * Draws the polygon outlining Maple Ridge's city boundary
     * </p>
     *
     * @param googleMap - GoogleMap obj
     */
    public void drawPolygon(GoogleMap googleMap) {
        Polygon polygon = googleMap.addPolygon(new PolygonOptions()
                .add(
                        new LatLng(49.1687455, -122.4623709),
                        new LatLng(49.1710831, -122.462488899999983),
                        new LatLng(49.1715323, -122.458484499999983),
                        new LatLng(49.1719397, -122.452480099999988),
                        new LatLng(49.1718602, -122.450289),
                        new LatLng(49.1715016, -122.4448288),
                        new LatLng(49.1710531, -122.4391078),
                        new LatLng(49.1707041, -122.435858399999987),
                        new LatLng(49.1701188, -122.4329765),
                        new LatLng(49.1697433, -122.430007299999986),
                        new LatLng(49.1695193, -122.4286838),
                        new LatLng(49.1692039, -122.427403399999989),
                        new LatLng(49.1683981, -122.4246397),
                        new LatLng(49.1721185, -122.4246398),
                        new LatLng(49.1721625, -122.424447200000017),
                        new LatLng(49.1722431, -122.424363899999989),
                        new LatLng(49.1724946, -122.4242924),
                        new LatLng(49.1726548, -122.423947499999983),
                        new LatLng(49.1731139, -122.424024499999987),
                        new LatLng(49.1733741, -122.4238843),
                        new LatLng(49.1739948, -122.4238909),
                        new LatLng(49.1742194, -122.4238334),
                        new LatLng(49.1748227, -122.423949799999988),
                        new LatLng(49.1750024, -122.4239066),
                        new LatLng(49.1751815, -122.423753600000012),
                        new LatLng(49.1752884, -122.4235466),
                        new LatLng(49.1754683, -122.4235445),
                        new LatLng(49.175559, -122.4236943),
                        new LatLng(49.1757575, -122.4238155),
                        new LatLng(49.1761797, -122.4237009),
                        new LatLng(49.1762069, -122.423741699999979),
                        new LatLng(49.1761545, -122.4240442),
                        new LatLng(49.176191, -122.424153499999989),
                        new LatLng(49.1763858, -122.4235338),
                        new LatLng(49.17696, -122.423225299999984),
                        new LatLng(49.1771399, -122.4232369),
                        new LatLng(49.1772033, -122.4233185),
                        new LatLng(49.1771964, -122.4237302),
                        new LatLng(49.1771258, -122.424005399999984),
                        new LatLng(49.1771451, -122.4242796),
                        new LatLng(49.1773971, -122.4242904),
                        new LatLng(49.1775584, -122.4241651),
                        new LatLng(49.1780411, -122.4235558),
                        new LatLng(49.1782116, -122.4234852),
                        new LatLng(49.1782216, -122.423690900000011),
                        new LatLng(49.1777392, -122.4243551),
                        new LatLng(49.1777936, -122.424436799999981),
                        new LatLng(49.1778745, -122.4244358),
                        new LatLng(49.1780627, -122.4242827),
                        new LatLng(49.1783664, -122.423853799999989),
                        new LatLng(49.1786084, -122.4236864),
                        new LatLng(49.1783101, -122.4242422),
                        new LatLng(49.1919333, -122.4242213),
                        new LatLng(49.1982872, -122.424211499999984),
                        new LatLng(49.2054657, -122.424138),
                        new LatLng(49.218706, -122.4241579),
                        new LatLng(49.2228419, -122.424164099999985),
                        new LatLng(49.2261492, -122.424169),
                        new LatLng(49.2274034, -122.4241709),
                        new LatLng(49.2343872, -122.4241814),
                        new LatLng(49.2658812, -122.4240748),
                        new LatLng(49.2658657, -122.4080919),
                        new LatLng(49.3531288, -122.4080109),
                        new LatLng(49.3532551, -122.579631399999982),
                        new LatLng(49.3531414, -122.5796702),
                        new LatLng(49.3529435, -122.5796719),
                        new LatLng(49.3527457, -122.5796874),
                        new LatLng(49.3526647, -122.579688),
                        new LatLng(49.3524669, -122.5797035),
                        new LatLng(49.3524311, -122.579734299999984),
                        new LatLng(49.3523862, -122.579773),
                        new LatLng(49.3522969, -122.579908499999988),
                        new LatLng(49.3519815, -122.579790300000013),
                        new LatLng(49.3519046, -122.579718899999989),
                        new LatLng(49.3515845, -122.5794213),
                        new LatLng(49.3512247, -122.5794519),
                        new LatLng(49.3511447, -122.5796312),
                        new LatLng(49.3511365, -122.579934600000016),
                        new LatLng(49.3512827, -122.5805673),
                        new LatLng(49.3512748, -122.5808703),
                        new LatLng(49.3512572, -122.580994300000015),
                        new LatLng(49.3511766, -122.581105399999984),
                        new LatLng(49.351078, -122.5811886),
                        new LatLng(49.3509073, -122.5812165),
                        new LatLng(49.3506018, -122.5813578),
                        new LatLng(49.350189, -122.5816224),
                        new LatLng(49.3501727, -122.5817466),
                        new LatLng(49.3495316, -122.5819953),
                        new LatLng(49.348978, -122.582390300000014),
                        new LatLng(49.348799, -122.5826569),
                        new LatLng(49.34862, -122.583493899999979),
                        new LatLng(49.3485269, -122.5837311),
                        new LatLng(49.3484415, -122.5838262),
                        new LatLng(49.3476144, -122.584211199999984),
                        new LatLng(49.3467613, -122.5847946),
                        new LatLng(49.3466184, -122.5848286),
                        new LatLng(49.3463686, -122.584780699999982),
                        new LatLng(49.3462316, -122.5848372),
                        new LatLng(49.3459303, -122.5853124),
                        new LatLng(49.3456845, -122.585919),
                        new LatLng(49.3455315, -122.5861499),
                        new LatLng(49.3451143, -122.586469799999989),
                        new LatLng(49.3449046, -122.586547199999984),
                        new LatLng(49.3446523, -122.5865603),
                        new LatLng(49.3445179, -122.586640499999987),
                        new LatLng(49.3444261, -122.586807599999986),
                        new LatLng(49.3443922, -122.587077),
                        new LatLng(49.3443307, -122.5872116),
                        new LatLng(49.3438405, -122.5878618),
                        new LatLng(49.3434552, -122.5882676),
                        new LatLng(49.342861, -122.588783699999979),
                        new LatLng(49.3420606, -122.5896261),
                        new LatLng(49.3417142, -122.589921),
                        new LatLng(49.3415029, -122.5903628),
                        new LatLng(49.3411812, -122.5906051),
                        new LatLng(49.3407935, -122.591092500000016),
                        new LatLng(49.3406189, -122.591198799999987),
                        new LatLng(49.3405268, -122.591254899999981),
                        new LatLng(49.3398452, -122.59147710000002),
                        new LatLng(49.3394505, -122.591527099999979),
                        new LatLng(49.3392226, -122.5914717),
                        new LatLng(49.3392203, -122.5914708),
                        new LatLng(49.3388789, -122.5912874),
                        new LatLng(49.3387759, -122.591324799999981),
                        new LatLng(49.3386059, -122.5917768),
                        new LatLng(49.3378801, -122.5933367),
                        new LatLng(49.33773, -122.593536899999989),
                        new LatLng(49.3375958, -122.593606199999982),
                        new LatLng(49.3374325, -122.5936112),
                        new LatLng(49.3372914, -122.5935544),
                        new LatLng(49.3372181, -122.593481399999988),
                        new LatLng(49.3370774, -122.5932268),
                        new LatLng(49.3369114, -122.5931259),
                        new LatLng(49.3368133, -122.5931876),
                        new LatLng(49.33661, -122.5934284),
                        new LatLng(49.3312791, -122.5933758),
                        new LatLng(49.3276505, -122.593358399999985),
                        new LatLng(49.3276253, -122.5989461),
                        new LatLng(49.3276127, -122.6001987),
                        new LatLng(49.322104, -122.6000654),
                        new LatLng(49.3187584, -122.6000076),
                        new LatLng(49.2953738, -122.599711699999986),
                        new LatLng(49.2804923, -122.5995742),
                        new LatLng(49.2712551, -122.599587),
                        new LatLng(49.2713231, -122.6108392),
                        new LatLng(49.2656042, -122.610873499999983),
                        new LatLng(49.2641289, -122.6108823),
                        new LatLng(49.2640981, -122.601126899999983),
                        new LatLng(49.2567316, -122.600654299999988),
                        new LatLng(49.2568433, -122.6150455),
                        new LatLng(49.256895, -122.6219088),
                        new LatLng(49.2496198, -122.6220925),
                        new LatLng(49.2495322, -122.6446577),
                        new LatLng(49.2489385, -122.644658399999983),
                        new LatLng(49.2422942, -122.6446659),
                        new LatLng(49.2423079, -122.6558314),
                        new LatLng(49.2422759, -122.655831599999985),
                        new LatLng(49.2420781, -122.6558604),
                        new LatLng(49.2418263, -122.655889699999989),
                        new LatLng(49.2411788, -122.655949100000015),
                        new LatLng(49.2405043, -122.6560088),
                        new LatLng(49.239587, -122.6560838),
                        new LatLng(49.2387057, -122.6561449),
                        new LatLng(49.2374377, -122.656263599999988),
                        new LatLng(49.2372489, -122.6562786),
                        new LatLng(49.2364754, -122.656339),
                        new LatLng(49.2355312, -122.6564417),
                        new LatLng(49.2350276, -122.65648640000002),
                        new LatLng(49.2348568, -122.6565013),
                        new LatLng(49.2346679, -122.6565163),
                        new LatLng(49.2339934, -122.6565759),
                        new LatLng(49.2337686, -122.656605),
                        new LatLng(49.2335528, -122.65662020000002),
                        new LatLng(49.2330851, -122.656650899999988),
                        new LatLng(49.2327434, -122.6566808),
                        new LatLng(49.2323965, -122.656723),
                        new LatLng(49.231774, -122.6567753),
                        new LatLng(49.231771, -122.665320099999988),
                        new LatLng(49.2214953, -122.6653275),
                        new LatLng(49.2214955, -122.6693405),
                        new LatLng(49.2197457, -122.6691713),
                        new LatLng(49.2188373, -122.669204799999989),
                        new LatLng(49.2179471, -122.6693343),
                        new LatLng(49.2161659, -122.669291299999983),
                        new LatLng(49.2156172, -122.6692812),
                        new LatLng(49.2150684, -122.6692574),
                        new LatLng(49.2149245, -122.6692584),
                        new LatLng(49.2144928, -122.6693024),
                        new LatLng(49.2133506, -122.6693787),
                        new LatLng(49.2127558, -122.6689845),
                        new LatLng(49.2123501, -122.6686577),
                        new LatLng(49.212215, -122.668617399999988),
                        new LatLng(49.2112808, -122.6690492),
                        new LatLng(49.2108943, -122.6691617),
                        new LatLng(49.2097527, -122.6694439),
                        new LatLng(49.2090605, -122.6702137),
                        new LatLng(49.2087843, -122.6705212),
                        new LatLng(49.2084157, -122.6705923),
                        new LatLng(49.2079392, -122.6707053),
                        new LatLng(49.2073639, -122.670819),
                        new LatLng(49.2068706, -122.671330199999986),
                        new LatLng(49.2062598, -122.6716363),
                        new LatLng(49.2060848, -122.6716928),
                        new LatLng(49.2060842, -122.6694889),
                        new LatLng(49.2, -122.6694927),
                        new LatLng(49.1998329, -122.669492800000015),
                        new LatLng(49.1997354, -122.6694909),
                        new LatLng(49.1960623, -122.669417),
                        new LatLng(49.1957891, -122.6673185),
                        new LatLng(49.1988067, -122.6560464),
                        new LatLng(49.199186, -122.655168700000019),
                        new LatLng(49.1995601, -122.6542861),
                        new LatLng(49.1999292, -122.653398399999986),
                        new LatLng(49.2002932, -122.652505799999986),
                        new LatLng(49.200652, -122.651608399999986),
                        new LatLng(49.2036067, -122.6438725),
                        new LatLng(49.2068489, -122.635537099999979),
                        new LatLng(49.2075229, -122.633695),
                        new LatLng(49.207804, -122.6328603),
                        new LatLng(49.2079402, -122.6324397),
                        new LatLng(49.2082035, -122.6315924),
                        new LatLng(49.208455, -122.630736899999988),
                        new LatLng(49.2085763, -122.630306099999984),
                        new LatLng(49.2088098, -122.6294387),
                        new LatLng(49.2090313, -122.628564),
                        new LatLng(49.2092405, -122.627682299999989),
                        new LatLng(49.2093405, -122.627239),
                        new LatLng(49.2094374, -122.626794),
                        new LatLng(49.2096219, -122.625899599999983),
                        new LatLng(49.2097095, -122.6254501),
                        new LatLng(49.2098753, -122.624547199999981),
                        new LatLng(49.2100285, -122.623639),
                        new LatLng(49.210169, -122.6227261),
                        new LatLng(49.2102968, -122.621808799999982),
                        new LatLng(49.210347, -122.6214065),
                        new LatLng(49.2103912, -122.62100460000002),
                        new LatLng(49.2104298, -122.6206013),
                        new LatLng(49.2104626, -122.6201968),
                        new LatLng(49.2104898, -122.619791399999983),
                        new LatLng(49.2105112, -122.619385100000017),
                        new LatLng(49.2105268, -122.6189783),
                        new LatLng(49.2105367, -122.618571),
                        new LatLng(49.2105663, -122.617046799999983),
                        new LatLng(49.2106027, -122.615522900000016),
                        new LatLng(49.2106458, -122.6139994),
                        new LatLng(49.2107053, -122.6121238),
                        new LatLng(49.2107096, -122.611771),
                        new LatLng(49.2107086, -122.6114182),
                        new LatLng(49.2107022, -122.6110654),
                        new LatLng(49.2106906, -122.610713),
                        new LatLng(49.2106736, -122.6103611),
                        new LatLng(49.2106512, -122.6100099),
                        new LatLng(49.2106216, -122.6096366),
                        new LatLng(49.2105866, -122.6092703),
                        new LatLng(49.2105458, -122.608905399999983),
                        new LatLng(49.2104991, -122.6085421),
                        new LatLng(49.2104468, -122.6081806),
                        new LatLng(49.2103887, -122.607821099999981),
                        new LatLng(49.210325, -122.6074639),
                        new LatLng(49.2102556, -122.6071092),
                        new LatLng(49.2101806, -122.6067571),
                        new LatLng(49.2101, -122.6064079),
                        new LatLng(49.2100139, -122.6060618),
                        new LatLng(49.2099224, -122.605719),
                        new LatLng(49.2098254, -122.605379699999986),
                        new LatLng(49.2097231, -122.605044),
                        new LatLng(49.2096155, -122.6047122),
                        new LatLng(49.2095026, -122.6043845),
                        new LatLng(49.2093846, -122.6040611),
                        new LatLng(49.2092615, -122.603742100000019),
                        new LatLng(49.2091334, -122.6034278),
                        new LatLng(49.2090004, -122.603118199999983),
                        new LatLng(49.2085217, -122.6021229),
                        new LatLng(49.207432, -122.6001046),
                        new LatLng(49.2070596, -122.5994435),
                        new LatLng(49.2066827, -122.5987884),
                        new LatLng(49.2063014, -122.598139299999985),
                        new LatLng(49.2059156, -122.5974964),
                        new LatLng(49.2055255, -122.596859699999982),
                        new LatLng(49.205131, -122.5962292),
                        new LatLng(49.204728, -122.595596299999983),
                        new LatLng(49.2043199, -122.594971),
                        new LatLng(49.2039068, -122.5943534),
                        new LatLng(49.2034888, -122.5937435),
                        new LatLng(49.2030659, -122.5931415),
                        new LatLng(49.2026382, -122.592547499999981),
                        new LatLng(49.2022058, -122.591961499999982),
                        new LatLng(49.2017688, -122.5913837),
                        new LatLng(49.2015197, -122.591077),
                        new LatLng(49.2012677, -122.5907759),
                        new LatLng(49.2010128, -122.590480499999984),
                        new LatLng(49.2007554, -122.5901909),
                        new LatLng(49.2004953, -122.5899071),
                        new LatLng(49.2002324, -122.589629099999982),
                        new LatLng(49.199967, -122.589357),
                        new LatLng(49.1996989, -122.5890908),
                        new LatLng(49.1994284, -122.5888306),
                        new LatLng(49.1991554, -122.5885764),
                        new LatLng(49.1988801, -122.5883283),
                        new LatLng(49.1986023, -122.588086299999986),
                        new LatLng(49.1983224, -122.587850499999988),
                        new LatLng(49.1980402, -122.587621),
                        new LatLng(49.1977558, -122.5873977),
                        new LatLng(49.1973385, -122.5870907),
                        new LatLng(49.1969191, -122.5867906),
                        new LatLng(49.1964976, -122.5864976),
                        new LatLng(49.196074, -122.5862116),
                        new LatLng(49.1956484, -122.5859326),
                        new LatLng(49.1932844, -122.5844614),
                        new LatLng(49.1926047, -122.584103),
                        new LatLng(49.1919271, -122.583735399999981),
                        new LatLng(49.1912517, -122.583358800000013),
                        new LatLng(49.1905784, -122.5829732),
                        new LatLng(49.1899074, -122.5825786),
                        new LatLng(49.1892387, -122.582175),
                        new LatLng(49.187691, -122.5808348),
                        new LatLng(49.1864413, -122.5795277),
                        new LatLng(49.1847591, -122.5763835),
                        new LatLng(49.1825008, -122.5688618),
                        new LatLng(49.1805058, -122.5612872),
                        new LatLng(49.1785216, -122.5549076),
                        new LatLng(49.1768902, -122.550815799999981),
                        new LatLng(49.1750009, -122.5473998),
                        new LatLng(49.172193, -122.5411419),
                        new LatLng(49.1689319, -122.5298224),
                        new LatLng(49.1673167, -122.5179249),
                        new LatLng(49.1668788, -122.511433),
                        new LatLng(49.1669874, -122.509860099999983),
                        new LatLng(49.1670961, -122.5082872),
                        new LatLng(49.1673133, -122.5051414),
                        new LatLng(49.168541, -122.4911438),
                        new LatLng(49.1695195, -122.481511699999984),
                        new LatLng(49.1699011, -122.4699972),
                        new LatLng(49.1687455, -122.4623709)
                )
                .strokeColor(POLGON_BORDER_COLOUR)
                .strokeWidth(POLYGON_STROKE_WIDTH_PX)
                .fillColor(POLYGON_ALPHA));
    }

    /**
     * <p>
     * Draws the heat map
     * </p>
     *
     * @param crimeLatLngs - Lat and lngs of all crimes
     */
    @TargetApi(Build.VERSION_CODES.N)
    public void drawHeatMap(ArrayList<Object> crimeLatLngs) {
        this.mProvider = new HeatmapTileProvider.Builder()
                .data(crimeLatLngs.stream().map(x -> (LatLng) x).collect(Collectors.toList())).build();
        this.mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }


    /**
     * <p>
     * Add school pins to map
     * </p>
     *
     * @param schools - Schools
     */
    public void addSchoolPins(ArrayList<Object> schools) {
        for (Object school : schools) {
            Marker marker = this.mMap.addMarker(new MarkerOptions()
                    .position(((School) school).getLatLng())
                    .icon(BitmapDescriptorFactory.
                            fromBitmap(resizeMapIcon("map_pin", 60, 60))));
            marker.setTag(school);
        }
    }

    /**
     * <p>
     * Resize map icon
     * </p>
     *
     * @param iconName - Icon name
     * @param width    - Width
     * @param height   - Height
     * @return Icon
     */
    public Bitmap resizeMapIcon(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(
                getResources(),
                getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    /**
     * <p>
     * Parse json crime data
     * </p>
     */
    private class DataParser extends AsyncTask<Context, Void, ArrayList<ArrayList<Object>>> {

        /**
         * <p>
         * Map activity
         * </p>
         */
        private MapsActivity map;

        /**
         * <p>
         * Constructor
         * </p>
         *
         * @param context
         */
        public DataParser(MapsActivity context) {
            this.map = context;
        }

        /**
         * <p>
         * toString the json crime data
         * </p>
         *
         * @param context - Acitivity
         * @param file    - Json file
         * @return Crime data as a string
         */
        public String loadJSONFromAsset(Context context, String file) {
            String json = null;
            try {
                InputStream is = context.getAssets().open(file);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }

        /**
         * <p>
         * Parses the lat and lng for all crimes
         * </p>
         *
         * @param data - JSON object
         * @return - List of lat and lngs for all crimes
         */
        public ArrayList<Object> parseCrimeJSON(JSONObject data) {
            ArrayList<Object> crimeLatLngs = new ArrayList<>();
            try {
                JSONArray features = data.getJSONArray("features");
                for (int i = 0; i < features.length(); i++) {
                    JSONArray latLng = ((JSONObject) features.get(i)).getJSONObject("geometry")
                            .getJSONArray("coordinates");
                    double lat = latLng.getDouble(1);
                    double lng = latLng.getDouble(0);

                    LatLng point = new LatLng(lat, lng);
                    crimeLatLngs.add(point);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return crimeLatLngs;
        }

        /**
         * <p>
         * Parses the school data
         * </p>
         *
         * @param data - JSON object
         * @return - List of school data
         */
        public ArrayList<Object> parseSchoolJSON(JSONObject data) {
            ArrayList<Object> schoolData = new ArrayList<>();
            try {
                JSONArray features = data.getJSONArray("features");
                for (int i = 0; i < features.length(); i++) {
                    JSONArray latLng = ((JSONObject) features.get(i)).getJSONObject("geometry")
                            .getJSONArray("coordinates");
                    double lat = latLng.getDouble(1);
                    double lng = latLng.getDouble(0);

                    LatLng point = new LatLng(lat, lng);
                    String site = features.getJSONObject(i).getJSONObject("properties").getString("WebPage");
                    schoolData.add(new School(point, site));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return schoolData;
        }

        @Override
        protected ArrayList<ArrayList<Object>> doInBackground(Context... contexts) {
            try {
                ArrayList<ArrayList<Object>> result = new ArrayList<>();
                result.add(parseCrimeJSON(new JSONObject(loadJSONFromAsset(contexts[0], "Property_Crimes_last_recorded_14_days.json"))));
                result.add(parseSchoolJSON(new JSONObject(loadJSONFromAsset(contexts[0], "Schools.json"))));
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<Object>> result) {
            map.drawHeatMap(result.get(0));
            map.addSchoolPins(result.get(1));
        }
    }
}