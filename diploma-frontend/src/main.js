import { createApp } from 'vue';
import App from './App.vue';
import axios from 'axios';
import VueKatex from '@hsorby/vue3-katex'
import 'katex/dist/katex.min.css';

const app = createApp(App);

app.use(VueKatex, {
  ariaHidden: false,
  globalOptions: {
    ariaHidden: false,
  // Define global options for KaTeX here
  
  }
  
})

app.provide('$axios', axios.create({
    baseURL: 'http://localhost:8081/api/v1', // Adjust the base URL according to your API
  }));

app.mount('#app');