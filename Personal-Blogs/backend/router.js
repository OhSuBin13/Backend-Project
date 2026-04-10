import express, { response } from "express";
import fse from "fs-extra";
import { comparePassword, generateHashPassword } from "./authentication.js";

const router = express.Router();

async function readDB() {
  const Articles = await fse.readJSON("../backend/db.json");
  return Articles;
}

router.get("/articles", async (req, res) => {
  const Articles = await readDB();
  res.json(Articles);
});

router.get("/articles/:id", async (req, res) => {
  try {
    const Articles = await readDB();
    const { id } = req.params;
    const article = Articles.articles.find(
      (article) => article.id == parseInt(id),
    );

    if (article) {
      res.json(article);
    } else {
      res.status(404).json({ message: "Article not found" });
    }
  } catch (error) {
    res.status(500).json({ message: "Internal server error", error });
  }
});

router.post("/articles", async (req, res) => {
  try {
    let Articles = await readDB();

    if (!Array.isArray(Articles.articles)) {
      Articles.articles = [];
    }

    const article = {
      id: Articles.articles.length + 1,
      title: req.body.title,
      creationtime: req.body.time,
      content: req.body.content,
    };

    Articles.articles.push(article);

    await fse.writeJSON("../backend/db.json", Articles, { spaces: 2 });

    res.json(Articles);
  } catch (error) {
    console.error("Error adding article:", error);
    res.status(500).json({ message: "Internal server error" });
  }
});

router.put("/articles/:id", async (req, res) => {
  try {
    let Articles = await readDB();

    const { id } = req.params;
    const updatedArticle = {
      id: parseInt(id),
      title: req.body.title,
      creationtime: req.body.date,
      content: req.body.content,
    };

    const articleIndex = Articles.articles.findIndex(
      (article) => article.id == parseInt(id),
    );

    if (articleIndex === -1) {
      return res.status(404).json({ message: "Article not found" });
    }

    Articles.articles[articleIndex] = updatedArticle;

    await fse.writeJSON("../backend/db.json", Articles, { spaces: 2 });

    res.json(Articles);
  } catch (error) {
    console.error("Error updating article:", error);
    res.status(500).json({ message: "Internal server error" });
  }
});

router.delete("/articles/:id", async (req, res) => {
  const { id } = req.params;
  const articles = await readDB();
  const filteredArticles = articles.articles.filter(
    (v) => v.id !== parseInt(id),
  );
  await fse.writeJSON(
    "../backend/db.json",
    { articles: filteredArticles },
    { spaces: 2 },
  );
  console.log("deleted");
  res.status(200).json({ response: "deleted successfully!" });
});

router.post("/login", async (req, res) => {
  try {
    const log = await fse.readJSON("../backend/log.json");
    const { name, password } = req.body;

    log.login.push({
      name,
      date: new Date(),
    });
    await fse.writeJSON("../backend/log.json", log, { spaces: 2 });

    const userData = await fse.readJSON("../backend/users.json");
    const user = userData.users.find((user) => user.username === name);

    if (user) {
      const isMatch = await comparePassword(password, user.password);
      if (isMatch) {
        console.log("Login successful");
        return res.status(200).json({ response: "Login successful" });
      }
    }

    console.log("Login failed");
    res.status(404).json({ response: "Incorrect credentials" });
  } catch (error) {
    console.log(error);
  }
});

export default router;
