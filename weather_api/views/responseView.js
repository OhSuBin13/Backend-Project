const sendWeatherResponse = async (res, data, source) => {
  res.status(200).json({
    source,
    data,
  });
};

const sendErrorResponse = async (res, error, source) => {
  res.status(500).json({
    error: error.message,
  });
};

module.exports = { sendWeatherResponse, sendErrorResponse };
