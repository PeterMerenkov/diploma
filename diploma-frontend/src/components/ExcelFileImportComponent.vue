<!-- Component for import excel file and send it to backend -->
<template>
  <div class="container mt-5">
    <form @submit.prevent="calculateResult">
      <div class="form-group">
        <label for="rawDataFile" class="form-label">Сырые данные</label>
        <input type="file" class="form-control" id="rawDataFile" @change="onRawValuesFileChange" />
        <button @click="downloadRawValues" class="btn btn-primary mb-2">Скачать с сервера raw_values.xlsx</button>
        <button v-if="isRawValueFileSelected" @click="updateRawValuesFile" class="btn btn-primary mb-2">Обновить серверный файл raw_values.xlsx</button>
      </div>
      <div class="form-group">
        <label for="deltasFile" class="form-label">Дельты</label>
        <input type="file" class="form-control" id="deltasFile" @change="onDeltasFileChange" />
        <button @click="downloadDeltas" class="btn btn-primary mb-2">Скачать с сервера deltas.xlsx</button>
        <button v-if="isDeltasFileSelected" @click="updateDeltasFile" class="btn btn-primary mb-2">Обновить серверный файл deltas.xlsx</button>
      </div>
      <div class="form-group">
        <label for="termFile" class="form-label">Терм-множества</label>
        <input type="file" class="form-control" id="termFile" @change="onTermSetsFileChange" />
        <button @click="downloadTermSets" class="btn btn-primary mb-2">Скачать с сервера term_set.xlsx</button>
        <button v-if="isTermSetsFileSelected" @click="updateTermSetsFile" class="btn btn-primary mb-2">Обновить серверный файл term_set.xlsx</button>
      </div>
      <button type="submit" class="btn btn-success">Рассчитать</button>
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
      isRawValueFileSelected: false,
      deltasFile: null,
      isDeltasFileSelected: false,
      termSetsFile: null,
      isTermSetsFileSelected: false,
    };
  },
  methods: {
    onRawValuesFileChange(e) {
      this.rawValuesFile = e.target.files[0];
      this.isRawValueFileSelected = true;
    },
    onDeltasFileChange(e) {
      this.deltasFile = e.target.files[0];
      this.isDeltasFileSelected = true;
    },
    onTermSetsFileChange(e) {
      this.termSetsFile = e.target.files[0];
      this.isTermSetsFileSelected = true;
    },
    async calculateResult() {
      try {
        const response = await axios.get('http://localhost:8081/api/v1/excel', {
          responseType: 'blob',
        });

        const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        saveAs(blob, 'result.xlsx');
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
.container {
  max-width: 600px;
}
.form-group {
  margin-bottom: 1.5rem;
}
.form-control {
  margin-bottom: 0.5rem;
}
</style>