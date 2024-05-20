<template>
  <div id="app" class="text-center">
    <h1 class="display-3 text-primary font-weight-bold">ДИПЛОМ</h1>
    <h2 class="display-4 text-dark">Расчет на основе excel файлов</h2>

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
</style>
