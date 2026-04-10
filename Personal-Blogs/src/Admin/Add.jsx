import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function Add() {
  const [title, setTitle] = useState("");
  const [date, setDate] = useState("");
  const [content, setContent] = useState("");
  const [published, setPublished] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (published) {
      postArticle(title, date, content)
        .then(() => navigate("/"))
        .catch((error) => console.error("Error posting article:", error))
        .finally(() => setPublished(false)); // Reset `published` to avoid multiple submissions
    }
  }, [published]);

  const handleSubmit = (e) => {
    e.preventDefault();
    setPublished(true);
  };

  return (
    <div className="flex flex-col justify-center items-center">
      <h1 className="text-center handwritten">Add Article</h1>
      <form
        onSubmit={handleSubmit}
        className="flex flex-col justify-center items-start *:margin"
      >
        <input
          type="text"
          name="title"
          className="bg-gray-100 border-2 border-gray-300 rounded-lg p-2 focus:border-gray-500 focus:outline-none transition-colors duration-300"
          placeholder="Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <input
          type="text"
          className="bg-gray-100 border-2 border-gray-300 rounded-lg p-2 focus:border-gray-500 focus:outline-none transition-colors duration-300"
          name="publishing date"
          placeholder="Publishing Date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          required
        />
        <textarea
          name="content"
          className="text-center placeholder:text-4xl font-bg-gray-100 border-2 border-gray-300 rounded-lg p-2 focus:border-gray-500 focus:outline-none transition-colors duration-300"
          placeholder="Content"
          rows={20}
          cols={80}
          value={content}
          onChange={(e) => setContent(e.target.value)}
          required
        />
        <input
          type="submit"
          value="Publish"
          className="rounded p-4 bg-slate-400"
        />
      </form>
    </div>
  );
}

async function postArticle(title, date, content) {
  const result = await fetch("http://localhost:3000/api/articles", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      title: title,
      time: date,
      content: content,
    }),
  });

  if (!result.ok) {
    throw new Error(`HTTP error! status: ${result.status}`);
  }
  return result.json();
}
