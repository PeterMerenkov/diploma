<template>
  <div id="app">
    <h1 class="main-heading">ДИПЛОМ</h1>
    <h2 class="sub-heading">Расчет на основе данных их excel файла</h2>

    <ExcelFileImportComponent />
  </div>
</template>

<script>
import ExcelFileImportComponent from './components/ExcelFileImportComponent.vue';

export default {
  name: 'App',
  components: {
    ExcelFileImportComponent,
  },
};
</script>

<style>
.main-heading {
  color: #007BFF;
  font-size: 52px;
  text-align: center;
  font-weight: bold;
  font-family: 'Arial', sans-serif;
}

.sub-heading {
  color: #333;
  font-size: 36px;
  text-align: center;
  font-family: 'Arial', sans-serif;
}
</style>
