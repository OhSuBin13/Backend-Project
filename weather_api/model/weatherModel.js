const axios = require('axios');
const redisClient = require('../config/redisClient');

(async () => {
  try {
    await redisClient.connect();
    console.log('Redis Connected');
  } catch (err) {
    console.error('Redis Connection Error', err);
    process.exit(1);
  }
})();

const fetchWeatherDataFromAPI = async (city) => {
  const apiKey = process.env.WEATHER_API_KEY;
  const apiUrl = `https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${city}?unitGroup=metric&contentType=json&key=${apiKey}`;

  try {
    const response = await axios.get(apiUrl);
}