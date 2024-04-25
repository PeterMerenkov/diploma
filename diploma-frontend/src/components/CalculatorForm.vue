<template>
  <div class="calculator">
    <h2 class="title">{{ config.title }}</h2>
    <div v-if="false" v-katex:display="{ expression: config.formula, options: { throwOnError: false,  }}"></div>
    <div class="input-container">
      <div v-for="input in config.inputs" :key="input.id" class="input-group">
        <label :for="input.id">{{ input.label }}:</label>
        <input type="number" v-model="input.value" />
        <p v-if="input.description" class="input-description">{{ input.description }}</p>
      </div>
    </div>
    <button @click="calculate">Вычислить</button>
    <div class="result-container" v-if="showResult">
      <template v-if="calculationError">
        <strong style="color: red;">Ошибка: {{ calculationError }}</strong>
      </template>
      <template v-else>
        <div><strong>Результат:</strong></div>
        <div>
          <div>
            <div class="result-var">{{ config.resultVar }} = {{ result }}</div>
            <p v-if="config.resultDescription" class="input-description">{{ config.resultDescription }}</p>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import { ref, inject } from 'vue';

export default {
  props: {
    config: {
      type: Object,
      required: true,
    },
  },
  setup(props) {
    const axios = inject('$axios');
    const result = ref(null);
    const calculationError = ref(null);
    const showResult = ref(false);


    const calculate = async () => {
      try {
        const response = await axios.get(props.config.calculateEndpoint, {
          params: props.config.inputs.reduce((params, input) => {
            params[input.id] = input.value;
            return params;
          }, {}),
        });
        result.value = response.data;
        calculationError.value = null; // Reset error if the calculation is successful
        showResult.value = true; // Show the result after successful calculation
      } catch (error) {
        console.error('Ошибка:', error);
        result.value = null;
        calculationError.value = 'Произошла ошибка при вычислении. Пожалуйста, проверьте введенные данные и повторите попытку.';
        showResult.value = true; // Even if there's an error, show the result container
      }
    };

    return {
      calculate,
      result,
      calculationError,
      showResult,
    };
  },
};
</script>

<style scoped>
.calculator {
  max-width: 600px;
  margin: auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  font-family: 'Arial', sans-serif;
}

.input-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-bottom: 20px;
  align-items: start;
}

.input-group {
  display: grid;
  grid-template-columns: auto 1fr;
  grid-template-rows: auto auto;
  gap: 10px;
}

input {
  grid-column: 2;
  grid-row: 1;
  flex: 1;
  padding: 8px;
  width: 100%;
  height: 40px;
  box-sizing: border-box;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
}

.input-description {
  grid-column: 2;
  grid-row: 2;
  align-self: start;
}

input[type="number"] {
  width: 100%;
  height: 40px;
}

button {
  background-color: #007BFF;
  color: #fff;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 10px;
}

.result-container {
  margin-top: 15px;
  font-size: 18px;
}

.formula {
  font-style: italic;
  margin-bottom: 15px;
  color: #333;
}

.input-description {
  font-size: 0.8em;
  color: #666;
  margin-top: 5px;
}

.input-group ::v-deep .input-description {
  align-self: start;
}

.input-group label {
  align-self: center;
}

.title {
  text-align: center;
}

.result-var {
  margin-top: 10px;
}
</style>
