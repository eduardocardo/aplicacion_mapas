package com.oscar.prueba_mapas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.internal.IUiSettingsDelegate;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, LocationSource {

    private GoogleMap mMap;
    int vista = 0;//variable para seleccionar el tipo de vista
    Button cambioVista, vistaMonumento,masZoom,menosZoom,incliMas,incliMenos;
    TextView latitud, valorLatitud, longitud, valorLongitud;
    Switch localizame;
    //escucha los cambios de locsalizacion
    private OnLocationChangedListener mListener;

    //manejamos nosotros la localizacion y no el mapa
    private LocationManager locationManager;

   //
    MarkerOptions marca = null;
    Marker indicador = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //inicializo los botones
        cambioVista = (Button) findViewById(R.id.button);
        localizame = (Switch) findViewById(R.id.switch1);
        vistaMonumento = (Button) findViewById(R.id.button2);
        masZoom=(Button)findViewById(R.id.button4);
        menosZoom=(Button)findViewById(R.id.button3);
        incliMas=(Button)findViewById(R.id.button6);
        incliMenos=(Button)findViewById(R.id.button5);

        //inicializo los textView y los hago invisibles
        latitud = (TextView) findViewById(R.id.textView);
        valorLatitud = (TextView) findViewById(R.id.textView2);
        longitud = (TextView) findViewById(R.id.textView3);
        valorLongitud = (TextView) findViewById(R.id.textView4);
        ocultarTextos();

        //instanciamos locationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //se establece la precision
        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);

        //compruebo si la version es la M o superior para pedir permisos en tiempo de ejecucuion
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //compruebo que tengo los permmmisos y en caso de que no los tenga los pido
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MapsActivity.
                        this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(MapsActivity.
                        this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
            }
        }
        String dispositivo =locationManager.getBestProvider(locationCriteria,
                true);//guardo la mejor opcion de ubicacion

        //seleccionamos que automaticamente se elija el mejor proveedor de busqueda
        locationManager.requestLocationUpdates(dispositivo, 1L, 2F,  this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings configuracion=  mMap.getUiSettings ();

        // añado un  marker en Astorga y muevo la camara hacia el
        LatLng astorga = new LatLng(42.45786859136961, -6.056009223556544);
        //al añadir el marker al mapa guardo el maeker que retorna
        indicador = mMap.addMarker(new MarkerOptions().position(astorga).title("Marker en astorga"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(astorga));
        //escuchador del click largo en el fragment del mapa
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                if (marca == null) { //si no he hecho ninguna marca
                    indicador.remove();//borro la marca de astorga
                    marca = new MarkerOptions();//inicializo la marca
                    marca.position(latLng);//le fijo las coordenadas del la pulsacion

                } else {//si hay una marca antrior

                    indicador.remove(); //borro la marca anterior
                    marca.position(latLng);//fijo las coodenadas de la pulsacion

                }
                indicador = mMap.addMarker(marca);//añado la marca al mapa

                //paso las coordenadas del valor de latitud y longitud a un textView
                valorLatitud.setText(String.valueOf(latLng.latitude));
                valorLongitud.setText(String.valueOf(latLng.longitude));
                mostrarTextos();
            }
        });


        //Escuchador del boton de cambio de vista
        cambioVista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (vista) {
                    case 0:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);//cambio el mapa a tipo satelite
                        break;
                    case 1:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);//cambio el mapa a tipo terreno
                        break;
                    case 2:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);//cambio el mapa a tipo hibrido
                        break;
                    case 3:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);////cambio el mapa a tipo normal
                        vista = -1;//fijo la variable a menos uno para que al sumarle quede en 0
                        break;     //y resetee el ciclo.
                }
                vista++;//incremento la variable vista para que en la siguiente llamada salga otra vista

            }
        });
        //escuchador del boton localizame
        localizame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //compruebo si estoy en la version andoid mashmallow o superior
                //si estoy en ella pido permisos en la ejecucion
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //compruebo que tengo los permmmisos y en caso de que no los tenga los pido
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.
                            PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MapsActivity.
                                this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                }

                if (isChecked) {
                    // activo la localizacion si el boton esta pulsado


                    mMap.setMyLocationEnabled(true);
                } else {
                    // desactivo la localizacion si no esta pulsado
                    mMap.setMyLocationEnabled(false);
                }
            }
        });

        //escuchador del boton vista monumento
        vistaMonumento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng madrid = new LatLng(40.417325, -3.683081); //
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);//pongo el mapa en el modo normal
                CameraPosition camPos = new CameraPosition.Builder()
                        .target(madrid)   //Centramos el mapa en Madrid
                        .zoom(19)         //Establecemos el zoom en 19
                        .bearing(45)      //Establecemos la orientación con el noreste arriba
                        .tilt(70)         //Bajamos el punto de vista de la cámara 70 grados
                        .build();

                CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);

                mMap.animateCamera(camUpd3);
            }
        });
        //escuchador del boton de mas zoom
        masZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition posAnt =mMap.getCameraPosition();
                if (posAnt.zoom==mMap.getMaxZoomLevel()) {
                    masZoom.setEnabled(false);
                }
                else {
                    menosZoom.setEnabled(true);
                    CameraPosition camPos = new CameraPosition.Builder()
                            .target(posAnt.target)   //Centramos el mapa en Madrid
                            .zoom(posAnt.zoom + 1)         //Establecemos el zoom en 19
                            .bearing(posAnt.bearing)      //Establecemos la orientación con el noreste arriba
                            .tilt(posAnt.tilt)         //Bajamos el punto de vista de la cámara 70 grados
                            .build();
                    CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                    mMap.animateCamera(camUpd3);
                }
            }
        });

        //escuchador del boton menos zoom
        menosZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition posAnt =mMap.getCameraPosition();
                if (posAnt.zoom==mMap.getMinZoomLevel()) {
                    menosZoom.setEnabled(false);
                }
                else {
                    masZoom.setEnabled(true);
                    CameraPosition camPos = new CameraPosition.Builder()
                            .target(posAnt.target)   //Centramos el mapa en Madrid
                            .zoom(posAnt.zoom - 1)         //Establecemos el zoom en 19
                            .bearing(posAnt.bearing)      //Establecemos la orientación con el noreste arriba
                            .tilt(posAnt.tilt)         //Bajamos el punto de vista de la cámara 70 grados
                            .build();
                    CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                    mMap.animateCamera(camUpd3);
                }
            }
        });

        incliMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition posAnt =mMap.getCameraPosition();
                float valTilt=posAnt.tilt-10;
                    if (valTilt<=0){
                        incliMenos.setEnabled(false);
                    }else {
                        incliMas.setEnabled(true);
                        masZoom.setEnabled(true);
                        CameraPosition camPos = new CameraPosition.Builder()
                                .target(posAnt.target)   //Centramos el mapa en Madrid
                                .zoom(posAnt.zoom)         //Establecemos el zoom en 19
                                .bearing(posAnt.bearing)      //Establecemos la orientación con el noreste arriba
                                .tilt(posAnt.tilt-10)         //Bajamos el punto de vista de la cámara 70 grados
                                .build();
                        Log.v("tipo", String.valueOf(posAnt.tilt));
                        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                        mMap.animateCamera(camUpd3);
                    }
            }
        });

        incliMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition posAnt =mMap.getCameraPosition();
                float valTilt=posAnt.tilt+10;
                if (valTilt>=90){//nunca llega pero es el valor maximo segun el developer
                incliMas.setEnabled(false);
                }else{
                    incliMenos.setEnabled(true);
                masZoom.setEnabled(true);
                CameraPosition camPos = new CameraPosition.Builder()
                        .target(posAnt.target)   //Centramos el mapa en Madrid
                        .zoom(posAnt.zoom)         //Establecemos el zoom en 19
                        .bearing(posAnt.bearing)      //Establecemos la orientación con el noreste arriba
                        .tilt(valTilt)         //Bajamos el punto de vista de la cámara 70 grados
                        .build();
                Log.v("tipo",String.valueOf(posAnt.tilt));
                CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                mMap.animateCamera(camUpd3);
            }}
        });



    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }

    //metodo que oculta los textView de latitud y longitud


    public void ocultarTextos() {
        longitud.setVisibility(View.INVISIBLE);
        valorLongitud.setVisibility(View.INVISIBLE);
        latitud.setVisibility(View.INVISIBLE);
        valorLatitud.setVisibility(View.INVISIBLE);
    }

    //metodo que muestra los textos
    public void mostrarTextos() {
        longitud.setVisibility(View.VISIBLE);
        valorLongitud.setVisibility(View.VISIBLE);
        latitud.setVisibility(View.VISIBLE);
        valorLatitud.setVisibility(View.VISIBLE);
    }
}
