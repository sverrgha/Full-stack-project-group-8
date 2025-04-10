import mapboxgl from 'mapbox-gl';
import mapboxConfig from "../config/mapboxConfig.js";
import {createApp, onUnmounted, ref} from "vue";
import ItemCard from "../components/ItemCard.vue";
import mapMarker from "../assets/map-marker.svg";
import router from "../router/index.js";


export function useMapbox() {
    const mapInstance = ref(null);
    const mapMarkers = ref([]);

    mapboxgl.accessToken = mapboxConfig.accessToken;

    console.log(mapboxConfig.accessToken)
    const initializeMap = (container) => {
        mapInstance.value = new mapboxgl.Map({
            container,
            style: mapboxConfig.defaultStyle,
            center: mapboxConfig.defaultCenter,
            zoom: mapboxConfig.defaultZoom,
            trackUserLocation: false,
            ...mapboxConfig.options
        });

        return mapInstance.value;
    }

    const addMarkers = (listings) => {
        mapMarkers.value.forEach(marker => marker.remove())
        mapMarkers.value = [];

        listings.forEach((listing) => {
            const markerElement = document.createElement('img');
            markerElement.className = 'marker';
            markerElement.src = mapMarker;
            markerElement.style.width = '30px';
            markerElement.style.height = '30px';
            markerElement.style.backgroundSize = '100%';

            const longitude = listing.location?.longitude || listing.longitude;
            const latitude = listing.location?.latitude || listing.latitude;

            if (!longitude || !latitude) {
                console.error("Invalid coordinates for listing:", listing);
                return;
            }

            const popup = new mapboxgl.Popup({
                closeButton: true,
                closeOnClick: true,
                anchor: 'bottom'
            })

            const popupContainer = document.createElement('div');

            createApp(ItemCard, {
                id: listing.id,
                title: listing.title,
                location: listing.city,
                price: listing.price,
                imageUrl: listing.imageUrl,
            })
                .use(router)
                .mount(popupContainer);
            popup.setDOMContent(popupContainer);


            const marker = new mapboxgl.Marker(markerElement)
                .setLngLat([longitude, latitude])
                .setPopup(popup)
                .addTo(mapInstance.value);

            mapMarkers.value.push(marker);
        })
        console.log(mapMarkers.value);
    }

    const fitToMarkers = (listings) => {
        if (!listings.length) return;

        const bounds = new mapboxgl.LngLatBounds();
        listings.forEach(listing => {
            const longitude = listing.location?.longitude || listing.longitude;
            const latitude = listing.location?.latitude || listing.latitude;

            if (longitude && latitude) {
                bounds.extend([longitude, latitude]);
            }
        });

        if (!bounds.isEmpty()) {
            mapInstance.value.fitBounds(bounds, {
                padding: 100,
                animate: false
            });
        }
    };

    onUnmounted(() => {
        if (mapInstance.value) {
            mapMarkers.value.forEach(marker => marker.remove());
            mapInstance.value.remove();
        }
    });

    return {
        map: mapInstance,
        markers: mapMarkers,
        initializeMap,
        addMarkers,
        fitToMarkers
    };
}