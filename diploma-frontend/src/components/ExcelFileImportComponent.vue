<!-- Component for import excel file and send it to backend -->
<template>
  <div class="calculator">
    <form @submit.prevent="calculateResult">
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
      <button @click="calculateResult" class="btn btn-primary">Отправить</button>
      <div>
        <button @click="downloadRawValues" class="btn btn-secondary">Скачать raw_values.xlsx</button>
        <button @click="updateRawValuesFile" class="btn btn-secondary">Обновить raw_values.xlsx</button>
      </div>
      <div>
        <button @click="downloadDeltas" class="btn btn-secondary">Скачать deltas.xlsx</button>
        <button @click="updateDeltasFile" class="btn btn-secondary">Обновить deltas.xlsx</button>
      </div>
      <div>
        <button @click="downloadTermSets" class="btn btn-secondary">Скачать term_set.xlsx</button>
        <button @click="updateTermSetsFile" class="btn btn-secondary">Обновить term_set.xlsx</button>
      </div>
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
    async calculateResult() {
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
    async downloadRawValues() {
      try {
        const response = await axios.get('http://localhost:8081/api/v1/excel/raw-values-file', {
          responseType: 'blob',
        });

        const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        saveAs(blob, 'raw_values.xlsx');
      } catch (error) {
        console.error(error);
      }
    },
    async downloadDeltas() {
      try {
        const response = await axios.get('http://localhost:8081/api/v1/excel/deltas-file', {
          responseType: 'blob',
        });

        const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        saveAs(blob, 'deltas.xlsx');
      } catch (error) {
        console.error(error);
      }
    },
    async downloadTermSets() {
      try {
        const response = await axios.get('http://localhost:8081/api/v1/excel/term-sets-file', {
          responseType: 'blob',
        });

        const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        saveAs(blob, 'term_set.xlsx');
      } catch (error) {
        console.error(error);
      }
    },
    async updateRawValuesFile() {
      try {
        const response = await axios.post('http://localhost:8081/api/v1/excel/update-raw-values-file', 
        {
          rawValuesFile: this.rawValuesFile
        },
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        console.log(response);
      } catch (error) {
        console.error(error);
      }
    },
    async updateDeltasFile() {
      try {
        const response = await axios.post('http://localhost:8081/api/v1/excel/update-deltas-file', 
        {
          deltasFile: this.deltasFile
        },
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        console.log(response);
      } catch (error) {
        console.error(error);
      }
    },
    async updateTermSetsFile() {
      try {
        const response = await axios.post('http://localhost:8081/api/v1/excel/update-term-sets-file', 
        {
          termSetsFile: this.termSetsFile
        },
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        console.log(response);
      } catch (error) {
        console.error(error);
      }
    },
  },
};
</script>

<style scoped>
</style>