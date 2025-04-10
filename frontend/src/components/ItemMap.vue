<script setup>
import { ref, onMounted, watch, toRefs } from 'vue';
import { useMapbox } from '../composables/useMapbox';
import 'mapbox-gl/dist/mapbox-gl.css';

const props = defineProps({
  listings: {
    type: Array,
    required: 'true'
  }
});

const { listings } = toRefs(props);
const mapContainer = ref(null);
const { map, initializeMap, addMarkers, fitToMarkers } = useMapbox();

onMounted(() => {
  const mapInstance = initializeMap(mapContainer.value);

  mapInstance.on('load', () => {
    if (listings.value && listings.value.length > 0) {
      addMarkers(listings.value);
      fitToMarkers(listings.value);
    }
  });

});

watch(listings, (newListings) => {
  if (map.value && map.value.loaded() && newListings && newListings.length > 0) {
    addMarkers(newListings);
    fitToMarkers(newListings);
  }
}, { deep: true });
</script>

<template>
  <div>
    <div ref="mapContainer" class="map-container"></div>
  </div>
</template>

<style>
.map-container {
  width: 100%;
  height: 500px;
}

.marker {
  cursor: pointer;
}
</style>