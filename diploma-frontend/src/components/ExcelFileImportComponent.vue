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
      <button type="submit" class="btn btn-primary">Отправить</button>
    </form>
    <textarea v-if="result" v-model="result" readonly></textarea>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ExcelFileImportComponent',
  data() {
    return {
      result: null,
      rawDataFile: null,
      deltasFile: null,
    };
  },
  methods: {
    onFileChange1(e) {
      this.rawDataFile = e.target.files[0];
    },
    onFileChange2(e) {
      this.deltasFile = e.target.files[0];
    },
    async onSubmit() {
      const formData = new FormData();
      formData.append('rawDataFile', this.rawDataFile);
      formData.append('deltasFile', this.deltasFile);
      
      try {
        const response = await axios.post('http://localhost:8081/api/v1/excel', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        this.result = JSON.stringify(response.data);
      } catch (error) {
        console.error(error);
      }
    },
  },
};
</script>

<style scoped>
</style>