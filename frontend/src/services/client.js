// src/api/client.js
//Axios setup with token injection
import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor for adding tokens
apiClient.interceptors.request.use(
    (config) => {
        if (process.env.NODE_ENV !== 'production') {
            console.log('Outgoing request:', config.url, config.params);
        }
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API Error:', error.response?.status, error.response?.data);
        return Promise.reject(error);
    }
);

export default apiClient;