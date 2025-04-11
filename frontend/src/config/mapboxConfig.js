export default {
    accessToken: import.meta.env.VITE_MAPBOX_ACCESS_TOKEN,
    defaultStyle: 'mapbox://styles/mapbox/streets-v11',
    defaultCenter: [-74.5, 40],
    defaultZoom: 9,
    options: {
        pitch: 0,
        bearing: 0,
        minZoom: 3,
        maxZoom: 18,
    }
};