<template>
  <div id="app">
    <h1 class="main-heading">ПГУТИ</h1>
    <h2 class="sub-heading">Расчет задержек при передаче данных <br /> в системе туманных вычислений</h2>

    <Calculator :config="acceptableDistanceConfig" />
    <Calculator :config="averageDelayConfig" />
    <Calculator :config="trafficDensityConfig" />
    <Calculator :config="incomingTrafficConfig" />
  </div>
</template>

<script>
import Calculator from './components/CalculatorForm.vue';

export default {
  name: 'App',
  components: {
    Calculator,
  },
  data() {
    return {
        acceptableDistanceConfig: {
          title: 'Формула допустимого расстояние между источником/приемником информации и вычислительным устройством',
          formula: ' L = \\frac{Z - T}{2\\gamma} ',
          inputs: [
            { id: 'maxWaitTime', label: 'Z', description: 'Время, в течение которого необходимо получить результат' },
            { id: 'processingTime', label: 'T', description: 'Время, необходимое для обработки информации' },
            { id: 'propagationSpeed', label: 'γ', description: 'Среда передачи информации, которая определяет время распространения сигналов на единицу длины' },
          ],
          resultVar: 'L',
          resultDescription: 'Допустимое расстояние между источником/приемником информации и вычислительным устройством',
          calculateEndpoint: '/calculator/calculate-acceptable-distance',
        },
        averageDelayConfig: {
          title: 'Формула средней задержки при доставке сообщения',
          formula: 'W = \\frac{\\rho}{1 - \\rho} \\times \\frac{1}{\\mu}',
          inputs: [
            { id: 'serverLoad', label: 'ρ', description: 'Плотность трафика, которую можно посчитать как отношение между скоростью входящих сообщений и производительностью' },
            { id: 'serverPerformance', label: 'µ', description: 'Производительность сервера (скорость передачи, относящаяся к количеству сообщений/единицу времени)' },
          ],
          resultVar: 'W',
          resultDescription: 'Средняя задержка при доставке сообщения',
          calculateEndpoint: '/calculator/calculate-average-delay',
        },
        trafficDensityConfig: {
          title: 'Формула плотности трафика',
          formula: '\\rho = \\frac{a}{\\mu}',
          inputs: [
            { id: 'incomingMessagesRate', label: 'a', description: 'Скорость входящих сообщений в единицу времени' },
            { id: 'serverPerformance', label: 'µ', description: 'Производительность сервера (скорость передачи, относящаяся к количеству сообщений/единицу времени)' },
          ],
          resultVar: 'ρ',
          resultDescription: 'Плотность трафика',
          calculateEndpoint: '/calculator/calculate-traffic-density',
        },
        incomingTrafficConfig: {
          title: 'Формула входящего трафика',
          formula: 'a = 2R\\eta v',
          inputs: [
            { id: 'radius', label: 'R', description: 'Дальность связи (м)' },
            { id: 'deviceDensity', label: 'η', description: 'Плотность подключенных устройств, оснащенных сетевыми узлами (1/м²)' },
            { id: 'deviceSpeed', label: 'v', description: 'Скорость устройств (если они передвигаются) (м/с)' },
          ],
          resultVar: 'a',
          resultDescription: 'Входящий трафик',
          calculateEndpoint: '/calculator/calculate-incoming-traffic',
        },
    };
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
