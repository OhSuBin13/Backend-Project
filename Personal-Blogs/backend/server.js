import express from "express";
import router from "./router.js";
import cors from "cors";

const app = express();
app.use(cors());
app.use(express.json());
app.use("/api", router);
app.listen(3000, () => {
  console.log("Server is running on port 3000");
});
