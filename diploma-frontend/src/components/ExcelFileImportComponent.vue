<!-- Component for import excel file and send it to backend -->
<template>
  <div class="calculator">
    <form @submit.prevent="onSubmit">
      <div class="form-group">
        <label for="file">Выберите файл</label>
        <input type="file" class="form-control-file" id="file" @change="onFileChange" />
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
      file: null,
    };
  },
  methods: {
    onFileChange(e) {
      this.file = e.target.files[0];
    },
    async onSubmit() {
      const formData = new FormData();
      formData.append('file', this.file);
      
      try {
        const response = await axios.post('http://localhost:8081/api/v1/excel', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        this.result = response.data;
      } catch (error) {
        console.error(error);
      }
    },
  },
};
</script>

<style scoped>
</style>