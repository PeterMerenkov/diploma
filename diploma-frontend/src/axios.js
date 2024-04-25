import { createApp } from 'vue';
import axios from 'axios';

const app = createApp({});
app.config.globalProperties.$axios = axios.create({
  baseURL: 'http://localhost:8081/api/v1',
});

export default app;
