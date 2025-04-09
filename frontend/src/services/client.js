// In your client.js, ensure the authorization token is being added
import axios from 'axios';

const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api',
});

// Inject token into requests
apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default apiClient;