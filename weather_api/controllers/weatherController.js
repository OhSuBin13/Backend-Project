const { getWeatherModel } = require("../model/weatherModel");
const responseView = require("../views/responseView");

const getWeather = async (req, res) => {
  const { city } = req.params;
  if (!city) {
    return responseView.sendErrorResponse(res, "City is required");
  }

  try {
    const result = await getWeatherModel(city);
    responseView.sendWeatherResponse(res, result.data, result.source);
  } catch (error) {
    responseView.sendErrorResponse(res, error, error.message);
  }
};

module.exports = { getWeather };
