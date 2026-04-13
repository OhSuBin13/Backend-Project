const axios = require("axios");
const redisClient = require("../config/redisClient");

(async () => {
  try {
    await redisClient.connect();
    console.log("Redis Connected");
  } catch (err) {
    console.error("Redis Connection Error", err);
    process.exit(1);
  }
})();

const fetchWeatherDataFromAPI = async (city) => {
  const apiKey = process.env.WEATHER_API_KEY;
  const apiUrl = `https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${city}?unitGroup=metric&contentType=json&key=${apiKey}`;

  try {
    const response = await axios.get(apiUrl);
    return response.data;
  } catch (error) {
    console.error("Error fetching weather data from API:", error);
    throw new Error("Failed to fetch weather data from API");
  }
};

const getWeatherDataFromCache = async (city) => {
  try {
    const cachedData = await redisClient.get(city);
    return cachedData ? JSON.parse(cachedData) : null;
  } catch (error) {
    console.error("Error retrieving data from Redis cache:", error);
    throw new Error("Failed to retrieve data from cache");
  }
};

const saveWeatherDataToCache = async (city, weatherData) => {
  try {
    await redisClient.set(city, JSON.stringify(weatherData), "EX", 3600);
  } catch (error) {
    console.error("Error saving data to Redis cache:", error);
    throw new Error("Failed to save data to cache");
  }
};

const getWeatherModel = async (city) => {
  try {
    const cachedData = await getWeatherDataFromCache(city);
    if (cachedData) {
      console.log(`Data for ${city} fetched from cache.`);
      return { source: "cache", data: cachedData };
    }

    const weatherData = await fetchWeatherDataFromAPI(city);
    await saveWeatherDataToCache(city, weatherData);
    console.log(`Data for ${city} fetched from API and saved to cache.`);
    return { source: "api", data: weatherData };
  } catch (error) {
    console.error("Unable to get weather data:", error);
    throw new Error("Failed to get weather data");
  }
};

module.exports = {
  getWeatherModel,
};
