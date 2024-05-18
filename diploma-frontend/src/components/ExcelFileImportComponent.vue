<!-- Component for import excel file and send it to backend -->
<template>
  <div class="calculator">
    <form @submit.prevent="onSubmit">
      <div class="form-group">
        <label for="rawDataFile">Выберите файл с данными</label>
        <input type="file" class="form-control-file" id="rawDataFile" @change="onFileChange1" />
      </div>
      <div class="form-group">
        <label for="deltasFile">Выберите файл с дельтами</label>
        <input type="file" class="form-control-file" id="deltasFile" @change="onFileChange2" />
      </div>
      <div class="form-group">
        <label for="termFile">Выберите файл с терм множеством</label>
        <input type="file" class="form-control-file" id="termFile" @change="onFileChange3" />
      </div>
      <button type="submit" class="btn btn-primary">Отправить</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';
import { saveAs } from 'file-saver';

export default {
  name: 'ExcelFileImportComponent',
  data() {
    return {
      rawValuesFile: null,
      deltasFile: null,
      termSetsFile: null,
    };
  },
  methods: {
    onFileChange1(e) {
      this.rawValuesFile = e.target.files[0];
    },
    onFileChange2(e) {
      this.deltasFile = e.target.files[0];
    },
    onFileChange3(e) {
      this.termSetsFile = e.target.files[0];
    },
    async onSubmit() {
      const formData = new FormData();
      formData.append('rawValuesFile', this.rawValuesFile);
      formData.append('deltasFile', this.deltasFile);
      formData.append('termSetsFile', this.termSetsFile);
      
      try {
        const response = await axios.post('http://localhost:8081/api/v1/excel', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          responseType: 'blob',
        });

        const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        saveAs(blob, 'file.xlsx');
      } catch (error) {
        console.error(error);
      }
    },
  },
};
</script>

<style scoped>
</style>