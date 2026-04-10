import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";

export default function Edit() {
  const { id } = useParams();
  const [title, setTitle] = useState("");
  const [date, setDate] = useState("");
  const [content, setContent] = useState("");
  const [edited, setEdited] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    async function fetchArticle() {
      try {
        const response = await fetch(
          `http://localhost:3000/api/articles/${id}`,
        );
        const article = await response.json();
        setTitle(article.title);
        setDate(article.creationtime);
        setContent(article.content);
        console.log(article);
      } catch (error) {
        console.error("Error fetching article:", error);
      }
    }

    fetchArticle();
  }, []);

  useEffect(() => {
    if (edited === true) {
      editArticle(title, date, content, id);
      navigate("/");
    }
  }, [edited, title, date, content, id, navigate]);

  const handleSubmit = (e) => {
    e.preventDefault();
    setEdited(true);
  };

  return (
    <div className="flex flex-col justify-center items-center">
      <h1 className="text-center">Update Article</h1>
      <form
        onSubmit={handleSubmit}
        className="flex flex-col justify-center items-start *:margin"
      >
        <input
          type="text"
          name="title"
          placeholder="Title"
          value={title}
          className="bg-gray-100 border-2 border-gray-300 rounded-lg p-2 focus:border-gray-500 focus:outline-none transition-colors duration-300"
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <input
          type="text"
          name="publishing date"
          className="bg-gray-100 border-2 border-gray-300 rounded-lg p-2 focus:border-gray-500 focus:outline-none transition-colors duration-300"
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
          value="Update"
          className="rounded p-4 bg-slate-400"
        />
      </form>
    </div>
  );
}

async function editArticle(title, date, content, id) {
  const result = await fetch(`http://localhost:3000/api/articles/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      title: title,
      date: date,
      content: content,
    }),
  });

  if (!result.ok) {
    throw new Error(`Failed to update article with status ${result.status}`);
  }

  return result.json();
}
